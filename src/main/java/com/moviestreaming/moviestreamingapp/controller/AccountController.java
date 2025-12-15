package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.Account;
import com.moviestreaming.moviestreamingapp.repository.AccountRepository;
import com.moviestreaming.moviestreamingapp.repository.SubscriptionPlanRepository;
import com.moviestreaming.moviestreamingapp.security.SecurityContext;
import com.moviestreaming.moviestreamingapp.exception.UnauthorizedException;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountRepository accountRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public AccountController(AccountRepository accountRepository, 
                           SubscriptionPlanRepository subscriptionPlanRepository) {
        this.accountRepository = accountRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    // Admin only
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        if (!SecurityContext.isAdmin()) {
            throw new UnauthorizedException("Only admins can view all accounts");
        }
        return ResponseEntity.ok(accountRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Integer id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    public ResponseEntity<Account> getAccountByEmail(@RequestParam String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        return account.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        // Check if email already exists
        Optional<Account> existing = accountRepository.findByEmail(account.getEmail());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        Account savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    // Account owner or admin
    @PutMapping("/{id}/subscription")
    public ResponseEntity<Account> updateSubscription(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {
        
        Optional<Account> accountOptional = accountRepository.findById(id);
        
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            
            if (!SecurityContext.isAdmin() && !SecurityContext.canAccessAccount(account.getAccount_code())) {
                throw new UnauthorizedException("You can only update your own subscription");
            }
            
            if (request.containsKey("sub_code")) {
                account.setSub_code(request.get("sub_code"));
            }
            Account updatedAccount = accountRepository.save(account);
            return ResponseEntity.ok(updatedAccount);
        }
        return ResponseEntity.notFound().build();
    }

    // Account owner or admin
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable Integer id,
            @RequestBody Account accountDetails) {
        
        Optional<Account> accountOptional = accountRepository.findById(id);
        
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            
            if (!SecurityContext.isAdmin() && !SecurityContext.canAccessAccount(account.getAccount_code())) {
                throw new UnauthorizedException("You can only update your own account");
            }
            
            if (accountDetails.getEmail() != null) {
                account.setEmail(accountDetails.getEmail());
            }
            if (accountDetails.getPasswordHash() != null) {
                account.setPasswordHash(accountDetails.getPasswordHash());
            }
            if (accountDetails.getCreatedDate() != null) {
                account.setCreatedDate(accountDetails.getCreatedDate());
            }
            if (accountDetails.getSub_code() != null) {
                account.setSub_code(accountDetails.getSub_code());
            }
            // Only admins can change roles
            if (accountDetails.getRole() != null) {
                if (SecurityContext.isAdmin()) {
                    account.setRole(accountDetails.getRole());
                } else {
                    throw new UnauthorizedException("Only admins can change account roles");
                }
            }
            Account updatedAccount = accountRepository.save(account);
            return ResponseEntity.ok(updatedAccount);
        }
        return ResponseEntity.notFound().build();
    }

    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        if (!SecurityContext.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete accounts");
        }
        
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        
        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        
        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Email not found"));
        }
        
        Account account = accountOptional.get();
        account.setPasswordHash(newPassword);
        accountRepository.save(account);
        accountRepository.flush();  // Force immediate write to database
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Password reset successfully"));
    }
}

