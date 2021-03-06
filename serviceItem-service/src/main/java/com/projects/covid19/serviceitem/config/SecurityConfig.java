package com.projects.covid19.serviceitem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.projects.covid19.serviceitem.components.CustomRestAccessDenied;
import com.projects.covid19.serviceitem.components.CustomRestAuthenticationEntryPoint;
import com.projects.covid19.serviceitem.filters.JwtAuthenticationFilter;

//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(
//        securedEnabled = true,
//        jsr250Enabled = true,
//        prePostEnabled = true
//)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	//@Autowired
	//private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private CustomRestAuthenticationEntryPoint customRestAuthenticationEntryPoint;
	
	@Autowired
	private CustomRestAccessDenied customRestAccessDenied;
	
	@Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
	
	/*@Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /*@Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }*/
    
    @Override
  	protected void configure(HttpSecurity httpSecurity) throws Exception {
    	   httpSecurity
		.csrf().disable()
		.exceptionHandling()
        .authenticationEntryPoint(customRestAuthenticationEntryPoint)
        .accessDeniedHandler(customRestAccessDenied)
        .and()
		    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
		.authorizeRequests()
			//.antMatchers(HttpMethod.GET, "/api/service/ping").permitAll()  
		   .anyRequest().authenticated(); 
    	   httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    	   
}

}
