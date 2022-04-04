package org.sid.FamilyaProject.uniqueAnnotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.sid.FamilyaProject.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueNumeroFiche implements ConstraintValidator<UniqueNumeroFicheInterface,String> {
	
	@Autowired
	UserRepository userRepository;
	static int count=0;
	static String state="notCalled";
	
	@Override	
	public void initialize(UniqueNumeroFicheInterface constraintAnnotation) {
		
		//ConstraintValidator.super.initialize(constraintAnnotation);
	}
	
	@Override
	public boolean isValid(String ficheNumber, ConstraintValidatorContext context) {
		
		System.out.println("=====Before====count=="+UniqueNumeroFiche.count);
//		System.out.println("=====Before===state==="+UniqueNumeroFiche.state);
//		if(UniqueNumeroFiche.state.equals("notcalled"))UniqueNumeroFiche.count=0;
        UniqueNumeroFiche.count++;        
		System.out.println("=====After===count==="+UniqueNumeroFiche.count);
//		System.out.println("=====after===state==="+UniqueNumeroFiche.state);
//		
//		if(UniqueNumeroFiche.state.equals("notCalled")) { 
//			    System.out.println("=====inside if======"+UniqueNumeroFiche.count);
//				//System.out.println("==========="+userRepository.getUserByFicheNumber(ficheNumber));
				
				
//				if(userRepository.getUserByFicheNumber(ficheNumber)!=null) {
//					
//					 UniqueNumeroFiche.state="called";
//					 if(UniqueNumeroFiche.count==2) UniqueNumeroFiche.state="notcalled";
//					return false;
//					
//				}		
//				
//		}			
				
		
		
		while(UniqueNumeroFiche.count<2) {
			
			System.out.println("=====Inside while====count=="+UniqueNumeroFiche.count);			
			return userRepository.getUserByFicheNumber(ficheNumber)!=null;
			
			
		};
		
		
		
		
//			UniqueNumeroFiche.state="called";
//			if(UniqueNumeroFiche.count==2)UniqueNumeroFiche.state="notcalled";
		  if(UniqueNumeroFiche.count==2)UniqueNumeroFiche.count=0;
			System.out.println("=====End===count==="+UniqueNumeroFiche.count);
//			System.out.println("=====end===state==="+UniqueNumeroFiche.state);
			return true; 		
		
	}
	
		
//		if(UniqueNumeroFiche.count==1 ) UniqueNumeroFiche.count=0;
//		else UniqueNumeroFiche.count=1;
//		return true; 
	
	
	
}













