package be.profacile.savefunds.validation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires du validateur MontantPositifValidator.
 */
class MontantPositifValidatorTest {

    /**
     * Instance du validateur à tester.
     */
    private final MontantPositifValidator validator = new MontantPositifValidator();

    /**
     * Vérifie qu'un montant strictement positif est accepté.
     */
    @Test
    void shouldReturnTrueForPositiveAmount() {
        assertTrue(validator.isValid(new BigDecimal("100.50"), null));
    }

    /**
     * Vérifie que zéro est refusé.
     */
    @Test
    void shouldReturnFalseForZeroAmount() {
        assertFalse(validator.isValid(BigDecimal.ZERO, null));
    }

    /**
     * Vérifie qu'un montant négatif est refusé.
     */
    @Test
    void shouldReturnFalseForNegativeAmount() {
        assertFalse(validator.isValid(new BigDecimal("-5.00"), null));
    }

    /**
     * Vérifie qu'une valeur null est acceptée.
     * Le contrôle null peut être géré séparément avec @NotNull.
     */
    @Test
    void shouldReturnTrueForNullValue() {
        assertTrue(validator.isValid(null, null));
    }
}