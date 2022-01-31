package org.sid.FamilyaProject.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.sid.FamilyaProject.users.User;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;



@Entity
@Table(name="member")
@NoArgsConstructor  


public class Member implements Serializable {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	 
	private Long Id_member; 
	
	//@Basic()
	private String  nom; 
	private String matricule; 
	private String fonction; 
	private Date date_adhesion; 	
	private String typeContrat; 
	private String categorieMembre;	
	private double capital_Initial;
	
	
	
	
	@OneToMany(mappedBy="member", fetch=FetchType.LAZY, cascade=CascadeType.ALL)	
	private Set<Debiteur> debiteurs; 
	
	@OneToMany(mappedBy="memberPaying", fetch=FetchType.LAZY, cascade=CascadeType.ALL)  
	private Set< Payement>  payements; 
	
	
	@OneToMany(mappedBy="membre",fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	private Set<Events> events;
	
	@OneToMany(mappedBy="membreDansInteret",fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	private Set<InteretParMembre> intereretParMembre;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userForeignKeyInMember", nullable = false)
    private User memberUser ;
	
	
	public Member(String nom, String matricule, String fonction, Date date_adhesion, 
			String typeContrat, String categorieMembre, double capital_Initial) {
		
		this.nom = nom;
		this.matricule = matricule;
		this.fonction = fonction;
		this.date_adhesion = date_adhesion;		
		this.typeContrat = typeContrat;
		this.categorieMembre = categorieMembre;
		this.capital_Initial = capital_Initial;
	}

	
	
	
	
	
	
	public String getNom() {
		return this.nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getMatricule() {
		return matricule;
	}
	
	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}
	
	public String getFonction() {
		return fonction;
	}
	
	public void setFonction(String fonction) {
		this.fonction = fonction;
	}
	
	public Date getDate_adhesion() {
		return date_adhesion;
	}
	
	public void setDate_adhesion(Date date_adhesion) {
		this.date_adhesion = date_adhesion;
	}
	
	
	
	public String getTypeContrat() {
		return typeContrat;
	}
	
	public void setTypeContrat(String typeContrat) {
		this.typeContrat = typeContrat;
	}
	
	public String getCategorieMembre() {
		return categorieMembre;
	}
	
	public void setCategorieMembre(String categorieMembre) {
		this.categorieMembre = categorieMembre;
	}
	public double getCapital_Initial() {
		return capital_Initial;
	}
	
	public void setCapital_Initial(double capital_Initial) {
		this.capital_Initial = capital_Initial;
	}

	
	public Set<Payement> getPayements() {
		return payements;
	}

	public void setPayements(Set<Payement> payements) {
		this.payements = payements;
	}


	public Set<Debiteur> getDebiteurs() {
		return debiteurs;
	}

	public void setDebiteurs(Set<Debiteur> debiteurs) {
		this.debiteurs = debiteurs;
	}







	public Set<Events> getEvents() {
		return events;
	}







	public void setEvents(Set<Events> events) {
		this.events = events;
	}







	public Set<InteretParMembre> getIntereretParMembre() {
		return intereretParMembre;
	}







	public void setIntereretParMembre(Set<InteretParMembre> intereretParMembre) {
		this.intereretParMembre = intereretParMembre;
	}







	public Long getId_member() {
		return Id_member;
	}







	public void setId_member(Long id_member) {
		Id_member = id_member;
	}












	public User getMemberUser() {
		return memberUser;
	}







	public void setMemberUser(User memberUser) {
		this.memberUser = memberUser;
	}





 

	

	
	
	
	
	
	
	
}
