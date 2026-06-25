package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des utilisateurs
 */
public interface UserService {
    
    /**
     * Trouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Trouve un utilisateur par son ID
     */
    Optional<User> findById(Long id);
    
    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);
    
    /**
     * Liste tous les utilisateurs
     */
    List<User> findAll();
    
    /**
     * Crée un nouvel utilisateur
     */
    User create(User user);
    
    /**
     * Met à jour un utilisateur existant
     */
    User update(Long id, User user);
    
    /**
     * Supprime un utilisateur
     */
    void delete(Long id);

    void changePassword(Long id, String ancienMotDePasse, String nouveauMotDePasse);
}