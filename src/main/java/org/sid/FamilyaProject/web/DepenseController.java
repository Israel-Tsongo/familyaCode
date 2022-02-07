package org.sid.FamilyaProject.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.sid.FamilyaProject.dao.DepenseRepository;
import org.sid.FamilyaProject.entities.Depense;
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
public class DepenseController {
	
	
	
	@Autowired
	private DepenseRepository depRepo;
	
	
	
	//************** ACCEUILLE************************
	
	@GetMapping(path="/depense")
	public String depense(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
	   	 
	   double totalDepense=depRepo.getTotalOutgo() !=null?depRepo.getTotalOutgo() : 0.00 ;
	   
	  
	 
	    model.addAttribute("lst",trt.converter(DepList));
		model.addAttribute("pages",new int[DepList.getTotalPages()]);
		model.addAttribute("currentPage",page);
		model.addAttribute("pageTitle","Depense");
		model.addAttribute("totalDepense", String.format("%.3f", totalDepense));
		model.addAttribute("keyWord", mc);
		
		return "depense";
	   
	}
	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping(path="/depSearcher")
	public ModelAndView searchDepense(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
				
			   Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
			   double totalDepense=depRepo.getTotalOutgo() !=null?depRepo.getTotalOutgo() : 0.00 ;
			   
			   ModelAndView mv = new ModelAndView();		           
	           mv.addObject("lst", trt.converter(DepList));
	           mv.addObject("pages", new int[DepList.getTotalPages()]);	
	           mv.addObject("currentPage",page);
	           mv.addObject("totalDepense", String.format("%.3f",  totalDepense));  
	           mv.addObject("keyWord", mc);
			   mv.setViewName("/depense:mainContainerInDep");
	           return  mv;
		   }else {
			       Page <Depense> depList =depRepo.findByMotifContains(mc,PageRequest.of(page,size));
				   double totalDepense=depRepo.getTotalOutgo() !=null?depRepo.getTotalOutgo() : 0.00 ;
			       ModelAndView mv = new ModelAndView();		           
		             mv.addObject("lst", trt.searchDepConverter(depList));
		             mv.addObject("pages", new int[depList.getTotalPages()]);	
		             mv.addObject("currentPage",page);
		             mv.addObject("totalDepense",  String.format("%.3f", totalDepense));
		             mv.addObject("currentSize",size);	
		             mv.addObject("keyWord", mc);
		             mv.setViewName("/depense::mainContainerInDep");
		             return  mv;
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/depPost")
	public ModelAndView postDepenseData(@RequestParam() double depenseMontant,  @RequestParam() String motif,			
			                                    @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
		                                    ) {
		
		List<String> errorList= new ArrayList<String>()	; 
		Traitement trt = new Traitement();
		ModelAndView mv=null ;
		 
		try {
		
		 depRepo.save(new Depense(depenseMontant,motif, new Date()));
			
		}catch(Exception exc) {			
		    errorList.add("Une erreur s'est produite lors de l'enregistrement d une nouvelle depense");
			System.out.println("Une erreur s'est produite lors de l'enregistrement d une nouvelle depense");			
			System.out.println(exc.getMessage());
			
		}
		
		Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
		double totalDepense=depRepo.getTotalOutgo() !=null?depRepo.getTotalOutgo() : 0.00 ;
		   
		mv = new ModelAndView("/depense::mainContainerInDep");					   
		mv.addObject("lst", trt.converter(DepList));
		mv.addObject("pages", new int[DepList.getTotalPages()]);	
		mv.addObject("currentPage",page);
        mv.addObject("totalDepense", String.format("%.3f",  totalDepense));
        mv.addObject("errorList",errorList);
		return mv;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteDep")
	
	public ModelAndView deleteDepense(@RequestParam() Long  idDep,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		  ModelAndView mv=null ;
		 
		  if (idDep>0) {
			  
		   depRepo.deleteById(idDep);		   
		             
		             
		  }else  { System.out.println("Error lors de la suppression");}
		  
		 Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
		 double totalDepense=depRepo.getTotalOutgo() !=null?depRepo.getTotalOutgo():0 ;   
		            
         mv = new ModelAndView("/depense::mainContainerInDep");		   
         mv.addObject("lst", trt.converter(DepList));
         mv.addObject("pages", new int[DepList.getTotalPages()]);
         mv.addObject("currentPage",page);
         mv.addObject("totalDepense", String.format("%.3f",  totalDepense));
		
		return  mv;
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateDep")
	public ModelAndView updateDepense(@RequestParam() Long  idDep , @RequestParam() double depenseMontant ,  @RequestParam() String motif,			
            @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     Traitement trt=new Traitement();
		      ModelAndView mv=null ;
		           if(idDep>0) {
			
					     depRepo.updateDepense(idDep,depenseMontant,motif, new Date());	
					     
					     
			     
			   
			  }else  { System.out.println("Rien a Update");}
		
             Page <List<List<Object>>> DepList =depRepo.getAllDep(PageRequest.of(page,size));			
		     double totalDepense=depRepo.getTotalOutgo() !=null?depRepo.getTotalOutgo() : 0 ;
		 
		     mv = new ModelAndView("/depense::mainContainerInDep");								   
		     mv.addObject("lst", trt.converter(DepList));
		     mv.addObject("pages", new int[DepList.getTotalPages()]);
		     mv.addObject("currentPage",page);
		     mv.addObject("totalDepense",  String.format("%.3f", totalDepense));
			 return mv;
	
	}
	
	
	@GetMapping("/depense/generatePDF/{keyWord}")
	public ResponseEntity<byte[]> generatePDF(Model model ,@PathVariable(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFilePath="src/main/resources/Coffee.jrxml";
		 	   String fileName="depense";
		 	   
		       List<Depense> depList =depRepo.findByMotifContains(!mc.equals("all")? mc:"");
  
		 	   map.put("nameFor", "Israel");			   
			   
			   //List<Payement> testList=payeRepo.findAll();

		       return  trt.generatePDF(depList, jasperFilePath, map, fileName);
	   
	}
	

}
