package com.pearchCash.payments.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken  extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;

    private final Object principal;
    private String jwtToken;

    // Constructor for creating the token with only JWT (unauthenticated)
    public JwtAuthenticationToken(String jwtToken) {
        super(null);
        this.jwtToken = jwtToken;
        this.principal = null;
        setAuthenticated(false);  // This is an unauthenticated token
    }

    // Constructor for creating the token with principal and authorities (authenticated)
    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.jwtToken = null;
        setAuthenticated(true);  // This is an authenticated token
    }

    // This method should return credentials, but since we only use JWT, return null
    @Override
    public Object getCredentials() {
        return null;
    }

    // Return the principal (user details or username)
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    // Getter for JWT token (optional if you need it elsewhere)
    public String getJwtToken() {
        return jwtToken;
    }

}
