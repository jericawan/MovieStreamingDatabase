package com.moviestreaming.moviestreamingapp.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts all requests and extracts authentication information from headers
 * In production, this would validate JWT tokens or session cookies
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Extract role from header (for demo purposes)
        String role = request.getHeader("X-User-Role");
        String accountCode = request.getHeader("X-Account-Code");
        String profileCode = request.getHeader("X-Profile-Code");
        
        // Set in security context
        if (role != null) {
            SecurityContext.setRole(role);
        }
        if (accountCode != null) {
            SecurityContext.setAccountCode(accountCode);
        }
        if (profileCode != null) {
            SecurityContext.setProfileCode(profileCode);
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // Clean up after request
        SecurityContext.clear();
    }
}

