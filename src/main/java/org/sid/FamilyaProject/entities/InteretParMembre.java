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
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.metier.Traitement;
import org.sid.FamilyaProject.users.User;

import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor @ToString

public class InteretParMembre {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long id_interet;	
	
	private double interetDuMembre;	
	private Date dateInteret;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="foreignKeyInteretParMembre")
	private Member membreDansInteret;
	
	

	
	
	
	public void partageInteret(PayementRepository payeRepo, InteretParMembreRepository interetRepo, EventsRepository eventRepo,DepenseRepository depenseRepo, MemberRepository memberRepo,UserRepository userRepo,ArchiveRepository archivRepo,List<String> errorList ) {
		     
		     double interetTotalDansArchive;		     
		     double interetTotal  = 0.00;
		     double totalDepense =0.00;
		     double interetNet=0.00;
		     double interetPartageable=0.00;
		     double getTotalCapitauxInitiaux=0.00;
		     double totalContributions=0.00;
		     //double capitalInitialParMembre=0.00;
		     double interetParChacun=0.00;
		     double sommePenalite=0.00;
		     double currentMemberTotalContrib=0.00;
		     double capitalPlusTotalContributionsDuMembre=0.00;
		     double totalContributionPlusTotalCapitauxInitiaux=0.00;
		     Member currentMember=null;
		     double partDuFondateur=0.00;
		     Traitement trt= new Traitement();
		     List<List<Object>> listCapitaux;
		         Double getSommeSubscriptions =payeRepo.getSommeSubscriptions();
		         totalContributions= getSommeSubscriptions!=null? getSommeSubscriptions : 0;
		         
		         Double getTotalCapitauxInitiauxFromDatabase = memberRepo.getTotalCapitauxInitiaux();
  		         getTotalCapitauxInitiaux= getTotalCapitauxInitiauxFromDatabase!=null? (double)getTotalCapitauxInitiauxFromDatabase : 0.00;

		         Double totalBenefitInArchive=archivRepo.totalBenefitInArchive();
  		         interetTotalDansArchive= totalBenefitInArchive!=null ? totalBenefitInArchive:0;
  		         
  		         Double totalPenalite=archivRepo.totalPenalite();
		         sommePenalite=totalPenalite!=null ? totalPenalite:0;

		         interetTotal=(interetTotalDansArchive+sommePenalite);
		         
		         Double getTotalOutgo =depenseRepo.getTotalOutgo();
		         totalDepense= getTotalOutgo!=null? getTotalOutgo : 0;
		         
		         listCapitaux=memberRepo.getCapitalInitialParMembre();		         
                 interetNet=(interetTotal-totalDepense);
                 partDuFondateur=trt.rounder(interetNet*0.2);
                 interetPartageable=(interetNet-partDuFondateur);
                 
//		         System.out.println("++++++++++++ interetPartageable :"+interetPartageable);
//		         System.out.println("++++++++++++ partFondateur :"+partDuFondateur);
//		         System.out.println("++++++++++++ interetNet:"+interetNet);
//		         System.out.println("++++++++++++ interetTotal:"+interetTotal);
//		         System.out.println("++++++++++++ totalDepense :"+totalDepense);
		         
		         if(interetPartageable>0 && interetTotal>0) {
		        	 
				         for(List<Object> obj : listCapitaux) {	
				        	 
				        	 currentMember = memberRepo.getMemberByMatricule((String)obj.get(0));			        	       
			        	     Double getSommeContribByMaticule =payeRepo.getSommeContribByMaticule((String)obj.get(0));
			        	     currentMemberTotalContrib=getSommeContribByMaticule !=null?getSommeContribByMaticule:0;
			        	     
			        	     capitalPlusTotalContributionsDuMembre=(trt.rounder(currentMember.getCapital_Initial())+currentMemberTotalContrib);
			        	     totalContributionPlusTotalCapitauxInitiaux=(getTotalCapitauxInitiaux+totalContributions);				        	 
				        	 	        	 
				        	 memberRepo.updateCapitalInitialById(currentMember.getId_member(), capitalPlusTotalContributionsDuMembre);
				        	 
				        	 InteretParMembre interet= interetRepo.getInteretInstanceByMatricule((String)obj.get(0));
				        	 if(interetRepo.interetDuMembreByMatricule((String)obj.get(0))==null) {				        		 
							           
							           InteretParMembre interetInstance=new InteretParMembre();				        	      
							           Set<InteretParMembre> setInteret =new HashSet<InteretParMembre> ();								
								       setInteret.add(interetInstance);				        	     
								       interetInstance.setDateInteret(new Date());							       				        	       
					        	       
//					        	       System.out.println("==1====>"+interetPartageable);
//					        	       System.out.println("=====2=>"+capitalPlusTotalContributionsDuMembre);
//					        	       System.out.println("==3====>"+totalContributionPlusTotalCapitauxInitiaux);
					        	       
					        	       interetParChacun=trt.rounder(((interetPartageable*capitalPlusTotalContributionsDuMembre)/(totalContributionPlusTotalCapitauxInitiaux)));		        	      
					        	       interetInstance.setInteretDuMembre(trt.rounder(interetParChacun));
					        	       currentMember.setIntereretParMembre(setInteret);				        	      
					        	       interetInstance.setMember(currentMember);				        	      
					        	       interetRepo.save(interetInstance);
					        	       
				        	 }else {
				        		 
				        		 
				        		  double initialInteret=interetRepo.interetDuMembreByMatricule((String)obj.get(0));
				        		  double newInteret=trt.rounder(((interetPartageable*capitalPlusTotalContributionsDuMembre)/(totalContributionPlusTotalCapitauxInitiaux)));
				        		  interetRepo.updateInteretMembre(interet.getId_interet(),(initialInteret+newInteret));
				        		 
				        	 } 
				        	 
				        	 User fondateur=userRepo.getUserFondateur();
//				        	 System.out.println("++++++++++++ fondateur==fondateur:"+fondateur);
//				        	 System.out.println("++++++++++++ matricule:"+(String)obj.get(0));
//				        	 System.out.println("++++++++++++ fondateur==fondateur:"+fondateur.equals(userRepo.getUserByMatricule((String)obj.get(0))));
//				        	 System.out.println("++++++++++++ fondateur==fondateur:"+(fondateur==userRepo.getUserByMatricule((String)obj.get(0))));
				        	 
				        	 if(fondateur!=null && fondateur.equals(userRepo.getUserByMatricule((String)obj.get(0)))) {
				        		  
				        		  double initialInteretFondateur=interetRepo.interetDuMembreByMatricule((String)obj.get(0));
//						        	 System.out.println("++++++++++++ initialInteretFondateur"+initialInteretFondateur);
//						        	 System.out.println("++++++++++++ partDuFondateur"+partDuFondateur);
//						        	 System.out.println("++++++++++++ interet.getId_interet()"+interet.getId_interet());
				        		  interetRepo.updateInteretMembre(interet.getId_interet(),(initialInteretFondateur+partDuFondateur));
				        		  
				        	  }else {
				        		  
				        		  System.out.println("La personne n est pas fondateur ");
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


	

}
