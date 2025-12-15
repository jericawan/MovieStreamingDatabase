package com.moviestreaming.moviestreamingapp.model;

import java.io.Serializable;
import java.util.Objects;

public class MovieActorId implements Serializable {
    
    private String movie_code;
    private String actor_code;
    
    public MovieActorId() {}
    
    public MovieActorId(String movie_code, String actor_code) {
        this.movie_code = movie_code;
        this.actor_code = actor_code;
    }
    
    // Getters and setters
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
    
    // MUST override equals() and hashCode() for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieActorId that = (MovieActorId) o;
        return Objects.equals(movie_code, that.movie_code) && 
               Objects.equals(actor_code, that.actor_code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(movie_code, actor_code);
    }
}

