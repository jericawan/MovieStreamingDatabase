package com.moviestreaming.moviestreamingapp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.Account;
import com.moviestreaming.moviestreamingapp.repository.AccountRepository;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AccountRepository accountRepository;

    public AuthController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        
        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
        
        Account account = accountOptional.get();
        
        if (!account.getPasswordHash().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("accountID", account.getAccountID());
        response.put("account_code", account.getAccount_code());
        response.put("email", account.getEmail());
        response.put("role", account.getRole());
        response.put("sub_code", account.getSub_code());
        response.put("createdDate", account.getCreatedDate());
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
}

