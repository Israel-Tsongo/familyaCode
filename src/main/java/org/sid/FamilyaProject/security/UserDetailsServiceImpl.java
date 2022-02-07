package org.sid.FamilyaProject.security;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.sid.FamilyaProject.dao.RoleRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service 
public class UserDetailsServiceImpl implements UserDetailsService  {
	
	              
	
					@Autowired
					private UserRepository userRepository;
					
				
					@Autowired
					private BCryptPasswordEncoder encoder;					
					
					@Autowired
					private RoleRepository roleRepository;
					
					 private ModelMapper modelMapper;
					
										 
					
					
					public UserDetailsServiceImpl(ModelMapper modelMapper) {
						  super();
						  this.modelMapper=modelMapper;
						  
						
					} 
					
					
					@Override
					public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
						User user =userRepository.getUserByEmail(email);
						if(user==null) {
							throw new UsernameNotFoundException("Could not find User Please");
						}
						System.out.println("-------User-------"+user.getMatricule());
						return new MyUserDetails(user);
					}
					
					public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
						User user =userRepository.getUserByEmail(email);
						if(user==null) {
							throw new UsernameNotFoundException("Could not find User Please");
						}
						return new MyUserDetails(user);
					}
					
					
					public void saveUser(User user) {
						user.setPassword(encoder.encode(user.getPassword()));
						user.setEnabled(true);
						user.setMatricule(getMatricule());
						Role userRole = roleRepository.findByRoleName("SITE_USER");
						user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
						modelMapper.map(user,user);
						userRepository.save(user);
						
						
					}
					
					public String getMatricule() {
						//UserRepository userRepositorys
						Long value=0L;
					   String matricule="";
					
					   System.out.println("all Auth user  "+userRepository.findAllAuthUser());
					   
					  if(!(userRepository.findAllAuthUser().isEmpty())) {
							List<Long>lst=userRepository.getLatestId();
							value=lst.get(lst.size()-1);
							
							for(Long l : lst) { System.out.println("id  "+l); }
							
							System.out.println("last id  "+value);
						
						} 
							
						Long V=value+1;
						System.out.println("Matros  "+V);
						Date d=new Date();
						matricule=V.toString()+d.toString().substring(26, 28);
						System.out.println("Matros  "+matricule);
				   
						return matricule;
						
					}
					
					public Optional <User> findUserByEmail(String email){
						return userRepository.findUserByEmail(email);
						
						
					}
					public User getUserByEmail(String email) {
						return userRepository.getUserByEmail(email);
					}
					public boolean userExists(String email) {
						    return findUserByEmail(email).isPresent();
						
					}
					
					
					

}
