package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.SubscriptionPlan;
import com.moviestreaming.moviestreamingapp.repository.SubscriptionPlanRepository;

@RestController
@RequestMapping("/subscription-plans")
@CrossOrigin(origins = "*")
public class SubscriptionPlanController {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanController(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    // Get all subscription plans
    @GetMapping
    public ResponseEntity<List<SubscriptionPlan>> getAllPlans() {
        return ResponseEntity.ok(subscriptionPlanRepository.findAll());
    }

    // Get plan by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> getPlanById(@PathVariable Integer id) {
        Optional<SubscriptionPlan> plan = subscriptionPlanRepository.findById(id);
        return plan.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // Get most popular subscription plan
    @GetMapping("/popular")
    public ResponseEntity<SubscriptionPlan> getMostPopularPlan() {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findMostPopularPlan();
        if (!plans.isEmpty()) {
            return ResponseEntity.ok(plans.get(0));
        }
        return ResponseEntity.notFound().build();
    }

    // Create new plan (Admin only)
    @PostMapping
    public ResponseEntity<SubscriptionPlan> createPlan(@RequestBody SubscriptionPlan plan) {
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return ResponseEntity.ok(savedPlan);
    }

    // Update plan (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> updatePlan(
            @PathVariable Integer id,
            @RequestBody SubscriptionPlan planDetails) {
        
        Optional<SubscriptionPlan> planOptional = subscriptionPlanRepository.findById(id);
        
        if (planOptional.isPresent()) {
            SubscriptionPlan plan = planOptional.get();
            // Merge incoming changes with existing data
            if (planDetails.getPlanName() != null) {
                plan.setPlanName(planDetails.getPlanName());
            }
            if (planDetails.getMonthlyPrice() > 0) {
                plan.setMonthlyPrice(planDetails.getMonthlyPrice());
            }
            if (planDetails.getMaxUsers() > 0) {
                plan.setMaxUsers(planDetails.getMaxUsers());
            }
            if (planDetails.getSub_code() != null) {
                plan.setSub_code(planDetails.getSub_code());
            }
            SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
            return ResponseEntity.ok(updatedPlan);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete plan (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Integer id) {
        if (subscriptionPlanRepository.existsById(id)) {
            subscriptionPlanRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

