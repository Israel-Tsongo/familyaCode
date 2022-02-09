package org.sid.FamilyaProject.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sid.FamilyaProject.dao.ArchiveRepository;
import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.OperationRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Archive;
import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.Events;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Operation;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.sf.jasperreports.engine.JRException;

@Controller
public class DebiteurController {

	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private DebiteurRepository  debitRepo;
	
	@Autowired
	private OperationRepository operaRepo;
	
	@Autowired
	private ArchiveRepository archiveRepo;
	
	@Autowired
	private EventsRepository eventRepo;
	
	//************** ACCEUILLE************************
	
	@GetMapping(path="/debiteur")
	public String debiteur(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
		Page <List<List<Object>>> debArchivList =archiveRepo.getArchiveList(PageRequest.of(page,size));			

       double totalEnDette=debitRepo.totalEnDette() !=null?debitRepo.totalEnDette() : 0.00 ;

	   
	    model.addAttribute("lst",trt.converter(debList));
		model.addAttribute("pages",new int[debList.getTotalPages()]);
		model.addAttribute("pagesArchive",new int[debArchivList.getTotalPages()]);
		
		model.addAttribute("currentPage",page);
		model.addAttribute("pageTitle","Debiteur");
		model.addAttribute("totalDette",String.format("%.3f", totalEnDette));
		model.addAttribute("keyWord", mc);
		
		return "debiteur";
	   
	}
	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping(path="/debSearcher")
	public ModelAndView searchDebByMatricule(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin ||mc.isEmpty()) {			   	
				
			   Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
			   double totalEnDette=debitRepo.totalEnDette() !=null?debitRepo.totalEnDette() : 0.00 ;			   
			   ModelAndView mv = new ModelAndView();		           
	           mv.addObject("lst", trt.converter(debList));
	           mv.addObject("pages", new int[debList.getTotalPages()]);	
	           mv.addObject("currentPage",page);
	           mv.addObject("totalCapitaux", String.format("%.3f", totalEnDette));  
	           mv.addObject("keyWord", mc);
			   mv.setViewName("/debiteur::mainContainerInDeb");
	           return  mv;
		   }else {
			   Page <Debiteur> searchdebList =debitRepo.findByEnteredMatricContains(mc,PageRequest.of(page,size));			
			   double totalEnDette=debitRepo.totalEnDette() !=null?debitRepo.totalEnDette() : 0.00 ;			   
			       ModelAndView mv = new ModelAndView("/debiteur::mainContainerInDeb");		           
		             mv.addObject("lst", trt.searchDebConverter(searchdebList));
		             mv.addObject("pages", new int[searchdebList.getTotalPages()]);	
		             mv.addObject("currentPage",page);
		             mv.addObject("currentSize",size);	
		             mv.addObject("totalCapitaux",String.format("%.3f", totalEnDette));  
		             mv.addObject("keyWord", mc);
		
		             return  mv;
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/debPost")
	public ModelAndView postDebData(@RequestParam(required = true) String matricule,  @RequestParam(required = true) double montant,			
			                                     @RequestParam() double echeance, @RequestParam(required = true) String typeInteret,
			                                     @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
			                                   ) {
		List<String> errorList = new ArrayList<String>() ; 
		Traitement trt = new Traitement();
		ModelAndView mv=null ;
		 
		try {
			
			if( memberRepo.getUserByMatricule(matricule)!=null ) {
				
				if(debitRepo.getDebiteurByMatricule(matricule)==null) {
				
				    if(montant<= 6*userRepo.getSalaryByMatricule(matricule)) {
				    	
				      
				    	
				      List<Double> debiteurEntry= trt.debiteurCalculMontant(montant,echeance,typeInteret);				    
					  Debiteur deb =new Debiteur(matricule,montant, echeance ,1.5, trt.rounder(debiteurEntry.get(0)), new Date(),trt.rounder(debiteurEntry.get(1)),trt.rounder(debiteurEntry.get(0)), typeInteret);
		              Member membDeb = memberRepo.getUserByMatricule(deb.getEnteredMatric());
		              
		              Set<Debiteur>setDeb= new HashSet<Debiteur>();	
		              setDeb.add(deb);
		              membDeb.setDebiteurs(setDeb);
		              deb.setMember(membDeb);         
	              
				      debitRepo.save(deb);
					  operaRepo.save(new Operation("Un membre de matricule "+matricule+" s'est Empruntee une somme de "+montant+" $" ,new Date()));	
                      
					  if(eventRepo.matricIsExist(matricule)) {
                    	  
							  List<Events> event = eventRepo.getEventByMatricule(matricule);
	                    	  for(Events ev: event) {
	                    		  
	                    	       eventRepo.deleteById(ev.getId_event());
	                    	  }
                      }else {
                    	  System.out.println("Le numero matricule saisi n'est pas dans la table des remboursement");
                    	  
                      }
					  
					  
					  
				    }else {
				    	errorList.add("L'emprunt doit etre inferieur a 6 fois le salaire");
				    	
				    	System.out.println("L'emprunt doit etre inferieur a 6 fois le salaire");				    	
				    	
				    }
				    
			   }else {
				   
			    	errorList.add("On ne prend pas une seconde dette tant que la premiere n est pas encore remboursee");

				   
			   }
				      
			}else {
				
		    	errorList.add("Ce Matricule ne correspond a aucun membre");
				System.out.println("Ce Matricule ne correspond a aucun membre");
			}
			
		}catch(Exception exc) {	
			
			errorList.add("Une erreur s'est produite lors de l'enregistrement d'un nouveau Debiteur");
			System.out.println("Une erreur s'est produite lors de l'enregistrement d un nouveau Debiteur");			
			System.out.println(exc.getMessage()   );
			
		}
		
		Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
	    double totalEnDette=debitRepo.totalEnDette() !=null?debitRepo.totalEnDette() : 0 ;					   
		
	    mv = new ModelAndView("/debiteur::mainContainerInDeb");					   
		mv.addObject("lst", trt.converter(debList));
		mv.addObject("pages", new int[debList.getTotalPages()]);	
		mv.addObject("currentPage",page);
        mv.addObject("totalCapitaux", String.format("%.3f", totalEnDette));		
		mv.addObject("errorList",errorList);
		return mv;
		
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteDeb")
	
	public ModelAndView deleteDeb(@RequestParam() Long  idDeb,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		  ModelAndView mv=null ;
		 
		  if (idDeb>0) {
			  
		   debitRepo.deleteById(idDeb);		   
		            
		             
		  }else  { System.out.println("error on delete");}
		  Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
		   double totalEnDette=debitRepo.totalEnDette() !=null?debitRepo.totalEnDette() : 0 ;		   
		            
		   mv = new ModelAndView("/debiteur::mainContainerInDeb");		   
		   mv.addObject("lst", trt.converter(debList));
		   mv.addObject("pages", new int[debList.getTotalPages()]);
		   mv.addObject("currentPage",page);
		   mv.addObject("totalCapitaux",String.format("%.3f", totalEnDette));   
		
		return  mv;
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateDeb")
	public ModelAndView updateMember(@RequestParam() Long idDeb,@RequestParam() String matricule,  @RequestParam() double montant,			
            @RequestParam() double echeance,@RequestParam() String typeInteret,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     Traitement trt=new Traitement();
		      ModelAndView mv=null ;
		      if(idDeb>0) {	
		    	           
			      List<Double> debiteurEntry= trt.debiteurCalculMontant(montant,echeance,typeInteret);

				 debitRepo.updateDebiteur(idDeb,matricule,montant , echeance ,typeInteret, new Date(),trt.rounder(debiteurEntry.get(0)),trt.rounder(debiteurEntry.get(0)),trt.rounder(debiteurEntry.get(1)));						   
					    
			   
			  }else  { System.out.println("error on update");}
		    
           Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
		   double totalEnDette=debitRepo.totalEnDette() !=null?debitRepo.totalEnDette() : 0 ;						 
		   mv = new ModelAndView("/debiteur::mainContainerInDeb");								   
		   mv.addObject("lst", trt.converter(debList));
		   mv.addObject("pages", new int[debList.getTotalPages()]);
		   mv.addObject("currentPage",page);
		   mv.addObject("totalCapitaux", String.format("%.3f", totalEnDette));
		     
		 
		 return mv;
		
	   
	
	}
	
	
	
	
	
	@GetMapping("debiteur/generatePDF/{keyWord}")
	public ResponseEntity<byte[]> generatePDF(Model model ,@PathVariable(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFilePath="src/main/resources/Coffee.jrxml";
		 	   String fileName="debiteur";
		 	   
			   List <Debiteur> searchdebList =debitRepo.findByEnteredMatricContains(!mc.equals("all")? mc:"");			
			   map.put("nameFor", "Israel");			   
			   
			   //List<Payement> testList=payeRepo.findAll();

		       return  trt.generatePDF(searchdebList, jasperFilePath, map, fileName);
	   
	}
	
	
	
	@PostMapping(path="/archive")
	public ModelAndView archive(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin||mc.isEmpty()) {			   	
				
			   Page <List<List<Object>>> debArchivList =archiveRepo.getArchiveList(PageRequest.of(page,size));			
			   double totalEnDette=debitRepo.totalEnDette() !=null?debitRepo.totalEnDette() : 0.00 ;			   
			   ModelAndView mv = new ModelAndView();		           
	           mv.addObject("lstArchive", trt.converter(debArchivList));
	           mv.addObject("pagesArchive", new int[debArchivList.getTotalPages()]);	
	           mv.addObject("currentPage",page);
	           mv.addObject("totalCapitaux", String.format("%.3f", totalEnDette));  
	           mv.addObject("keyWord", mc);
			   mv.setViewName("/debiteur::mainContainerInDeb");
	           return  mv;
		   }else {
			   Page <Archive> searchArchivList =archiveRepo.findByEnteredMatricContains(mc,PageRequest.of(page,size));			
			   double totalEnDette=debitRepo.totalEnDette() !=null?debitRepo.totalEnDette() : 0.00 ;			   
			       ModelAndView mv = new ModelAndView("/debiteur::mainContainerInDeb");		           
		             mv.addObject("lstArchive", trt.searchArchivConverter(searchArchivList));
		             mv.addObject("pagesArchive", new int[searchArchivList.getTotalPages()]);	
		             mv.addObject("currentPage",page);
		             mv.addObject("currentSize",size);	
		             mv.addObject("totalCapitaux",String.format("%.3f", totalEnDette));  
		             mv.addObject("keyWord", mc);
		
		             return  mv;
		   
		   }
	}
	
	
	
	
}
