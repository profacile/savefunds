package be.profacile.savefunds.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NumeroEntrepriseValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNumeroEntreprise {

    String message() default "Numéro entreprise invalide";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}