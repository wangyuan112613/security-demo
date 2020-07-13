package com.example.securitydemo.repository;

import com.example.securitydemo.model.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {
    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Users> findOneWithAuthoritiesByUsername(String username);
}
