package com.moviestreaming.moviestreamingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviestreaming.moviestreamingapp.model.Studio;
import com.moviestreaming.moviestreamingapp.repository.StudioRepository;

@RestController
@RequestMapping("/studios")
@CrossOrigin(origins = "*")
public class StudioController {

    private final StudioRepository studioRepository;

    public StudioController(StudioRepository studioRepository) {
        this.studioRepository = studioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Studio>> getAllStudios() {
        return ResponseEntity.ok(studioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Studio> getStudioById(@PathVariable Integer id) {
        Optional<Studio> studio = studioRepository.findById(id);
        return studio.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Admin only
    @PostMapping
    public ResponseEntity<Studio> addStudio(@RequestBody Studio studio) {
        return ResponseEntity.ok(studioRepository.save(studio));
    }

    // Admin only
    @PutMapping("/{id}")
    public ResponseEntity<Studio> updateStudio(@PathVariable Integer id, @RequestBody Studio studioDetails) {
        Optional<Studio> studioOptional = studioRepository.findById(id);
        
        if (studioOptional.isPresent()) {
            Studio studio = studioOptional.get();
            if (studioDetails.getStudioName() != null) studio.setStudioName(studioDetails.getStudioName());
            if (studioDetails.getAddress() != null) studio.setAddress(studioDetails.getAddress());
            if (studioDetails.getStudioCode() != null) studio.setStudioCode(studioDetails.getStudioCode());
            return ResponseEntity.ok(studioRepository.save(studio));
        }
        return ResponseEntity.notFound().build();
    }

    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudio(@PathVariable Integer id) {
        if (studioRepository.existsById(id)) {
            studioRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

