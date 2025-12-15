package com.moviestreaming.moviestreamingapp.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @Column(name = "ratingID")
    private int ratingID;

    @Column(name = "profile_code")
    private String profile_code;

    @Column(name = "movie_code")
    private String movie_code;

    @Column(name = "ratingValue")
    private int ratingValue;

    @Column(name = "ratingDate")
    private LocalDate ratingDate;

    @Column(name = "review")
    private String review;

    public Rating() {}

    public int getRatingID() {
        return ratingID;
    }

    public void setRatingID(int ratingID) {
        this.ratingID = ratingID;
    }

    public String getProfile_code() {
        return profile_code;
    }

    public void setProfile_code(String profile_code) {
        this.profile_code = profile_code;
    }

    public String getMovie_code() {
        return movie_code;
    }

    public void setMovie_code(String movie_code) {
        this.movie_code = movie_code;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public LocalDate getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDate ratingDate) {
        this.ratingDate = ratingDate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}

