package com.osa.stocky.api;

import java.io.Serializable;

/**
 * User DTO object used in request body for user creation.
 * {@link UserResource#createUser(java.lang.String, com.osa.stocky.api.UserDTO)}
 * 
 * @author oleksii
 * @since 7 Nov 2022
 */
public class UserDTO implements Serializable {

    private String name;
    
    private String password;
    
    private String subscriptionPlan;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }
}
