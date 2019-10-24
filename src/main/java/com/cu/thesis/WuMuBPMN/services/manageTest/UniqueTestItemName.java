package com.cu.thesis.WuMuBPMN.services.manageTest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTestItemNameValidator.class)
public @interface UniqueTestItemName {
    //String message() default "{com.dolszewski.blog.UniqueLogin.message}";
    String message() default "same test item name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}