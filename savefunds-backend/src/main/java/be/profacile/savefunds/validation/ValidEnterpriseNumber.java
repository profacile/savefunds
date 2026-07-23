package be.profacile.savefunds.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnterpriseNumberValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnterpriseNumber {

    String message() default "Numéro company invalide";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}