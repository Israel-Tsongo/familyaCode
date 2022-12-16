package org.sid.FamilyaProject.web;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Events;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Payement;
import org.sid.FamilyaProject.metier.Traitement;
import org.sid.FamilyaProject.security.UserDetailsServiceImpl;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.sf.jasperreports.engine.JRException;



@Controller
public class AuthentificationController {
	
	   
	    @Autowired
		private UserDetailsServiceImpl userDetailsService ; 
	   
	      
	   
		
		@Autowired
		private PayementRepository payeRepo;
		
		
		
		@Autowired
		private EventsRepository eventRepo;
		
		@Autowired
		private UserRepository userRepo;
		
	
        @InitBinder        
        public void initBinder(WebDataBinder dataBinder) {
        	
        	StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        	dataBinder.registerCustomEditor(String.class,stringTrimmerEditor);
        	
        }
        
        
		@GetMapping("/login" )
		public String login() {
			
			return "signin";
		}
		
		
		@GetMapping("/register")
		public ModelAndView register(Model model) {
			ModelAndView modelAndView = new ModelAndView();
			
			 User user = new User();
			 model.addAttribute("user", user);
			 model.addAttribute("pageTitle","Authentification");
			
			modelAndView.setViewName("signup"); // resources/template/login.html
			
			return modelAndView;
		}
		
		@PostMapping("/register")
		public String registerSave(Model model, @Valid  User user, BindingResult bindingResult ,ModelMap  modelMap , @RequestParam(name="repeatPassword",defaultValue=" ") String repeatPassword) {
			
			String modelAndView = "";
			//Check for the validation errors
			if(bindingResult.hasErrors()) {			    
				
				modelMap.addAttribute("bindingResult",bindingResult);
				model.addAttribute("pageTitle","Authentification");
				modelAndView="signup";
				
				return modelAndView;
				
			// Check if the user exist
				
			}else if(userDetailsService.userExists(user.getEmail())) {
				//System.out.println("in exist");
				
				bindingResult.addError(new FieldError("user","email","Email address already in use"));
				
				model.addAttribute("successMessage","user already exists !");
				model.addAttribute("pageTitle","Authentification");
				modelAndView="signup";
				
				return modelAndView;
				
			}else {
				   //check if the password match
				   if(user.getPassword() != null && repeatPassword != null) {
				
						    if(!(user.getPassword().equals(repeatPassword))) {
						    	
								bindingResult.addError(new FieldError("user","password","Password must match"));
								model.addAttribute("pageTitle","Authentification");
								modelAndView="signup";
								
								return modelAndView;
								
						    }else {
						    	
						    	//System.out.println("in Reapetpasword");
						    	userDetailsService.saveUser(user);								
								model.addAttribute("successMessage","User is registered successfully");								
								modelAndView="signin";
								
						    }
				    
				   }
				
					
				
			}
			
					
			
			return modelAndView;
		}
		
		
		
		@RequestMapping(value = "/home", method = RequestMethod.GET)
		public String home(Model model, Authentication authentication) {
			
			String email=" ";
			String matricule="";
			Set<Role> currentRoles=null;
			String role="";
			User usr=null;
			String modelAndView = "";
			
			System.out.println("1<====ROLES====>");
			
			if(authentication !=null) {
				
				email= authentication.getName();
				usr= userRepo.getUserByEmail(email);
				matricule=usr.getMatricule();
				currentRoles=usr.getRoles();
				List <String> rolesList=currentRoles.stream().map(s -> s.getRole_name()).collect(Collectors.toList());
											
			
				if(rolesList.contains("SUPER_USER") && userRepo.getUserByMatricule(matricule).getFonction().equals("President") ) {
					
					model.addAttribute("fonctionType","Se logger comme President");
					
					modelAndView="loginAs"; 
					
		        	return modelAndView;					
						 			
				}else if(rolesList.contains("ADMIN_USER") && userRepo.getUserByMatricule(matricule).getFonction().equals("Gerant") ) {
					
					model.addAttribute("fonctionType","Se logger comme Gerant");
					
					modelAndView="loginAs";					
					
		        	return modelAndView;					
						 
				} else if(rolesList.contains("ADMIN_USER") && userRepo.getUserByMatricule(matricule).getFonction().equals("Financier")) {
					
					model.addAttribute("fonctionType","Se logger comme Financier");
					
					modelAndView="loginAs"; 
					
		        	return modelAndView;
		        	
				}else if(rolesList.contains("SITE_USER") && userRepo.getUserByMatricule(matricule).getFonction().equals("Membre")) {
					
					modelAndView="redirect:/loginAsMember";
				}					
						
			}			
								
			     return modelAndView;
		}
	      
		//************** RECHERCHER PAR NOM************************
		
