package com.example.securitydemo.service;

import com.example.securitydemo.model.Authority;
import com.example.securitydemo.model.Operation;
import com.example.securitydemo.repository.AuthorityRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    public Map<String, Set<ConfigAttribute>> loadDataSource() {
        log.info("begin read the auth list...");
        List<Authority> authorities = authorityRepository.findAll();
        if (authorities == null || authorities.size() <1) {
            log.error("the auth list is empty !");
            return null;
        }

        Map<String,Set<ConfigAttribute>> map = new HashMap<>();
        for (Authority authority : authorities) {
            List<Operation> operations = authority.getOperations();
            if (operations == null || operations.size() < 1) {
                log.debug("the auth[{}]' url is empty !", authority.getName());
                continue;
            }
            for (Operation operation : operations) {
                Set<ConfigAttribute> set = map.get(operation.getUrl());
                if(set == null || set.size() < 1) {
                    set = new HashSet<>();
                    map.put(operation.getUrl(),set);
                }
                set.add(new SecurityConfig(authority.getName()));
            }
        }
        return map;
    }
}
