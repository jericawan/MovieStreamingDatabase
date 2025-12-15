package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.moviestreaming.moviestreamingapp.model.WatchHistory;
import java.util.List;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Integer> {
    
    // Find watch history by profile
    @Query("SELECT w FROM WatchHistory w WHERE w.profile_code = :profileCode ORDER BY w.watchDate DESC")
    List<WatchHistory> findByProfileCode(@Param("profileCode") String profileCode);
    
    // Find watch history by movie
    @Query("SELECT w FROM WatchHistory w WHERE w.movie_code = :movieCode ORDER BY w.watchDate DESC")
    List<WatchHistory> findByMovieCode(@Param("movieCode") String movieCode);
}

