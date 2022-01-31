package org.sid.FamilyaProject.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@NoArgsConstructor   @ToString

public class Depense  {	
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long  Id;
	private double montantDepense;
	private Date dateDuDepense;
	private String motif;
	
	public Depense(double montantDepense, String motif, Date dateDuDepense) {
		
		this.montantDepense = montantDepense;		
		this.motif = motif;
		this.dateDuDepense = dateDuDepense;
	}
	
	
	public double getMontantDepense() {
		return montantDepense;
	}
	public void setMontantDepense(double montantDepense) {
		this.montantDepense = montantDepense;
	}
	public Date getDate() {
		return dateDuDepense;
	}
	public void setDate(Date date) {
		this.dateDuDepense = date;
	}
	public String getMotif() {
		return motif;
	}
	public void setMotif(String motif) {
		this.motif = motif;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public Date getDateDuDepense() {
		return dateDuDepense;
	}
	public void setDateDuDepense(Date dateDuDepense) {
		this.dateDuDepense = dateDuDepense;
	}
	
	
	
	
	
	

}
