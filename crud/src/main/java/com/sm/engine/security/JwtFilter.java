package com.sm.engine.security;

import com.sm.engine.config.JwtConfig;
import com.sm.engine.domain.User;
import com.sm.engine.service.UserService;
import com.sm.engine.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The jwt filter to extract jwt token from header and authentication with decoded user.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    /**
     * The role prefix
     */
    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * The injected user service.
     */
    @Autowired
    private UserService userService;

    /**
     * The injected jwt config
     */
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * The jwt service.
     */
    @Autowired
    private JwtService jwtService;

    /**
     * Extract jwt token from header and authentication with decoded user.
     *
     * @param req   the request.
     * @param res   the response.
     * @param chain the filter chain.
     * @throws IOException      throws if io error happens.
     * @throws ServletException throws if servlet error happens.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(jwtConfig.getAuthHeader());
        if (header == null || !header.startsWith(jwtConfig.getTokenPrefix())) {
            chain.doFilter(req, res);
            return;
        }
        String username = jwtService.decode(header.replace(jwtConfig.getTokenPrefix(), ""));
        User user = userService.findByUsername(username);
        Helper.checkUser(user, username);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole().toString()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }
}

