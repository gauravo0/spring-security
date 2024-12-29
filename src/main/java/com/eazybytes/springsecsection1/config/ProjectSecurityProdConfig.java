package com.eazybytes.springsecsection1.config;

import com.eazybytes.springsecsection1.exceptionhandling.CustomAccessDeniedHandler;
import com.eazybytes.springsecsection1.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("prod")
public class ProjectSecurityProdConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(smc->smc.invalidSessionUrl("/invalidSession")
                        .maximumSessions(1).maxSessionsPreventsLogin(true))
        .requiresChannel(rcc-> rcc.anyRequest().requiresSecure());
        //http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());
        http.csrf(CsrfConfigurer -> CsrfConfigurer.disable()).
                authorizeHttpRequests((requests) -> requests.
                requestMatchers("/myAccount","/myBalance","/myLoans","/myCards").authenticated().
                requestMatchers("/notices","/contact","/error","/register","/invalidSession").permitAll());
        http.formLogin(withDefaults());
        http.httpBasic(hbc->hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        //http.exceptionHandling(ehc->ehc.
          //      authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));

        http.exceptionHandling(ehc->ehc.
                accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

   /* @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);

    }*/

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //@Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
