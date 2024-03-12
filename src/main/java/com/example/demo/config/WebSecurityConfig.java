package com.example.demo.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.service.UserDetailsServiceImpl;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	   UserDetailsServiceImpl userDetailsService;
	 
	   @Bean
	   public BCryptPasswordEncoder passwordEncoder() {
	      BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	      return bCryptPasswordEncoder;
	   }
	 
	   @Autowired
	   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	 
	      // Setting Service to find User in the database.
	      // And Setting PassswordEncoder
	      auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	 
	   }
	 
	   @Override
	   protected void configure(HttpSecurity http) throws Exception {

           http.csrf(csrf -> csrf.disable());

           // Requires login with role ROLE_EMPLOYEE or ROLE_MANAGER.
           // If not, it will redirect to /admin/login.
           http.authorizeRequests(requests -> requests.antMatchers("/admin/orderList", "/admin/order", "/admin/accountInfo")//
                   .access("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER')"));

           // Pages only for MANAGER
           http.authorizeRequests(requests -> requests.antMatchers("/admin/product").access("hasRole('ROLE_MANAGER')"));

           // When user login, role XX.
           // But access to the page requires the YY role,
           // An AccessDeniedException will be thrown.
           http.authorizeRequests(withDefaults()).exceptionHandling(handling -> handling.accessDeniedPage("/403"));

           // Configuration for Login Form.
           http.authorizeRequests(withDefaults()).formLogin(login -> login//
     
                   //
                   .loginProcessingUrl("/j_spring_security_check") // Submit URL
                   .loginPage("/admin/login")//
                   .defaultSuccessUrl("/admin/accountInfo")//
                   .failureUrl("/admin/login?error=true")//
                   .usernameParameter("userName")//
                   .passwordParameter("password")).logout(logout -> logout.logoutUrl("/admin/logout").logoutSuccessUrl("/"));
	 
	   }
}
