package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.Profile;
import com.moviestreaming.moviestreamingapp.repository.ProfileRepository;

@RestController
@RequestMapping("/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileRepository profileRepository;

    public ProfileController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // Get all profiles
    @GetMapping
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.ok(profileRepository.findAll());
    }

    // Get profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable Integer id) {
        Optional<Profile> profile = profileRepository.findById(id);
        return profile.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Get all profiles for a specific account
    @GetMapping("/account/{accountCode}")
    public ResponseEntity<List<Profile>> getProfilesByAccount(@PathVariable String accountCode) {
        List<Profile> profiles = profileRepository.findByAccountCode(accountCode);
        return ResponseEntity.ok(profiles);
    }

    // Create new profile
    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {
        // Check if account has reached max profiles (handled by trigger in DB)
        try {
            Profile savedProfile = profileRepository.save(profile);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Unable to create profile. Max profiles may have been reached.");
        }
    }

    // Update profile
    @PutMapping("/{id}")
    public ResponseEntity<Profile> updateProfile(
            @PathVariable Integer id,
            @RequestBody Profile profileDetails) {
        
        Optional<Profile> profileOptional = profileRepository.findById(id);
        
        if (profileOptional.isPresent()) {
            Profile profile = profileOptional.get();
            // Merge incoming changes with existing data
            if (profileDetails.getProfileName() != null) {
                profile.setProfileName(profileDetails.getProfileName());
            }
            if (profileDetails.getAccount_code() != null) {
                profile.setAccount_code(profileDetails.getAccount_code());
            }
            
            Profile updatedProfile = profileRepository.save(profile);
            return ResponseEntity.ok(updatedProfile);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete profile
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Integer id) {
        if (profileRepository.existsById(id)) {
            profileRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

