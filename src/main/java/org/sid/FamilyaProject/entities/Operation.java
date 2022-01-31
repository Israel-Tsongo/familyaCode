package org.sid.FamilyaProject.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.ToString;



@Entity
@Table(name="Operation")
@NoArgsConstructor   @ToString
public class Operation {
	
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long idOperation;
	
	
	private String operation;		
	private  Date date;
	
	public Operation( String operation,  Date date) {
		
				
		this.operation = operation;		
		this.date = date;
	}

	public Long getIdOperation() {
		return idOperation;
	}

	public void setIdOperation(Long idOperation) {
		this.idOperation = idOperation;
	}

	

	

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
	
	

}
