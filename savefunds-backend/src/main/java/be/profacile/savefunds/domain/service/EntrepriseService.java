package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.Entreprise;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des entreprises
 *
 */
public interface EntrepriseService {

    Optional<Entreprise> findById(Long id);

    Optional<Entreprise> findByUserId(Long userId);

    List<Entreprise> findAllByUserId(Long userId);

    List<Entreprise> findAll();

    Entreprise create(Entreprise entreprise);

    Entreprise update(Long id, Entreprise entreprise);

    void delete(Long id);
}
