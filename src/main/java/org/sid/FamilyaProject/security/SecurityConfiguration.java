package org.sid.FamilyaProject.security;

import org.modelmapper.ModelMapper;
import org.sid.FamilyaProject.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Autowired
		private UserRepository userRepository;
		
		@Bean
		public ModelMapper modelMapper() {
			
			  return new ModelMapper();
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
			auth.authenticationProvider(authenticationProvider());
			
		}
		
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
						http.authorizeRequests()
						// URLs matching for access rights						
						.antMatchers("/register").permitAll()
						.antMatchers("/dashboard").hasAnyAuthority("SUPER_USER", "ADMIN_USER")	
						.antMatchers("/index").hasAnyAuthority("SUPER_USER", "ADMIN_USER")						
						.antMatchers("/contribution").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/debiteur").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/rembourse").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/interet").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/depense").hasAnyAuthority("SUPER_USER", "ADMIN_USER")
						.antMatchers("/userDetails").hasAnyAuthority("SUPER_USER","ADMIN_USER")						
						.antMatchers("/home").hasAnyAuthority("SITE_USER","ADMIN_USER","SUPER_USER")
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