		@PostMapping("/siteUserSearcher")
		public String searchByMatriculeInsiteUser( Authentication authentication, Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
			
				 Traitement trt = new Traitement();
				 String email="";
				 String matricule="";
				 User usr=null;			   
				 email= authentication.getName();
				 usr= userDetailsService.getUserByEmail(email);
				 matricule=usr.getMatricule();
				 
			   if(mc!=null && !mc.isEmpty()) {			   	
					
				   
				         Page <Payement> searchContribList =payeRepo.findByDatePayementAndMatriculeContains(matricule,mc,PageRequest.of(page,size));
				         double totalContribution=payeRepo.getSommeContribByMaticule(matricule) !=null?payeRepo.getSommeContribByMaticule(matricule) : 0.00 ;				       	
			             model.addAttribute("lst", trt.searchConverterPaye(searchContribList));
			             model.addAttribute("pages", new int[searchContribList.getTotalPages()]);	
			             model.addAttribute("pages2", new int[0]);
			             model.addAttribute("currentPage",page);
			             model.addAttribute("currentPage2",0);
			             model.addAttribute("currentSize",size);	
			             model.addAttribute("pageTitle","user_site_contribution");
			             model.addAttribute("totalContribution", String.format("%.3f",totalContribution));  
			             model.addAttribute("keyWord", mc);			
			             return "home::userSiteContainer";
			             
				  
			   }else {
			               
				   		   
						   Page <Payement> siteUserList = payeRepo.getPayementByMatric(matricule,PageRequest.of(page,size));
					       double totalContribution=payeRepo.getSommeContribByMaticule(matricule) !=null?payeRepo.getSommeContribByMaticule(matricule) : 0.00 ;
					       						   
				           model.addAttribute("lst", trt.searchConverterPaye(siteUserList));
				           model.addAttribute("pages", new int[siteUserList.getTotalPages()]);
				           model.addAttribute("pages2", new int[0]);
				           model.addAttribute("pageTitle","Contribution");
				           model.addAttribute("currentPage",page);
				           model.addAttribute("currentPage2",0);
				           model.addAttribute("currentSize",size);
				           model.addAttribute("totalContribution",String.format("%.3f", totalContribution));  
				           model.addAttribute("keyWord", mc);					   
						   
						   return "home::userSiteContainer";
				   
				
			   }
		}

		
		
		@PostMapping("/remboursementsByMatricule")
		public String remboursementsByMatricule( Authentication authentication, Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
			
				 Traitement trt = new Traitement();
				 String email="";
				 String matricule="";
				 User usr=null;			   
				 email= authentication.getName();
				 usr= userRepo.getUserByEmail(email);
				 matricule=usr.getMatricule();
				 
				 
				 if(mc!=null && !mc.isEmpty()) {	   	
					 
					         Page <Events> searchRembourseList =eventRepo.findByDateRemboursementsContains(matricule,mc,PageRequest.of(page,size));
					         
				             model.addAttribute("lstR", trt.searchRembourseConverter(searchRembourseList));
				             model.addAttribute("pages2", new int[searchRembourseList.getTotalPages()]);
				             model.addAttribute("pages", new int[0]);
				             model.addAttribute("currentPage",0);
				             model.addAttribute("currentPage2",page);
				             model.addAttribute("currentSize",size);
				             model.addAttribute("keyWord", mc);	
				             
				             return "home::userSiteContainer";
				             
					  
				   }else {
					          
					           
							   Page <Events> rembourseUserList = eventRepo.getEventsByMatricule(matricule,PageRequest.of(page,size));
						       						   
					           model.addAttribute("lstR", trt.searchRembourseConverter(rembourseUserList));
					           model.addAttribute("pages2", new int[rembourseUserList.getTotalPages()]);
					           model.addAttribute("pages", new int[0]);
					           model.addAttribute("currentPage",0);
					           model.addAttribute("currentPage2",page);
					           model.addAttribute("currentSize",size);					            
					           model.addAttribute("keyWord", mc);							   
							   
							   return "home::userSiteContainer";
					   
					
				   }
				 
				 
		}
		
		
		@PostMapping(value="/contribAndRembourse/generatePDF/",produces="application/pdf")
		public ResponseEntity<byte[]> generatePDF(Authentication authentication,Model model, @RequestParam(name="currentTable") String currentTable,@RequestParam(name="keyWord", defaultValue ="") String keyWord) throws Exception, JRException  {
					
					
					String email="";
					String matricule="";
					User usr=null;			   
					email= authentication.getName();
					usr= userRepo.getUserByEmail(email);
					matricule=usr.getMatricule();
					String dateValue=keyWord==null?"":keyWord;
			 	   Traitement trt = new Traitement();
			 	   HashMap<String,Object> map = new HashMap<>();
			 	   String jasperFileName="";
			 	   String fileName="";
			 	 
			 	   if(currentTable.equals("Remboursements")) {
			 		     
						 List<Events>  remboursementList =eventRepo.findByDateRemboursementsContainsList(matricule,dateValue);

					     fileName="remboursements";
					     jasperFileName="remboursement.jrxml";
					 	 map.put("nameFor", "Israel");				 	 
					 	 return  trt.generatePDF(remboursementList, jasperFileName, map, fileName);
			 	   }
			 	
			 	   
			 	   List <Payement> searchContribList = payeRepo.findByDatePayementAndMatriculeContains(matricule,dateValue);
				   jasperFileName="contribution.jrxml";
			 	   fileName="contributions";
			 	   map.put("nameFor", "Israel");			 	 
				   return  trt.generatePDF(searchContribList, jasperFileName, map, fileName);
		   
		}
		
		
		
}
