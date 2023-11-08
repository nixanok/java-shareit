package ru.practicum.shareit.user.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = WithoutSpacesValidator.class)
@Documented
public @interface WithoutSpaces {

    String message() default "{ru.practicum.shareit.validation.WithoutSpaces.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
