package org.sid.FamilyaProject.entities;

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
public class Archive {
	
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	
	
	private Long Id_debArchiv;
	
	private double sommeEmprunt;	
	private double duree_echeance;
	private double taux;
	private String enteredMatric;	
	private double dettePlusInteret;
	private String typeInteret;
	private Date date_emprunt;
	private String nom;
	private double beneficeGenere;
		

	public Archive(String nom, String enteredMatric, double sommeEmprunt, double duree_echeance, double taux, 
			double dettePlusInteret, String typeInteret, Date date_emprunt,double beneficeGenere) {
		this.nom=nom;
		this.sommeEmprunt = sommeEmprunt;
		this.duree_echeance = duree_echeance;
		this.taux = taux;
		this.enteredMatric = enteredMatric;
		this.dettePlusInteret = dettePlusInteret;
		this.typeInteret = typeInteret;
		this.date_emprunt = date_emprunt;
		this.beneficeGenere=beneficeGenere;
		
	}

	public Long getId_debArchiv() {
		return Id_debArchiv;
	}

	public void setId_debArchiv(Long id_debArchiv) {
		Id_debArchiv = id_debArchiv;
	}

	public double getSommeEmprunt() {
		return sommeEmprunt;
	}

	public void setSommeEmprunt(double sommeEmprunt) {
		this.sommeEmprunt = sommeEmprunt;
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

	public String getEnteredMatric() {
		return enteredMatric;
	}

	public void setEnteredMatric(String enteredMatric) {
		this.enteredMatric = enteredMatric;
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

	public Date getDate_emprunt() {
		return date_emprunt;
	}

	public void setDate_emprunt(Date date_emprunt) {
		this.date_emprunt = date_emprunt;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public double getBeneficeGenere() {
		return beneficeGenere;
	}

	public void setBeneficeGenere(double beneficeGenere) {
		this.beneficeGenere = beneficeGenere;
	}

	
	
	
	
	
	
	
	
	
	

}
