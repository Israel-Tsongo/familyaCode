package org.sid.FamilyaProject.web;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import org.sid.FamilyaProject.dao.ArchiveRepository;
import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.DepenseRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.PrevarchiveRepository;

import org.sid.FamilyaProject.entities.InteretParMembre;
import org.sid.FamilyaProject.metier.ArchiveDataBase;
import org.sid.FamilyaProject.metier.Traitement;
import org.sid.FamilyaProject.security.UserDetailsServiceImpl;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller

public class DashbordController {
	
	@Autowired
	private PayementRepository payeRepo;
	
	@Autowired
	private MemberRepository memberRepo;
	@Autowired
	private DebiteurRepository debiteurRepo;
	@Autowired
	private InteretParMembreRepository interetRepo;
	@Autowired
	private DepenseRepository depenseRepo;
	
	@Autowired
	private ArchiveRepository archivRepo;
	
	@Autowired
	private PrevarchiveRepository prevarchiveRepo;
	
	
	@Autowired
	private EventsRepository eventRepo;
	
	 @Autowired
		private UserDetailsServiceImpl userDetailsService ; 
	
	//************** ACCEUILLE************************
	
		@GetMapping("/dashboard")
		public String dashbord(Model model) {		 
			
		   double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null? (double)memberRepo.getTotalCapitauxInitiaux() : 0.00;
		   double totalDette=debiteurRepo.totalEnDette() !=null? (double)debiteurRepo.totalEnDette() : 0;			   
		   double totalDepense=depenseRepo.getTotalOutgo() !=null? (double)depenseRepo.getTotalOutgo() : 0;
		   double interetGeneral=archivRepo.totalBenefitInArchive() !=null? archivRepo.totalBenefitInArchive() : 0;			   
		   
		   double interetNet=(interetGeneral-totalDepense);			   
		   double totalContribution=payeRepo.getSommeSubscriptions() !=null? payeRepo.getSommeSubscriptions() : 0;			   
		   double sommePenalite=archivRepo.totalPenalite()!=null ? archivRepo.totalPenalite():0;
		   
		  double soldeTotal= (totalCapitauxInitiaux+totalContribution+sommePenalite);	  
		 
					  
		   model.addAttribute("totalEnCaisse",String.format("%.3f",soldeTotal));
		   model.addAttribute("capitatInitial",String.format("%.3f", totalCapitauxInitiaux));
		   model.addAttribute("totalContribution",String.format("%.3f", totalContribution));
		   model.addAttribute("sommePenalite",String.format("%.3f", sommePenalite));
		   model.addAttribute("totalDepense",String.format("%.3f", totalDepense));
		   model.addAttribute("totalInteret",String.format("%.3f", interetGeneral));
		   model.addAttribute("interetNet",String.format("%.3f", interetNet));
		   model.addAttribute("totalDette",String.format("%.3f", totalDette));
		   model.addAttribute("pageTitle","Dashboard");
			
			return "dashbord";
		   
		}
		
		
		@PostMapping("/setPost")
		public String setterBtn(Model model,Authentication authentication,ModelMap  modelMap ,@RequestParam(defaultValue="false") boolean btnEnd , @RequestParam(defaultValue="") String password ) {	
			     
			      List<String> errorList = new ArrayList<String>();			      
			      Traitement trt = new Traitement();
			      
			      InteretParMembre interetParMem=new  InteretParMembre();
			      BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
			      User usr=null;
			      String email="";
			      String userPassword="";
			      Set<Role> currentRoles=null;
			      String role="";
			      
			      
				 if(authentication !=null) {
					 
						email= authentication.getName();
						usr= userDetailsService.getUserByEmail(email);
						userPassword=usr.getPassword();
						currentRoles=usr.getRoles();
						
						for(Role rol : currentRoles) {
							
							if  (rol.getRole_name().equals("SUPER_USER")) {
								
							    role="SUPER_USER";					    
							}
						}
						
				 }else {
					 
						 errorList.add("Vous n etes pas admin");
						 System.out.println(" Vous n ete pas admin");
					 
				   }
				 
			       System.out.println("==role:====>"+role);
			       System.out.println("==Btn:====>"+btnEnd);
			       System.out.println("==role:====>"+enc.matches(password, userPassword));
			       
				   if(role.equals("SUPER_USER") && enc.matches(password, userPassword)) {
						
					   if(btnEnd) {										
							  
				   			   interetParMem.partageInteret(payeRepo,interetRepo, eventRepo, depenseRepo, memberRepo,archivRepo, errorList);
				   			   trt.archiveDataBase(payeRepo,memberRepo,prevarchiveRepo,archivRepo,errorList);							
						}
				      
				    }else {
				    	
				    	errorList.add("Mot  de passe incorect");
				    	System.out.println("Mauvais mot de passe ou alors vous n avez pas les droits administarteurs");
				    	
				    }
			
				   double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null? (double)memberRepo.getTotalCapitauxInitiaux() : 0;				   
				   double totalDette=debiteurRepo.totalEnDette() !=null? (double)debiteurRepo.totalEnDette() : 0;			   
				   double totalDepense=depenseRepo.getTotalOutgo() !=null? (double)depenseRepo.getTotalOutgo() : 0;
				   double interetGeneral=archivRepo.totalBenefitInArchive() !=null? archivRepo.totalBenefitInArchive() : 0;	   
				   double sommePenalite=archivRepo.totalPenalite()!=null ? archivRepo.totalPenalite():0;
				   double interetNet=(interetGeneral-totalDepense);			   
				   double totalContribution=payeRepo.getSommeSubscriptions() !=null? payeRepo.getSommeSubscriptions() : 0;
				   double soldeTotal= (totalCapitauxInitiaux +totalContribution+sommePenalite);
				   
				    model.addAttribute("pageTitle","Authentification");
				    model.addAttribute("totalEnCaisse",String.format("%.3f",soldeTotal));
				    model.addAttribute("capitatInitial",String.format("%.3f", totalCapitauxInitiaux));
				    model.addAttribute("totalContribution",String.format("%.3f", totalContribution));
				    model.addAttribute("totalDepense",String.format("%.3f", totalDepense));
				    model.addAttribute("totalInteret",String.format("%.3f", interetGeneral));
				    model.addAttribute("sommePenalite",String.format("%.3f", sommePenalite));
				    model.addAttribute("interetNet",String.format("%.3f", interetNet));
				    model.addAttribute("totalDette",String.format("%.3f", totalDette));					    
				    model.addAttribute("errorList",errorList);
				    return "dashbord::mainDash";
			
		}	
		
		
		//Using generated security password: ac2db985-46af-421e-947e-00da6e5d0f9b
		
		

}
