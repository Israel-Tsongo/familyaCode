package org.sid.FamilyaProject.web;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;

import org.sid.FamilyaProject.security.UserDetailsServiceImpl;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class AuthentificationController {
	
	   
	    @Autowired
		private UserDetailsServiceImpl userDetailsService ; 
	   
	    @Autowired
		private MemberRepository memberRepo;
	    
	    @Autowired
		private DebiteurRepository debiteurRepo;
		
		@Autowired
		private InteretParMembreRepository interetRepo;
		
		@Autowired
		private PayementRepository payeRepo;
		
		
	    
	
	  // private static final Logger log = LoggerFactory.getLogger(AuthentificationController.class)
	
	
        @InitBinder        
        public void initBinder(WebDataBinder dataBinder) {
        	
        	StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        	dataBinder.registerCustomEditor(String.class,stringTrimmerEditor);
        	
        }
        
        
		@GetMapping("/login" )
		public String login() {
			
			return "signin";
		}
		
		
		@GetMapping(value="/register")
		public ModelAndView register() {
			ModelAndView modelAndView = new ModelAndView();
			
			 User user = new User();
			 modelAndView.addObject("user", user);
			 modelAndView.addObject("pageTitle","Authentification");
			
			modelAndView.setViewName("signup"); // resources/template/login.html
			
			return modelAndView;
		}
		
		@PostMapping(value = "/register")
		public ModelAndView registerSave(@Valid User user, BindingResult bindingResult ,ModelMap  modelMap , @RequestParam(name="repeatPassword",defaultValue=" ") String repeatPassword) {
			
			
			
			ModelAndView modelAndView = new ModelAndView();
			//Check for the validation errors
			if(bindingResult.hasErrors()) {		
			    
				
				
				modelMap.addAttribute("bindingResult",bindingResult);
				modelAndView.addObject("pageTitle","Authentification");
				modelAndView.setViewName("signup");
				
				return modelAndView;
				
			// Check if the user exist
				
			}else if(userDetailsService.userExists(user.getEmail())) {
				//System.out.println("in exist");
				
				bindingResult.addError(new FieldError("user","email","Email address already in use"));
				
				modelAndView.addObject("successMessage","user already exists !");
				modelAndView.addObject("pageTitle","Authentification");
				modelAndView.setViewName("signup");
				
				return modelAndView;
				
			}else {
				   //check if the password match
				   if(user.getPassword() != null && repeatPassword != null) {
				
						    if(!(user.getPassword().equals(repeatPassword))) {
						    	
								bindingResult.addError(new FieldError("user","password","Password must match"));
								modelAndView.addObject("pageTitle","Authentification");
								modelAndView.setViewName("signup");
								
								return modelAndView;
								
						    }else {
						    	
						    	//System.out.println("in Reapetpasword");
						    	userDetailsService.saveUser(user);
								
								modelAndView.addObject("successMessage","User is registered successfully");
								modelAndView.setViewName("signin");	
								
						    }
				    
				   }
				
					
				
			}
			
					
			
			return modelAndView;
		}
		
		
		
		@RequestMapping(value = "/home", method = RequestMethod.GET)
		public ModelAndView home(Model model, Authentication authentication) {
			
			String email="";
			String matricule="";
			Set<Role> currentRoles=null;
			String role="";
			User usr=null;
			ModelAndView modelAndView = new ModelAndView();
			
			
			
			if(authentication !=null) {
				
				email= authentication.getName();
				usr= userDetailsService.getUserByEmail(email);
				matricule=usr.getMatricule();
				currentRoles=usr.getRoles();				
				modelAndView.addObject("user",usr);
				
				for (Role rol : currentRoles) {
					
					if  (rol.getRole_name().equals("ADMIN_USER") || rol.getRole_name().equals("SUPER_USER") ) {
						
					    role="ADMIN_USER";					    
						modelAndView.setViewName("redirect:/dashboard"); 
					}
				}
				
			}
			
			
				if(memberRepo.getUserByMatricule(matricule)!=null && !(role.equals("ADMIN_USER"))) {
							System.out.println("-------matric-----");
				    	List<List<Object>> detailMembre=payeRepo.getDetails(matricule);
				    	Double detteCourante = debiteurRepo.detteCouranteByMatricule(matricule)  !=null ? debiteurRepo.detteCouranteByMatricule(matricule): 0.0;
				    	Double interet = interetRepo.interetDuMembreByMatricule(matricule)  !=null ? interetRepo.interetDuMembreByMatricule(matricule) : 0.0;
				    	
				    	if(!(payeRepo.getPayementByMatric(matricule).isEmpty())) {
				    		
				    		modelAndView.addObject("nom",detailMembre.get(0).get(0));
							 modelAndView.addObject("matricule",detailMembre.get(0).get(1));
							 modelAndView.addObject("capital",detailMembre.get(0).get(2));
							 modelAndView.addObject("contributions",detailMembre.get(0).get(3));
							 modelAndView.addObject("solde",detailMembre.get(0).get(4));			    		
				    		
				    	}else {
				    		
				    		List<List<Object>> lst=memberRepo.getCapitalMatriculeNom(matricule);
				    		modelAndView.addObject("nom",lst.get(0).get(0));
							 modelAndView.addObject("matricule",lst.get(0).get(1));
							 modelAndView.addObject("capital",lst.get(0).get(2));
							 modelAndView.addObject("pageTitle","Authentification");
							 modelAndView.addObject("contributions",0.0);
							 modelAndView.addObject("solde",lst.get(0).get(2));
							 
				    	}
						 						
				    	     modelAndView.addObject("detteCourante",detteCourante);			    		
						     modelAndView.addObject("interet",interet);
						
						    modelAndView.setViewName("home"); // resources/template/home.html
			
		     	}else if(memberRepo.getUserByMatricule(matricule)==null && !(role.equals("ADMIN_USER")))  {
		     		
		     		    modelAndView.addObject("usr",usr);
			        	modelAndView.setViewName("homeEmpty");
		
			     }
				
				
				
			return modelAndView;
		}
	      
	
	
	

}
