package org.sid.FamilyaProject.uniqueAnnotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sid.FamilyaProject.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueNumeroPiece implements ConstraintValidator<UniqueNumeroPieceInterface,String> {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public boolean isValid(String pieceNumber, ConstraintValidatorContext context) {
		// TODO Auto-generated method stub
		return userRepository.getUserByPieceNumber(pieceNumber)!=null;
	}

}
