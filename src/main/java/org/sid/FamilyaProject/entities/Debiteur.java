package org.sid.FamilyaProject.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import lombok.ToString;




@Entity

@NoArgsConstructor  @ToString
public class Debiteur  implements Serializable  {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long id_debiteur;	
	private double sommeEmprunt;
	private Date date_emprunt;
	private double duree_echeance;
	private double taux;
	private String enteredMatric;	
	private double premierRemboursement;
	private double detteCourante;
	private double dettePlusInteret;
	private String typeInteret;
	private double currentPenalite;	
	private double formerPenalite;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="foreignKeyMember", nullable=false)
	private Member  member;
	
	
	public Debiteur(String enteredMatric, double sommeEmprunt,  double duree_echeance, double taux,  double detteCourante , Date date_emprunt, double premierRemboursement, double dettePlusInteret, String typeInteret) {
		
		this.enteredMatric=enteredMatric;
		this.sommeEmprunt = sommeEmprunt;
		this.date_emprunt = date_emprunt;
		this.duree_echeance = duree_echeance;
		this.taux = taux;
		this.detteCourante=detteCourante;
		this.premierRemboursement=premierRemboursement;
		this.typeInteret=typeInteret;
		this.dettePlusInteret=dettePlusInteret;
		this.currentPenalite=0.0;
		this.formerPenalite=0.0;
		
	}
	
	
	public String getEnteredMatric() {
		return enteredMatric;
	}



	public void setEnteredMatric(String enteredMatric) {
		this.enteredMatric = enteredMatric;
	}
	
	public double getSommeEmprunt() {
		return sommeEmprunt;
	}
	public void setSommeEmprunt  (double sommeEmprunt) {
		this.sommeEmprunt = sommeEmprunt;
	}
	public Date getDate_emprunt() {
		return date_emprunt;
	}
	public void setDate_emprunt(Date date_emprunt) {
		this.date_emprunt = date_emprunt;
	}
	public double getDuree_echeance() {
		return duree_echeance;
	}
	public void setDuree_echeance(double duree_echeance) {
		this.duree_echeance = duree_echeance;
	}
	public double getTaux() {
		return taux;
	}
	public void setTaux(double taux) {
		this.taux = taux;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}


	public Long getId_debiteur() {
		return id_debiteur;
	}


	public void setId_debiteur(Long id_debiteur) {
		this.id_debiteur = id_debiteur;
	}


	public double getPremierRemboursement() {
		return premierRemboursement;
	}


	public void setPremierRemboursement(double premierRemboursement) {
		this.premierRemboursement = premierRemboursement;
	}


	public double getDettePlusInteret() {
		return dettePlusInteret;
	}


	public void setDettePlusInteret(double dettePlusInteret) {
		this.dettePlusInteret = dettePlusInteret;
	}


	public String getTypeInteret() {
		return typeInteret;
	}


	public void setTypeInteret(String typeInteret) {
		this.typeInteret = typeInteret;
	}


	public double getDetteCourante() {
		return detteCourante;
	}


	public void setDetteCourante(double detteCourante) {
		this.detteCourante = detteCourante;
	}


	public double getCurrentPenalite() {
		return currentPenalite;
	}


	public void setCurrentPenalite(double currentPenalite) {
		this.currentPenalite = currentPenalite;
	}


	public double getFormerPenalite() {
		return formerPenalite;
	}


	public void setFormerPenalite(double formerPenalite) {
		this.formerPenalite = formerPenalite;
	}


	
 

}
