package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.repository.UserRepository;
import be.profacile.savefunds.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des utilisateurs
 *
 * EXEMPLE COMPLET - Les stagiaires doivent s'en inspirer
 * pour créer EntrepriseServiceImpl, AnalyseServiceImpl, etc.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.debug("Recherche utilisateur par email : {}", email);

        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        log.debug("Recherche utilisateur par ID : {}", id);

        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.debug("Vérification existence email : {}", email);

        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.debug("Récupération de tous les utilisateurs");

        return userRepository.findAll();
    }

    @Override
    public User create(User user) {
        log.info("Création utilisateur : {}", user != null ? user.getEmail() : null);

        // Validations
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }

        // Validation : email unique
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé : " + user.getEmail());
        }

        // Sauvegarde
        User saved = userRepository.save(user);

        log.info("Utilisateur créé avec ID : {}", saved.getId());

        return saved;
    }

    @Override
    public User update(Long id, User user) {
        log.info("Mise à jour utilisateur ID : {}", id);

        // Vérifier que l'utilisateur existe
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // ✅ MISE À JOUR CHAMP PAR CHAMP (évite la perte de données)

        if (user.getNom() != null) {
            existing.setNom(user.getNom());
        }

        if (user.getPrenom() != null) {
            existing.setPrenom(user.getPrenom());
        }

        if (user.getEmailVerified() != null) {
            existing.setEmailVerified(user.getEmailVerified());
        }

        if (user.getRole() != null) {
            existing.setRole(user.getRole());
        }

        // Note: L'email ne doit PAS être modifiable après création
        // (identifiant unique de connexion)

        // Sauvegarde
        User updated = userRepository.save(existing);

        log.info("Utilisateur mis à jour : ID {}, email {}", updated.getId(), updated.getEmail());

        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression utilisateur ID : {}", id);

        // Vérifier que l'utilisateur existe
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userRepository.deleteById(id);

        log.info("Utilisateur supprimé : ID {}", id);
    }

    @Override
    public void changePassword(Long id, String ancienMotDePasse, String nouveauMotDePasse) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (!passwordEncoder.matches(ancienMotDePasse, user.getPasswordHash())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(nouveauMotDePasse));
        userRepository.save(user);
        log.info("Mot de passe modifié pour utilisateur ID : {}", id);
    }
}