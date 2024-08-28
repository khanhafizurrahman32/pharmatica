package org.example.pharmaticb.utilities.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringEnumerationValidator implements ConstraintValidator<StringEnumeration, String> {
    private Set<String> enumNames;

    public static Set<String> getNamesSet(Class<? extends Enum<?>> enumClass) {
        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        String[] enumNames = new String[enumConstants.length];

        return new HashSet<>(Arrays.asList(enumNames));
    }
    @Override
    public void initialize(StringEnumeration constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return false;
    }
}
