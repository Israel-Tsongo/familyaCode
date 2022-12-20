package org.sid.FamilyaProject.web;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.OperationRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Operation;
import org.sid.FamilyaProject.metier.Traitement;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	@Autowired
	private OperationRepository operaRepo;
	
	
	
	
	//************** ACCEUILLE************************
	
	@GetMapping("/index")
	public String index(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> MemberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));		
		
		Double getTotalCapitauxInitiaux = memberRepo.getTotalCapitauxInitiaux();
	    double totalCapitauxInitiaux=getTotalCapitauxInitiaux !=null?getTotalCapitauxInitiaux: 0.00 ;
       
	    model.addAttribute("lst",trt.converter(MemberList));
		model.addAttribute("pages",new int[MemberList.getTotalPages()]);
		model.addAttribute("currentPage",page);
		model.addAttribute("currentSize",size);
		model.addAttribute("pageTitle","Membre");
		model.addAttribute("totalCapitaux",String.format("%.3f",totalCapitauxInitiaux));
		model.addAttribute("keyWord", mc);		
		return "index";
	   
	}
	
	

	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping("/indexSearcher")
	public String  searchByMatricule(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
			
			   Page <List<List<Object>>> MemberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
			   Double getTotalCapitauxInitiaux = memberRepo.getTotalCapitauxInitiaux();
			   double totalCapitauxInitiaux=getTotalCapitauxInitiaux !=null?getTotalCapitauxInitiaux : 0.00 ;
		       
			   		           
	           model.addAttribute("lst", trt.converter(MemberList));
	           model.addAttribute("pages", new int[MemberList.getTotalPages()]);	
	           model.addAttribute("currentPage",page);
	           model.addAttribute("currentSize",size);
	           model.addAttribute("totalCapitaux",String.format("%.3f",totalCapitauxInitiaux));	           
	           model.addAttribute("keyWord", mc);			   
	           return "index::mainContainer";
	           
		   }else {
			       Page <Member> searchMemberList =memberRepo.findByUserMatriculeContains(mc,PageRequest.of(page,size));
			       Double getTotalCapitauxInitiaux = memberRepo.getTotalCapitauxInitiaux();
				   double totalCapitauxInitiaux=getTotalCapitauxInitiaux !=null?getTotalCapitauxInitiaux : 0.00 ;
			       			       
			       		           
		             model.addAttribute("lst", trt.searchConverter(searchMemberList));
		             model.addAttribute("pages", new int[searchMemberList.getTotalPages()]);	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentSize",size);
		             model.addAttribute("totalCapitaux",String.format("%.3f",totalCapitauxInitiaux));  
		             model.addAttribute("keyWord", mc);            
		
		             return "index::mainContainer";
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@PostMapping("/indexPost")
	public String postIndexData(Model model,@RequestParam(defaultValue=" ") String code,  @RequestParam() String matricule, @RequestParam(defaultValue=" ") String mandataire,			
			                                  @RequestParam() double capital,@RequestParam() @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Date dateDadhesion,
			                                  @RequestParam() String contrat,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size		                                     
			                                  ) {
		
		List<String> errorList = new ArrayList<String>();
		Traitement trt = new Traitement();
		String mv="" ;	
		
		 
		try {
			
			if( memberRepo.getMemberByMatricule(matricule)==null) {				
				 
					 
				     				    	 
				    	 BigDecimal salaire=userRepository.getUserByMatricule(matricule).getSalaire();
				    	 BigDecimal retenu=userRepository.getUserByMatricule(matricule).getRetenu();
				    			 
				    	 if (capital>=(salaire.doubleValue()*(retenu.doubleValue()/100))) {
				
						  User usr=userRepository.getUserByMatricule(matricule);
						  Member member =new Member(dateDadhesion,contrat,capital,mandataire);
					      
					      member.setMemberUser(usr);
					      usr.setMember(member);
						  memberRepo.save(member );	
						  operaRepo.save(new Operation("Un utilisateur de matricule "+usr.getMatricule() +" vient d'etre ajouté comme Membre  de familia" ,new Date()));	

						 
						  
				    	 }else {
				    		 
				    		 double temp= 3.0*retenu.doubleValue();
				    		 errorList.add("Le capital initial du membre doit etre superieur ou egale a "+Double.toString(temp)+" % "+"En considerant votre retenu de "+Double.toString(retenu.doubleValue()));
							 System.out.println("Le capital initial du membre doit etre superieur ou egale a "+Double.toString(temp)+" %"+"En considerant votre retenu de "+Double.toString(retenu.doubleValue()));
				    		 
				    	 }
				     
				   
				 
				 
			}else {
				
				errorList.add("Ce numero matricule appartient a un membre deja present dans la base de donne de membres");
				System.out.println("Ce numero matricule appartient a un membre dans la base");
				
			}
			
		}catch(Exception exc) {	
			
			mv = "index::mainContainer";			
		    errorList.add("Une erreur s'est produite lors de l'enregistrement d un nouveau membre");
			System.out.println("Une erreur s'est produite lors de l'enregistrement d un nouveau membre");			
			System.out.println(exc.getMessage()   );
			
		}
		
		Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
		Double getTotalCapitauxInitiaux = memberRepo.getTotalCapitauxInitiaux();
	    double totalCapitauxInitiaux=getTotalCapitauxInitiaux !=null?getTotalCapitauxInitiaux : 0.00 ;
       		   
		mv = "index::mainContainer";					   
		model.addAttribute("lst", trt.converter(memberList));
		model.addAttribute("pages",new int[memberList.getTotalPages()]);
		model.addAttribute("currentSize",size);
		model.addAttribute("currentPage",page);					   
        model.addAttribute("totalCapitaux",String.format("%.3f", totalCapitauxInitiaux));		
		model.addAttribute("errorList",errorList);
		return mv ;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deteteMember")
	
	public String deleteMember(Model model, @RequestParam() Long  idMember,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		 
		 
		  if (idMember>0) {
			Member membre =memberRepo.getMemberById(idMember) ;
		   memberRepo.deleteById(idMember);	
		  operaRepo.save(new Operation("Un Membre de matricule "+membre.getMemberUser().getMatricule() +" vient d'etre supprime de la liste des membres" ,new Date()));	

		   
		  }else  { System.out.println("id invalid");}
		  
		     Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
		     Double getTotalCapitauxInitiaux = memberRepo.getTotalCapitauxInitiaux();
			 double totalCapitauxInitiaux=getTotalCapitauxInitiaux !=null?getTotalCapitauxInitiaux : 0.00 ;
		       		            
	         		   
	         model.addAttribute("lst", trt.converter(memberList));
	         model.addAttribute("pages", new int[memberList.getTotalPages()]);
	         model.addAttribute("currentSize",size);
	         model.addAttribute("currentPage",page);
	         model.addAttribute("totalCapitaux",String.format("%.3f",totalCapitauxInitiaux));
	         
		    return  "index::mainContainer";
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/update")
	public String updateMember(Model model, @RequestParam() Long  idMember , @RequestParam(defaultValue="") String mandataire, @RequestParam() String code,  @RequestParam() String matricule,			
            @RequestParam() String capital,@RequestParam() @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Date dateDadhesion,
            @RequestParam() String contrat,  @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     
		     List<String> errorList = new ArrayList<String>();
		     Traitement trt=new Traitement();
		     
		     
		     if(idMember>0) {
			
					 memberRepo.updateMember(idMember, mandataire,Double.parseDouble(capital), contrat, dateDadhesion);
					 operaRepo.save(new Operation("Un membre de matricule "+matricule+" vient d'etre mise à jour sur la liste de membres,nouvelles infos: capital: " +capital+" mandataire: "+mandataire+"contrat: "+contrat+"date d'adhesion :"+dateDadhesion,new Date()));	

					 

			  }else  {
				  System.out.println("Error when updating (id invalid)");
			  }
		      //trt.getGerantAndFinancier(memberRepo,userRepo);
		      Page<List<List<Object>>> memberList =memberRepo.getAllFromMembersTable(PageRequest.of(page,size));
		      
		      Double getTotalCapitauxInitiaux = memberRepo.getTotalCapitauxInitiaux();
			  double totalCapitauxInitiaux=getTotalCapitauxInitiaux !=null?getTotalCapitauxInitiaux : 0.00 ;
		       					 
			 								   
			 model.addAttribute("lst", trt.converter(memberList));
			 model.addAttribute("pages", new int[memberList.getTotalPages()]);
			 model.addAttribute("currentSize",size);
		     model.addAttribute("currentPage",page);
		     model.addAttribute("totalCapitaux",String.format("%.3f",totalCapitauxInitiaux));
		     model.addAttribute("errorList",errorList);
		 
			return "index::mainContainer";
		
	   
	
	}
	
	@PostMapping("/detailMembre")
	public String detail(Model model, @RequestParam(required=true) Long idMember) {
		
		String matricule= memberRepo.getMatriculeById(idMember);
		
    	List<List<Object>> detailMembre=payeRepo.getDetails(matricule);
    	
    	Double detteCouranteByMatricule=debiteurRepo.detteCouranteByMatricule(matricule);
    	double detteCourante = detteCouranteByMatricule !=null ? detteCouranteByMatricule: 0.0;
    	Double interetDuMembreByMatricule =interetRepo.interetDuMembreByMatricule(matricule);
    	double interet = interetDuMembreByMatricule   !=null ? interetDuMembreByMatricule : 0.0;
    	
		 
		 
		 if(memberRepo.getMemberByMatricule(matricule)!=null) {
				
		    	
		    	if(payeRepo.getPayementByMatricule(matricule)==null || payeRepo.getPayementByMatricule(matricule).isEmpty() ) {
		    		
			    	 List<List<Object>> lst= memberRepo.getCapitalMatriculeNom(matricule);
			    	 model.addAttribute("nom",lst.get(0).get(0));
					 model.addAttribute("matricule",lst.get(0).get(1));
					 model.addAttribute("capital",String.format("%.3f",lst.get(0).get(2)));
					 model.addAttribute("pageTitle","Authentification");
					 model.addAttribute("contributions",0.0);
					 model.addAttribute("solde",String.format("%.3f",lst.get(0).get(2)));
		    		
		    		
		    	}else {
		    		 model.addAttribute("nom",detailMembre.get(0).get(0));
					 model.addAttribute("matricule",detailMembre.get(0).get(1));
					 model.addAttribute("capital",String.format("%.3f",detailMembre.get(0).get(2)));
					 model.addAttribute("contributions",String.format("%.3f",detailMembre.get(0).get(3)));
					 model.addAttribute("solde",String.format("%.3f",detailMembre.get(0).get(4)));
					 
		    	}
				 						
		    	     model.addAttribute("detteCourante",detteCourante);			    		
				     model.addAttribute("interet",interet);
		
		 
		 }
		 return "index::detailModalContent";
	
	}
	
	@PostMapping(value="/membre/generatePDF/",produces="application/pdf")
	
	public ResponseEntity<byte[]> generatePDF(Model model ,@RequestParam(name="keyWord") String mc) throws Exception, JRException  {
		
		 	   Traitement trt = new Traitement();
		 	   HashMap<String,Object> map = new HashMap<>();
//		 	   
		 	   String jasperFileName="membre.jrxml";	 	   
		 	   
		 	   String fileName="Membre";
		       List <Member> searchMemberList =memberRepo.findByUserNomContains(!mc.equals("all")? mc:"");
              
		 	   map.put("membre", "Israel");			   
			   
			   
		       return  trt.generatePDF(searchMemberList, jasperFileName, map, fileName);
	   
	}
	
}
