package com.moviestreaming.moviestreamingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.moviestreaming.moviestreamingapp.model.MovieActor;

public interface MovieActorRepository extends JpaRepository<MovieActor, Integer> {
}

