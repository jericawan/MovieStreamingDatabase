package com.moviestreaming.moviestreamingapp.model;

import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "studio")
public class Studio {

    @Id
    @Column(name = "studioID")
    private int studioId;

    @Column(name = "studio_code", unique = true)
    private String studioCode;

    @Column(name = "studioName")
    private String studioName;

    @Column(name = "address")
    private String address;

    public Studio() {}

    public int getStudioId() {
        return studioId;
    }

    public void setStudioId(int studioId) {
        this.studioId = studioId;
    }

    public String getStudioCode() {
        return studioCode;
    }

    public void setStudioCode(String studioCode) {
        this.studioCode = studioCode;
    }

    public String getStudioName() {
        return studioName;
    }

    public void setStudioName(String studioName) {
        this.studioName = studioName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
