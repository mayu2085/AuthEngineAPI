package com.sm.engine.controller;

import com.sm.engine.domain.LoginRequest;
import com.sm.engine.domain.LoginResponse;
import com.sm.engine.domain.User;
import com.sm.engine.security.JwtService;
import com.sm.engine.service.LdapConfigurationService;
import com.sm.engine.service.LdapService;
import com.sm.engine.service.UserService;
import com.sm.engine.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * The login controller to provide login endpoint.
 */
@RestController
public class LoginController {

    /**
     * The ldap configuration service.
     */
    @Autowired
    private LdapConfigurationService ldapConfigurationService;

    /**
     * The user service.
     */
    @Autowired
    private UserService userService;

    /**
     * The jwt service.
     */
    @Autowired
    private JwtService jwtService;

    /**
     * This method is used to login through ldap server and return JWT token with user information.
     *
     * @return the JWT token with user info
     * @throws BadCredentialsException throws if username or password is wrong
     */
    @PostMapping(value = "/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) throws BadCredentialsException {
        String username = request.getUsername();
        LdapService ldapService = Helper.buildLdapService(ldapConfigurationService);
        if (!ldapService.authenticate(request.getUsername(), request.getPassword())) {
            throw new BadCredentialsException("Username or password is wrong!");
        }
        User user = userService.findByUsername(username);
        Helper.checkUser(user, username);
        LoginResponse result = new LoginResponse();
        result.setUser(user);
        result.setToken(jwtService.encode(user));
        return result;
    }
}
