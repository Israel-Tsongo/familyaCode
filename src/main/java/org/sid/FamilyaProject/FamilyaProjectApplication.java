package org.sid.FamilyaProject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.DepenseRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.Events;
import  org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Payement;
import org.sid.FamilyaProject.security.UserDetailsServiceImpl;
@SpringBootApplication
public class FamilyaProjectApplication implements CommandLineRunner {
	
	@Autowired
	 private  MemberRepository memberRepo;
	
	@Autowired
	private  DebiteurRepository debiteurRepo;
	
	@Autowired
	private  PayementRepository payRepo;
	
	@Autowired
	private  InteretParMembreRepository interetRepo;
	
	@Autowired
	private  EventsRepository eventRepo;
	
	@Autowired
	private  UserRepository userRepository;
	
	
	
	@Autowired
	private  DepenseRepository depenseRepo;
	//------------------------Total En Caisse--------------------------
	
	public double totalEnCaisse() {
		double total=0.0;
		
		 List<Double> listCotisation= memberRepo.totalEnCaisse();
		  for(Double number : listCotisation) {
			  
			  total+=number;			  
		  }	
		
		return total;		
	}
	
	//------------------------Total En Dette --------------------------
	
		public double totalEnDette() {
			double dette=0.0;
			
			/* List<Double> listDette= debiteurRepo.totalEnDette();
			  for(Double number : listDette) {
				  
				  dette+=number;			  
			  }	*/
			
			return dette;		
		}
	
	/* public void  interetParMembre () {
		 List<List<Object>>listInteret = interetRepo.interetParMembre();
		 
		 
		// System.out.println("###############"+listInteret.get(0)+"########");	
		 for(List<Object> number : listInteret) {
			  
			 System.out.println("###############"+number.get(0)+"########");
			  System.out.println("###############"+number.get(1)+"########");
			  System.out.println("###############"+number.get(2)+"########");
		  }			     
		 
	 } */
	 
	/*public void  historiqueRembourseDette(){
		
		List<List<Object>> historyList =eventRepo.historyRemboursementDette();
		for(List<Object> lst : historyList) {
			  
			 System.out.println("###############"+lst.get(0)+"########");
			  System.out.println("###############"+lst.get(1)+"########");
			  System.out.println("###############"+lst.get(2)+"########");
			  
		  }		
		
	}*/
	 
public void  getSubscritionsWithOwner(){
	/*	
		List<List<Object>> subOwnList =memberRepo.getSubscriptionsWithOwnerMember();
		for(List<Object> lst : subOwnList) {
			  
			  System.out.println("###############"+lst.get(0)+"########");
			  System.out.println("###############"+lst.get(1)+"########");
			  System.out.println("###############"+lst.get(2)+"########");
			  System.out.println("###############"+lst.get(3)+"########");
			  System.out.println("###############"+lst.get(4)+"########");
			  
		  }	*/	
		
	}
	
// Question 2
public void  getDebtAndSubscriptionsWithOwner(){
	
	List<List<Object>> debtSubOwnList =memberRepo.getDebtAndSubscriptionsWithOwner();
	for(List<Object> lst : debtSubOwnList ) {
		  
		  System.out.println("###############"+lst.get(0)+"########");
		  System.out.println("###############"+lst.get(1)+"########");
		  System.out.println("###############"+lst.get(2)+"########");
		  System.out.println("###############"+lst.get(3)+"########");
		  System.out.println("###############"+lst.get(4)+"########");
		  
	  }		
	
}

//Question 2
public void  getOwnersAndCapitals(){
	/*
	//List<List<Object>> memberList =memberRepo.findAll();
	//for(List<Object> lst : memberList ) {
		  
		  System.out.println("###############"+lst.get(0)+"########");
		  System.out.println("###############"+lst.get(1)+"########");
		  System.out.println("###############"+lst.get(2)+"########");
		  System.out.println("###############"+lst.get(3)+"########");
		  System.out.println("###############"+lst.get(4)+"########");
		  */
	//  }		
	
}




   



	public static void main(String[] args) {
		
		SpringApplication.run(FamilyaProjectApplication.class, args);
		
	}
	
	

