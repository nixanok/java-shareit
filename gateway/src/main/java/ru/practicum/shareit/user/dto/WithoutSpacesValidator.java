package ru.practicum.shareit.user.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WithoutSpacesValidator implements ConstraintValidator<WithoutSpaces, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !value.contains(" ");
    }

}
