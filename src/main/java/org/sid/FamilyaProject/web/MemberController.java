package org.sid.FamilyaProject.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Payement;
import org.sid.FamilyaProject.metier.Traitement;
import org.sid.FamilyaProject.security.MyUserDetails;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.sf.jasperreports.engine.JRException;

@Controller
public class MemberController {
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private DebiteurRepository debiteurRepo;
	
	@Autowired
	private InteretParMembreRepository interetRepo;
	
	@Autowired
	private PayementRepository payeRepo;
	
	@Autowired
	private UserRepository userRepository;
	
	
	//************** ACCEUILLE************************
	
	@GetMapping(path="/index")
	public String index(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> MemberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));		
		
	   
	   double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
       
	    
		

	    model.addAttribute("lst",trt.converter(MemberList));
		model.addAttribute("pages",new int[MemberList.getTotalPages()]);
		model.addAttribute("currentPage",page);
		model.addAttribute("pageTitle","Membre");
		model.addAttribute("totalCapitaux",totalCapitauxInitiaux);
		model.addAttribute("keyWord", mc);		
		return "index";
	   
	}
	
	

	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping(path="/indexSearcher")
	public ModelAndView searchByMatricule(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
			
			   Page <List<List<Object>>> MemberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
			   double totalCapitauxInitiaux1=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;

			   ModelAndView mv = new ModelAndView();		           
	           mv.addObject("lst", trt.converter(MemberList));
	           mv.addObject("pages", new int[MemberList.getTotalPages()]);	
	           mv.addObject("currentPage",page);
	           mv.addObject("totalCapitaux", totalCapitauxInitiaux1);	           
	           mv.addObject("keyWord", mc);
			   mv.setViewName("/index::mainContainer");
			   
			   
	           return  mv;
		   }else {
			       Page <Member> searchMemberList =memberRepo.findByNomContains(mc,PageRequest.of(page,size));
				   double totalCapitauxInitiaux2=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
			       
			       ModelAndView mv = new ModelAndView("/index::mainContainer");		           
		             model.addAttribute("lst", trt.searchConverter(searchMemberList));
		             model.addAttribute("pages", new int[searchMemberList.getTotalPages()]);	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentSize",size);
		             model.addAttribute("totalCapitaux", totalCapitauxInitiaux2);  
		             model.addAttribute("keyWord", mc);
		             
		
		             return  mv;
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/indexPost")
	public ModelAndView postIndexData( @RequestParam() String matricule, @RequestParam(defaultValue=" ") String mandateur,			
			                                     @RequestParam() double capital,@RequestParam() String fonction,
			                                     @RequestParam() String categorie,@RequestParam() String contrat,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
			                                     ) {
		
		List<String> errorList = new ArrayList<String>();
		Traitement trt = new Traitement();
		ModelAndView mv=null ;
		
		
		 
		try {
			
			if( memberRepo.getUserByMatricule(matricule)==null) {
				     
				     if(userRepository.getUserByMatricule(matricule) !=null ) {
				    	 
				    	 BigDecimal salaire=userRepository.getUserByMatricule(matricule).getSalaire();
				    	 BigDecimal retenu=userRepository.getUserByMatricule(matricule).getRetenu();
				    			 
				    	 if (capital>=(salaire.doubleValue()*(retenu.doubleValue()/100))) {
				
						  User usr=userRepository.getUserByMatricule(matricule);
						  Member member =new Member(usr.getNom(),matricule,fonction, new Date(),contrat,categorie,capital);
					      
					      member.setMemberUser(usr);
					      usr.setMember(member);
						  memberRepo.save(member );	
						  
				    	 }else {
				    		 
				    		 double temp= 3.0*retenu.doubleValue();
				    		 errorList.add("Le capital initial du membre doit etre superieur ou egale a "+Double.toString(temp)+" % "+"En considerant votre retenu de "+Double.toString(retenu.doubleValue()));
							 System.out.println("Le capital initial du membre doit etre superieur ou egale a "+Double.toString(temp)+" %"+"En considerant votre retenu de "+Double.toString(retenu.doubleValue()));
				    		 
				    	 }
			           
				     }else {
				    	 
				    	 
						 errorList.add("Aucun utilisateur ne correspond au matricule entre");
						 System.out.println("Aucun utilisateur ne correspond au matricule entre");
				     }
			           
			}else {
				
				errorList.add("Ce numero matricule appartient a un membre deja present dans la base de donne de membres");
				System.out.println("Ce numero matricule appartient a un membre dans la base");
				
			}
			
		}catch(Exception exc) {	
			mv = new ModelAndView("/index::mainContainer");
		    errorList.add("- Une erreur s'est produite lors de l'enregistrement d un nouveau membre");
			System.out.println("Une erreur s'est produite lors de l'enregistrement d un nouveau membre");			
			System.out.println(exc.getMessage()   );
			
		}
		Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
		double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
		   
		mv = new ModelAndView("/index::mainContainer");					   
		mv.addObject("lst", trt.converter(memberList));
		mv.addObject("pages",new int[memberList.getTotalPages()]);			  
		mv.addObject("currentPage",page);					   
        mv.addObject("totalCapitaux",String.format("%.3f", totalCapitauxInitiaux));		
		mv.addObject("errorList",errorList);
		return mv ;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteMember")
	
	public ModelAndView deleteMember(@RequestParam() Long  idMember,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		 ModelAndView mv=null ;
		 
		  if (idMember>0) {
			  
		   memberRepo.deleteById(idMember);	   
		   
		  }else  { System.out.println("Rien a foutre");}
		  
		     Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
		     double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
		            
	         mv = new ModelAndView("/index::mainContainer");		   
	         mv.addObject("lst", trt.converter(memberList));
	         mv.addObject("pages", new int[memberList.getTotalPages()]);
	         mv.addObject("currentPage",page);
	         mv.addObject("totalCapitaux", totalCapitauxInitiaux);
	         
		    return  mv;
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/update")
	public ModelAndView updateMember(@RequestParam() Long  idMember , @RequestParam(defaultValue="") String mandateur,  @RequestParam() String matricule,			
            @RequestParam() String capital,@RequestParam() String fonction,
            @RequestParam() String categorie,@RequestParam() String contrat,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     Traitement trt=new Traitement();
		     ModelAndView mv=null ;
		           if(idMember>0) {
			
					     memberRepo.updateMember(idMember, matricule,Double.parseDouble(capital) , fonction, categorie, contrat, new Date());						   
						 
			      
			   
			  }else  { System.out.println("error in update Update");}
		    
		      Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
			 double totalCapitauxInitiaux=memberRepo.getTotalCapitauxInitiaux() !=null?memberRepo.getTotalCapitauxInitiaux() : 0.00 ;
					 
			 mv = new ModelAndView("/index::mainContainer");								   
			 mv.addObject("lst", trt.converter(memberList));
			 mv.addObject("pages", new int[memberList.getTotalPages()]);
		     mv.addObject("currentPage",page);
		     mv.addObject("totalCapitaux", totalCapitauxInitiaux);
		 
			return mv;
		
	   
	
	}
	
	@PostMapping("/detailMembre")
	public ModelAndView detail(@RequestParam(required=true) Long  idMember) {
		
		String matricule= memberRepo.getMatriculeById(idMember);
		
    	List<List<Object>> detailMembre=payeRepo.getDetails(matricule);
    	Double detteCourante = debiteurRepo.detteCouranteByMatricule(matricule)  !=null ? debiteurRepo.detteCouranteByMatricule(matricule): 0.0;
    	Double interet = interetRepo.interetDuMembreByMatricule(matricule)  !=null ? interetRepo.interetDuMembreByMatricule(matricule) : 0.0;
    	
    	
	
		
		 ModelAndView modelAndView=null ;
		 modelAndView = new ModelAndView("/index::detailModalContent");		
		 
		 
		 if(memberRepo.getUserByMatricule(matricule)!=null) {
				
		    	
		    	if(payeRepo.getPayementByMatric(matricule)==null || payeRepo.getPayementByMatric(matricule).isEmpty() ) {
		    		
			    	List<List<Object>> lst=memberRepo.getCapitalMatriculeNom(matricule);
			    	modelAndView.addObject("nom",lst.get(0).get(0));
					 modelAndView.addObject("matricule",lst.get(0).get(1));
					 modelAndView.addObject("capital",lst.get(0).get(2));
					 modelAndView.addObject("pageTitle","Authentification");
					 modelAndView.addObject("contributions",0.0);
					 modelAndView.addObject("solde",lst.get(0).get(2));
		    		
		    		
		    	}else {
		    		 modelAndView.addObject("nom",detailMembre.get(0).get(0));
					 modelAndView.addObject("matricule",detailMembre.get(0).get(1));
					 modelAndView.addObject("capital",detailMembre.get(0).get(2));
					 modelAndView.addObject("contributions",detailMembre.get(0).get(3));
					 modelAndView.addObject("solde",detailMembre.get(0).get(4));
					 
		    	}
				 						
		    	     modelAndView.addObject("detteCourante",detteCourante);			    		
				     modelAndView.addObject("interet",interet);
		
		 
		 }
		 return modelAndView;
	
	}
	
	@GetMapping("membre/generatePDF/{keyWord}")
	public ResponseEntity<byte[]> generatePDF(Model model ,@PathVariable(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
		 	   String jasperFilePath="src/main/resources/Coffee.jrxml";
		 	   String fileName="contributions";
		       List <Member> searchMemberList =memberRepo.findByNomContains(!mc.equals("all")? mc:"");

		 	   map.put("nameFor", "Israel");			   
			   
			   //List<Payement> testList=payeRepo.findAll();

		       return  trt.generatePDF(searchMemberList, jasperFilePath, map, fileName);
	   
	}
	
}
