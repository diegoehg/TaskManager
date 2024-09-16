package com.encora.taskmanager.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private Pattern pattern;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        String regex = constraintAnnotation.regex();
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true; // Let @NotEmpty handle null values
        }
        return pattern.matcher(password).matches();
    }
}