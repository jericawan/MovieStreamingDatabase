package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.MovieActor;
import com.moviestreaming.moviestreamingapp.repository.MovieActorRepository;

@RestController
@RequestMapping("/movie-actors")
@CrossOrigin(origins = "*")
public class MovieActorController {

    private final MovieActorRepository movieActorRepository;

    public MovieActorController(MovieActorRepository movieActorRepository) {
        this.movieActorRepository = movieActorRepository;
    }

    // Get all movie-actor relationships
    @GetMapping
    public ResponseEntity<List<MovieActor>> getAllMovieActors() {
        return ResponseEntity.ok(movieActorRepository.findAll());
    }

    // Get movie-actor by ID
    @GetMapping("/{id}")
    public ResponseEntity<MovieActor> getMovieActorById(@PathVariable Integer id) {
        Optional<MovieActor> movieActor = movieActorRepository.findById(id);
        return movieActor.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // Add actor to movie
    @PostMapping
    public ResponseEntity<MovieActor> addMovieActor(@RequestBody MovieActor movieActor) {
        MovieActor saved = movieActorRepository.save(movieActor);
        return ResponseEntity.ok(saved);
    }

    // Remove actor from movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovieActor(@PathVariable Integer id) {
        if (movieActorRepository.existsById(id)) {
            movieActorRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

