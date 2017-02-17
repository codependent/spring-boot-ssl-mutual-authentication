package com.codependent.mutualauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.anyRequest().authenticated()
			.and()
				.x509()
					.subjectPrincipalRegex("CN=(.*?)(?:,|$)")
					.userDetailsService(userDetailsService());
	}
	
	@Bean
    public UserDetailsService userDetailsService() {
        return (username -> {
        	if (username.equals("codependent-client1") || username.equals("codependent-client2")) {
                return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
            }else{
            	return null;
            }
        });
    }
	
}
