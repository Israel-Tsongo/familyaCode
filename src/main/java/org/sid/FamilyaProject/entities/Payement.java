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
@NoArgsConstructor   @ToString

	

public class Payement  implements  Serializable  {
	
	

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long Id_paye;
	private Date datePayement;
	private double contribMensuel;
	private String enteredMatric;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="foreignKeyMemberPaying", nullable=false)
	private Member  memberPaying;
	
	
			public Payement( String enteredMatric, double contribMensuel,Date date) {
					
				    this.enteredMatric = enteredMatric;
					this.contribMensuel = contribMensuel;
					this.datePayement=date;
					
				}
	



	public Long getId_paye() {
		return Id_paye;
	}



	public void setId_paye(Long id_paye) {
		Id_paye = id_paye;
	}



	public double getContribMensuel() {
		return contribMensuel;
	}



	public void setContribMensuel(double contribMensuel) {
		this.contribMensuel = contribMensuel;
	}



	public String getEnteredMatric() {
		return enteredMatric;
	}



	public void setEnteredMatric(String enteredMatric) {
		this.enteredMatric = enteredMatric;
	}



	public Member getMemberPaying() {
		return memberPaying;
	}



	public void setMemberPaying(Member memberPaying) {
		this.memberPaying = memberPaying;
	}




	public Date getDate_payement() {
		return datePayement;
	}




	public void setDate_payement(Date date_payement) {
		this.datePayement = date_payement;
	}


	

	
	
	
	

}
