package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.Movie;
import com.moviestreaming.moviestreamingapp.repository.MovieRepository;
import com.moviestreaming.moviestreamingapp.security.SecurityContext;
import com.moviestreaming.moviestreamingapp.exception.UnauthorizedException;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Integer id) {
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<Movie>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(movieRepository.searchByTitle(title));
    }

    @GetMapping("/search/year")
    public ResponseEntity<List<Movie>> searchByYear(@RequestParam int year) {
        return ResponseEntity.ok(movieRepository.searchByYear(year));
    }

    @GetMapping("/search/duration")
    public ResponseEntity<List<Movie>> searchByDuration(@RequestParam int duration) {
        return ResponseEntity.ok(movieRepository.searchByDuration(duration));
    }

    @GetMapping("/search/studio")
    public ResponseEntity<List<Movie>> searchByStudio(@RequestParam String studioCode) {
        return ResponseEntity.ok(movieRepository.searchByStudioCode(studioCode));
    }

    // Admin only
    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        if (!SecurityContext.isAdmin()) {
            throw new UnauthorizedException("Only admins can add movies");
        }
        return ResponseEntity.ok(movieRepository.save(movie));
    }

    // Admin only
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Integer id, @RequestBody Movie movieDetails) {
        if (!SecurityContext.isAdmin()) {
            throw new UnauthorizedException("Only admins can update movies");
        }
        
        Optional<Movie> movieOptional = movieRepository.findById(id);
        
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            if (movieDetails.getTitle() != null) movie.setTitle(movieDetails.getTitle());
            if (movieDetails.getYear() > 0) movie.setYear(movieDetails.getYear());
            if (movieDetails.getLength() > 0) movie.setLength(movieDetails.getLength());
            if (movieDetails.getStudioCode() != null) movie.setStudioCode(movieDetails.getStudioCode());
            if (movieDetails.getMovieCode() != null) movie.setMovieCode(movieDetails.getMovieCode());
            return ResponseEntity.ok(movieRepository.save(movie));
        }
        return ResponseEntity.notFound().build();
    }

    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Integer id) {
        if (!SecurityContext.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete movies");
        }
        
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
