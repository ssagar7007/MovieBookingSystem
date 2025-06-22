package com.sagar.MovieBookingSystem.Controller;

import com.sagar.MovieBookingSystem.DTO.LoginRequestDTO;
import com.sagar.MovieBookingSystem.DTO.LoginResponseDTO;
import com.sagar.MovieBookingSystem.DTO.RegisterRequestDTO;
import com.sagar.MovieBookingSystem.Entity.User;
import com.sagar.MovieBookingSystem.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register-normal-user")
    public ResponseEntity<User> registerNormalUser(@RequestBody RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.ok(authenticationService.registerNormalUser(registerRequestDTO));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return ResponseEntity.ok(authenticationService.login(loginRequestDTO));
    }
}
