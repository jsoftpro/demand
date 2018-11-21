package pro.jsoft.spring.config;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.val;
import pro.jsoft.spring.security.TokenAuthEntryPoint;
import pro.jsoft.spring.security.TokenAuthenticationFilter;
import pro.jsoft.spring.security.TokenProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan(basePackages = "pro.jsoft.spring.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
	
    @Autowired
    public SecurityConfig(
    		AuthenticationManagerBuilder authenticationManagerBuilder, 
    		UserDetailsService userDetailsService,
            TokenProvider tokenProvider) {
    	super();
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
	}
    
    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder
                .userDetailsService(userDetailsService);
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		  http.csrf().disable();
		  http.addFilterBefore(newCorsFilter(), ChannelProcessingFilter.class);
		  http.headers().cacheControl();
		  http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		  http
		  	.logout().disable()
	      	.formLogin().disable()
	      	.httpBasic().disable()
		    .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/html/**", "/fonts/**", "/css/**", "/images/**", "/js/**").permitAll()
		    	.antMatchers("/api/**").fullyAuthenticated()
		    	.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
			    .anyRequest().authenticated()
		    .and()
		    .exceptionHandling().authenticationEntryPoint(securityException401EntryPoint())
		    .and()
		    .addFilterBefore(new TokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
	}
	
	private Filter newCorsFilter() {
		val source = new UrlBasedCorsConfigurationSource();
        val config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
	}
	
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/html/**")
            .antMatchers("/scripts/**")
            .antMatchers("/styles/**")
            .antMatchers("/images/**");
    }
    
    @Bean
    public TokenAuthEntryPoint securityException401EntryPoint(){
        return new TokenAuthEntryPoint();
    }
}
