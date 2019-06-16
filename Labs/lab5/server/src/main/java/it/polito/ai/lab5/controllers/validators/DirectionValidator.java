package it.polito.ai.lab5.controllers.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class DirectionValidator implements ConstraintValidator<ValidateDirection, String> {


        private List<String> valueList;

        @Override
        public void initialize(ValidateDirection constraintAnnotation) {
            valueList = new ArrayList<>();
            for(String val : constraintAnnotation.acceptedValues()) {
                valueList.add(val);
            }
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if(!valueList.contains(value)) {
                return false;
            }
            return true;
        }


}
