package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.moviestreaming.moviestreamingapp.model.Rating;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    
    // Find ratings by movie code
    @Query("SELECT r FROM Rating r WHERE r.movie_code = :movieCode ORDER BY r.ratingDate DESC")
    List<Rating> findByMovieCode(@Param("movieCode") String movieCode);
    
    // Find ratings by profile code
    @Query("SELECT r FROM Rating r WHERE r.profile_code = :profileCode ORDER BY r.ratingDate DESC")
    List<Rating> findByProfileCode(@Param("profileCode") String profileCode);
    
    // Find all ratings ordered by movie
    @Query("SELECT r FROM Rating r ORDER BY r.movie_code")
    List<Rating> findAllOrderedByMovie();
    
    // Calculate average rating for a movie
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.movie_code = :movieCode")
    Double calculateAverageRating(@Param("movieCode") String movieCode);
    
    // Find rating by profile and movie
    @Query("SELECT r FROM Rating r WHERE r.profile_code = :profileCode AND r.movie_code = :movieCode")
    Optional<Rating> findByProfileAndMovie(@Param("profileCode") String profileCode, @Param("movieCode") String movieCode);
    
    // Delete rating by profile and movie
    @Modifying
    @Transactional
    @Query("DELETE FROM Rating r WHERE r.profile_code = :profileCode AND r.movie_code = :movieCode")
    void deleteByProfileAndMovie(@Param("profileCode") String profileCode, @Param("movieCode") String movieCode);
}

