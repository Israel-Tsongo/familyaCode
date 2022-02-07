package org.sid.FamilyaProject.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sid.FamilyaProject.dao.InteretParMembreRepository;

import org.sid.FamilyaProject.dao.OperationRepository;
import org.sid.FamilyaProject.entities.InteretParMembre;

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
public class InteretParMembreController {
	
	
	
	@Autowired
	private InteretParMembreRepository interetRepo;
	
	@Autowired
	private OperationRepository operaRepo;
	
	
	
	
	//************** ACCEUILLE************************
	
	@GetMapping(path="/interet")
	public String interet(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page,@RequestParam(name="pageOpera",defaultValue = "0") int pageOpera, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> interetList =interetRepo.interetParMembre(PageRequest.of(page,size));	
		Page <List<List<Object>>> operaList =operaRepo.getAllOperation(PageRequest.of(pageOpera,size));		
		
		double totalInteretNet=interetRepo.totalInteretNet() !=null? interetRepo.totalInteretNet():0;			   
	    
	    	
	    model.addAttribute("pageTitle","Interet");
	    model.addAttribute("lst",trt.converter(interetList));	    
		model.addAttribute("pages",new int[interetList.getTotalPages()]);		
		model.addAttribute("lstOpera",trt.converter(operaList));
		model.addAttribute("pagesOpera",new int[operaList.getTotalPages()]);		
		model.addAttribute("currentPage",page);	
		model.addAttribute("currentPageOpera",pageOpera);
		model.addAttribute("totalInteretNet",String.format("%.3f" ,totalInteretNet));
		model.addAttribute("keyWord", mc);
		
		
	    return "interet";
	}
	
	
	
	//************** RECHERCHER PAR NOM ************************
	
