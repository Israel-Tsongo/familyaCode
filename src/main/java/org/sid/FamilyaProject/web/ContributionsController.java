package org.sid.FamilyaProject.web;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sid.FamilyaProject.dao.ArchiveRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.OperationRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Operation;
import org.sid.FamilyaProject.entities.Payement;
import org.sid.FamilyaProject.metier.Traitement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import net.sf.jasperreports.engine.JRException;


@Controller
public class ContributionsController {
	
	

	@Autowired
	private PayementRepository payeRepo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ArchiveRepository archivRepo;
	
	@Autowired
	private OperationRepository operaRepo;
	
	//************** ACCEUILLE************************
	
	@GetMapping("/contribution")
	public String Contribution(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin, @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc, @RequestParam(name="dateKeyWord", defaultValue = "") String dateKeyWord) {
		
		
		Traitement trt = new Traitement();		
		
		Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));		
		Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(pageAllInfo,size));
	    double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
	   
	    
	    model.addAttribute("lstSolde",trt.converterCalculInteretAlaSortie(capitalContribList,archivRepo));
	    model.addAttribute("lst",trt.converter(contribList));
		model.addAttribute("pages",new int[contribList.getTotalPages()]);
		model.addAttribute("pages2",new int[capitalContribList.getTotalPages()]);
		model.addAttribute("pageTitle","Contribution");
		model.addAttribute("currentPage",page);
		model.addAttribute("currentPage2",pageAllInfo);
		model.addAttribute("currentSize",size);
		model.addAttribute("totalContribution",String.format("%.3f",totalContribution));
		model.addAttribute("keyWord", mc);	
		model.addAttribute("dateKeyWord", dateKeyWord);
		
		return "contribution";
	   
	}
	
	
