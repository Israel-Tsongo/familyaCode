package org.sid.FamilyaProject.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.sid.FamilyaProject.dao.DepenseRepository;
import org.sid.FamilyaProject.dao.OperationRepository;
import org.sid.FamilyaProject.entities.Depense;
import org.sid.FamilyaProject.entities.Operation;
import org.sid.FamilyaProject.metier.Traitement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.sf.jasperreports.engine.JRException;




@Controller
public class DepenseController {
	
	@Autowired
	private OperationRepository operaRepo;
	
	@Autowired
	private DepenseRepository depRepo;
	

	
	
	
	//************** ACCEUILLE************************
	
	@GetMapping("/depense")
	public String depense(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));
		

	   Double getTotalOutgo=depRepo.getTotalOutgo();
	   double totalDepense=getTotalOutgo !=null?getTotalOutgo : 0.00 ;
	   
	 
	    model.addAttribute("lst",trt.converter(DepList));
		model.addAttribute("pages",new int[DepList.getTotalPages()]);
		model.addAttribute("currentPage",page);
		model.addAttribute("currentSize",size);
		model.addAttribute("pageTitle","Depense");
		model.addAttribute("totalDepense", String.format("%.3f", totalDepense));
		model.addAttribute("keyWord", mc);		
		return "depense";
	   
	}
	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping("/depSearcher")
	public String searchDepense(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		 
		   
		   if(pagin) {			   	
				
			  
			   Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
			   
			   Double getTotalOutgo=depRepo.getTotalOutgo();
			   double totalDepense=getTotalOutgo !=null?getTotalOutgo : 0.00 ;
			   
	           model.addAttribute("lst", trt.converter(DepList));
	           model.addAttribute("pages", new int[DepList.getTotalPages()]);	
	           model.addAttribute("currentSize",size);
	           model.addAttribute("currentPage",page);
	           model.addAttribute("totalDepense", String.format("%.3f",  totalDepense));  
	           model.addAttribute("keyWord", mc);
			   
	           return "/depense::mainContainerInDep";
		   }else {
			       Page <Depense> depList =depRepo.findByMotifContains(mc,PageRequest.of(page,size));
			       
			       Double getTotalOutgo=depRepo.getTotalOutgo();
				   double totalDepense=getTotalOutgo !=null?getTotalOutgo : 0.00 ;
				   
		             model.addAttribute("lst", trt.searchDepConverter(depList));
		             model.addAttribute("pages", new int[depList.getTotalPages()]);	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("totalDepense",  String.format("%.3f", totalDepense));
		             model.addAttribute("currentSize",size);	
		             model.addAttribute("keyWord", mc);
		            
		             return  "depense::mainContainerInDep";
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/depPost")
	public String postDepenseData(Model model, @RequestParam() double depenseMontant,  @RequestParam() String motif,			
			                                    @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
		                                    ) {
		
		List<String> errorList= new ArrayList<String>()	; 
		Traitement trt = new Traitement();
		
		 
		try {
		
		 	
		 depRepo.save(new Depense(depenseMontant,motif, new Date()));
		  operaRepo.save(new Operation("Une depense ayant pour motif "+motif +"de montant :"+depenseMontant+" vient d'etre ajouté sur la liste" ,new Date()));	


			
		}catch(Exception exc) {			
		    errorList.add("Une erreur s'est produite lors de l'enregistrement d une nouvelle depense");
			System.out.println("Une erreur s'est produite lors de l'enregistrement d une nouvelle depense");			
			System.out.println(exc.getMessage());
			
		}
		
		Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
		
		Double getTotalOutgo=depRepo.getTotalOutgo();
		double totalDepense=getTotalOutgo !=null?getTotalOutgo : 0.00 ;		   
							   
		model.addAttribute("lst", trt.converter(DepList));
		model.addAttribute("pages", new int[DepList.getTotalPages()]);	
		model.addAttribute("currentPage",page);
		model.addAttribute("currentSize",size);
        model.addAttribute("totalDepense", String.format("%.3f",  totalDepense));
        model.addAttribute("errorList",errorList);
		return "depense::mainContainerInDep";
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteDep")
	
	public String  deleteDepense(Model model, @RequestParam() Long idDep,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 
		  Traitement trt=new Traitement();
		 
		 
		  if (idDep>0) {
			  
			Depense depense =depRepo.getDepenseById(idDep);
		   depRepo.deleteById(idDep);	
		  operaRepo.save(new Operation("Une depense avec pour motif"+depense.getMotif()+"vient d etre supprime ", new Date()));	

		             
		             
		  }else  { System.out.println("Error lors de la suppression");}
		  
		 Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
		 Double getTotalOutgo=depRepo.getTotalOutgo();
		 double totalDepense=getTotalOutgo !=null?getTotalOutgo : 0.00 ;		            
         		   
         model.addAttribute("lst", trt.converter(DepList));
         model.addAttribute("pages", new int[DepList.getTotalPages()]);
         model.addAttribute("currentPage",page);
         model.addAttribute("currentSize",size);
         model.addAttribute("totalDepense", String.format("%.3f",  totalDepense));
		
		return "depense::mainContainerInDep";
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateDep")
	public String updateDepense(Model model, @RequestParam() Long  idDep , @RequestParam() double depenseMontant ,  @RequestParam() String motif,			
            @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     Traitement trt=new Traitement();
		      
		      if(idDep>0) {
		    	  		
		    	     Depense depense =depRepo.getDepenseById(idDep);
					 depRepo.updateDepense(idDep,depenseMontant,motif, new Date());	
					 operaRepo.save(new Operation("Une depense du "+depense.getDate()+" vient d'etre mise à jour sur la liste de depenses,nouvelles infos: montant depense: " +depenseMontant+" Motif: "+motif, new Date()));	

					     
					    
			  }else  { System.out.println("Id invalid");}
		
             Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
            
             Double getTotalOutgo=depRepo.getTotalOutgo();
      	    double totalDepense=getTotalOutgo !=null?getTotalOutgo : 0.00 ;		 
		     								   
		     model.addAttribute("lst", trt.converter(DepList));
		     model.addAttribute("pages", new int[DepList.getTotalPages()]);
		     model.addAttribute("currentSize",size);
		     model.addAttribute("currentPage",page);
		     model.addAttribute("totalDepense",  String.format("%.3f", totalDepense));
			 return "depense::mainContainerInDep";
	
	}
	
	
	@PostMapping(value="/depense/generatePDF/",produces="application/pdf")
	public ResponseEntity<byte[]> generatePDF(Model model ,@RequestParam(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFileName="depenses.jrxml";
		 	   String fileName="depense";
		 	   
		       List<Depense> depList =depRepo.findByMotifContains(!mc.equals("all")? mc:"");
  
		 	   map.put("nameFor", "Israel");  
			   
		       return  trt.generatePDF(depList, jasperFileName, map, fileName);
	   
	}
	

}
