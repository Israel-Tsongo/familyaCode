package org.sid.FamilyaProject.web;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.security.UserDetailsServiceImpl;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginAsController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService ;
	
	

	
	@PostMapping(path="/loginAsMember")
	public String logAsMember(Model model,RedirectAttributes rd ) {
		
		rd.addFlashAttribute("typeOnlogin","Member");
		
		System.out.println("=====> loginAsMember ");
		
		return "redirect:/home";
	   
	}
	
	
	
	@PostMapping(path="/loginAsAuthority")
	public String logAsAuthority(Authentication authentication,HttpServletRequest req) {
		String email="";
		String matricule="";
		String newUserEmail="";
		String newUserPassword="";		
		User usr=null;
		
		if(authentication !=null) {
			
			email= authentication.getName();
			usr= userDetailsService.getUserByEmail(email);
			matricule=usr.getMatricule();
		
			String fonction = memberRepo.getUserByMatricule(matricule).getFonction();
			
			if(fonction.equals("Financier")) {
				
				newUserEmail="finance@gmail.com";
				newUserPassword=(String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
				
			}
			if(fonction.equals("Gerant")) {
				newUserEmail="gestion@gmail.com";
				newUserPassword=(String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
				
			}
			

	  		//UserDetails user =userDetailsService.loadUserByUsername(newUserEmail); 
		   
		   
	  		UsernamePasswordAuthenticationToken authReq=new UsernamePasswordAuthenticationToken(newUserEmail,newUserPassword);	     		
	  		  		
	  		Authentication auth=authenticationManager.authenticate(authReq);
	  		SecurityContext sc=SecurityContextHolder.getContext();
	  		sc.setAuthentication(auth);
	  		HttpSession session=req.getSession(true);
	  		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
  		    
			
		}
	  	
		return "redirect:/dashboard";	

	   
	}

}