@GetMapping("/tableViewContrib")
	
	public String tableViewContrib(Model model ,@RequestParam(name="page",defaultValue = "0") int page,  @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size, @RequestParam(name="dateKeyWord", defaultValue = "") String dateKeyWord )  {	
		 
		Traitement trt=new Traitement();
		 		  
		   	   
		 Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));		   
		 double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null? memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
    
					   
			model.addAttribute("lst", trt.converter(memberList));
		    model.addAttribute("pages", new int[memberList.getTotalPages()]);
		    model.addAttribute("currentPage",page);
		    model.addAttribute("currentPage2",pageAllInfo);
		    model.addAttribute("currentSize",size);
		    model.addAttribute("totalCapitaux", totalCapitauxInitiaux); 
		    model.addAttribute("pageTitle","Contribution");
		             
		 
		  
		
		return  "/index::mainContainer";
	}
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping("/contribSearcher")
	public String searchByMatriculeInContrib(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc, @RequestParam(name="dateKeyWord", defaultValue = "") String dateKeyWord)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
				
			  Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));
			  Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(pageAllInfo,size));
			  
			  double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
			   	
			   
			   model.addAttribute("lstSolde",trt.converterCalculInteretAlaSortie(capitalContribList,archivRepo));
	           model.addAttribute("lst", trt.converter(contribList));
	           model.addAttribute("pages", new int[contribList.getTotalPages()]);	
	           model.addAttribute("pages2",new int[capitalContribList.getTotalPages()]);
	           model.addAttribute("pageTitle","Contribution");
	           model.addAttribute("currentPage",page);
	           model.addAttribute("currentPage2",pageAllInfo);
	           model.addAttribute("currentSize",size);
	           model.addAttribute("totalContribution",String.format("%.3f", totalContribution));  
	           model.addAttribute("keyWord", mc);
	           model.addAttribute("dateKeyWord", dateKeyWord);
			   
			  
	           return "contribution::mainContainerContrib";
		   }else {
			         Page <Payement> searchContribList = null;
			         Page <List<List<Object>>>  searchByMatricContribList = null ;
			         
			         if(!mc.isEmpty() && dateKeyWord.isEmpty())			        	 
					      searchContribList =payeRepo.findByenteredMatricContains(mc,PageRequest.of(page,size));

			         
			         else if(!dateKeyWord.isEmpty() && mc.isEmpty()  ) 
			        	 searchContribList =payeRepo.findByDatePayementOnlyContains(dateKeyWord, PageRequest.of(page,size));
			         
			         else if(!dateKeyWord.isEmpty() && !mc.isEmpty()) {
			        	  searchContribList =payeRepo.findByDatePayementContains(mc,dateKeyWord, PageRequest.of(page,size));
			         }
			         
				     searchByMatricContribList =payeRepo.getByMaticuleSubscriptionsAndCapitalWithOwnerMember(mc,PageRequest.of(pageAllInfo,size));
					 double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
					 
			         model.addAttribute("lstSolde",trt.converterCalculInteretAlaSortie(searchByMatricContribList,archivRepo));
		             model.addAttribute("lst", trt.searchConverterPaye(searchContribList));
		             model.addAttribute("pages", new int[searchContribList.getTotalPages()]);	
		             model.addAttribute("pages2", new int[searchByMatricContribList.getTotalPages()]);	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentPage2",pageAllInfo);
		             model.addAttribute("currentSize",size);	
		             model.addAttribute("pageTitle","Contribution");
		             model.addAttribute("totalContribution", String.format("%.3f",totalContribution));  
		             model.addAttribute("keyWord", mc);
		             model.addAttribute("dateKeyWord", dateKeyWord);
		             
		             return "/contribution::mainContainerContrib";
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@RequestMapping(value="/contribPost",method=RequestMethod.POST)
	public String postIndexData(Model model,@RequestParam() String matricule,  @RequestParam() double contribution,			
			                                     @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size		                                     
			                                     ) {
		List<String> errorList =new ArrayList<String>();
		Traitement trt = new Traitement();		
		 
		try {
			
			if( memberRepo.getUserByMatricule(matricule)!=null ) {
				
		              Payement pay =new Payement(matricule,trt.rounder(contribution), new Date());
		              Member membPay = memberRepo.getUserByMatricule(pay.getEnteredMatric());
		              Set<Payement>setPay= new HashSet<Payement>();	
		              setPay.add(pay);
		              membPay.setPayements(setPay);
		              pay.setMemberPaying(membPay);	     
					  payeRepo.save(pay);
					  operaRepo.save(new Operation("Un membre de matricule "+matricule+" a contribuer 'une somme de "+contribution+" $", new Date()));	

					  
			}else {
				
				errorList.add("Le matricule entre ne correspond a aucun membre");			
				
			}
			
		}catch(Exception exc) {			
		     
			errorList.add("Une erreur  est survenu lors de l'enregistrement d'une contribution");
			errorList.add("Veiller recharger la page et reessayer avec le bon matricule") ;
			
			System.out.println("Une erreur s'est produite lors de l'enregistrement dune contribution");			
			System.out.println(exc.getMessage());
			
			//model.addAttribute("errorList",errorList);
			
		}
		
		Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));
		Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(pageAllInfo,size));
	    double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
		
	    model.addAttribute("lst",trt.converter(contribList));
	    model.addAttribute("lstSolde", trt.converterCalculInteretAlaSortie(capitalContribList,archivRepo));		
		model.addAttribute("pages2", new int[capitalContribList.getTotalPages()]);
		model.addAttribute("pages", new int[contribList.getTotalPages()]);
		model.addAttribute("pageTitle","Contribution");
		model.addAttribute("currentPage",page);
		model.addAttribute("currentPage2",pageAllInfo);
		model.addAttribute("currentSize",size);
		model.addAttribute("totalContribution",String.format("%.3f",totalContribution));		
		model.addAttribute("errorList",errorList);
		return "contribution::mainContainerContrib";
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteContrib")
	
	public String deleteMember(Model model ,@RequestParam() Long  idContrib,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size )  {	
		
		  List<String> errorList = new ArrayList<String>();
		  Traitement trt=new Traitement();
		  String mv="" ;
		 
		  if (idContrib>0) {
			  
		   payeRepo.deleteById(idContrib);		   
		   Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));
		   Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(pageAllInfo,size));

	       double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
		   
		            
		             mv="contribution::mainContainerContrib";
		             model.addAttribute("lstSolde",trt.converterCalculInteretAlaSortie(capitalContribList,archivRepo));
		             model.addAttribute("lst", trt.converter(contribList ));
		             model.addAttribute("pages", new int[contribList .getTotalPages()]);
		             model.addAttribute("pages2", new int[capitalContribList .getTotalPages()]);
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentPage2",pageAllInfo);
		             model.addAttribute("currentSize",size);
		             model.addAttribute("pageTitle","Contribution");
		             model.addAttribute("totalContribution", String.format("%.3f",totalContribution));          
		             
		  }else  {
			  
				errorList.add("Une erreur  est survenu lors de la suppression d'une contribution , selectionner le sujet puis supprimer");				
				mv="contribution::mainContainerContrib";				
				
			  }
		  
	            model.addAttribute("errorList",errorList);
		        return  mv;
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateContrib")
	public String updateMember(Model model ,@RequestParam() Long  idContrib , @RequestParam() String matricule,  @RequestParam() double contribution,@RequestParam(name="page",defaultValue = "0") int page,  @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		    
		     Traitement trt=new Traitement();
		      String mv="" ;
		           if(idContrib>0) {
			
					     payeRepo.updateContribution(idContrib, matricule, trt.rounder(contribution), new Date());						   
					     Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));
						 Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(pageAllInfo,size));

					     
					      double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
						 
						  mv="contribution::mainContainerContrib";								   
						  model.addAttribute("lst", trt.converter(contribList));
						  model.addAttribute("lstSolde",trt.converterCalculInteretAlaSortie(capitalContribList,archivRepo));
						  model.addAttribute("pages", new int[contribList.getTotalPages()]);
						  model.addAttribute("pages2", new int[capitalContribList.getTotalPages()]);
						  model.addAttribute("currentPage",page);
						  model.addAttribute("currentPage2",pageAllInfo);
						  model.addAttribute("currentSize",size);
						  model.addAttribute("pageTitle","Contribution");
						  model.addAttribute("totalContribution", String.format("%.3f",totalContribution));
			     
			   
			  }else  { 
				 
				  mv = "contribution::mainContainerContrib";			  
			  }		
		 
			return mv;
		
	   
	
	}
	
	
	@PostMapping(value="/contrib/generatePDF/",produces="application/pdf")
	public ResponseEntity<byte[]> generatePDF(Model model ,@RequestParam(name="keyWord") String mc,@RequestParam(name="currentTable") String currentTable,@RequestParam(name="dateKeyWord", defaultValue ="") String dateKeyWord) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   List <Payement> searchContribList=null;
		 	   String dateValue=dateKeyWord.equals("all")?"":dateKeyWord;
		 	   String matricule=mc.equals("all")?"":mc;
		 	   String jasperFileName="";
		 	   String fileName="";
		 	 
		 	   if(currentTable.equals("soldes")) {
		 		   
					 List<Object>  searchByMatricContribList =payeRepo.getByMaticuleSubscriptionsAndCapitalWithOwnerMembers(!mc.equals("all")? mc:"");

				     fileName="operations";
				     jasperFileName="contribution.jrxml";
				 	 map.put("nameFor", "Israel");				 	 
				 	 return  trt.generatePDF(searchByMatricContribList, jasperFileName, map, fileName);
		 	   }
		 	  
		 	   
		 	  
		 	 if(!matricule.isEmpty() && dateValue.isEmpty())			        	 
			      searchContribList =payeRepo.findByenteredMatricContains(mc);

	         
	         else if(!dateValue.isEmpty() && matricule.isEmpty()  ) 
	        	 searchContribList =payeRepo.findByDatePayementOnlyContains(dateKeyWord);
	         
	         else if(!dateValue.isEmpty() && !matricule.isEmpty()) {
	        	  searchContribList =payeRepo.findByDatePayementContains(mc,dateKeyWord);
	         }
	         else if(dateValue.isEmpty() && matricule.isEmpty()) {
	        	  searchContribList =payeRepo.findByenteredMatricContains("");
	         }
		 	    
			   jasperFileName="contribution.jrxml";
		 	   fileName="contributions";
		 	   map.put("nameFor", "Israel");			 	 
			   return  trt.generatePDF(searchContribList, jasperFileName, map, fileName);
	   
	}
	
	@PostMapping(value="/proofContrib/",produces="application/pdf")
	public ResponseEntity<byte[]> generateContribProof(@RequestParam(name="matricule") String matricule,@RequestParam(name="montant") double montant) throws JRException, Exception{
		  
		
		  Traitement trt=new Traitement();
		  HashMap<String,Object> map = new HashMap<>();
	 	  String jasperFileName="";
	 	  String fileName="";
		  
	 	     fileName="contribution";
	 	     jasperFileName="proof.jrxml";
		 	 map.put("typeOperation","COTISATION");
		 	 map.put("info", "la contribution");
		 	 map.put("nom",userRepo.getUserNameByMatricule(matricule));
		 	 map.put("matricule",matricule);
		 	 map.put("montant",montant);
	 	    
		 return	trt.generateProof(jasperFileName, map, fileName);
	}
	
	

}
