package org.sid.FamilyaProject.web;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sid.FamilyaProject.dao.DebiteurRepository;

import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Member;

import org.sid.FamilyaProject.metier.Traitement;

import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import net.sf.jasperreports.engine.JRException;

@Controller
public class MemberController {
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private DebiteurRepository debiteurRepo;
	
	@Autowired
	private InteretParMembreRepository interetRepo;
	
	@Autowired
	private PayementRepository payeRepo;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRepository userRepo;
	
	
	//************** ACCEUILLE************************
	
	@GetMapping("/index")
	public String index(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> MemberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));		
		
	   
	   double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
       
	    model.addAttribute("lst",trt.converter(MemberList));
		model.addAttribute("pages",new int[MemberList.getTotalPages()]);
		model.addAttribute("currentPage",page);
		model.addAttribute("currentSize",size);
		model.addAttribute("pageTitle","Membre");
		model.addAttribute("totalCapitaux",totalCapitauxInitiaux);
		model.addAttribute("keyWord", mc);		
		return "index";
	   
	}
	
	

	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping("/indexSearcher")
	public String  searchByMatricule(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
			
			   Page <List<List<Object>>> MemberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
			   double totalCapitauxInitiaux1=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;

			   		           
	           model.addAttribute("lst", trt.converter(MemberList));
	           model.addAttribute("pages", new int[MemberList.getTotalPages()]);	
	           model.addAttribute("currentPage",page);
	           model.addAttribute("currentSize",size);
	           model.addAttribute("totalCapitaux", totalCapitauxInitiaux1);	           
	           model.addAttribute("keyWord", mc);			   
	           return "index::mainContainer";
	           
		   }else {
			       Page <Member> searchMemberList =memberRepo.findByNomContains(mc,PageRequest.of(page,size));
				   double totalCapitauxInitiaux2=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
			       
			       		           
		             model.addAttribute("lst", trt.searchConverter(searchMemberList));
		             model.addAttribute("pages", new int[searchMemberList.getTotalPages()]);	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentSize",size);
		             model.addAttribute("totalCapitaux", totalCapitauxInitiaux2);  
		             model.addAttribute("keyWord", mc);            
		
		             return "index::mainContainer";
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/indexPost")
	public String postIndexData(Model model,  @RequestParam() String matricule, @RequestParam(defaultValue=" ") String mandataire,			
			                                  @RequestParam() double capital,@RequestParam() String fonction,
			                                  @RequestParam() String categorie,@RequestParam() String contrat,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
			                                  ) {
		
		List<String> errorList = new ArrayList<String>();
		Traitement trt = new Traitement();
		String mv="" ;	
		
		 
		try {
			
			if( memberRepo.getUserByMatricule(matricule)==null) {
				
				 if(!matricule.equals("122") && !matricule.equals("222") && !matricule.equals("322") ) {
					 
				     if(userRepository.getUserByMatricule(matricule) !=null  ) {
				    	 
				    	 BigDecimal salaire=userRepository.getUserByMatricule(matricule).getSalaire();
				    	 BigDecimal retenu=userRepository.getUserByMatricule(matricule).getRetenu();
				    			 
				    	 if (capital>=(salaire.doubleValue()*(retenu.doubleValue()/100))) {
				
						  User usr=userRepository.getUserByMatricule(matricule);
						  Member member =new Member(usr.getNom(),matricule,fonction, new Date(),contrat,categorie,capital,mandataire);
					      
					      member.setMemberUser(usr);
					      usr.setMember(member);
						  memberRepo.save(member );	
						  trt.verifyGerantAndFinancier(memberRepo,userRepo,matricule,fonction,errorList);
						  
				    	 }else {
				    		 
				    		 double temp= 3.0*retenu.doubleValue();
				    		 errorList.add("Le capital initial du membre doit etre superieur ou egale a "+Double.toString(temp)+" % "+"En considerant votre retenu de "+Double.toString(retenu.doubleValue()));
							 System.out.println("Le capital initial du membre doit etre superieur ou egale a "+Double.toString(temp)+" %"+"En considerant votre retenu de "+Double.toString(retenu.doubleValue()));
				    		 
				    	 }
				     
				     
					     }else {				    	 
					    	 
							 errorList.add("Aucun utilisateur ne correspond au matricule entre");
							 System.out.println("Aucun utilisateur ne correspond au matricule entre");
					     }
			         
				     
				    }else {
					 
				    	 errorList.add("Ce numero matricule ne pas utilisable");
						 System.out.println("Ce numero matricule ne pas utilisable");
					 
				    }
				 
				 
			}else {
				
				errorList.add("Ce numero matricule appartient a un membre deja present dans la base de donne de membres");
				System.out.println("Ce numero matricule appartient a un membre dans la base");
				
			}
			
		}catch(Exception exc) {	
			
			mv = "index::mainContainer";			
		    errorList.add("Une erreur s'est produite lors de l'enregistrement d un nouveau membre");
			System.out.println("Une erreur s'est produite lors de l'enregistrement d un nouveau membre");			
			System.out.println(exc.getMessage()   );
			
		}
		trt.getGerantAndFinancier(memberRepo,userRepo);
		Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
		double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
		   
		mv = "index::mainContainer";					   
		model.addAttribute("lst", trt.converter(memberList));
		model.addAttribute("pages",new int[memberList.getTotalPages()]);
		model.addAttribute("currentSize",size);
		model.addAttribute("currentPage",page);					   
        model.addAttribute("totalCapitaux",String.format("%.3f", totalCapitauxInitiaux));		
		model.addAttribute("errorList",errorList);
		return mv ;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteMember")
	
	public String deleteMember(Model model, @RequestParam() Long  idMember,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		 
		 
		  if (idMember>0) {
			  
		   memberRepo.deleteById(idMember);	   
		   
		  }else  { System.out.println("Rien a foutre");}
		  
		     Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
		     double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
		            
	         		   
	         model.addAttribute("lst", trt.converter(memberList));
	         model.addAttribute("pages", new int[memberList.getTotalPages()]);
	         model.addAttribute("currentSize",size);
	         model.addAttribute("currentPage",page);
	         model.addAttribute("totalCapitaux", totalCapitauxInitiaux);
	         
		    return  "index::mainContainer";
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/update")
	public String updateMember(Model model, @RequestParam() Long  idMember , @RequestParam(defaultValue="") String mandataire,  @RequestParam() String matricule,			
            @RequestParam() String capital,@RequestParam() String fonction,
            @RequestParam() String categorie,@RequestParam() String contrat, @RequestParam() String date, @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     List<String> errorList = new ArrayList<String>();
		     Traitement trt=new Traitement();
		     ModelAndView mv=null ;
		     
		     if(idMember>0) {
			
					 memberRepo.updateMember(idMember, matricule,mandataire,Double.parseDouble(capital) , fonction, categorie, contrat, new Date());
					 trt.verifyGerantAndFinancier(memberRepo,userRepo,matricule,fonction,errorList);
					
			  }else  { 
				  System.out.println("Error when updating");
			  }
		      trt.getGerantAndFinancier(memberRepo,userRepo);
		      Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
			 double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
					 
			 								   
			 model.addAttribute("lst", trt.converter(memberList));
			 model.addAttribute("pages", new int[memberList.getTotalPages()]);
			 model.addAttribute("currentSize",size);
		     model.addAttribute("currentPage",page);
		     model.addAttribute("totalCapitaux", totalCapitauxInitiaux);
		     model.addAttribute("errorList",errorList);
		 
			return "index::mainContainer";
		
	   
	
	}
	
	@PostMapping("/detailMembre")
	public String detail(Model model, @RequestParam(required=true) Long idMember) {
		
		String matricule= memberRepo.getMatriculeById(idMember);
		
    	List<List<Object>> detailMembre=payeRepo.getDetails(matricule);
    	Double detteCourante = debiteurRepo.detteCouranteByMatricule(matricule)  !=null ? debiteurRepo.detteCouranteByMatricule(matricule): 0.0;
    	Double interet = interetRepo.interetDuMembreByMatricule(matricule)  !=null ? interetRepo.interetDuMembreByMatricule(matricule) : 0.0;
    	
		 
		 
		 if(memberRepo.getUserByMatricule(matricule)!=null) {
				
		    	
		    	if(payeRepo.getPayementByMatricule(matricule)==null || payeRepo.getPayementByMatricule(matricule).isEmpty() ) {
		    		
			    	 List<List<Object>> lst=memberRepo.getCapitalMatriculeNom(matricule);
			    	 model.addAttribute("nom",lst.get(0).get(0));
					 model.addAttribute("matricule",lst.get(0).get(1));
					 model.addAttribute("capital",lst.get(0).get(2));
					 model.addAttribute("pageTitle","Authentification");
					 model.addAttribute("contributions",0.0);
					 model.addAttribute("solde",lst.get(0).get(2));
		    		
		    		
		    	}else {
		    		 model.addAttribute("nom",detailMembre.get(0).get(0));
					 model.addAttribute("matricule",detailMembre.get(0).get(1));
					 model.addAttribute("capital",detailMembre.get(0).get(2));
					 model.addAttribute("contributions",detailMembre.get(0).get(3));
					 model.addAttribute("solde",detailMembre.get(0).get(4));
					 
		    	}
				 						
		    	     model.addAttribute("detteCourante",detteCourante);			    		
				     model.addAttribute("interet",interet);
		
		 
		 }
		 return "index::detailModalContent";
	
	}
	
	@PostMapping(value="/membre/generatePDF/",produces="application/pdf")
	
	public ResponseEntity<byte[]> generatePDF(Model model ,@RequestParam(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
//		 	   
		 	   String jasperFileName="membre.jrxml";	 	   
		 	   
		 	   String fileName="Membre";
		       List <Member> searchMemberList =memberRepo.findByNomContains(!mc.equals("all")? mc:"");

		 	   map.put("membre", "Israel");			   
			   
			   
		       return  trt.generatePDF(searchMemberList, jasperFileName, map, fileName);
	   
	}
	
}
