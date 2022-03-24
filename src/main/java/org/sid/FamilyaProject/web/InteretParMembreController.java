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
		model.addAttribute("pages2",new int[operaList.getTotalPages()]);		
		model.addAttribute("currentPage",page);	
		model.addAttribute("currentSize",size);
		model.addAttribute("currentPage2",pageOpera);
		model.addAttribute("totalInteretNet",String.format("%.3f" ,totalInteretNet));
		model.addAttribute("keyWord", mc);
		return "interet";
	}
	
	
	
	//************** RECHERCHER PAR NOM ************************
	
	@PostMapping(path="/interetSearcher")
	public String searchInteretByName(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page,@RequestParam(name="pageOpera",defaultValue = "0") int pageOpera, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
			  
			   Page <List<List<Object>>> interetList =interetRepo.interetParMembre(PageRequest.of(page,size));
			   Page <List<List<Object>>> operaList =operaRepo.getAllOperation(PageRequest.of(pageOpera,size));	
			   double totalInteretNet=interetRepo.totalInteretNet() !=null? interetRepo.totalInteretNet() : 0;			   
			   
			   model.addAttribute("lst",trt.converter(interetList));	    
			   model.addAttribute("pages",new int[interetList.getTotalPages()]);
				
	           model.addAttribute("lstOpera",trt.converter(operaList));
	           model.addAttribute("pages2", new int[operaList.getTotalPages()]);	
	           model.addAttribute("currentPage",page);
	           model.addAttribute("currentSize",size);
	           model.addAttribute("currentPage2",pageOpera);
	           model.addAttribute("totalInteretNet", String.format("%.3f" ,totalInteretNet));  
	           model.addAttribute("keyWord", mc);
			   
	           return  "interet::containerMainInInteret";
		   }else {
			     
			       Page <InteretParMembre> searchInteretList =interetRepo.findByMatriculeEnteredContains(mc,PageRequest.of(page,size));
			       Page <Operation> searchOperaList =operaRepo.findByOperationContains(mc,PageRequest.of(pageOpera,size));

				   double totalInteretNet=interetRepo.totalInteretNet() !=null? interetRepo.totalInteretNet() : 0;			   
			       
			       		           
		             model.addAttribute("lst", trt.searchInteretConverter(searchInteretList));
		             model.addAttribute("pages", new int[searchInteretList.getTotalPages()]);
		             model.addAttribute("lstOpera", trt.searchOperaConverter(searchOperaList));
			         model.addAttribute("pages2", new int[searchOperaList.getTotalPages()]);
			         model.addAttribute("currentPage2",pageOpera);
			         model.addAttribute("currentSize",size);			         
		             model.addAttribute("currentPage",page);
		             model.addAttribute("totalInteretNet",String.format("%.3f" ,totalInteretNet));  
		             model.addAttribute("keyWord", mc);
		
		             return  "interet::containerMainInInteret";
		   
		   }
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/withDraw")
	public String withDrawInteret(Model model, @RequestParam() Long  idWithD , @RequestParam() String matricule,@RequestParam() String nom,  @RequestParam() double montantRetrait,			
            @RequestParam(name="page",defaultValue = "0") int page,@RequestParam(name="page",defaultValue = "0") int pageOpera, @RequestParam(name="size",defaultValue = "5") int size, @RequestParam(name="keyWord", defaultValue = "") String mc   ) throws JRException, Exception  {	
		     List<String> errorList=new ArrayList<String>();
		     
		      Traitement trt=new Traitement();	     
		 	  
		      
		      
		           if(idWithD>0) {
		        	    
					         if( montantRetrait <= interetRepo.getInteretDuMembreParMatricule(matricule)) {
					        	    
							        	 double reste=(interetRepo.getInteretDuMembreParMatricule(matricule)-montantRetrait);
										 operaRepo.save(new Operation("Un membre de matricule "+matricule+" a effectue un retrait d''une somme de "+montantRetrait+" $", new Date()));	
							        	 interetRepo.updateInteret(idWithD, trt.rounder(reste), new Date());			        	   
							        	 InteretParMembre itm=interetRepo.getUserByMatricule(matricule);
							        	 
							        	 if(interetRepo.getInteretDuMembreParMatricule(matricule)!=null){
							        		   
								        		   if(interetRepo.getInteretDuMembreParMatricule(matricule)==0){ 
								        			   
									        			   interetRepo.deleteById(itm.getId_interet());
									        			   errorList.add("Vous venez de tout retirer,desormais votre interet est de 0 $");
								        		   
								        		       }        		   
							        		   
							        	       }
					        		   
					            }else {
					        	  
							        	     errorList.add("Vous ne pouvez pas retirer un montant superieur a l'interet disponible");								        	 
							        	     System.out.println("Vous ne pouvez pas retirer un montant superieur a votre interet disponible");							        	     
					        	             
					             }
					     
				
			            }else  {
				  
       	                    errorList.add("Veiller selectionner le membre qui veut effectuer un retrait");
	        	 				  
			              }
		          
		           
	           Page <List<List<Object>>> interetList =interetRepo.interetParMembre(PageRequest.of(page,size));
			   Page <List<List<Object>>> operaList =operaRepo.getAllOperation(PageRequest.of(pageOpera,size));	
			   double totalInteretNet=interetRepo.totalInteretNet()!=null? interetRepo.totalInteretNet() : 0;	
			  	
			   model.addAttribute("lst",trt.converter(interetList));	    
			   model.addAttribute("pages",new int[interetList.getTotalPages()]);								
	           model.addAttribute("lstOpera",trt.converter(operaList));
	           model.addAttribute("pages2", new int[operaList.getTotalPages()]);	
	           model.addAttribute("currentPage",page);
	           model.addAttribute("currentSize",size);
	           model.addAttribute("currentPage2",pageOpera);
	           model.addAttribute("totalInteretNet", String.format("%.3f" ,totalInteretNet));  
	           model.addAttribute("keyWord", mc);
		       model.addAttribute("errorList",errorList);
		       
			  return "interet::containerMainInInteret";
		
	   
	
	}
	
	
	@PostMapping(value="/interet/interetGeneratePDF/",produces="application/pdf")
	public ResponseEntity<byte[]> generatePDF(@RequestParam(name="currentTable") String type, @RequestParam(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFileName="";
		 	   String fileName="";
		 	   
		 	   
		 	    if(type.equals("operations")) {
		 		   
				     List<Operation> searchOperaList =operaRepo.findByOperationContains(!mc.equals("all")? mc:"");
				 	 fileName="operations";
				 	 jasperFileName="operations.jrxml";
				 	 map.put("nameFor", "Israel");				 	 
				 	 return  trt.generatePDF(searchOperaList, jasperFileName, map, fileName);
		 	   }
		 	    
		 	   List<InteretParMembre> searchInteretList =interetRepo.findByMatriculeEnteredContains(!mc.equals("all")? mc:"");
			   fileName="interets";
			   jasperFileName="interet.jrxml";
			   map.put("nameFor", "Israel");			 	 
			   return  trt.generatePDF(searchInteretList, jasperFileName, map, fileName);
			   
		 	   
		 	  
		 	    
	}
	
	
	@PostMapping(value="/proof/",produces="application/pdf")
	public ResponseEntity<byte[]> generatePDF(@RequestParam(name="name") String nom, @RequestParam(name="matricule") String matricule, @RequestParam(name="montant") double montantRetrait) throws JRException, Exception{
		 
		 Traitement trt=new Traitement();
		  HashMap<String,Object> map = new HashMap<>();
	 	  String jasperFileName="";
	 	  String fileName="";
		  
	 	     fileName="retrait";
	 	     jasperFileName="proof.jrxml";
		 	 map.put("typeOperation", "RETRAIT");
		 	 map.put("info", "le retrait");
		 	 map.put("nom",nom);
		 	 map.put("matricule",matricule);
		 	 map.put("montant",montantRetrait);
	 	    
		 return	trt.generateProof(jasperFileName, map, fileName);
	}
	

}
