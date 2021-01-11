package com.projects.covid19.user.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.covid19.user.components.JwtManager;
import com.projects.covid19.user.dto.requests.LoginRequest;
import com.projects.covid19.user.dto.requests.SignUpRequest;
import com.projects.covid19.user.dto.responses.ApiResponse;
import com.projects.covid19.user.dto.responses.JwtAuthenticationResponse;
import com.projects.covid19.user.exceptions.GeneralException;
import com.projects.covid19.user.models.User;
import com.projects.covid19.user.services.UserService;
import com.projects.covid19.user.utils.RoleType;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
    UserService userService;
    
    @Autowired
    JwtManager jwtManager;
    
    @Autowired
    AuthenticationManager authenticationManager;


    @GetMapping("/ping")
	public ResponseEntity<?> ping() {
		
		return ResponseEntity.ok(new ApiResponse(0, "User service is up and running!"));
	}
	
	@PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws GeneralException {
		
		User user = userService.registerUser(signUpRequest, RoleType.ROLE_USER);
        
        return ResponseEntity.ok(new ApiResponse(0, "User registered successfully", user));
    }
	
	@PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignUpRequest signUpRequest) throws GeneralException {
       
        userService.registerUser(signUpRequest, RoleType.ROLE_ADMIN);
        
        return ResponseEntity.ok(new ApiResponse(0, "Admin registered successfully"));
    }
	
	@PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtManager.generateToken(authentication);
        return ResponseEntity.ok(new ApiResponse(0, "Token generated successfully",new JwtAuthenticationResponse(jwt)));
    }
	
	@GetMapping("/{userId}")
	public ResponseEntity<?> findUserById(@PathVariable(name = "userId") Long userId) {
		User user = userService.findById(userId).orElseThrow();
		return ResponseEntity.ok(new ApiResponse(0, "User retrieved successfully", user));
	}

}
