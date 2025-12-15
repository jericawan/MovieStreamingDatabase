package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.Actor;
import com.moviestreaming.moviestreamingapp.model.Movie;
import com.moviestreaming.moviestreamingapp.repository.ActorRepository;
import com.moviestreaming.moviestreamingapp.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/actors")
@CrossOrigin(origins = "*")
public class ActorController {

    private final ActorRepository actorRepository;
    
    @Autowired
    private MovieRepository movieRepository;

    public ActorController(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors() {
        return ResponseEntity.ok(actorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable Integer id) {
        Optional<Actor> actor = actorRepository.findById(id);
        return actor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/lastname")
    public ResponseEntity<List<Actor>> searchByLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(actorRepository.searchByLastName(lastName));
    }
    
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<Movie>> getActorMovies(@PathVariable Integer id) {
        return ResponseEntity.ok(actorRepository.findMoviesByActorId(id));
    }

    // Admin only
    @PostMapping
    public ResponseEntity<Actor> addActor(@RequestBody Actor actor) {
        return ResponseEntity.ok(actorRepository.save(actor));
    }

    // Admin only
    @PutMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable Integer id, @RequestBody Actor actorDetails) {
        Optional<Actor> actorOptional = actorRepository.findById(id);
        
        if (actorOptional.isPresent()) {
            Actor actor = actorOptional.get();
            if (actorDetails.getFirstName() != null) actor.setFirstName(actorDetails.getFirstName());
            if (actorDetails.getLastName() != null) actor.setLastName(actorDetails.getLastName());
            if (actorDetails.getBirthDate() != null) actor.setBirthDate(actorDetails.getBirthDate());
            if (actorDetails.getActorCode() != null) actor.setActorCode(actorDetails.getActorCode());
            return ResponseEntity.ok(actorRepository.save(actor));
        }
        return ResponseEntity.notFound().build();
    }

    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Integer id) {
        if (actorRepository.existsById(id)) {
            actorRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}