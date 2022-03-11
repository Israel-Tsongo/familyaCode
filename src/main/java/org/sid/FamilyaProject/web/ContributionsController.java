package org.sid.FamilyaProject.web;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
	private OperationRepository operaRepo;
	
	//************** ACCEUILLE************************
	
	@GetMapping(path="/contribution")
	public String Contribution(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin, @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));		
		Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(page,size));
	   double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
	   
	  
	    model.addAttribute("lstSolde",trt.converter(capitalContribList));

	    model.addAttribute("lst",trt.converter(contribList));
		model.addAttribute("pages",new int[contribList.getTotalPages()]);
		model.addAttribute("pages2",new int[capitalContribList.getTotalPages()]);
		model.addAttribute("pageTitle","Contribution");
		model.addAttribute("currentPage",page);
		model.addAttribute("currentPage2",pageAllInfo);
		model.addAttribute("currentSize",size);
		model.addAttribute("totalContribution",String.format("%.3f",totalContribution));
		model.addAttribute("keyWord", mc);		
		
		return "contribution";
	   
	}
	
	
@GetMapping("/tableViewContrib")
	
	public ModelAndView tableViewContrib(@RequestParam(name="page",defaultValue = "0") int page,  @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		 ModelAndView mv=null ;			  
		   	   
		 Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));		   
		 double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null? memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
    
			mv = new ModelAndView("/index::mainContainer");		   
			mv.addObject("lst", trt.converter(memberList));
		    mv.addObject("pages", new int[memberList.getTotalPages()]);
		    mv.addObject("currentPage",page);
		    mv.addObject("currentPage2",pageAllInfo);
		    mv.addObject("currentSize",size);
		    mv.addObject("totalCapitaux", totalCapitauxInitiaux); 
		    mv.addObject("pageTitle","Contribution");
		             
		 
		  
		
		return  mv;
	}
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping(path="/contribSearcher")
	public ModelAndView searchByMatriculeInContrib(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
				
			  Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));
			  Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(page,size));
			  
			  double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
			   ModelAndView mv = new ModelAndView();	
			   
			   mv.addObject("lstSolde",trt.converter(capitalContribList));
	           mv.addObject("lst", trt.converter(contribList));
	           mv.addObject("pages", new int[contribList.getTotalPages()]);	
	           mv.addObject("pages2",new int[capitalContribList.getTotalPages()]);
	           mv.addObject("pageTitle","Contribution");
	           mv.addObject("currentPage",page);
	           mv.addObject("currentPage2",pageAllInfo);
	           mv.addObject("currentSize",size);
	           mv.addObject("totalContribution",String.format("%.3f", totalContribution));  
	           mv.addObject("keyWord", mc);
			   mv.setViewName("/contribution::mainContainerContrib");
			  
	           return  mv;
		   }else {
			   Page <Payement> searchContribList =payeRepo.findByenteredMatricContains(mc,PageRequest.of(page,size));
			   Page <List<List<Object>>>  searchByMatricContribList =payeRepo.getByMaticuleSubscriptionsAndCapitalWithOwnerMember(mc,PageRequest.of(page,size));

			       double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
			       
			       ModelAndView mv = new ModelAndView("/contribution::mainContainerContrib");	
			      
			       mv.addObject("lstSolde",trt.converter(searchByMatricContribList));
		             mv.addObject("lst", trt.searchConverterPaye(searchContribList));
		             mv.addObject("pages", new int[searchContribList.getTotalPages()]);	
		             mv.addObject("pages2", new int[searchByMatricContribList.getTotalPages()]);	
		             mv.addObject("currentPage",page);
		             mv.addObject("currentPage2",pageAllInfo);
		             mv.addObject("currentSize",size);	
		             mv.addObject("pageTitle","Contribution");
		             mv.addObject("totalContribution", String.format("%.3f",totalContribution));  
		             mv.addObject("keyWord", mc);
		
		             return  mv;
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@RequestMapping(value="/contribPost",method=RequestMethod.POST)
	public ModelAndView postIndexData(@RequestParam() String matricule,  @RequestParam() double contribution,			
			                                     @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size		                                     
			                                     ) {
		List<String> errorList =new ArrayList<String>();
		Traitement trt = new Traitement();
		ModelAndView mv=null ;
		 
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
			
			//mv.addObject("errorList",errorList);
			
		}
		
		Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));
		Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(page,size));
	    double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
		   
		mv = new ModelAndView("/contribution::mainContainerContrib");					   
		mv.addObject("lst", trt.converter(contribList));
		mv.addObject("lstSolde",trt.converter(capitalContribList));
		mv.addObject("pages2", new int[capitalContribList.getTotalPages()]);
		mv.addObject("pages", new int[contribList.getTotalPages()]);
		mv.addObject("pageTitle","Contribution");
		mv.addObject("currentPage",page);
		mv.addObject("currentPage2",pageAllInfo);
		mv.addObject("currentSize",size);
        mv.addObject("totalContribution",String.format("%.3f",totalContribution));		
		mv.addObject("errorList",errorList);
		return mv;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteContrib")
	
	public ModelAndView deleteMember(@RequestParam() Long  idContrib,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size )  {	
		
		 List<String> errorList = new ArrayList<String>();
		 Traitement trt=new Traitement();
		  ModelAndView mv=null ;
		 
		  if (idContrib>0) {
			  
		   payeRepo.deleteById(idContrib);		   
		   Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));
		   Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(page,size));

	       double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
		   
		            
		             mv = new ModelAndView("/contribution::mainContainerContrib");
		             mv.addObject("lstSolde", trt.converter(capitalContribList ));
		             mv.addObject("lst", trt.converter(contribList ));
		             mv.addObject("pages", new int[contribList .getTotalPages()]);
		             mv.addObject("pages2", new int[capitalContribList .getTotalPages()]);
		             mv.addObject("currentPage",page);
		             mv.addObject("currentPage2",pageAllInfo);
		             mv.addObject("currentSize",size);
		             mv.addObject("pageTitle","Contribution");
		             mv.addObject("totalContribution", String.format("%.3f",totalContribution));          
		             
		  }else  {
			  
				errorList.add("Une erreur  est survenu lors de la suppression d'une contribution , selectionner le sujet puis supprimer");
				
				mv = new ModelAndView("/contribution::mainContainerContrib");
				//mv.setViewName();
				
			  }
		  
	    mv.addObject("errorList",errorList);
		return  mv;
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateContrib")
	public ModelAndView updateMember(@RequestParam() Long  idContrib , @RequestParam() String matricule,  @RequestParam() double contribution,@RequestParam(name="page",defaultValue = "0") int page,  @RequestParam(name="pageAllInfo",defaultValue = "0") int pageAllInfo, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		    
		     Traitement trt=new Traitement();
		      ModelAndView mv=null ;
		           if(idContrib>0) {
			
					     payeRepo.updateContribution(idContrib, matricule, trt.rounder(contribution), new Date());						   
					     Page <List<List<Object>>> contribList =payeRepo.getSubscriptionsWithMembers(PageRequest.of(page,size));
						 Page <List<List<Object>>> capitalContribList =payeRepo.getSubscriptionsAndCapitalWithOwnerMember(PageRequest.of(page,size));

					     
					      double totalContribution=payeRepo.getSommeSubscriptions() !=null?payeRepo.getSommeSubscriptions() : 0.00 ;
						 
						  mv = new ModelAndView("/contribution::mainContainerContrib");								   
						  mv.addObject("lst", trt.converter(contribList));
						  mv.addObject("lstSolde",trt.converter(capitalContribList));

						  mv.addObject("pages", new int[contribList.getTotalPages()]);
						  mv.addObject("pages2", new int[capitalContribList.getTotalPages()]);
						  mv.addObject("currentPage",page);
						  mv.addObject("currentPage2",pageAllInfo);
						  mv.addObject("currentSize",size);
						  mv.addObject("pageTitle","Contribution");
						  mv.addObject("totalContribution", String.format("%.3f",totalContribution));
			     
			   
			  }else  { 
				  
				  
				  mv = new ModelAndView("/contribution::mainContainerContrib");
				  
				
			  }
		
		 
			return mv;
		
	   
	
	}
	
	
	@GetMapping("/contrib/generatePDF/{currentTable}/{keyWord}")
	public ResponseEntity<byte[]> generatePDF(Model model ,@PathVariable(name="keyWord") String mc,@PathVariable(name="currentTable") String currentTable) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFilePath="";
		 	   String fileName="";
		 	 
		 	   if(currentTable.equals("soldes")) {
		 		   
					 List<Object>  searchByMatricContribList =payeRepo.getByMaticuleSubscriptionsAndCapitalWithOwnerMembers(!mc.equals("all")? mc:"");

				     fileName="operations";
				 	 jasperFilePath="src/main/resources/contribution.jrxml";
				 	 map.put("nameFor", "Israel");				 	 
				 	 return  trt.generatePDF(searchByMatricContribList, jasperFilePath, map, fileName);
		 	   }
		 	    
			   List <Payement> searchContribList = payeRepo.findByenteredMatricContains(!mc.equals("all")? mc:"");
			   jasperFilePath="src/main/resources/contribution.jrxml";
		 	   fileName="contributions";
		 	   map.put("nameFor", "Israel");			 	 
			   return  trt.generatePDF(searchContribList, jasperFilePath, map, fileName);
	   
	}
	
	@GetMapping("/proofContrib/{matricule}/{montant}")
	public ResponseEntity<byte[]> generateContribProof(@PathVariable(name="matricule") String matricule,@PathVariable(name="montant") double montant) throws JRException, Exception{
		  
		
		 Traitement trt=new Traitement();
		  HashMap<String,Object> map = new HashMap<>();
	 	  String jasperFilePath="";
	 	  String fileName="";
		  
	 	     fileName="contribution";
		 	 jasperFilePath="src/main/resources/proof.jrxml";
		 	 map.put("typeOperation","COTISATION");
		 	 map.put("info", "la contribution");
		 	 map.put("nom",userRepo.getUserNameByMatricule(matricule));
		 	 map.put("matricule",matricule);
		 	 map.put("montant",montant);
	 	    
		 return	trt.generateProof(jasperFilePath, map, fileName);
	}
	
	

}