	@Override
	public void run(String... args) throws Exception {
		
		
		
		
		//Parent
		//Member membre1 = new Member("Israel","12317","Directeur", new Date(),"Contrat_indetermine","Initiateur",5500.0);
		
		//child
		//Debiteur debuteur1 = new Debiteur( "4017" ,300, new Date(), 10.0,2.5 );
		
		//membre1.setDebiteur(debuteur1);
		//debuteur1.setMember(membre1);
		
	 	
		
		
		//memberRepo.save(membre1);
		
		
		/*
		memberRepo.save( new Member("Jimmy","4017","Collaborateur", new Date(), "Contrat_limite","Adherant",4000.0));
		memberRepo.save( new Member( "Samuel","30317","Directeur", new Date(),"Contrat_indetermine","Fondateur",2200.0));
		memberRepo.save( new Member("Victoire","12317","Collaborateurr", new Date(), "Contrat_limite","Adherant",600.0));
		
		memberRepo.save( new Member("Esnower","3317","Directeur", new Date(), "Contrat_indetermine","Fondateur",1000.0));
		Debiteur debiteur2 = new Debiteur( "30317" , 700 , new Date(), 25.0,6.5 );	
		
		Set<Debiteur>setDebiteur= new HashSet<Debiteur>();
		setDebiteur.add(debiteur2);
		//membre2.setDebiteur(debuteur2);
		//debuteur2.setMember(membre2);
		//memberRepo.save(membre2);
		
		Member memb=memberRepo.getUserByMatricule(debiteur2.getEnteredMatric());
	
		System.out.println("==============="+ memb.getNom() + "================");
		
		memb.setDebiteurs(setDebiteur);
		debiteur2.setMember(memb);
		debiteurRepo.save(debiteur2);
		
		
		Payement payer = new Payement("12317",200.0);
		
		Member membPaye = memberRepo.getUserByMatricule(payer.getEnteredMatric());
		System.out.println("==============="+ payer.getEnteredMatric() + "================");
		
		Set<Payement>setPayer= new HashSet<Payement>();		
		setPayer.add(payer);
		
		payer.setMemberPaying(membPaye);
		membPaye.setPayements(setPayer);
		payRepo.save(payer);
		
		*/
		
		//System.out.println("--------Total En Caisse---------"+totalEnCaisse()+"---------------------------");
		//System.out.println("--------Total En Dette---------"+totalEnDette()+"---------------------------");
		// interetParMembre ();
		// historiqueRembourseDette();
		//getSubscritionsWithOwner();
		//getDebtAndSubscriptionsWithOwner();
		//getOwnersAndCapitals();
		
		
		//System.out.println("========"+debiteurRepo.getDetteByMatricule("4017").size()+"=======");
		//System.out.println("========"+debiteurRepo.getDetteByMatricule("4017").get(0).get(1)+"=======");
		//System.out.println("========"+debiteurRepo.getDetteByMatricule("4017").get(3)+"=======");		
		
		/*
		Events e=new Events("8080", new Date(), 5 );		
		
		 Member   curentMember  =memberRepo.getUserByMatricule(e.getEntered_matricule());
		 if(  curentMember !=null ) {
			 
			// && debiteurRepo.matricIsExist(e.getEntered_matricule())=="1"
			 
			 			
				Set<Events> setterEvent =new HashSet<Events> ();
				
				 setterEvent.add(e);
				curentMember.setEvents(setterEvent);
				e.setMembre(curentMember);
			     e.computing(interetRepo,e.getRemboursement_courant(),  memberRepo, debiteurRepo, eventRepo ,e,depenseRepo );				
				
			 
		
	
	}else {
			 
			 System.out.println("Le membre n'existe pas dans la base de donnee ");
			 
		 }
		
		
		
		
		//System.out.println("========"+ b+"=======");
		//System.out.println("========"+ eventRepo.matricIsExist(e.getEntered_matricule())+"=======");
		//System.out.println("========"+ eventRepo.matricIsExist("40170")+"=======");
		
		// System.out.println("========"+ debiteurRepo.getDureEcheanceByMatricule(e.getEntered_matricule())+"=======");
		// System.out.println("========"+ eventRepo.getEcheanceCourantByMatricule(e.getEntered_matricule())+"=======");
		
		
		*/
		
		
		/*
		Events e=new Events("8080", 18.167, new Date() );		
		
		 Member   curentMember  =memberRepo.getUserByMatricule(e.getEntered_matricule());
		 if(  curentMember !=null ) {
			 
			// && debiteurRepo.matricIsExist(e.getEntered_matricule())=="1"
			 
			 			
				Set<Events> setterEvent =new HashSet<Events> ();
				
				 setterEvent.add(e);
				curentMember.setEvents(setterEvent);
				e.setMembre(curentMember);
			    e.interetConstant(interetRepo,e.getRemboursement_courant(),  memberRepo, debiteurRepo, eventRepo ,e,depenseRepo );				
				
			 
		
	
	}else {
			 
			 System.out.println("Le membre n'existe pas dans la base de donnee ");
			 
		 }
		
		*/
		
		//UserDetailsServiceImpl udsl = new UserDetailsServiceImpl(userRepository);
		//udsl.getMatricule();
		
	}
	
	
	
	
		

}
