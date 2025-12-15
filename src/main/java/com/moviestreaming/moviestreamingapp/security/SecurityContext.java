package com.moviestreaming.moviestreamingapp.security;

/**
 * Simple security context to hold current user information
 * In a real app, this would use Spring Security
 */
public class SecurityContext {
    
    private static final ThreadLocal<String> currentRole = new ThreadLocal<>();
    private static final ThreadLocal<String> currentAccountCode = new ThreadLocal<>();
    private static final ThreadLocal<String> currentProfileCode = new ThreadLocal<>();
    
    public static void setRole(String role) {
        currentRole.set(role);
    }
    
    public static String getRole() {
        return currentRole.get() != null ? currentRole.get() : "GUEST";
    }
    
    public static void setAccountCode(String accountCode) {
        currentAccountCode.set(accountCode);
    }
    
    public static String getAccountCode() {
        return currentAccountCode.get();
    }
    
    public static void setProfileCode(String profileCode) {
        currentProfileCode.set(profileCode);
    }
    
    public static String getProfileCode() {
        return currentProfileCode.get();
    }
    
    public static void clear() {
        currentRole.remove();
        currentAccountCode.remove();
        currentProfileCode.remove();
    }
    
    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getRole());
    }
    
    public static boolean isAccount() {
        return "ACCOUNT".equalsIgnoreCase(getRole()) || "USER".equalsIgnoreCase(getRole());
    }
    
    public static boolean isProfile() {
        return "PROFILE".equalsIgnoreCase(getRole());
    }
    
    public static boolean canAccessAccount(String accountCode) {
        return isAdmin() || (isAccount() && accountCode.equals(getAccountCode()));
    }
    
    public static boolean canAccessProfile(String profileCode) {
        // Admin can access any profile
        if (isAdmin()) return true;
        
        // Users (ACCOUNT/USER role) can access if profile code matches
        if (profileCode != null && profileCode.equals(getProfileCode())) {
            return true;
        }
        
        // Legacy: PROFILE role can also access
        if (isProfile() && profileCode.equals(getProfileCode())) {
            return true;
        }
        
        return false;
    }
}

