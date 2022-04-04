package org.sid.FamilyaProject.uniqueAnnotations;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy=UniqueNumeroFiche.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueNumeroFicheInterface{
	
	
	String message() default "This Numero fiche is already registered";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}