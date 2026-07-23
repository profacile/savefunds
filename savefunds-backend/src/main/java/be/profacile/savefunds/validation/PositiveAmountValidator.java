package be.profacile.savefunds.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * Validator qui vérifie qu’un montant est positif
 */
public class PositiveAmountValidator implements ConstraintValidator<PositiveAmount, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {

        // null autorisé → utiliser @NotNull si obligatoire
        if (value == null) {
            return true;
        }

        return value.compareTo(BigDecimal.ZERO) > 0;
    }
}