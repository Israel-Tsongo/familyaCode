package org.sid.FamilyaProject.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
	           mv.addObject("lst", trt.converter(eventList));
	           mv.addObject("pages", new int[eventList.getTotalPages()]);	
	           mv.addObject("currentPage",page);
	            
	           mv.addObject("keyWord", mc);
			   mv.setViewName("/remboursement::mainContainerInRembourse");
	           return  mv;
		   }else {
			       Page <Events> searchEventList =eventRepo.findByEnteredMatriculeContains(mc,PageRequest.of(page,size));
			       
			       ModelAndView mv = new ModelAndView("/remboursement::mainContainerInRembourse");		           
		             mv.addObject("lst", trt.searchRembourseConverter(searchEventList));
		             mv.addObject("pages", new int[searchEventList.getTotalPages()]);	
		             mv.addObject("currentPage",page);
		             mv.addObject("currentSize",size);	
		             mv.addObject("keyWord", mc);
		
		             return  mv;
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/remboursePost")
	public ModelAndView postRembourseData(@RequestParam() String matricule,  @RequestParam() double remboursement,				                                     
			                                     @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
		                                    ) {
		List<String> errorList = new ArrayList<String>()	; 
		Traitement trt = new Traitement();
		ModelAndView mv=null ;
		 
		try {
			
			if( memberRepo.getUserByMatricule(matricule)!=null && debiteurRepo.getDebiteurByMatricule(matricule)!=null ) {
				
				
				       String date=debiteurRepo.getDebiteurDateByMatricule(matricule);
			           
			           int v=Integer.parseInt(date.toString().substring(5, 7));
			           System.out.println(v);
			           
				       String currentDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
				       
				       System.out.println("====================="+Integer.parseInt(currentDate.toString().substring(5,7)));				       
				      
				       
				       Events e=new Events(matricule,  remboursement, new Date(),0.0 );
				       Member curentMember  =memberRepo.getUserByMatricule(e.getEntered_matricule());
				       String typeInteret = debiteurRepo.typeInteretByMatricule(e.getEntered_matricule())!=null? debiteurRepo.typeInteretByMatricule(e.getEntered_matricule()):" ";
				       
						if( typeInteret.equals("Degressif")) {							 
							 			
							Set<Events> setterEvent =new HashSet<Events> ();									
							setterEvent.add(e);
							curentMember.setEvents(setterEvent);
							e.setMembre(curentMember);
					        e.computing(interetRepo,e.getRemboursement_courant(),  memberRepo, debiteurRepo, eventRepo ,e,depenseRepo, archivRepo,errorList );				
								  
						 }else{
							 Set<Events> setterEvent =new HashSet<Events> ();
								
								setterEvent.add(e);
								curentMember.setEvents(setterEvent);
								e.setMembre(curentMember);
						        e.interetConstant(interetRepo,e.getRemboursement_courant(),  memberRepo, debiteurRepo, eventRepo ,e,depenseRepo,archivRepo,errorList );				
										  
						 }
					  
			           
			}else {
				
				errorList.add("Le matricule saisi ne correspond a aucun membre");
				
			}
			
		}catch(Exception exc) {			
			mv = new ModelAndView("/remboursement::mainContainerInRembourse");
			errorList.add("Une erreur s'est produite lors de l'enregistrement d un nouveau remboursement");

			System.out.println("Une erreur s'est produite lors de l'enregistrement d un nouveau remboursement");			
			System.out.println(exc.getMessage()   );
			
		}
		
		   Page<List<List<Object>>> eventList =eventRepo.RemboursementDetteTable(PageRequest.of(page,size));					   
		   
		   mv = new ModelAndView("/remboursement::mainContainerInRembourse");					   
		   mv.addObject("lst", trt.converter(eventList));
		   mv.addObject("pages", new int[eventList.getTotalPages()]);	
		   mv.addObject("currentPage",page);
		   mv.addObject("errorList",errorList);
		return mv;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteRembourse")
	
	public ModelAndView deleteRembourse(@RequestParam() Long  idRemb,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		  ModelAndView mv=null ;
		 
		  if (idRemb>0) {
			  
		   eventRepo.deleteById(idRemb);		   
		          
		  }else  { System.out.println("error lors de la suppression");}
		  Page <List<List<Object>>> eventList =eventRepo.RemboursementDetteTable(PageRequest.of(page,size));

		  mv = new ModelAndView("/remboursement::mainContainerInRembourse");		   
	      mv.addObject("lst", trt.converter(eventList));
	      mv.addObject("pages", new int[eventList.getTotalPages()]);
	      mv.addObject("currentPage",page);  
		
		return  mv;
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateRembourse")
	public ModelAndView updateRembourse(@RequestParam() Long idRemb,@RequestParam() String matricule,  @RequestParam() double remboursement,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     Traitement trt=new Traitement();
		      ModelAndView mv=null ;
		      
		       if(idRemb>0) {
			
					     eventRepo.updateRembourse(idRemb, matricule,remboursement,  new Date());						   
				
			   
			  }else  { System.out.println("Rien a Update");}
		           
	             Page<List<List<Object>>> eventList =eventRepo.RemboursementDetteTable(PageRequest.of(page,size));				 
				  mv = new ModelAndView("/remboursement::mainContainerInRembourse");								   
				  mv.addObject("lst", trt.converter(eventList));
				  mv.addObject("pages", new int[eventList.getTotalPages()]);
				  mv.addObject("currentPage",page);
		 
			      return mv;
		
	   
	
	}
	
	
	
	
	@GetMapping("rembourse/generatePDF/{keyWord}")
	public ResponseEntity<byte[]> generatePDF(Model model ,@PathVariable(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFilePath="src/main/resources/Coffee.jrxml";
		 	   String fileName="remboursements";
		       List<Events> searchEventList =eventRepo.findByEnteredMatriculeContains(!mc.equals("all")? mc:"");

		 	   map.put("nameFor", "Israel");			   
			   
			   //List<Payement> testList=payeRepo.findAll();

		       return  trt.generatePDF(searchEventList, jasperFilePath, map, fileName);
	   
	}
	
	
	

}
