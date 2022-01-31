package org.sid.FamilyaProject.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class MyUserDetails implements UserDetails  {
	
	

						@Autowired
						private UserRepository userRepository;
	
						private User user;
						
	
						public MyUserDetails() {
							super();
							// TODO Auto-generated constructor stub
						}

						public MyUserDetails(User user) {
							this.user=user;
						}
						
						@Override
						public Collection<? extends GrantedAuthority> getAuthorities() {
							Set<Role> roles=user.getRoles();
							List<SimpleGrantedAuthority> authorities = new ArrayList<>();
							
							for(Role role:roles) {
								authorities.add(new SimpleGrantedAuthority(role.getRole_name()));
							}
							
							return authorities;
						}
					
						@Override
						public String getPassword() {
							// TODO Auto-generated method stub
							return user.getPassword();
						}
					
						
						public String getEmail() {
							// TODO Auto-generated method stub
							return user.getEmail();
						}
						
						
						public String getPhoneNumber() {
							// TODO Auto-generated method stub
							return user.getMobile();
						}
					
						@Override
						public boolean isAccountNonExpired() {
							// TODO Auto-generated method stub
							return true;
						}
					
						@Override
						public boolean isAccountNonLocked() {
							// TODO Auto-generated method stub
							return true;
						}
					
						@Override
						public boolean isCredentialsNonExpired() {
							// TODO Auto-generated method stub
							return true;
						}
					
						@Override
						public boolean isEnabled() {
							// TODO Auto-generated method stub
							return user.isEnabled();
						}

						@Override
						public String getUsername() {
							
							return user.getEmail();
						}

                      
                      

}
