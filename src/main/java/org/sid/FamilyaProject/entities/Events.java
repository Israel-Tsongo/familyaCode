package org.sid.FamilyaProject.entities;



import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import org.sid.FamilyaProject.dao.ArchiveRepository;
import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.DepenseRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.metier.Traitement;


import lombok.NoArgsConstructor;
import lombok.ToString; 


@Entity
@NoArgsConstructor @ToString

public class Events {
	
	
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long id_event;
	private String enteredMatricule;
	private double echeance_courant;	
	private Date date_event;
	private double dette;
	private double remboursement_courant;
	private double montant_restant;
	private double interet_partiel;
	private double prochainMontant;
	
	
	@ManyToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="foreignKeyForMembers" , nullable=false)
	Member membre;

 
	
	public Events( String entered_matricule,double remboursement_courant ,Date date_event ) {
		
		this.enteredMatricule=entered_matricule;
		this.date_event=date_event;
		this.remboursement_courant=remboursement_courant;
		
		
	}
	

	public void computing (String tabName,InteretParMembreRepository interetRepo, double remboursement, MemberRepository memberRepo, DebiteurRepository debiteurRepo, EventsRepository eventRepo, Events e, DepenseRepository depenseRepo, ArchiveRepository archivRepo,List<String> errorList ) {
		Traitement trt = new Traitement ();
		double montant_restant=0.00;
		double dettePlusInteret=0.00;
		double dette =0.00;
		double taux=0.00;
		double echeance=0.00;		
		double curent_echeance =0.00;
		double interetPartiel=0.00;
		double N=0.00;
		
		List<List<Double>> detteInfo= debiteurRepo.getDetteByMatricule(enteredMatricule);
		             
		for(List<Double> obj: detteInfo) {		
			
			     taux= obj.get(1);	
			     echeance= obj.get(2);
			     
				if( eventRepo.matricIsExist(getEntered_matricule())){
					 
					dette=eventRepo.getDetteByMatricule(getEntered_matricule()).get(eventRepo.getDetteByMatricule(getEntered_matricule()).size()-1);
					montant_restant=eventRepo.getMontantRestantByMatricule(getEntered_matricule()).get(eventRepo.getDetteByMatricule(getEntered_matricule()).size()-1);
					curent_echeance =eventRepo.getEcheanceCourantByMatricule(getEntered_matricule()).get(eventRepo.getEcheanceCourantByMatricule(getEntered_matricule()).size()-1);					
					
				    interetPartiel=((montant_restant*(taux/100)));
				    dettePlusInteret=montant_restant+interetPartiel;				    
				    N=(echeance-curent_echeance);
				    
				    double dettePlusInteretRestant = -1 <= trt.rounder(dettePlusInteret-remboursement) && trt.rounder(dettePlusInteret-remboursement) <= 1 ?  0.00 : trt.rounder(dettePlusInteret-remboursement) ;

				    setDette(trt.rounder(dettePlusInteret));
					setMontant_restant(dettePlusInteretRestant);
					
					
				}else {	
					
					    curent_echeance=0.00;
					    dette=obj.get(0);	
					    N=echeance	;														
						interetPartiel=((dette*taux)/100);
						dettePlusInteret=(dette+interetPartiel);												 
						montant_restant = (dettePlusInteret-remboursement);
						setMontant_restant(trt.rounder(montant_restant));
						setDette(trt.rounder(dettePlusInteret));
						 
						
				}
	
	     	}		
		System.out.println("======Avec N : ========="+N +"++++++++++++");
		System.out.println("=======dettePlusInteret: ========"+dettePlusInteret +"++++++++++++");
		 System.out.println("==============="+echeance +"++++++++++++");
		 System.out.println("==============="+curent_echeance +"++++++++++++");
		 System.out.println("==============="+(dettePlusInteret/N) +"++++++++++++");
		 
		                  if(tabName.equals("Anticiper")) {
		                	  
		                	  
		                  }else {
		                
		                   if(N==0.0) {
		                	   errorList.add("Vous ne possedez pas de dettes");
		                	   System.out.println("Vous ne possedez pas de dettes");
							   

		                   }else {
		                	   
		                	   	if(remboursement >=trt.rounder((dettePlusInteret/N)) ) {
		                	   	
		                	   		if(remboursement <((trt.rounder((dettePlusInteret/N)))+2) ) {   
		                	   
						                	    curent_echeance=curent_echeance+1;
						 					    setEcheance_courant(curent_echeance);
						 					    setInteret_partiel( trt.rounder(interetPartiel));	
						 					    double detteProchaine=getMontant_restant()+((getMontant_restant()*taux)/100);						 					   
						 					    double Nprochain = N-1;
						 					    setProchainMontant(trt.rounder((detteProchaine/Nprochain)));
						 					   
						 					  
						 					     if(montant_restant >=0 && N>0 ) {						 						  
						 						  
						 					    	             Debiteur debit =debiteurRepo.getDebiteurByMatricule(enteredMatricule);								 						        	        
			 						        	                 debiteurRepo.updateDetteCourante( debit.getId_debiteur(), getMontant_restant() ) ;
						 					    	             eventRepo.save(e);
								 						         double reste=eventRepo.getMontantRestantByMatricule(getEntered_matricule()).get(eventRepo.getDetteByMatricule(getEntered_matricule()).size()-1);
								 						         
								 						         if(reste==0) {
								 						        	 
								 						        	        
								 						        	try { 
								 						        	        Debiteur debiteur =debiteurRepo.getDebiteurByMatricule(enteredMatricule);
								 						        	        Member memb = memberRepo.getUserByMatricule(getEnteredMatricule());
								 						        	        double interetGenere=eventRepo.totalBenefitByMatricule(getEnteredMatricule())!=null ? eventRepo.totalBenefitByMatricule(getEnteredMatricule()): 0;
								 						        	        Archive archiv =  new Archive(memb.getNom(),debiteur.getEnteredMatric(),debiteur.getSommeEmprunt(),debiteur.getDuree_echeance(),debiteur.getTaux(),debiteur.getDettePlusInteret(),debiteur.getTypeInteret(),debiteur.getDate_emprunt(),trt.rounder(interetGenere),debiteur.getFormerPenalite() );
								 						        	       								 						        	       
								 						        	        archivRepo.save(archiv);
								 						        	        
								 						        	        debiteurRepo.deleteById(debiteur.getId_debiteur()) ;
								 						        	        errorList.add("Vous n avez plus de dettes");
								 						        	        errorList.add("Merci d'avoir tout rembourse");

								 						    	   			System.out.println( "Vous n avez plus de dettes " );
								 						    	   		    
								 										   
								 											   
								 										   }catch(Exception exc) {
								 											   
								 											  errorList.add("Echec de l'archivage du debiteur");
								 											  System.out.println("Echec de l'archivage du debiteur ");
								 											  
								 											   System.out.println("Error "+exc.getMessage());
								 											   
								 										   }
								 						          }						 						       
				 						       
						 					     	}else {					  
						 					     	  errorList.add("Vous n avez pas de dettes ");
						 						      System.out.println("Vous n avez pas de dettes " );						 						  
						 					    }		                	   
		                	   		}else {
		                	   			
			                	   		errorList.add("En vu de respecter l'echeance convenue lors de la prise de la dette  vous devez rembourser "+ trt.rounder( (dettePlusInteret/N)));

		                	   		}
		    	
		                       }else {  
				                    	 errorList.add("Vous devez Rembourser une somme  superieur ou egale a "+ trt.rounder( (dettePlusInteret/N)));
				                    	 System.out.println("Vous devez Rembourser une somme  superieur ou egale a "+ trt.rounder( (dettePlusInteret/N))) ;		                    	
		                	
		                       }				 
		                   }
				  }		
	}
			
	//////////////////////////////////////////////////////////////////////////////////		
	
	
	public void interetConstant (String tabName,InteretParMembreRepository interetRepo, double remboursement, MemberRepository memberRepo, DebiteurRepository debiteurRepo, EventsRepository eventRepo, Events e, DepenseRepository depenseRepo,ArchiveRepository archivRepo,List<String> errorList ) {
		
		
		Traitement trt = new Traitement ();
		double montant_restant=0.00;
		double dettePlusInteret=0.00;
		double interetGeneral=0.00;
		double dette =0.00;
		double taux=0.00;
		double echeance=0.00;		
		double curent_echeance =0.00;
		double interetPartiel=0.00;
		double N=0.00;
		
	
		List<List<Double>> detteInfo= debiteurRepo.getDetteByMatricule(e.enteredMatricule);
		             
		for(List<Double> obj: detteInfo) {		
			
			    taux= obj.get(1);	
			    echeance= obj.get(2);
				if( eventRepo.matricIsExist(e.getEntered_matricule())){					
					
					dette=eventRepo.getDetteByMatricule(e.getEntered_matricule()).get(eventRepo.getDetteByMatricule(e.getEntered_matricule()).size()-1);
					montant_restant=eventRepo.getMontantRestantByMatricule(e.getEntered_matricule()).get(eventRepo.getDetteByMatricule(e.getEntered_matricule()).size()-1);
					curent_echeance =eventRepo.getEcheanceCourantByMatricule(getEntered_matricule()).get(eventRepo.getEcheanceCourantByMatricule(e.getEntered_matricule()).size()-1);					
					
					interetGeneral=((obj.get(0)*taux)/100)*echeance;
				    interetPartiel=((obj.get(0)*(taux/100)));
				    dettePlusInteret=(obj.get(0)+interetGeneral);
				    			    
				    N=(echeance-curent_echeance);
				    double detteTemp = -1 <= trt.rounder(montant_restant-remboursement) && trt.rounder(montant_restant-remboursement) <= 1 ?  0.00 : trt.rounder(montant_restant-remboursement) ;
				    e.setDette(detteTemp );
					e.setMontant_restant(detteTemp);
					
				
				}else {	
					 
					     curent_echeance=0.00;
					     dette=obj.get(0);	
					     N=echeance	;	
					     interetGeneral=((dette*taux)/100)*echeance;
						 interetPartiel=((dette*taux)/100);
						 dettePlusInteret=(dette+interetGeneral);												 
						 montant_restant = (dettePlusInteret-remboursement);
						 e.setMontant_restant(trt.rounder(montant_restant));
						 e.setDette(trt.rounder(dettePlusInteret));
						
				}
	
	     	}		
							 System.out.println("======Avec N : ========="+N +"++++++++++++");
							 System.out.println("=======dettePlusInteret: ========"+dettePlusInteret +"++++++++++++");
							 System.out.println("==============="+echeance +"++++++++++++");
							 System.out.println("==============="+curent_echeance +"++++++++++++");
							 System.out.println("==============="+(dettePlusInteret/echeance) +"++++++++++++");
		                  
					   if(tabName.equals("Anticiper")) {
						   
						
						   System.out.println("======+Anticiper remboursement+++++++++++");
						   
						   
					   }else {
							 
							 
						   if(echeance==curent_echeance && N==0.0) {
							   
		                	   errorList.add("Vous ne possedez pas de dettes");
		                	   System.out.println(" Vous ne possedez pas de dettes");
							   

		                   }else {
		                	   
		                	   	if(remboursement >=trt.rounder((dettePlusInteret/echeance)) ) {	
		                	   		
		                	   		    double currentPenalty=debiteurRepo.getCurrentPenaliteByMatricule(enteredMatricule) ;
		                	   		    Debiteur debit =debiteurRepo.getDebiteurByMatricule(enteredMatricule);
		                	   		    
		                	   		   System.out.println(" ================currentPenalty====================="+currentPenalty);
		                	   		   System.out.println(" =============debit.getPremierRemboursement()=========="+debit.getPremierRemboursement());
		                	   		   System.out.println(" ===========remboursement==============="+remboursement);
		                	   		   System.out.println(" ==========(debit.getPremierRemboursement()+currentPenalty)============"+(debit.getPremierRemboursement()+currentPenalty));
		                	   		   System.out.println(" ===========(remboursement==debit.getPremierRemboursement()+currentPenalty)============="+(remboursement==debit.getPremierRemboursement()+currentPenalty));
		                	   		   
		                	   		    if((remboursement < ((trt.rounder((dettePlusInteret/echeance)))+2))) {
		                	   		    	 
						                	    curent_echeance=curent_echeance+1;
						 					    e.setEcheance_courant(curent_echeance);
						 					    e.setInteret_partiel( trt.rounder(interetPartiel));							 					    
						 					    e.setProchainMontant((echeance==curent_echeance && N==0.0)?0.0:trt.rounder((dettePlusInteret/echeance)));
						 					  
						 					    if(montant_restant >=0 && N>0 ) {
						 					    	 
						 					    	 			 	     
							        	                               debiteurRepo.updateDetteCourante(debit.getId_debiteur(), getMontant_restant() ) ;							 						  
							 					    	               eventRepo.save(e);
									 						           double reste=eventRepo.getMontantRestantByMatricule(e.getEntered_matricule()).get(eventRepo.getDetteByMatricule(e.getEntered_matricule()).size()-1);
						 					    	                  
									 						           if(reste==0) {
										 						        	 
										 						        	try { 								 						        		   
										 						        		
										 						        		Debiteur debiteur =debiteurRepo.getDebiteurByMatricule(e.enteredMatricule);
									 						        	        Member memb = memberRepo.getUserByMatricule(e.getEnteredMatricule());								 						        		
										 						        		double interetGenere=eventRepo.totalBenefitByMatricule(e.getEnteredMatricule())!=null ? eventRepo.totalBenefitByMatricule(e.getEnteredMatricule()): 0;
										 						        	        Archive archiv =  new Archive(memb.getNom(),debiteur.getEnteredMatric(),debiteur.getSommeEmprunt(),debiteur.getDuree_echeance(),debiteur.getTaux(),debiteur.getDettePlusInteret(),debiteur.getTypeInteret(),debiteur.getDate_emprunt(),trt.rounder(interetGenere),debiteur.getFormerPenalite());
										 						        	        archivRepo.save(archiv);					 						        		
										 						        	        debiteurRepo.deleteById(debiteur.getId_debiteur()) ;
										 						        	        errorList.add("Vous n avez plus de dettes");
										 						        	        errorList.add("Merci d'avoir tout rembourse");
										 						    	   			System.out.println("Vous n avez plus de dettes " );										 						    	   		    
										 										   
										 											   
										 										   }catch(Exception exc) {
										 											   
										 											  errorList.add("error lors de la suppression");
										 											   System.out.println("Error "+exc.getMessage());
										 										   }
										 						               }
									 						           
						 					    	             
							 					     	}else {					  
							 					     	    errorList.add("Vous n avez pas de dettes");
							 						        System.out.println("Vous n avez pas de dettes " );						 						  
							 					       }
						 					     
						 					     
		                	        	} else {
		                	   		
		                	   		         errorList.add("En vu de respecter l'echeance convenue lors de la prise de la dette  vous devez rembourser "+ trt.rounder( (dettePlusInteret/echeance)));
		                	   		
		                	   	}
		    	
		                       }else { 
		                    	   
		                    	   errorList.add("Vous devez Rembourser une somme  egale a "+ trt.rounder( (dettePlusInteret/echeance)));
		                    	   System.out.println("Vous devez Rembourser une somme  superieur ou egale a "+ trt.rounder( (dettePlusInteret/echeance))) ;		                    	
		                	
		                       }				 
		        }
						   
						   
						   
	}				   
						   
						   
		 }		
	
	
	

	
	
	public Long getId_event() {
		return id_event;
	}

	public void setId_event(Long id_event) {
		this.id_event = id_event;
	}

	public String getEntered_matricule() {
		return enteredMatricule;
	}

	public void setEntered_matricule(String entered_matricule) {
		this.enteredMatricule = entered_matricule;
	}

	public double getEcheance_courant() {
		return echeance_courant;
	}

	public void setEcheance_courant(double echeance_courant) {
		this.echeance_courant = echeance_courant;
	}

	public Date getDate_event() {
		return date_event;
	}

	public void setDate_event(Date date_event) {
		this.date_event = date_event;
	}

	public double getDette() {
		return dette;
	}

	public void setDette(double dette) {
		this.dette = dette;
	}

	public double getRemboursement_courant() {
		return remboursement_courant;
	}

	public void setRemboursement_courant(double remboursement_courant) {
		this.remboursement_courant = remboursement_courant;
	}

	public double getMontant_restant() {
		return montant_restant;
	}

	public void setMontant_restant(double montant_restant) {
		this.montant_restant = montant_restant;
	}

	public double getInteret_partiel() {
		return interet_partiel;
	}

	public void setInteret_partiel(double interet_partiel) {
		this.interet_partiel = interet_partiel;
	}

	public Member getMembre() {
		return membre;
	}

	public void setMembre(Member membre) {
		this.membre = membre;
	}





	public String getEnteredMatricule() {
		return enteredMatricule;
	}





	public void setEnteredMatricule(String enteredMatricule) {
		this.enteredMatricule = enteredMatricule;
	}





	public double getProchainMontant() {
		return prochainMontant;
	}





	public void setProchainMontant(double prochainMontant) {
		this.prochainMontant = prochainMontant;
	}



	
	
	
	

}
