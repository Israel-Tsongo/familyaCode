package org.sid.FamilyaProject.web;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.security.UserDetailsServiceImpl;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginAsController {
	
//	@Autowired
//	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService ;
	
	@Autowired
	private DebiteurRepository debiteurRepo;
	
	@Autowired
	private InteretParMembreRepository interetRepo;
	
	@Autowired
	private PayementRepository payeRepo;
	
	@Autowired
	private UserRepository userRepo;
	

	
	@GetMapping("/loginAsMember")
	public String logAsMember(Authentication authentication,Model model ) { //,RedirectAttributes rd
		
		
		String email="";
		String matricule="";		
		User usr=null;
		String modelAndView = "";
		
		System.out.println("1=====> loginAsMember ");
		if(authentication !=null) {
			
			email= authentication.getName();
			usr= userRepo.getUserByEmail(email);
			matricule=usr.getMatricule();			
			model.addAttribute("user",usr);
			
			Member currentMember=memberRepo.getMemberByMatricule(matricule);
			
			//rd.addFlashAttribute("typeOnlogin","Member");		
			System.out.println("2=====> loginAsMember ");
		
		
		 if(currentMember==null){
     		
 		    model.addAttribute("usr",usr);
        	modelAndView="homeEmpty";
        	return modelAndView;
        	
        	
         }else {
		
			
	    	List<List<Object>> detailMembre=payeRepo.getDetails(matricule);
	    	Double detteCouranteByMatricule= debiteurRepo.detteCouranteByMatricule(matricule);
	    	double detteCourante = detteCouranteByMatricule !=null ? detteCouranteByMatricule: 0.0;
	    	Double interetDuMembreByMatricule=interetRepo.interetDuMembreByMatricule(matricule);
	    	double interet = interetDuMembreByMatricule !=null ? interetDuMembreByMatricule : 0.0;
	    	
	    	if(!(payeRepo.getPayementByMatricule(matricule).isEmpty())) {
	    		
	    		 model.addAttribute("nom",detailMembre.get(0).get(0));
				 model.addAttribute("matricule",detailMembre.get(0).get(1));
				 model.addAttribute("capital",detailMembre.get(0).get(2));
				 model.addAttribute("contributions",detailMembre.get(0).get(3));
				 model.addAttribute("solde",detailMembre.get(0).get(4));			    		
	    		
	    	}else {
	    		
	    		List<List<Object>> lst=memberRepo.getCapitalMatriculeNom(matricule);
	    		model.addAttribute("nom",lst.get(0).get(0));
				 model.addAttribute("matricule",lst.get(0).get(1));
				 model.addAttribute("capital",lst.get(0).get(2));
				 model.addAttribute("pageTitle","Authentification");
				 model.addAttribute("contributions",0.0);
				 model.addAttribute("solde",lst.get(0).get(2));
				 
	    	}
			 						
	    	     model.addAttribute("detteCourante",detteCourante);			    		
			     model.addAttribute("interet",interet);						     
			     model.addAttribute("pages", new int[0]);
			     model.addAttribute("pages2", new int[0]);
			     model.addAttribute("currentPage",0);
			     model.addAttribute("currentPage2",0);
			     model.addAttribute("currentSize",5);
			     modelAndView="home"; // resources/template/home.html

	     	 }
		 
	    }
		
		return modelAndView;
	   
	}
	
	
	
	@PostMapping("/loginAsAuthority")
	public String logAsAuthority(Authentication authentication,HttpServletRequest req) {
		
		String email="";
		String matricule="";
		Set<Role> currentRoles=null;
		
		User usr=null;
		String modelAndView = "";
		
		if(authentication !=null) {
			
			email= authentication.getName();
			usr= userDetailsService.getUserByEmail(email);
			matricule=usr.getMatricule();
			currentRoles=usr.getRoles();
			//model.addAttribute("user",usr);
			//model.addAttribute("user",usr);
			
			for (Role rol : currentRoles) {
				
				if  (rol.getRole_name().equals("ADMIN_USER") || rol.getRole_name().equals("SUPER_USER") ) {
									   					    
					 modelAndView="redirect:/dashboard"; 
				}
				
				
			}
		
		
		}
		

//			String fonction = memberRepo.getMemberByMatricule(matricule).getMemberUser().getFonction();
//			
//			if(fonction.equals("Financier")) {
//				
//				newUserEmail="finance@gmail.com";
//				newUserPassword=(String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
//				
//			}
//			if(fonction.equals("Gerant")) {
//				newUserEmail="gestion@gmail.com";
//				newUserPassword=(String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
//				
//			}
//			
//
//	  		//UserDetails user =userDetailsService.loadUserByUsername(newUserEmail); 
//		   
//		   
//	  		UsernamePasswordAuthenticationToken authReq=new UsernamePasswordAuthenticationToken(newUserEmail,newUserPassword);	     		
//	  		  		
//	  		Authentication auth=authenticationManager.authenticate(authReq);
//	  		SecurityContext sc=SecurityContextHolder.getContext();
//	  		sc.setAuthentication(auth);
//	  		HttpSession session=req.getSession(true);
//	  		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
//  		    
//			
//		}
	  	
		return modelAndView;	

	   
	}

}
