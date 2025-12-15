package com.moviestreaming.moviestreamingapp.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "Watchhistory")
public class WatchHistory {

    @Id
    @Column(name = "watchID")
    private int watchID;

    @Column(name = "profile_code")
    private String profile_code;

    @Column(name = "movie_code")
    private String movie_code;

    @Column(name = "watchDate")
    private LocalDate watchDate;

    @Column(name = "Progress")
    private int Progress;

    public WatchHistory() {}

    public int getWatchID() {
        return watchID;
    }

    public void setWatchID(int watchID) {
        this.watchID = watchID;
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

    public LocalDate getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(LocalDate watchDate) {
        this.watchDate = watchDate;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        this.Progress = progress;
    }
}

