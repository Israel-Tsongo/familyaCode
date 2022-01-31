package org.sid.FamilyaProject.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Payement;
import org.sid.FamilyaProject.metier.Traitement;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.sf.jasperreports.engine.JRException;

@Controller
public class ProfileController {
	
	
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private DebiteurRepository debiteurRepo;
	
	@Autowired
	private InteretParMembreRepository interetRepo;
	
	@Autowired
	private PayementRepository payeRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	
	//************** ACCEUILLE************************
	
	@GetMapping(value="/profile")
	public String profiles(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> profilList =userRepo.getAllUsers(PageRequest.of(page,size));		
		
	   

	    model.addAttribute("lst",trt.converter(profilList));
		model.addAttribute("pages",new int[profilList.getTotalPages()]);
		model.addAttribute("currentPage",page);
		model.addAttribute("pageTitle","Profile");
		
		model.addAttribute("keyWord", mc);		
		return "userDetails";
	   
	}
	
	

	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping(path="/searchUser")
	public ModelAndView searchUserByMatricule(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
			
				Page <List<List<Object>>> userList =userRepo.getAllUsers(PageRequest.of(page,size));		

			   ModelAndView mv = new ModelAndView();		           
	           mv.addObject("lst", trt.converter(userList));
	           mv.addObject("pages", new int[userList.getTotalPages()]);	
	           mv.addObject("currentPage",page);
	            
	           mv.addObject("keyWord", mc);
			   mv.setViewName("/userDetails::mainContainerProfile");
			   
			   
	           return  mv;
		   }else {
			       Page <User> searchUserList =userRepo.findByNomContains(mc,PageRequest.of(page,size));
			       
			       ModelAndView mv = new ModelAndView("/userDetails::mainContainerProfile");		           
		             model.addAttribute("lst", trt.searchUserConverter(searchUserList));
		             model.addAttribute("pages", new int[searchUserList.getTotalPages()]);	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentSize",size);	
		             model.addAttribute("keyWord", mc);
		             
		
		             return  mv;
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@GetMapping("/addUser")
	public String addUser() {
		
		return "redirect:/register" ;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deleteUser")
	
	public ModelAndView deleteMember(@RequestParam() Long  idUser,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		 ModelAndView mv=null ;
		 
		  if (idUser>0) {
			  
			  if(idUser !=1) {
				  
			    userRepo.removeUserRoleById(idUser);
			    Long  MemberId=memberRepo.getMemberIdByProfilId(idUser).longValue();
			    if(MemberId!=null) {			    	
			    	 memberRepo.deleteById(MemberId);		    	
			    }
			    
			    	userRepo.removeUserById(idUser);
			   
		   
			  }else {
      	    	
      	    	System.out.println("Vous n ete pas autorise");
      	    }  
		         
		   
		  }else  { System.out.println("Error when deleting");}
		  
		  Page <List<List<Object>>> UserList =userRepo.getAllUsers(PageRequest.of(page,size));		
		     
          mv = new ModelAndView("/userDetails::mainContainerProfile");		   
          mv.addObject("lst", trt.converter(UserList));
          mv.addObject("pages", new int[UserList.getTotalPages()]);
          mv.addObject("currentPage",page);
          mv.setViewName("/userDetails::mainContainerProfile"); 
		
		return  mv;
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateUserProf")
	public ModelAndView updateUserProfile(@RequestParam() Long  idUser ,@RequestParam() Long  idRole ,@RequestParam() String nom,  @RequestParam() String matricule,			
            @RequestParam() String email,@RequestParam() String mobile,
            @RequestParam(defaultValue=" ") String password,@RequestParam(defaultValue=" ") String currentPassword, @RequestParam() Long role,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
		     Traitement trt=new Traitement();
		     ModelAndView mv=null ;
		           if(idUser>0) {
		        	   
		        	    if(idUser !=1) {
			             
					     userRepo.updateUser(idUser,idRole, nom, matricule,email , mobile, password.isBlank() ? currentPassword:passwordEncoder.encode(password), role);						   
					     memberRepo.updateName(matricule, nom);
		        	    }else {
		        	    	
		        	    	System.out.println("Vous n ete pas autorise");
		        	    }
			   
			  }else  { System.out.println("error when updating");}
		           
	           Page <List<List<Object>>> userList =userRepo.getAllUsers(PageRequest.of(page,size));		

				  mv = new ModelAndView("/userDetails::mainContainerProfile");								   
				  mv.addObject("lst", trt.converter(userList));
				  mv.addObject("pages", new int[userList.getTotalPages()]);
				  mv.addObject("currentPage",page);				  
	              mv.setViewName("/userDetails::mainContainerProfile");
		 
			return mv;
	
	}
	
	@PostMapping("/autreDetailsProfile")
	public ModelAndView detail(@RequestParam(required=true) Long  idUser) {		
		
		
		 User usr=userRepo.getUserById(idUser);		
		 ModelAndView modelAndView=null ;
		 modelAndView = new ModelAndView("/userDetails::detailsModalContent");
		 
		 if(usr!=null) {	    		
			    	
			 		modelAndView.addObject("userId",usr.getUser_id());
			 		modelAndView.addObject("userRole",usr.getRoles());
			    	modelAndView.addObject("matricule",usr.getMatricule());
			    	modelAndView.addObject("nom",usr.getNom());
			    	modelAndView.addObject("numero_fiche_adhesion",usr.getNumeroFiche());
					modelAndView.addObject("adresse",usr.getAdresse());
					modelAndView.addObject("genre",usr.getGenre());
					modelAndView.addObject("typePiece",usr.getTypePiece());
					modelAndView.addObject("numeroPiece",usr.getNumeroPiece());
					modelAndView.addObject("salaire",usr.getSalaire());
					modelAndView.addObject("retenu",usr.getRetenu());
					return modelAndView;
		 
		 }else {
			 
			 System.out.println("Hello else");
		 } 
		 
		 return modelAndView;
	
	
	}
	
	//************** UPDATE OTHER USER ************************
	
		@PostMapping("/updateOtherUserDetails")
		public ModelAndView updateUserOtherDetails(@RequestParam() Long  idUser ,@RequestParam() String genre,  @RequestParam() String typePiece,			
	            @RequestParam() String numeroPiece,@RequestParam() String salaire,
	            @RequestParam(defaultValue=" ") String retenu,@RequestParam(defaultValue=" ") String adresse,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
			     
			     Traitement trt=new Traitement();
			     ModelAndView mv=null ;
			     System.out.println("==========ytytyty");
			           if(idUser>0) {
			        	   
			        	    if(idUser !=0 ) {
				             
			        	    	userRepo.updateOthersDetailsUser(idUser, genre, typePiece,numeroPiece , salaire, retenu, adresse);						   
						     
			        	    }else {
			        	    	
			        	    	System.out.println("Vous n ete pas autorise");
			        	    }
				   
				  }else  { System.out.println("error when updating");}
			           
		           Page <List<List<Object>>> userList =userRepo.getAllUsers(PageRequest.of(page,size));		

					  mv = new ModelAndView("/userDetails::mainContainerProfile");								   
					  mv.addObject("lst", trt.converter(userList));
					  mv.addObject("pages", new int[userList.getTotalPages()]);
					  mv.addObject("currentPage",page);				  
		              mv.setViewName("/userDetails::mainContainerProfile");
			 
				return mv;
		
		}
		
		@GetMapping("profile/generatePDF/{keyWord}")
		public ResponseEntity<byte[]> generatePDF(Model model ,@PathVariable(name="keyWord") String mc) throws Exception, JRException  {
			
			 	   Traitement trt = new Traitement();
			 	   HashMap<String,Object> map = new HashMap<>();
			 	   String jasperFilePath="src/main/resources/Coffee.jrxml";
			 	   String fileName="profile";
			       List<User> searchUserList =userRepo.findByNomContains(!mc.equals("all")? mc:"");

			 	   map.put("nameFor", "Israel");			   
				   
				   //List<Payement> testList=payeRepo.findAll();

			       return  trt.generatePDF(searchUserList, jasperFilePath, map, fileName);
		   
		}
	
	
	

}
