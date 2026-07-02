package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.dto.request.LoginRequest;
import be.profacile.savefunds.api.dto.request.RegisterRequest;
import be.profacile.savefunds.api.dto.request.ResetPasswordRequest;
import be.profacile.savefunds.api.dto.response.AuthResponse;
import be.profacile.savefunds.api.dto.response.MessageResponse;
import be.profacile.savefunds.api.dto.response.UserResponse;
import be.profacile.savefunds.domain.entity.PasswordResetToken;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.repository.PasswordResetTokenRepository;
import be.profacile.savefunds.domain.repository.UserRepository;
import be.profacile.savefunds.domain.service.AuthService;
import be.profacile.savefunds.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implémentation du service d'authentification.
 *
 * Cette classe gère :
 * - l'inscription d'un nouvel utilisateur
 * - la connexion d'un utilisateur existant
 * - la génération d'un token JWT
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Inscrit un nouvel utilisateur.
     *
     * @param request données d'inscription
     * @return réponse d'authentification contenant le token JWT et l'utilisateur
     */
    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(resolvePublicRegistrationRole(request.getRole()));
        user.setEmailVerified(false);

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getEmail());

        UserResponse userResponse = new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getNom(),
                saved.getPrenom(),
                saved.getRole(),
                saved.getEmailVerified(),
                saved.getPhotoUrl()
        );

        return new AuthResponse(token, userResponse);
    }

    /**
     * Connecte un utilisateur existant.
     *
     * @param request données de connexion
     * @return réponse d'authentification contenant le token JWT et l'utilisateur
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getRole(),
                user.getEmailVerified(),
                user.getPhotoUrl()
        );

        return new AuthResponse(token, userResponse);
    }

    @Override
    @Transactional
    public MessageResponse requestPasswordReset(String email) {
        AtomicReference<String> resetLink = new AtomicReference<>();
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = generateResetToken();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setTokenHash(hashToken(token));
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
            passwordResetTokenRepository.save(resetToken);

            // Dev/demo: le lien est renvoye dans le message. En production, brancher SMTP et ne pas exposer le lien ici.
            resetLink.set(frontendUrl + "/reset-password?token=" + token);
        });

        if (resetLink.get() != null) {
            return new MessageResponse("Lien de reinitialisation genere: " + resetLink.get());
        }
        return new MessageResponse("Si un compte existe pour cet email, un lien de reinitialisation sera envoye.");
    }

    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(hashToken(request.getToken()))
                .orElseThrow(() -> new IllegalArgumentException("Lien de reinitialisation invalide ou expire"));
        if (!resetToken.isUsable()) {
            throw new IllegalArgumentException("Lien de reinitialisation invalide ou expire");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);

        return new MessageResponse("Votre mot de passe a ete mis a jour. Vous pouvez vous connecter.");
    }

    private Role resolvePublicRegistrationRole(Role requestedRole) {
        if (requestedRole == null) {
            return Role.DIRIGEANT;
        }
        if (requestedRole == Role.ADMIN) {
            throw new IllegalArgumentException("Le role ADMIN ne peut pas etre cree depuis l'inscription publique");
        }
        return requestedRole;
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Impossible de securiser le token de reinitialisation", ex);
        }
    }

}
