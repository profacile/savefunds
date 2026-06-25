package be.profacile.savefunds.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation personnalisée pour valider qu’un montant est positif
 */
@Documented
@Constraint(validatedBy = MontantPositifValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MontantPositif {

    String message() default "Le montant doit être positif";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}