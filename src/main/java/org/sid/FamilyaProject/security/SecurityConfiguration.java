package org.sid.FamilyaProject.security;

import org.modelmapper.ModelMapper;
import org.sid.FamilyaProject.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Autowired
		private UserRepository userRepository;
		
		@Bean
		public ModelMapper modelMapper() {
			
			  return new ModelMapper();
		}
		
		@Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			
			  return super.authenticationManagerBean();
		}
	   
		@Bean
		public UserDetailsService userDetailsService(ModelMapper modelMapper) {
			return new UserDetailsServiceImpl(modelMapper);
		}
		
		
		@Bean
		public BCryptPasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		public DaoAuthenticationProvider authenticationProvider() {
			DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
			authProvider.setUserDetailsService(userDetailsService(modelMapper()));
			authProvider.setPasswordEncoder(passwordEncoder());
		
			return authProvider;
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.parentAuthenticationManager(authenticationManagerBean()).userDetailsService(userDetailsService(modelMapper()));
			auth.authenticationProvider(authenticationProvider());
			auth.eraseCredentials(false);
			
		}
		
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
						http.authorizeRequests()
						// URLs matching for access rights						
						//.antMatchers("/register").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/dashboard").hasAnyAuthority("SUPER_USER", "ADMIN_USER","SITE_USER")
						.antMatchers("/setPost").hasAnyAuthority("SUPER_USER")
						
						///======= MEMBRE=========/////
						.antMatchers("/index").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/indexSearcher").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/indexPost").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/deteteMember").hasAnyAuthority("SUPER_USER")
						.antMatchers("/update").hasAnyAuthority("SUPER_USER")
						.antMatchers("/detailMembre").hasAnyAuthority("SUPER_USER", "ADMIN_USER")						
						.antMatchers("/membre/generatePDF/").hasAnyAuthority("SUPER_USER")
						
						///======= CONTRIBUTION=========/////
						.antMatchers("/contribution").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/contribSearcher").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/contribPost").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/deteteContrib").hasAnyAuthority("SUPER_USER")
						.antMatchers("/updateContrib").hasAnyAuthority("SUPER_USER")
						.antMatchers("/contrib/generatePDF/").hasAnyAuthority("SUPER_USER")
						.antMatchers("/proofContrib/").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						
						
						
						///======= DEBITEUR =========/////
						.antMatchers("/debiteur").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/archive").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/debSearcher").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/debPost").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/deteteDeb").hasAnyAuthority("SUPER_USER")
						.antMatchers("/updateDeb").hasAnyAuthority("SUPER_USER")
						.antMatchers("/debiteur/generatePDF").hasAnyAuthority("SUPER_USER")
						
						
						
						///======= REMBOURSEMENT=========/////							
						.antMatchers("/rembourse").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/rembourseSearcher").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/remboursePost").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/deteteRembourse").hasAnyAuthority("SUPER_USER")
						.antMatchers("/updateRembourse").hasAnyAuthority("SUPER_USER")
						.antMatchers("/rembourse/generatePDF/").hasAnyAuthority("SUPER_USER")
						
						///======= INTERET=========/////
						.antMatchers("/interet").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/interetSearcher").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/withDraw").hasAnyAuthority("SUPER_USER")
						.antMatchers("/interet/generatePDF/").hasAnyAuthority("SUPER_USER")
						.antMatchers("/proof/").hasAnyAuthority("SUPER_USER")
						
						
						///======= DEPENSE =========/////
						.antMatchers("/depense").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/depSearcher").hasAnyAuthority("SUPER_USER","ADMIN_USER")						
						.antMatchers("/depPost").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/deteteDep").hasAnyAuthority("SUPER_USER")
						.antMatchers("/updateDep").hasAnyAuthority("SUPER_USER")
						.antMatchers("/depense/generatePDF/").hasAnyAuthority("SUPER_USER")
						
						
						///======= PROFIL USER =========/////
						.antMatchers("/profile").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/searchUser").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/deleteUser").hasAnyAuthority("SUPER_USER")
						.antMatchers("/updateUserProf").hasAnyAuthority("SUPER_USER")
						.antMatchers("/autreDetailsProfile").hasAnyAuthority("SUPER_USER","ADMIN_USER")
						.antMatchers("/profile/generatePDF/").hasAnyAuthority("SUPER_USER")
						///======= HOME FOR SITE USER=========/////
						.antMatchers("/home").hasAnyAuthority("SUPER_USER","ADMIN_USER","SITE_USER")
						.antMatchers("/loginAs").hasAnyAuthority("SUPER_USER","ADMIN_USER","SITE_USER")
						.antMatchers("/siteUserSearcher").hasAnyAuthority("SITE_USER")
						.antMatchers("/contribAndRembourse/generatePDF/").hasAnyAuthority("SITE_USER")
						.antMatchers("/remboursementsByMatricule").hasAnyAuthority("SITE_USER")
						
						
						.anyRequest().permitAll()
						.and()
						// form login
						.formLogin()
						.loginPage("/login").permitAll()
						.failureUrl("/login?error=true")
						.defaultSuccessUrl("/home")
						.usernameParameter("email")
						.passwordParameter("password")
						.and()
						// logout
						.logout()
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/login")
						.and().csrf().disable().cors();
						
//						.exceptionHandling()
//						.accessDeniedPage("/access-denied");
	
		}
		

		
		
		
	

}
