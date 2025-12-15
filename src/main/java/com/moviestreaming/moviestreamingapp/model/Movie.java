package com.moviestreaming.moviestreamingapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @Column(name = "movieID")
    private int movieId;

    @Column(name = "movie_code")
    private String movieCode;

    @Column(name = "title")
    private String title;

    @Column(name = "year")
    private int year;

    @Column(name = "studio_code")
    private String studioCode;   // <-- TEMPORARILY FLAT (important)

    @Column(name = "length")
    private int length;

    public Movie() {}

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieCode() {
        return movieCode;
    }

    public void setMovieCode(String movieCode) {
        this.movieCode = movieCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getStudioCode() {
        return studioCode;
    }

    public void setStudioCode(String studioCode) {
        this.studioCode = studioCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
