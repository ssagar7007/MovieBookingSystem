package com.sagar.MovieBookingSystem.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Extracts the username from JWT token
     */
    public String extractUsername(String jwtToken){
        return extractClaims(jwtToken, Claims::getSubject);
    }

    /**
     * Extracts the user ID from JWT token claims
     */
    public Long extractUserId(String jwtToken) {
        return extractClaims(jwtToken, claims -> {
            Object userId = claims.get("userId");
            if (userId instanceof Long) {
                return (Long) userId;
            } else if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId != null) {
                return Long.parseLong(userId.toString());
            }
            return null;
        });
    }

    /**
     * Extracts the user roles from JWT token claims
     */
    @SuppressWarnings("unchecked")
    public Collection<String> extractRoles(String jwtToken) {
        return extractClaims(jwtToken, claims -> {
            Object roles = claims.get("roles");
            if (roles instanceof Collection) {
                return (Collection<String>) roles;
            }
            return java.util.Collections.emptyList();
        });
    }

    /**
     * Generic method to extract any claim from JWT token
     */
    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(jwtToken);
        return claimResolver.apply(claims);
    }

    /**
     * Extracts all claims from JWT token
     */
    private Claims extractAllClaims(String jwtToken){
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    /**
     * Gets the signing key
     */
    public SecretKey getSignInKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generates a token with basic claims (username)
     */
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a token with custom claims including userId and roles
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        // Add userId and roles to claims
        Map<String, Object> claims = new HashMap<>(extraClaims);
        
        // Extract user ID from UserDetails if available
        if (userDetails instanceof com.sagar.MovieBookingSystem.Entity.User) {
            com.sagar.MovieBookingSystem.Entity.User user = 
                (com.sagar.MovieBookingSystem.Entity.User) userDetails;
            claims.put("userId", user.getId());
            claims.put("roles", user.getRoles() != null ? 
                user.getRoles() : java.util.Collections.emptySet());
        } else {
            // Fallback: extract roles from authorities
            claims.put("roles", userDetails.getAuthorities().stream()
                    .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }
        
        long now = System.currentTimeMillis();
        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Validates if the token is valid for the given user
     */
    public boolean isTokenValid(String jwtToken, UserDetails userDetails){
        try {
            final String username = extractUsername(jwtToken);
            return (userDetails.getUsername().equals(username) && !isTokenExpired(jwtToken));
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the token is expired
     */
    private boolean isTokenExpired(String jwtToken){
        try {
            return extractExpiration(jwtToken).before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Extracts the expiration date from token
     */
    private Date extractExpiration(String jwtToken){
        return extractClaims(jwtToken, Claims::getExpiration);
    }

}





















