package org.sid.FamilyaProject.uniqueAnnotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;




@Constraint(validatedBy=UniqueNumeroFiche.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueNumeroPieceInterface{
	
	
	String message() default "This Numero Piece is already registered";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}