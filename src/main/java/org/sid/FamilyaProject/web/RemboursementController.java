package org.sid.FamilyaProject.web;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sid.FamilyaProject.dao.ArchiveRepository;
import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.DepenseRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.Events;
import org.sid.FamilyaProject.entities.Member;
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
public class RemboursementController {
	
	
	

	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private EventsRepository eventRepo;
	
	@Autowired
	private DebiteurRepository debiteurRepo;
	
	@Autowired
	private InteretParMembreRepository interetRepo;
	
	@Autowired
	private DepenseRepository depenseRepo;
	
	@Autowired
	private ArchiveRepository archivRepo;
	
	
	//************** ACCEUILLE************************
	
	@GetMapping(path="/rembourse")
	public String rembourse(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> eventList =eventRepo.RemboursementDetteTable(PageRequest.of(page,size));	
	 
	    model.addAttribute("lst",trt.converter(eventList));
		model.addAttribute("pages",new int[eventList.getTotalPages()]);
		model.addAttribute("currentSize",size);
		model.addAttribute("currentPage",page);
		model.addAttribute("pageTitle","Remboursement");
		model.addAttribute("keyWord", mc);
		
		return "remboursement";
	   
	}
	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping(path="/rembourseSearcher")
	public ModelAndView searchByName(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {	
			   
				
				
			   Page <List<List<Object>>> eventList =eventRepo.RemboursementDetteTable(PageRequest.of(page,size));
			   
			   ModelAndView mv = new ModelAndView();		           
	           model.addAttribute("lst", trt.converter(eventList));
	           model.addAttribute("pages", new int[eventList.getTotalPages()]);	
	           model.addAttribute("currentPage",page);
	           model.addAttribute("currentSize",size);
	           model.addAttribute("keyWord", mc);
			   mv.setViewName("/remboursement::mainContainerInRembourse");
	           return  mv;
		   }else {
			       
			       Page <Events> searchEventList =eventRepo.findByEnteredMatriculeContains(mc,PageRequest.of(page,size));
			       
			       ModelAndView mv = new ModelAndView("/remboursement::mainContainerInRembourse");		           
		             model.addAttribute("lst", trt.searchRembourseConverter(searchEventList));
		             model.addAttribute("pages", new int[searchEventList.getTotalPages()]);	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentSize",size);	
		             model.addAttribute("keyWord", mc);
		
		             return  mv;
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/remboursePost")
	public String postRembourseData(Model model, @RequestParam() String matricule,  @RequestParam() double remboursement,@RequestParam() String tabName,				                                     
			                                     @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
		                                    ) {
		
		
		String currentDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		List<String> errorList = new ArrayList<String>()	; 
		Traitement trt = new Traitement();
		String mv="";
		double remboursementTempo=0.0;
		String date=eventRepo.matricIsExist(matricule) ? eventRepo.getDateEventByMatricule(matricule).get(eventRepo.getDateEventByMatricule(matricule).size()-1):debiteurRepo.getDebiteurDateByMatricule(matricule) ;
		double prochainRembourcement=eventRepo.matricIsExist(matricule) ? eventRepo.getEventByMatricule(matricule).get(eventRepo.getEventByMatricule(matricule).size()-1).getProchainMontant():debiteurRepo.getDebiteurByMatricule(matricule).getPremierRemboursement() ;

		try {
			
			if(memberRepo.getUserByMatricule(matricule)!=null) {
				
				      if(debiteurRepo.getDebiteurByMatricule(matricule) != null) {
				    	  
									 double currentPenalty=0.0;
						   	   		 Debiteur debit=debiteurRepo.getDebiteurByMatricule(matricule);
						   	   		  
				    	 	         int currentNewDate=Integer.parseInt(currentDate.toString().substring(5,7));				
						             //int currentNewDate=3;
				    	 		     int newDate=Integer.parseInt(date.toString().substring(5,7));
						             //int newDate=12;
				    	 		     
				    	 		     int tempDate=(newDate==12)?(0+1):(newDate+1);				    	 		     
				    	 		     
				    	 		      System.out.println("tabName: "+tabName);
									 if(tabName.equals("Retard") && (currentNewDate >tempDate)) {    
										 
													 double difference=(currentNewDate-tempDate);
													 double penalite=(0.015*prochainRembourcement)*difference;
													 System.out.println("======difference======"+difference );
													 debiteurRepo.updateCurrentPenalite(matricule,trt.rounder(penalite));	
													 
													  currentPenalty=debiteurRepo.getCurrentPenaliteByMatricule(matricule) ;
										   	   		  debit =debiteurRepo.getDebiteurByMatricule(matricule);
								   	   		  
								   	   		 
											 if(remboursement==(debit.getPremierRemboursement()+currentPenalty)) {
												 
													 remboursementTempo=remboursement;
													 remboursement=(remboursement-currentPenalty);									 
													 double formerPenalty= debiteurRepo.getFormerPenaliteByMatricule(matricule);
				  	                            	 double sommePenalty=(formerPenalty+currentPenalty);	  	                            	
				  	    	                         debiteurRepo.updateFormerPenalite(matricule, sommePenalty) ;								        	                            	   
					                            	 debiteurRepo.updateCurrentPenalite(matricule,0.0);
				                            	 
											 }else {
													 System.out.println("=====Vous devez rembourser en considerant les penalites suite au retard acumule soit "+(debit.getPremierRemboursement()+currentPenalty)+" $");
													 errorList.add("Vous devez rembourser en considerant les penalites suite au retard acumule soit "+(debit.getPremierRemboursement()+currentPenalty)+" $");
											 }
											 
									 }else if(tabName.equals("Retard") && (currentNewDate < tempDate)) {
										  
													 errorList.add("Il semblerait que la date prevue pour le remboursement n'est pas encore depassee");
													 System.out.println("Il semblerait que la date prevue pour le remboursement n'est pas encore depassee");
													 
									  }
				           
							
							if(tabName.equals("Anticiper")  || tabName.equals("Normale") || tabName.equals("Retard") && (currentNewDate >tempDate) && (remboursementTempo==(debit.getPremierRemboursement()+currentPenalty)) || debiteurRepo.getDebiteurByMatricule(matricule)==null) { //  currentNewDate <= tempDate  
								
									      
										     Events e=new Events(matricule,  remboursement, new Date());
									         Member curentMember = memberRepo.getUserByMatricule(e.getEntered_matricule());
									         String typeInteret = debiteurRepo.typeInteretByMatricule(e.getEntered_matricule())!=null? debiteurRepo.typeInteretByMatricule(e.getEntered_matricule()):" ";
								       
											if( typeInteret.equals("Degressif")) {							 
												 			
													Set<Events> setterEvent =new HashSet<Events> ();									
													setterEvent.add(e);
													curentMember.setEvents(setterEvent);
													e.setMembre(curentMember);
											        e.computing(tabName,interetRepo,e.getRemboursement_courant(), memberRepo, debiteurRepo, eventRepo ,e,depenseRepo, archivRepo,errorList );				
													  
											 }else{
												 
												    Set<Events> setterEvent =new HashSet<Events> ();								
													setterEvent.add(e);
													curentMember.setEvents(setterEvent);
													e.setMembre(curentMember);
											        e.interetConstant(tabName,interetRepo,e.getRemboursement_courant(),  memberRepo, debiteurRepo, eventRepo ,e,depenseRepo,archivRepo,errorList );				
															  
											 }
						
						 }
						 
				      	
							
							
			    }else {
			    	
			    	errorList.add("Le matricule saisi ne figure pas sur la liste des debiteurs");
			    }
					  
			           
			}else {
				
				
				errorList.add("Le matricule saisi ne correspond a aucun membre");
				
			}
			
		}catch(Exception exc) {			
			mv = "remboursement::mainContainerInRembourse";
			errorList.add("Une erreur s'est produite lors de l'enregistrement d un nouveau remboursement");

			System.out.println("Une erreur s'est produite lors de l'enregistrement d un nouveau remboursement");			
			System.out.println(exc.getMessage()   );
			
		}
		
		   Page<List<List<Object>>> eventList =eventRepo.RemboursementDetteTable(PageRequest.of(page,size));					   
		   
		   mv = "remboursement::mainContainerInRembourse";					   
		   model.addAttribute("lst", trt.converter(eventList));
		   model.addAttribute("pages", new int[eventList.getTotalPages()]);
		   model.addAttribute("currentSize",size);
		   model.addAttribute("currentPage",page);
		   model.addAttribute("errorList",errorList);
		return mv;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteRembourse")
	
	public String deleteRembourse(Model model, @RequestParam() Long  idRemb,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		  
		 
		  if (idRemb>0) {
			  
		   eventRepo.deleteById(idRemb);		   
		          
		  }else  { System.out.println("error lors de la suppression");}
		  Page <List<List<Object>>> eventList =eventRepo.RemboursementDetteTable(PageRequest.of(page,size));

		  		   
	      model.addAttribute("lst", trt.converter(eventList));
	      model.addAttribute("pages", new int[eventList.getTotalPages()]);
	      model.addAttribute("currentSize",size);
	      model.addAttribute("currentPage",page);  
		
		return  "remboursement::mainContainerInRembourse";
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateRembourse")
	public String updateRembourse(Model model, @RequestParam() Long idRemb,@RequestParam() String matricule,  @RequestParam() double remboursement,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     Traitement trt=new Traitement();
		      
		      
		       if(idRemb>0) {
			
				   eventRepo.updateRembourse(idRemb, matricule,remboursement,  new Date());						   
				
			   
			  }else  { System.out.println("Rien a Update");}
		           
	              Page<List<List<Object>>> eventList =eventRepo.RemboursementDetteTable(PageRequest.of(page,size));				 
				 								   
				  model.addAttribute("lst", trt.converter(eventList));
				  model.addAttribute("pages", new int[eventList.getTotalPages()]);
				  model.addAttribute("currentSize",size);
				  model.addAttribute("currentPage",page);
				  
			      return "remboursement::mainContainerInRembourse";
		
	   
	
	}
	
	
	
	
	@PostMapping(value="/rembourse/generatePDF/",produces="application/pdf")
	public ResponseEntity<byte[]> generatePDF(Model model, @RequestParam(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFileName="remboursement.jrxml";
		 	   String fileName="remboursements";
		       List<Events> searchEventList =eventRepo.findByEnteredMatriculeContains(!mc.equals("all")? mc:"");
		 	   map.put("nameFor", "Israel");		   
			   return  trt.generatePDF(searchEventList, jasperFileName, map, fileName);
	   
	}
	
	
	

}
