package org.sid.FamilyaProject.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.sid.FamilyaProject.dao.ArchiveRepository;
import org.sid.FamilyaProject.dao.DepenseRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.metier.Traitement;

import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor @ToString

public class InteretParMembre {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long id_interet;	
	private String matriculeEntered;	
	private double interetDuMembre;	
	private Date dateInteret;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="foreignKeyInteretParMembre")
	private Member membreDansInteret;
	
	

	
	
	
	public void partageInteret(PayementRepository payeRepo, InteretParMembreRepository interetRepo, EventsRepository eventRepo,DepenseRepository depenseRepo, MemberRepository memberRepo,ArchiveRepository archivRepo,List<String> errorList ) {
		     
		     double interetTotalDansArchive;		     
		     double interetTotal  = 0.00;
		     double totalDepense =0.00;
		     double interetNet=0.00;
		     double interetPartageable=0.00;
		     double totalCapitauxInitiaux=0.00;
		     double capitalInitialParMembre=0.00;
		     double interetParChacun=0.00;
		     double sommePenalite=0.00;
		     double currentMemberTotalContrib=0.00;
		     double capitalPlusTotalContributionsDuMembre=0.00;
		     double totalContributionPlusTotalCapitauxInitiaux=0.00;
		     Member currentMember=null;
		     double partDuFondateur=0.00;
		     Traitement trt= new Traitement();
		     List<List<Object>> listCapitaux;
		     
		        // interetTotalDansRembourse =eventRepo.getTotalGeneretedBenefitByAllMember() !=null ? eventRepo.getTotalGeneretedBenefitByAllMember():0;		         
		         interetTotalDansArchive=archivRepo.totalBenefitInArchive()!=null ? archivRepo.totalBenefitInArchive():0;
		         sommePenalite=archivRepo.totalPenalite()!=null ? archivRepo.totalPenalite():0;

		         interetTotal=(interetTotalDansArchive+sommePenalite);
		         totalDepense=depenseRepo.getTotalOutgo() !=null? depenseRepo.getTotalOutgo() : 0;
		         listCapitaux=memberRepo.getCapitalInitialParMembre();		         
		  	     totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null? memberRepo.getTotalCapitauxInitiaux() : 0 ;
                 interetNet=(interetTotal-totalDepense);
                 partDuFondateur=trt.rounder(((50*interetPartageable)/100));
                 interetPartageable=(interetNet-partDuFondateur);
                 
		         System.out.println("++++++++++++ interetPartageable :"+interetPartageable);
		         System.out.println("++++++++++++ interetTotal:"+interetTotal);
		         System.out.println("++++++++++++ totalDepense :"+totalDepense);
		         
		         if(interetPartageable>0 && interetTotal>0) {
		        	 
				         for(List<Object> obj : listCapitaux) {		        	 
				        	 
				        	
				        	 if(interetRepo.interetDuMembreByMatricule((String)obj.get(0))==null) {
				        		 
							           
							           InteretParMembre interetInstance=new InteretParMembre();				        	      
							           Set<InteretParMembre> setInteret =new HashSet<InteretParMembre> ();								
								       setInteret.add(interetInstance);				        	     
								       interetInstance.setDateInteret(new Date());
					        	       currentMember = memberRepo.getUserByMatricule((String)obj.get(0));
					        	       currentMemberTotalContrib=(double)payeRepo.getSommeContribByMaticule((String)obj.get(0));
					        	       
					        	       interetInstance.setMatriculeEntered((String)obj.get(0));		        	      
					        	       capitalInitialParMembre=trt.rounder((double)obj.get(1));
					        	       capitalPlusTotalContributionsDuMembre=(capitalInitialParMembre+currentMemberTotalContrib);					        	       
					        	       totalContributionPlusTotalCapitauxInitiaux=(double)payeRepo.getSommeSubscriptionsAndCapitaux();
					        	       interetParChacun=((interetPartageable*capitalPlusTotalContributionsDuMembre)/(totalContributionPlusTotalCapitauxInitiaux));		        	      
					        	       interetInstance.setInteretDuMembre(trt.rounder(interetParChacun));
					        	       currentMember.setIntereretParMembre(setInteret);				        	      
					        	       interetInstance.setMember(currentMember);				        	      
					        	       interetRepo.save(interetInstance);
					        	       
				        	 }else {
				        		 
				        		 
				        		  double initialInteret=interetRepo.interetDuMembreByMatricule((String)obj.get(0));
				        		  double newInteret=trt.rounder(((interetPartageable*capitalPlusTotalContributionsDuMembre)/(totalContributionPlusTotalCapitauxInitiaux)));
				        		  interetRepo.updateInteretMembre((String)obj.get(0),(initialInteret+newInteret));
				        		 
				        	 } 
				        	 
				        	 Member fondateur=memberRepo.getUserFondateur();
				        	 
				        	 if(fondateur!=null && fondateur.equals(memberRepo.getUserByMatricule((String)obj.get(0)))) {
				        		  
				        		  double initialInteretFondateur=interetRepo.interetDuMembreByMatricule((String)obj.get(0));
				        		  interetRepo.updateInteretMembre((String)obj.get(0),(initialInteretFondateur+partDuFondateur));
				        	  }else {
				        		  
				        		  System.out.println("Personne n'est reconue comme fondateur alors cet utilisateur n est pas le fondateur");
				        	  }
				        	       
				         }
		         
		         }else {  
		        	 
		        	 errorList.add("la somme des interets est negatif ou interet total est egal a 0.0");
			         System.out.println("+++++++++interet negatif ou interet total est egal a 0.0");
		        	 
		        	 
		         }  
		        	 
		         
		         
		      
		         
		
	}
	
	
	
	

	

	public double getInteretDuMembre() {
		return interetDuMembre;
	}

	public void setInteretDuMembre(double interetDuMembre) {
		this.interetDuMembre = interetDuMembre;
	}

	

	public Date getDateInteret() {
		return dateInteret;
	}

	public void setDateInteret(Date dateInteret) {
		this.dateInteret = dateInteret;
	}

	public Member getMember() {
		return membreDansInteret;
	}

	public void setMember(Member member) {
		this.membreDansInteret = member;
	}







	public Long getId_interet() {
		return id_interet;
	}







	public void setId_interet(Long id_interet) {
		this.id_interet = id_interet;
	}







	public Member getMembreDansInteret() {
		return membreDansInteret;
	}







	public void setMembreDansInteret(Member membreDansInteret) {
		this.membreDansInteret = membreDansInteret;
	}







	public String getMatriculeEntered() {
		return matriculeEntered;
	}







	public void setMatriculeEntered(String matriculeEntered) {
		this.matriculeEntered = matriculeEntered;
	}




	

	
	
	
	
	
	
	
	
	
	
	

}
