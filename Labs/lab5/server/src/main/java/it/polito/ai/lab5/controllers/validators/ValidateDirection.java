package it.polito.ai.lab5.controllers.validators;

import javax.validation.Payload;

public @interface ValidateDirection {

    String[] acceptedValues();

    String message() default "{it.polito.ai.lab5.controllers.validators.ValidateDirection.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
