package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.moviestreaming.moviestreamingapp.model.Actor;
import com.moviestreaming.moviestreamingapp.model.Movie;
import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Integer> {
    
    // Find actors by last name
    @Query("SELECT a FROM Actor a WHERE a.LasttName LIKE CONCAT('%', :lastName, '%')")
    List<Actor> searchByLastName(@Param("lastName") String lastName);
    
    // Find actor by actor_code
    @Query("SELECT a FROM Actor a WHERE a.actorCode = :actorCode")
    Actor findByActorCode(@Param("actorCode") String actorCode);
    
    // Find all movies an actor appeared in
    @Query(value = "SELECT m.* FROM movie m " +
                   "JOIN movie_actor ma ON m.movie_code = ma.movie_code " +
                   "JOIN Actor a ON ma.actor_code = a.actor_code " +
                   "WHERE a.actorID = :actorId",
           nativeQuery = true)
    List<Movie> findMoviesByActorId(@Param("actorId") Integer actorId);
}