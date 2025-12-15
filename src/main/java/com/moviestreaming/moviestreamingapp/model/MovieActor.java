package com.moviestreaming.moviestreamingapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "movie_actor")
@IdClass(MovieActorId.class)
public class MovieActor {

    @Id
    @Column(name = "movie_code")
    private String movie_code;

    @Id
    @Column(name = "actor_code")
    private String actor_code;

    public MovieActor() {}

    public String getMovie_code() {
        return movie_code;
    }

    public void setMovie_code(String movie_code) {
        this.movie_code = movie_code;
    }

    public String getActor_code() {
        return actor_code;
    }

    public void setActor_code(String actor_code) {
        this.actor_code = actor_code;
    }
}

