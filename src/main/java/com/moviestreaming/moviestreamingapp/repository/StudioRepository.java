package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.moviestreaming.moviestreamingapp.model.Studio;

public interface StudioRepository extends JpaRepository<Studio, Integer> {
}