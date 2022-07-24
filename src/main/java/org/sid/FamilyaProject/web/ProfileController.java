package org.sid.FamilyaProject.web;

import java.util.HashMap;
import java.util.List;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.UserRepository;
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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.sf.jasperreports.engine.JRException;

@Controller
public class ProfileController {
	
	
	
	@Autowired
	private MemberRepository memberRepo;
	
	
	@Autowired
	private UserRepository userRepo;
	
	
	//************** ACCEUILLE************************
	
	@GetMapping("/profile")
	public String profiles(Model model, @RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc) {
		
		Traitement trt = new Traitement();		
		 
		Page <List<List<Object>>> profilList =userRepo.getAllUsers(PageRequest.of(page,size));		
		
	   
	    model.addAttribute("lst",trt.converter(profilList));
		model.addAttribute("pages",new int[profilList.getTotalPages()]);
		model.addAttribute("currentPage",page);
		model.addAttribute("pageTitle","Profile");
		model.addAttribute("currentSize",size);		
		model.addAttribute("keyWord", mc);		
		return "userDetails";
	   
	}
	
	

	
	
	
	//************** RECHERCHER PAR NOM************************
	
	@PostMapping("/searchUser")
	public String searchUserByMatricule(Model model ,@RequestParam(name="pagination",defaultValue = "false") boolean pagin,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size,@RequestParam(name="keyWord", defaultValue = "") String mc)  {
		
		 Traitement trt = new Traitement();
		  
		   
		   if(pagin) {			   	
			
				Page <List<List<Object>>> userList =userRepo.getAllUsers(PageRequest.of(page,size));		

			   		           
	           model.addAttribute("lst", trt.converter(userList));
	           model.addAttribute("pages", new int[userList.getTotalPages()]);	
	           model.addAttribute("currentPage",page);
	           model.addAttribute("currentSize",size);
	           model.addAttribute("keyWord", mc);   
			   return  "userDetails::mainContainerProfile";
		   }else {
			       Page <User> searchUserList =userRepo.findByNomContains(mc,PageRequest.of(page,size));
			       	           
		             model.addAttribute("lst", trt.searchUserConverter(searchUserList));
		             model.addAttribute("pages", new int[searchUserList.getTotalPages()]);	
		             model.addAttribute("currentPage",page);
		             model.addAttribute("currentSize",size);	
		             model.addAttribute("keyWord", mc);	             
		
		             return  "userDetails::mainContainerProfile";
		   
		   }
	}
	
	
	//************** ENREGISTRER************************
	
	
	@GetMapping("/addUser")
	public String addUser() {
		
		return "redirect:/register" ;
	}
	
	
	//************** EFFACER ************************
	
	
	@PostMapping("/deleteUser")
	
	public String deleteMember(Model model, @RequestParam() Long  idUser,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size )  {	
		 Traitement trt=new Traitement();
		 
		 
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
		     
          		   
          model.addAttribute("lst", trt.converter(UserList));
          model.addAttribute("pages", new int[UserList.getTotalPages()]);
          model.addAttribute("currentSize",size);
          model.addAttribute("currentPage",page);
          
		
		return  "userDetails::mainContainerProfile";
	}
	
	
	
	//************** UPDATE ************************
	
	@PostMapping("/updateUserProf")
	public String updateUserProfile(Model model, @RequestParam() Long  idUser ,@RequestParam() Long  idRole ,@RequestParam() String nom,  @RequestParam() String matricule,			
            @RequestParam() String email,@RequestParam() String mobile,
            @RequestParam(defaultValue=" ") String password,@RequestParam(defaultValue=" ") String currentPassword, @RequestParam() Long role,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
		     BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
		     Traitement trt=new Traitement();
		    
		           if(idUser>0) {
		        	   
		        	    if(idUser !=1) {
			             
					     userRepo.updateUser(idUser,idRole, nom, matricule,email , mobile, password.isBlank() ? currentPassword:passwordEncoder.encode(password), role);						   
					     memberRepo.updateName(matricule, nom);
		        	    }else {
		        	    	
		        	    	System.out.println("Vous n ete pas autorise");
		        	    }
			   
			  }else  { System.out.println("error when updating");}
		           
	           Page <List<List<Object>>> userList =userRepo.getAllUsers(PageRequest.of(page,size));		
				 								   
				  model.addAttribute("lst", trt.converter(userList));
				  model.addAttribute("pages", new int[userList.getTotalPages()]);
				  model.addAttribute("currentSize",size);
				  model.addAttribute("currentPage",page);			  
	           
			      return "userDetails::mainContainerProfile";
	
	}
	
