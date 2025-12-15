package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.moviestreaming.moviestreamingapp.model.Profile;
import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    
    // Find all profiles for an account
    @Query("SELECT p FROM Profile p WHERE p.account_code = :accountCode")
    List<Profile> findByAccountCode(@Param("accountCode") String accountCode);
    
    // Count profiles for an account
    @Query("SELECT COUNT(p) FROM Profile p WHERE p.account_code = :accountCode")
    int countByAccountCode(@Param("accountCode") String accountCode);
}

