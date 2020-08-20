package com.example.securitydemo.controller;

import com.example.securitydemo.controller.vm.ApiResponse;
import com.example.securitydemo.controller.vm.JwtAuthenticationResponse;
import com.example.securitydemo.controller.vm.LoginRequest;
import com.example.securitydemo.model.Authority;
import com.example.securitydemo.model.Roles;
import com.example.securitydemo.model.Users;
import com.example.securitydemo.repository.AuthorityRepository;
import com.example.securitydemo.repository.RolesRepository;
import com.example.securitydemo.repository.UserRepository;
import com.example.securitydemo.security.jwt.TokenProvider;
import java.net.URI;
import java.util.Collections;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录和注册方法
 *
 *
 */
@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication,true);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/registry")
    public ResponseEntity<?> registerUser(@Valid @RequestBody LoginRequest registry) {
        if(userRepository.existsByUsername(registry.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                                      HttpStatus.BAD_REQUEST);
        }

        registry.setPassword(passwordEncoder.encode(registry.getPassword()));

        Roles role = rolesRepository.findById(2L)
                                    .orElseThrow(() -> new RuntimeException("User Role not set."));

        Users user = new Users();
        user.setUsername(registry.getUsername());
        user.setPassword(registry.getPassword());
        user.setEmail(registry.getEmail());
        user.setRoles(Collections.singleton(role));

        Users result = userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }
}