	@PostMapping("/autreDetailsProfile")
	public String detail(Model model, @RequestParam(required=true) Long  idUser) {		
		
		
		 User usr=userRepo.getUserById(idUser);	
		 
		 String modelAndView ="userDetails::detailsModalContent";
		 
		 if(usr!=null) {	    		
			    	
			 		model.addAttribute("userId",usr.getUser_id());
			 		model.addAttribute("userRole",usr.getRoles());
			    	model.addAttribute("matricule",usr.getMatricule());
			    	model.addAttribute("nom",usr.getNom());
			    	model.addAttribute("numero_fiche_adhesion",usr.getNumeroFiche());
					model.addAttribute("adresse",usr.getAdresse());
					model.addAttribute("genre",usr.getGenre());
					model.addAttribute("typePiece",usr.getTypePiece());
					model.addAttribute("numeroPiece",usr.getNumeroPiece());
					model.addAttribute("salaire",usr.getSalaire());
					model.addAttribute("retenu",usr.getRetenu());
					return modelAndView;
		 
		 }else {
			 
			 System.out.println("Hello else");
		 } 
		 
		 return modelAndView;
	
	
	}
	
	//************** UPDATE OTHER USER ************************
	
		@PostMapping("/updateOtherUserDetails")
		public String updateUserOtherDetails(Model model, @RequestParam() Long  idUser ,@RequestParam() String genre,  @RequestParam() String typePiece,			
	            @RequestParam() String numeroPiece,@RequestParam() String salaire,
	            @RequestParam(defaultValue=" ") String retenu,@RequestParam(defaultValue=" ") String adresse,@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "5") int size   )  {	
			     
			     Traitement trt=new Traitement();
			     
			           if(idUser>0) {
			        	   
			        	    if(idUser !=0 ) {
				             
			        	    	userRepo.updateOthersDetailsUser(idUser, genre, typePiece,numeroPiece , salaire, retenu, adresse);						   
						     
			        	    }else {
			        	    	
			        	    	System.out.println("Vous n ete pas autorise");
			        	    }
				   
				  }else  { System.out.println("error when updating");}
			           
		           Page <List<List<Object>>> userList =userRepo.getAllUsers(PageRequest.of(page,size));		

					  								   
					  model.addAttribute("lst", trt.converter(userList));
					  model.addAttribute("pages", new int[userList.getTotalPages()]);
					  model.addAttribute("currentSize",size);
					  model.addAttribute("currentPage",page);				  
		              
			 
				return "userDetails::mainContainerProfile";
		
		}
		
		@PostMapping(value="/profile/generatePDF/",produces="application/pdf")
		public ResponseEntity<byte[]> generatePDF(Model model ,@RequestParam(name="keyWord") String mc) throws Exception, JRException  {
			
			 	   Traitement trt = new Traitement();
			 	   HashMap<String,Object> map = new HashMap<>();
			 	   String jasperFileName="Coffee.jrxml";
			 	   String fileName="profile";
			       List<User> searchUserList =userRepo.findByNomContains(!mc.equals("all")? mc:"");

			 	   map.put("nameFor", "Israel");		   
				   
			       return  trt.generatePDF(searchUserList, jasperFileName, map, fileName);
		   
		}
	
	
	

}
