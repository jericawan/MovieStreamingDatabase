package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.moviestreaming.moviestreamingapp.model.SubscriptionPlan;
import java.util.List;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
    
    // Get most popular subscription plan
    @Query(value = "SELECT sp.* FROM subscriptionplan sp " +
                   "LEFT JOIN Account a ON sp.sub_code = a.sub_code " +
                   "GROUP BY sp.subscriptionID, sp.sub_code, sp.planName, sp.monthlyPrice, sp.MaxUsers " +
                   "ORDER BY COUNT(a.accountID) DESC", 
           nativeQuery = true)
    List<SubscriptionPlan> findMostPopularPlan();
}

