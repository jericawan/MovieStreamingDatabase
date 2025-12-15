package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.WatchHistory;
import com.moviestreaming.moviestreamingapp.repository.WatchHistoryRepository;

@RestController
@RequestMapping("/watch-history")
@CrossOrigin(origins = "*")
public class WatchHistoryController {

    private final WatchHistoryRepository watchHistoryRepository;

    public WatchHistoryController(WatchHistoryRepository watchHistoryRepository) {
        this.watchHistoryRepository = watchHistoryRepository;
    }

    // Get all watch history
    @GetMapping
    public ResponseEntity<List<WatchHistory>> getAllWatchHistory() {
        return ResponseEntity.ok(watchHistoryRepository.findAll());
    }

    // Get watch history by profile
    @GetMapping("/profile/{profileCode}")
    public ResponseEntity<List<WatchHistory>> getWatchHistoryByProfile(@PathVariable String profileCode) {
        List<WatchHistory> history = watchHistoryRepository.findByProfileCode(profileCode);
        return ResponseEntity.ok(history);
    }

    // Get watch history by movie
    @GetMapping("/movie/{movieCode}")
    public ResponseEntity<List<WatchHistory>> getWatchHistoryByMovie(@PathVariable String movieCode) {
        List<WatchHistory> history = watchHistoryRepository.findByMovieCode(movieCode);
        return ResponseEntity.ok(history);
    }

    // Add watch history entry
    @PostMapping
    public ResponseEntity<WatchHistory> addWatchHistory(@RequestBody WatchHistory watchHistory) {
        WatchHistory savedHistory = watchHistoryRepository.save(watchHistory);
        return ResponseEntity.ok(savedHistory);
    }

    // Update watch history (e.g., update progress)
    @PutMapping("/{id}")
    public ResponseEntity<WatchHistory> updateWatchHistory(
            @PathVariable Integer id,
            @RequestBody WatchHistory historyDetails) {
        
        Optional<WatchHistory> historyOptional = watchHistoryRepository.findById(id);
        
        if (historyOptional.isPresent()) {
            WatchHistory history = historyOptional.get();
            // Merge incoming changes with existing data
            if (historyDetails.getProgress() > 0) {
                history.setProgress(historyDetails.getProgress());
            }
            if (historyDetails.getWatchDate() != null) {
                history.setWatchDate(historyDetails.getWatchDate());
            }
            WatchHistory updatedHistory = watchHistoryRepository.save(history);
            return ResponseEntity.ok(updatedHistory);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete watch history entry
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchHistory(@PathVariable Integer id) {
        if (watchHistoryRepository.existsById(id)) {
            watchHistoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

