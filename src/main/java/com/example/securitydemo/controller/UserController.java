package com.example.securitydemo.controller;

import com.example.securitydemo.model.Users;
import com.example.securitydemo.repository.UserRepository;
import com.example.securitydemo.security.jwt.Constants;
import javax.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * 用户管理
 */
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Resource
    UserRepository userRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + Constants.USER + "\")")
    public ResponseEntity<Users> get(@PathVariable Long id) {
        Users user = userRepository.findById(id)
                                   .orElseThrow(() ->new UsernameNotFoundException("USER not found: " + id));

        return ResponseEntity.ok().body(user);
    }
}
