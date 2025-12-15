package com.moviestreaming.moviestreamingapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptionplan")
public class SubscriptionPlan {

    @Id
    @Column(name = "subscriptionID")
    private int subscriptionID;

    @Column(name = "sub_code", unique = true)
    private String sub_code;

    @Column(name = "planName")
    private String planName;

    @Column(name = "monthlyPrice")
    private double monthlyPrice;

    @Column(name = "MaxUsers")
    private int MaxUsers;

    public SubscriptionPlan() {}

    public int getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(int subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public String getSub_code() {
        return sub_code;
    }

    public void setSub_code(String sub_code) {
        this.sub_code = sub_code;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(double monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public int getMaxUsers() {
        return MaxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.MaxUsers = maxUsers;
    }
}

