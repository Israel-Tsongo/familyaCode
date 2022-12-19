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
import org.sid.FamilyaProject.dao.PrevarchiveRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Archive;
import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.Events;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Operation;
import org.sid.FamilyaProject.entities.Payement;
import org.sid.FamilyaProject.entities.Prevarchive;

import org.sid.FamilyaProject.metier.Traitement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
	private PrevarchiveRepository prevarchiveRepo;
	
	@Autowired
	private EventsRepository eventRepo;
	
	//************** ACCEUILLE************************
	
	@GetMapping("/debiteur")
	public String debiteur(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page,@RequestParam(name="pageArchiv",defaultValue = "0") int pagesArchiv, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
		Page <List<List<Object>>> debArchivList =archiveRepo.getArchiveList(PageRequest.of(page,size));			
		Double totalDette=debitRepo.totalEnDette();
        double totalEnDette= totalDette !=null?totalDette : 0.00 ;

	   
	    model.addAttribute("lst",trt.converter(debList));
		model.addAttribute("pages",new int[debList.getTotalPages()]);
		model.addAttribute("pages2",new int[debArchivList.getTotalPages()]);		
		model.addAttribute("currentSize",size);
		model.addAttribute("currentPage",page);
		model.addAttribute("currentPage2",pagesArchiv);
		model.addAttribute("pageTitle","Debiteur");
		model.addAttribute("totalDette",String.format("%.3f", totalEnDette));
		model.addAttribute("keyWord", mc);
		
		return "debiteurs";
	   
	}
	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping("/debSearcher")
	public String searchDebByMatricule(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="archiveType", defaultValue = "") String archiveType, @RequestParam(name="size",defaultValue = "5") int size,  @RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin || mc.isEmpty()) {			   	
				
			   Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
			   
			   Double totalDette=debitRepo.totalEnDette();
		       double totalEnDette= totalDette !=null?totalDette : 0.00 ;
			   		           
	           model.addAttribute("lst", trt.converter(debList));
	           model.addAttribute("pages", new int[debList.getTotalPages()]);
	           model.addAttribute("pages2", new int[0]);
	           model.addAttribute("currentPage2",0);
	           model.addAttribute("currentPage",page);
	           model.addAttribute("currentSize",size);
	           model.addAttribute("totalCapitaux", String.format("%.3f", totalEnDette));  
	           model.addAttribute("keyWord", mc);			   
	           return "debiteurs::mainContainerInDeb";
	           
		   }else {
			   Page <Debiteur> searchdebList =debitRepo.findEnteredMatricContains(mc,PageRequest.of(page,size));			
			   
			   Double totalDette=debitRepo.totalEnDette();
		       double totalEnDette= totalDette !=null?totalDette : 0.00 ;
			       		           
		             model.addAttribute("lst", trt.searchDebConverter(searchdebList));
		             model.addAttribute("pages",new int[searchdebList.getTotalPages()]);		             
		             model.addAttribute("pages2",new int[0]);
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentSize",size);
			         model.addAttribute("currentPage2",0);
		             model.addAttribute("totalCapitaux",String.format("%.3f", totalEnDette));  
		             model.addAttribute("keyWord", mc);
		
		             return "debiteurs::mainContainerInDeb";
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/debPost")
	public String postDebData(Model model, @RequestParam(required = true) String matricule,  @RequestParam(required = true) double montant,			
			                                     @RequestParam() double echeance, @RequestParam(required = true) String typeInteret,
			                                     @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
			                                   ) {
		List<String> errorList = new ArrayList<String>() ; 
		Traitement trt = new Traitement();
		
		 
		try {
			
			if( memberRepo.getMemberByMatricule(matricule)!=null ) {
				
				if(debitRepo.getDebiteurByMatricule(matricule)==null) {
				
				    if(montant<= 9*userRepo.getSalaryByMatricule(matricule)) {
				    	
				      
				      List<Double> debiteurEntry= trt.debiteurCalculMontant(montant,echeance,typeInteret);				    
					  Debiteur deb =new Debiteur(montant, echeance ,1.5, trt.rounder(debiteurEntry.get(0)), new Date(),trt.rounder(debiteurEntry.get(1)),trt.rounder(debiteurEntry.get(0)), typeInteret);
		              Member membDeb = memberRepo.getMemberByMatricule(matricule);
		              
		              Set<Debiteur>setDeb= new HashSet<Debiteur>();	
		              setDeb.add(deb);
		              membDeb.setDebiteurs(setDeb);
		              deb.setMember(membDeb);         
	              
				      debitRepo.save(deb);
					  operaRepo.save(new Operation("Un membre de matricule "+matricule+" s'est Emprunté une somme de "+montant+" $" ,new Date()));	
                      
					  if(eventRepo.matricIsExist(matricule)) {
                    	  
							  List<Events> event = eventRepo.getEventByMatricule(matricule);
	                    	  for(Events ev: event) {
	                    		  
	                    	       eventRepo.deleteById(ev.getId_event());
	                    	  }
                      }else {
                    	  System.out.println("Le numero matricule saisi n'est pas dans la table des remboursement");
                    	  
                      }
					  
					  
					  
				    }else {
				    	
				    	errorList.add("L'emprunt doit etre inferieur a 9 fois le salaire");
				    	System.out.println("L'emprunt doit etre inferieur a 9 fois le salaire");				    	
				    	
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
		Double totalDette=debitRepo.totalEnDette();
        double totalEnDette= totalDette !=null?totalDette : 0.00 ;
		
	    					   
		model.addAttribute("lst", trt.converter(debList));
		model.addAttribute("pages", new int[debList.getTotalPages()]);	
		model.addAttribute("currentPage",page);
		model.addAttribute("currentSize",size);
		model.addAttribute("pages2", new int[0]);
        model.addAttribute("currentPage2",0);
        model.addAttribute("totalCapitaux", String.format("%.3f", totalEnDette));		
		model.addAttribute("errorList",errorList);
		
		return "debiteurs::mainContainerInDeb";
		
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteDeb")
	
	public String deleteDeb(Model model, @RequestParam() Long  idDeb,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		  
		 
		  if (idDeb>0) {
		   
		   Debiteur debit=debitRepo.getDebiteurById(idDeb);
		   debitRepo.deleteById(idDeb); 
		   operaRepo.save(new Operation("Un membre de matricule "+debit.getMember().getMemberUser().getMatricule()+" vient d'etre supprimé de la liste de debiteurs", new Date()));	

		             
		  }else  {
			  System.out.println("error on delete");
		  }
		  Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
		 
		   Double totalDette=debitRepo.totalEnDette();
	       double totalEnDette= totalDette !=null?totalDette : 0.00 ;
		            
		  	   
		   model.addAttribute("lst", trt.converter(debList));
		   model.addAttribute("pages", new int[debList.getTotalPages()]);
		   model.addAttribute("currentPage",page);
		   model.addAttribute("pages2", new int[0]);
           model.addAttribute("currentPage2",0);
		   model.addAttribute("currentSize",size);		   
		   model.addAttribute("totalCapitaux",String.format("%.3f", totalEnDette));   
		
		return  "debiteurs::mainContainerInDeb";
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateDeb")
	public String updateMember( Model model, @RequestParam() Long idDeb,@RequestParam() String matricule,  @RequestParam() double montant,			
            @RequestParam() double echeance,@RequestParam() String typeInteret,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     Traitement trt=new Traitement();
		      
		      if(idDeb>0) {	
		    	           
			      List<Double> debiteurEntry= trt.debiteurCalculMontant(montant,echeance,typeInteret);

				 debitRepo.updateDebiteur(idDeb,montant , echeance ,typeInteret, new Date(),trt.rounder(debiteurEntry.get(0)),trt.rounder(debiteurEntry.get(0)),trt.rounder(debiteurEntry.get(1)));						   
				 operaRepo.save(new Operation("Un membre de matricule "+matricule+" vient d'etre mise à jour sur la liste de debiteurs,nouvelles infos: montant: " +montant+" Echeance: "+echeance+"typeInteret: "+typeInteret+"dettePlusInteret :"+trt.rounder(debiteurEntry.get(0))+"premierRemboursement: "+trt.rounder(debiteurEntry.get(1)), new Date()));	

			   
			  }else  { System.out.println("error on update");}
		    
           Page <List<List<Object>>> debList =debitRepo.getDetteWithMembers(PageRequest.of(page,size));			
           Double totalDette=debitRepo.totalEnDette();
           double totalEnDette= totalDette !=null?totalDette : 0.00 ;
		   							   
		   model.addAttribute("lst", trt.converter(debList));
		   model.addAttribute("pages", new int[debList.getTotalPages()]);
		   model.addAttribute("currentSize",size);
		   model.addAttribute("currentPage",page);
		   model.addAttribute("pages2", new int[0]);
           model.addAttribute("currentPage2",0);
		   model.addAttribute("totalCapitaux", String.format("%.3f", totalEnDette));		     
		 
		 return "debiteurs::mainContainerInDeb";
		
	   
	
	}	
	
	
	@PostMapping(value="debiteur/generatePDF/",produces="application/pdf")
	public ResponseEntity<byte[]> generatePDF(Model model, @RequestParam(name="state") String type, @RequestParam(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFileName="";
		 	   String fileName="";		 	   
		 	   
		 	   
		 	  if(type.equals("Archive")) {
		 		   
		 		 List <Archive> searchArchivList =archiveRepo.findByEnteredMatricContains(!mc.equals("all")? mc:"");							 	 fileName="archiveDette";
		 		     
		 		     fileName="archive";
		 		     jasperFileName="archive.jrxml";
				 	 map.put("nameFor", "Israel");				 	 
				 	 return  trt.generatePDF(searchArchivList, jasperFileName, map, fileName);
		 	   }
		 	   
		 	   
			   List <Debiteur> searchdebList =debitRepo.findEnteredMatricContains(!mc.equals("all")? mc:"");			
			   fileName="debiteur";
			   jasperFileName="debiteur.jrxml";
			   map.put("nameFor", "Israel");		   
			
		       return  trt.generatePDF(searchdebList, jasperFileName, map, fileName);
	   
	}
	
	
	
	@PostMapping(path="/archive")
	public String  archive(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="pagesArchiv",defaultValue = "0") int pagesArchiv, @RequestParam(name="size", defaultValue="5") int size,@RequestParam(name="archiveType", defaultValue = "") String archiveType, @RequestParam(name="keyWord",defaultValue ="") String mc)  {
		
		 Traitement trt = new Traitement();
		 Page <List<List<Object>>> debArchivList=null;
		 Page <Archive> searchArchivList=null;
		 Page <Prevarchive> searchPrevArchivList =null;
		 
		 
		   
		   if(pagin||mc.isEmpty()) {
			   
			   
			   if(archiveType.equals("currentArchive")) {
			   
				   	debArchivList =archiveRepo.getArchiveList(PageRequest.of(pagesArchiv,size));
				   	model.addAttribute("lstArchive", trt.converter(debArchivList));
				   	model.addAttribute("pages2", new int[debArchivList.getTotalPages()]);
			  
			   }else if(archiveType.equals("previewsArchive")) {
				   
				   debArchivList =prevarchiveRepo.previewsArchivList(PageRequest.of(pagesArchiv,size));
				   model.addAttribute("lstArchive", trt.converter(debArchivList));
				   model.addAttribute("pages2", new int[debArchivList.getTotalPages()]);
			   }
			   
			   Double totalDette=debitRepo.totalEnDette();
		       double totalEnDette= totalDette !=null?totalDette : 0.00 ;
			   
	           model.addAttribute("currentPage2",pagesArchiv);
	           model.addAttribute("pages", new int[0]);
	           model.addAttribute("currentPage",page);	           
	           model.addAttribute("currentSize",size);
	           model.addAttribute("totalCapitaux", String.format("%.3f", totalEnDette));  
	           model.addAttribute("keyWord", mc);
			   
	           return "debiteurs::mainContainerInDeb";
	           
		   }else {
			   
			   if(archiveType.equals("currentArchive")) {
				   
				   	 searchArchivList =archiveRepo.findByEnteredMatricContains(mc,PageRequest.of(pagesArchiv,size));
				   	 model.addAttribute("lstArchive", trt.searchArchivConverter(searchArchivList));
		             model.addAttribute("pages2", new int[searchArchivList.getTotalPages()]);
		             
			   }else if (archiveType.equals("previewsArchive")) {
				   
				     searchPrevArchivList =prevarchiveRepo.findByEnteredMatricContains(mc,PageRequest.of(pagesArchiv,size));
				     model.addAttribute("lstArchive", trt.searchPrevArchivConverter(searchPrevArchivList));
		             model.addAttribute("pages2", new int[searchPrevArchivList.getTotalPages()]);
			   }  
			   
					 Double totalDette=debitRepo.totalEnDette();
				     double totalEnDette= totalDette !=null?totalDette : 0.00 ;
		          	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("pages", new int[0]);	
		             model.addAttribute("currentPage2",pagesArchiv);
		             model.addAttribute("currentSize",size);	
		             model.addAttribute("totalCapitaux",String.format("%.3f", totalEnDette));  
		             model.addAttribute("keyWord", mc);
		
		             return  "debiteurs::mainContainerInDeb";
		   
		   }
	}
	
	
	
	
}
