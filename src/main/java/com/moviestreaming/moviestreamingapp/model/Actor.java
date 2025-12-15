package com.moviestreaming.moviestreamingapp.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Actor")
public class Actor {

    @Id
    @Column(name = "actorID")
    private int actorId;

    @Column(name = "actor_code", unique = true)
    private String actorCode;

    @Column(name = "FirstName")
    private String FirstName;

    @Column(name = "LastName")
    private String LasttName;

    @Column(name = "BirthDate")
    private LocalDate BirthDate;

    public Actor() {}

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public String getActorCode() {
        return actorCode;
    }

    public void setActorCode(String actorCode) {
        this.actorCode = actorCode;
    }

    public String getFirstName(){
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName(){
        return LasttName;
    }

    public void setLastName(String lastName) {
        this.LasttName = lastName;
    }

    public String getFullName(){
        return FirstName + " " + LasttName;
    }

    public LocalDate getBirthDate(){
        return BirthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.BirthDate = birthDate;
    }
}
