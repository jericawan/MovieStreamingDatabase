package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.Rating;
import com.moviestreaming.moviestreamingapp.repository.RatingRepository;
import com.moviestreaming.moviestreamingapp.security.SecurityContext;
import com.moviestreaming.moviestreamingapp.exception.UnauthorizedException;

@RestController
@RequestMapping("/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    private final RatingRepository ratingRepository;

    public RatingController(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @GetMapping
    public ResponseEntity<List<Rating>> getAllRatings() {
        return ResponseEntity.ok(ratingRepository.findAllOrderedByMovie());
    }

    @GetMapping("/movie/{movieCode}")
    public ResponseEntity<List<Rating>> getRatingsByMovie(@PathVariable String movieCode) {
        return ResponseEntity.ok(ratingRepository.findByMovieCode(movieCode));
    }

    @GetMapping("/profile/{profileCode}")
    public ResponseEntity<List<Rating>> getRatingsByProfile(@PathVariable String profileCode) {
        return ResponseEntity.ok(ratingRepository.findByProfileCode(profileCode));
    }

    @GetMapping("/movie/{movieCode}/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable String movieCode) {
        Double avgRating = ratingRepository.calculateAverageRating(movieCode);
        Map<String, Object> response = new HashMap<>();
        response.put("movieCode", movieCode);
        response.put("averageRating", avgRating != null ? Math.round(avgRating * 100.0) / 100.0 : 0.0);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        if (!SecurityContext.isAdmin() && !SecurityContext.canAccessProfile(rating.getProfile_code())) {
            throw new UnauthorizedException("You can only add ratings for your own profile");
        }
        
        Optional<Rating> existingRating = ratingRepository.findByProfileAndMovie(
            rating.getProfile_code(), rating.getMovie_code()
        );
        
        if (existingRating.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(ratingRepository.save(rating));
    }

    @PutMapping("/profile/{profileCode}/movie/{movieCode}")
    public ResponseEntity<Rating> updateRating(
            @PathVariable String profileCode,
            @PathVariable String movieCode,
            @RequestBody Rating ratingDetails) {
        
        // Authorization check: Only admin or the profile themselves can update ratings
        if (!SecurityContext.isAdmin() && !SecurityContext.canAccessProfile(profileCode)) {
            throw new UnauthorizedException("You can only update your own ratings");
        }
        
        Optional<Rating> ratingOptional = ratingRepository.findByProfileAndMovie(profileCode, movieCode);
        
        if (ratingOptional.isPresent()) {
            Rating rating = ratingOptional.get();
            // Merge incoming changes with existing data
            if (ratingDetails.getRatingValue() > 0) {
                rating.setRatingValue(ratingDetails.getRatingValue());
            }
            if (ratingDetails.getRatingDate() != null) {
                rating.setRatingDate(ratingDetails.getRatingDate());
            }
            if (ratingDetails.getReview() != null) {
                rating.setReview(ratingDetails.getReview());
            }
            Rating updatedRating = ratingRepository.save(rating);
            return ResponseEntity.ok(updatedRating);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/profile/{profileCode}/movie/{movieCode}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable String profileCode,
            @PathVariable String movieCode) {
        
        // Authorization check: Only admin or the profile themselves can delete ratings
        if (!SecurityContext.isAdmin() && !SecurityContext.canAccessProfile(profileCode)) {
            throw new UnauthorizedException("You can only delete your own ratings");
        }
        
        Optional<Rating> rating = ratingRepository.findByProfileAndMovie(profileCode, movieCode);
        
        if (rating.isPresent()) {
            ratingRepository.deleteByProfileAndMovie(profileCode, movieCode);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

