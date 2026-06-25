package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.dto.request.LoginRequest;
import be.profacile.savefunds.api.dto.request.RegisterRequest;
import be.profacile.savefunds.api.dto.response.AuthResponse;
import be.profacile.savefunds.api.dto.response.UserResponse;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.repository.UserRepository;
import be.profacile.savefunds.domain.service.AuthService;
import be.profacile.savefunds.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
        user.setRole(Role.DIRIGEANT);
        user.setEmailVerified(false);

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getEmail());

        UserResponse userResponse = new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getNom(),
                saved.getPrenom(),
                saved.getRole(),
                saved.getEmailVerified()
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
                user.getEmailVerified()
        );

        return new AuthResponse(token, userResponse);
    }
}