package be.profacile.savefunds.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator pour numéro entreprise belge
 *
 * Format attendu :
 * 10 chiffres
 */
public class NumeroEntrepriseValidator implements ConstraintValidator<ValidNumeroEntreprise, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return false;
        }

        // enlever espaces et points
        value = value.replaceAll("[ .]", "");

        // vérifier longueur
        if (value.length() != 10) {
            return false;
        }

        // vérifier chiffres
        if (!value.matches("\\d+")) {
            return false;
        }

        return true;
    }
}