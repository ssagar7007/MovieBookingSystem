package com.sagar.MovieBookingSystem.Controller;

import com.sagar.MovieBookingSystem.DTO.RegisterRequestDTO;
import com.sagar.MovieBookingSystem.Entity.User;
import com.sagar.MovieBookingSystem.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register-admin-user")
    public ResponseEntity<User> registerAdminUser(@RequestBody RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.ok(authenticationService.registerAdminUser(registerRequestDTO));
    }
}
