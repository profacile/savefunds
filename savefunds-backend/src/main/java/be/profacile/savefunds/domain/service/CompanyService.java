package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.Company;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des companies
 *
 */
public interface CompanyService {

    Optional<Company> findById(Long id);

    Optional<Company> findByUserId(Long userId);

    List<Company> findAllByUserId(Long userId);

    List<Company> findAll();

    Company create(Company company);

    Company update(Long id, Company company);

    void delete(Long id);
}
