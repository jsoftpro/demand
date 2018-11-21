package pro.jsoft.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import pro.jsoft.spring.security.BasicAuthEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@ComponentScan(basePackages = {"pro.jsoft.spring.security"})
public class SecurityDemoConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private BasicAuthEntryPoint authenticationEntryPoint;
	
	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		// For demo only. Needs to be replaced with Kerberos
		auth.inMemoryAuthentication()
			.passwordEncoder(passwordEncoder())
			.withUser("admin").password(passwordEncoder().encode("nimda")).roles("ADMIN")
			.and()
			.withUser("korchagin-vu@co.rosenergoatom.ru").password(passwordEncoder().encode("123")).roles("USER")
			.and()
			.withUser("pereslegin-va@co.rosenergoatom.ru").password(passwordEncoder().encode("123")).roles("USER")
			.and()
			.withUser("orlova-da@co.rosenergoatom.ru").password(passwordEncoder().encode("123")).roles("USER")
			.and()
			.withUser("rozenberg-bi@co.rosenergoatom.ru").password(passwordEncoder().encode("123")).roles("USER")
			.and()
			.withUser("poznyakova-nn@co.rosenergoatom.ru").password(passwordEncoder().encode("123")).roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		  	.and().headers().cacheControl();
		
		http.csrf().disable()
	  		.logout().disable()
      		.formLogin().disable()
      		.httpBasic()
      		.and()
      			.authorizeRequests()
      			.anyRequest().authenticated()
      		.and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}
		
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
	public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
}
