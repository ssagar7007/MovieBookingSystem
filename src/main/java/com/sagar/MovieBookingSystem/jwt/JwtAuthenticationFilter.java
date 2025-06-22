package com.sagar.MovieBookingSystem.jwt;

import com.sagar.MovieBookingSystem.Entity.User;
import com.sagar.MovieBookingSystem.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         final String authHeader = request.getHeader("Authorization");
         final String jwtToken;
         final String username;

         if(authHeader == null || !authHeader.startsWith("Bearer")){
             filterChain.doFilter(request,response);
             return;
         }

         //Extract jwt token from header
        jwtToken = authHeader.substring(7);
         username = jwtService.extractUsername(jwtToken);

         //Check if we have username and no authentication exists
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
             User userDetails = userRepository.findByUsername(username).orElseThrow(
                     ()->new RuntimeException("User not found "));
             if(jwtService.isTokenValid(jwtToken,userDetails)){
                 List<SimpleGrantedAuthority> authorities = userDetails.getRoles().stream()
                         .map(SimpleGrantedAuthority::new)
                         .toList();

                 UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null, authorities);

                 //Set authentication details
                 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(authToken);
             }
        }
        filterChain.doFilter(request,response);
    }
}