	@PostMapping(path="/interetSearcher")
	public ModelAndView searchInteretByName(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page,@RequestParam(name="pageOpera",defaultValue = "0") int pageOpera, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   System.out.println("interet searcher=== 0 >>");
		   if(pagin) {			   	
			   System.out.println("interet searcher=== 1 >>");
			   Page <List<List<Object>>> interetList =interetRepo.interetParMembre(PageRequest.of(page,size));
			   Page <List<List<Object>>> operaList =operaRepo.getAllOperation(PageRequest.of(pageOpera,size));	
			   double totalInteretNet=interetRepo.totalInteretNet() !=null? interetRepo.totalInteretNet() : 0;			   
			   ModelAndView mv = new ModelAndView();	
			   
			   mv.addObject("lst",trt.converter(interetList));	    
			   mv.addObject("pages",new int[interetList.getTotalPages()]);
				
	           mv.addObject("lstOpera",trt.converter(operaList));
	           mv.addObject("pagesOpera", new int[operaList.getTotalPages()]);	
	           mv.addObject("currentPage",page);
	           mv.addObject("currentPageOpera",pageOpera);
	           mv.addObject("totalInteretNet", String.format("%.3f" ,totalInteretNet));  
	           mv.addObject("keyWord", mc);
			   mv.setViewName("/interet::mainContainerInInteret");
	           return  mv;
		   }else {
			     System.out.println("=>"+size);
			       Page <InteretParMembre> searchInteretList =interetRepo.findByMatriculeEnteredContains(mc,PageRequest.of(page,size));
			       Page <Operation> searchOperaList =operaRepo.findByOperationContains(mc,PageRequest.of(pageOpera,size));

				   double totalInteretNet=interetRepo.totalInteretNet() !=null? interetRepo.totalInteretNet() : 0;			   
			       
			       ModelAndView mv = new ModelAndView("/interet::mainContainerInInteret");		           
		             mv.addObject("lst", trt.searchInteretConverter(searchInteretList));
		             mv.addObject("pages", new int[searchInteretList.getTotalPages()]);
		             mv.addObject("lstOpera", trt.searchOperaConverter(searchOperaList));
			         mv.addObject("pagesOpera", new int[searchOperaList.getTotalPages()]);
			         mv.addObject("currentPageOpera",pageOpera);
			         mv.addObject("currentSize",size);			         
		             mv.addObject("currentPage",page);
		             mv.addObject("totalInteretNet",String.format("%.3f" ,totalInteretNet));  
		             mv.addObject("keyWord", mc);
		
		             return  mv;
		   
		   }
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/withDraw")
	public ModelAndView withDrawInteret(@RequestParam() Long  idWithD , @RequestParam() String matricule,  @RequestParam() double montantRetrait,			
            @RequestParam(name="page",defaultValue = "0") int page,@RequestParam(name="page",defaultValue = "0") int pageOpera, @RequestParam(name="size",defaultValue = "5") int size, @RequestParam(name="keyWord", defaultValue = "") String mc   )  {	
		     List<String> errorList=new ArrayList<String>();
		     
		     Traitement trt=new Traitement();
		      ModelAndView mv=null ;
		      
		           if(idWithD>0) {
		        	    
					         if( montantRetrait <= interetRepo.getInteretDuMembreParMatricule(matricule)) {
					        	 
							        	 double reste=(interetRepo.getInteretDuMembreParMatricule(matricule)-montantRetrait);
										  operaRepo.save(new Operation("Un membre de matricule "+matricule+" a effectue un retrait d''une somme de "+montantRetrait+" $", new Date()));	
										  
							        	   interetRepo.updateInteret(idWithD, trt.rounder(reste), new Date());				        	   
							        	  
							        	   InteretParMembre itm=interetRepo.getUserByMatricule(matricule);
							        	   if(interetRepo.getInteretDuMembreParMatricule(matricule)!=null){
							        		   
								        		   if(interetRepo.getInteretDuMembreParMatricule(matricule)==0){ 
								        			   
									        			   interetRepo.deleteById(itm.getId_interet());
									        			   errorList.add("- Vous venez de tout retirer , desormais votre interet est de 0 $");
								        		   
								        		       }        		   
							        		   
							        	       }
					        		   
					            }else {
					        	  
							        	     errorList.add("- Vous ne pouvez pas retirer un montant superieur a l'interet disponible");
								        	 System.out.println("Vous ne pouvez pas retirer un montant superieur a votre interet disponible");
					        	 
					             }
					     
				
			            }else  {
				  
       	                    errorList.add("- Veiller selectionner le membre qui veut effectuer un retrait ");
	        	 				  
			              }
		          
		           
	           Page <List<List<Object>>> interetList =interetRepo.interetParMembre(PageRequest.of(page,size));
			   Page <List<List<Object>>> operaList =operaRepo.getAllOperation(PageRequest.of(pageOpera,size));	
			   double totalInteretNet=interetRepo.totalInteretNet()!=null? interetRepo.totalInteretNet() : 0;	
			   
			   mv = new ModelAndView("/interet::mainContainerInInteret");	
			   
			   mv.addObject("lst",trt.converter(interetList));	    
			   mv.addObject("pages",new int[interetList.getTotalPages()]);								
	           mv.addObject("lstOpera",trt.converter(operaList));
	           mv.addObject("pagesOpera", new int[operaList.getTotalPages()]);	
	           mv.addObject("currentPage",page);
	           mv.addObject("currentPageOpera",pageOpera);
	           mv.addObject("totalInteretNet", String.format("%.3f" ,totalInteretNet));  
	           mv.addObject("keyWord", mc);
		       mv.addObject("errorList",errorList);
		       
			return mv;
		
	   
	
	}
	
	
	@GetMapping("/interet/generatePDF/{type}/{keyWord}")
	public ResponseEntity<byte[]> generatePDF(Model model ,@PathVariable(name="keyWord") String mc,@PathVariable(name="type") String type) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFilePath="";
		 	   String fileName="";
		 	   
		 	   
		 	    if(type.equals("operations")) {
		 		   
				     List<Operation> searchOperaList =operaRepo.findByOperationContains(!mc.equals("all")? mc:"");
				 	 fileName="operations";
				 	 jasperFilePath="src/main/resources/Coffee.jrxml";
				 	 map.put("nameFor", "Israel");				 	 
				 	 return  trt.generatePDF(searchOperaList, jasperFilePath, map, fileName);
		 	   }
		 	    
		 	   List<InteretParMembre> searchInteretList =interetRepo.findByMatriculeEnteredContains(!mc.equals("all")? mc:"");
			   fileName="interets";
			   jasperFilePath="src/main/resources/Coffee.jrxml";
			   map.put("nameFor", "Israel");			 	 
			   return  trt.generatePDF(searchInteretList, jasperFilePath, map, fileName);
	}
	
	
	
	

}
