package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.moviestreaming.moviestreamingapp.model.Account;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    
    // Find account by email
    @Query("SELECT a FROM Account a WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);
}

