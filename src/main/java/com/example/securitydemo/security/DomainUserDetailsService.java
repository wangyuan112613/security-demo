package com.example.securitydemo.security;

import com.example.securitydemo.model.Authority;
import com.example.securitydemo.model.Roles;
import com.example.securitydemo.model.Users;
import com.example.securitydemo.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 实现接口UserDetailsService.
 * 通过覆盖接口的loadUserByUsername方法来实现认证用户操作
 */
@Slf4j
@Service("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {
    @Resource
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);

        return  userRepository.findByUsername(login)
                                           .map(user -> mapSpringSecurityUser(login, user))
                                           .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));
    }

    private User mapSpringSecurityUser(String lowercaseLogin, Users user) {
        List<GrantedAuthority> auths = new ArrayList<>();
        Set<Roles> roles = user.getRoles();

        for (Roles role : roles) {
            for (Authority authority : role.getAuthorities()) {
                auths.add(new SimpleGrantedAuthority(authority.getName()));
            }
        }
        return new User(user.getUsername(), user.getPassword(), auths);
//        var grantedAuthorities = user.getRoles()
//                                     .stream()
//                                     .map(authority -> new SimpleGrantedAuthority(authority.getName()))
//                                     .collect(Collectors.toList());
//
//        return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
