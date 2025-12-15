package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.moviestreaming.moviestreamingapp.model.Movie;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    
    // Search movies by title
    @Query("SELECT m FROM Movie m WHERE m.title LIKE CONCAT('%', :title, '%')")
    List<Movie> searchByTitle(@Param("title") String title);
    
    // Search movies by year
    @Query("SELECT m FROM Movie m WHERE m.year >= :year")
    List<Movie> searchByYear(@Param("year") int year);
    
    // Search movies by duration (length)
    @Query("SELECT m FROM Movie m WHERE m.length > :duration")
    List<Movie> searchByDuration(@Param("duration") int duration);
    
    // Search movies by studio code
    @Query("SELECT m FROM Movie m WHERE m.studioCode LIKE CONCAT('%', :studioCode, '%')")
    List<Movie> searchByStudioCode(@Param("studioCode") String studioCode);
    
    // Find movie by movie_code
    @Query("SELECT m FROM Movie m WHERE m.movieCode = :movieCode")
    Movie findByMovieCode(@Param("movieCode") String movieCode);
}