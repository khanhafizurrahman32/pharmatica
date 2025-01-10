package org.example.pharmaticb.utilities.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;

        if (password.length() < 6) {
            return false;
        }

//        for (char c : password.toCharArray()) {
//            if (Character.isLowerCase(c)) hasLower = true;
//            else if (Character.isUpperCase(c)) hasUpper = true;
//            else if (Character.isDigit(c)) hasDigit = true;
//            else if ("!@#$%^&*()-_=+[]{}|;:,.<>?".indexOf(c) >= 0) hasSpecial = true;
//        }

        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
}
