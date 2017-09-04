package com.sm.engine.security;

import com.sm.engine.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The web application security configuration.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * The authentication entry point.
     */
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * The access denied handler.
     */
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * The jwt filter.
     */
    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Configure the HTTP security.
     *
     * @param httpSecurity the HTTP security
     * @throws Exception if any error occurs
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.headers().cacheControl();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Authorization
        httpSecurity.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                // Admin
                .antMatchers("/users/**").hasRole(Role.Admin.toString()) //
                .antMatchers("/activity-logs/**").hasRole(Role.Admin.toString()) //
                .antMatchers("/ldap-configurations/**").hasRole(Role.Admin.toString()) //
                // Admin and Read/Write
                .antMatchers(HttpMethod.POST).hasAnyRole(Role.Admin.toString(), Role.RW.toString())
                .antMatchers(HttpMethod.PUT).hasAnyRole(Role.Admin.toString(), Role.RW.toString())
                .antMatchers(HttpMethod.DELETE).hasAnyRole(Role.Admin.toString(), Role.RW.toString())
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }
}
