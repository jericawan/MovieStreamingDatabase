package com.moviestreaming.moviestreamingapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @Column(name = "profileID")
    private int profileID;

    @Column(name = "profile_code", unique = true)
    private String profile_code;

    @Column(name = "account_code")
    private String account_code;

    @Column(name = "ProfileName")
    private String ProfileName;

    public Profile() {}

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public String getProfile_code() {
        return profile_code;
    }

    public void setProfile_code(String profile_code) {
        this.profile_code = profile_code;
    }

    public String getAccount_code() {
        return account_code;
    }

    public void setAccount_code(String account_code) {
        this.account_code = account_code;
    }

    public String getProfileName() {
        return ProfileName;
    }

    public void setProfileName(String profileName) {
        this.ProfileName = profileName;
    }
}

