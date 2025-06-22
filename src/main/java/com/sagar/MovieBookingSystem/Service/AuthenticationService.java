package com.sagar.MovieBookingSystem.Service;

import com.sagar.MovieBookingSystem.DTO.LoginRequestDTO;
import com.sagar.MovieBookingSystem.DTO.LoginResponseDTO;
import com.sagar.MovieBookingSystem.DTO.RegisterRequestDTO;
import com.sagar.MovieBookingSystem.Entity.User;
import com.sagar.MovieBookingSystem.Repository.UserRepository;
import com.sagar.MovieBookingSystem.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;


    public User registerNormalUser(RegisterRequestDTO registerRequestDTO){
        if(userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()){
            throw new RuntimeException("User already registered.");
        }
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRoles(roles);

        return userRepository.save(user);

    }

    public User registerAdminUser(RegisterRequestDTO registerRequestDTO){
        if(userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()){
            throw new RuntimeException("User already registered.");
        }
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRoles(roles);

        return userRepository.save(user);

    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
      User user = userRepository.findByUsername(loginRequestDTO.getUsername())
              .orElseThrow(()->new RuntimeException("User not found."));

      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequestDTO.getUsername(),
                      loginRequestDTO.getPassword()
              )
      );

      String token = jwtService.generateToken(user);
      return LoginResponseDTO.builder()
              .jwtToken(token)
              .username(user.getUsername())
              .roles(user.getRoles())
              .build();

    }
}
