package com.osa.stocky.user;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

/**
 * Class describes a user in the system.
 * 
 * @author oleksii
 * @since Nov 3, 2022
 */
@NamedNativeQuery(name = "find_by_name_and_password",
        query = "SELECT * FROM {h-schema}api_user WHERE name = :name AND password=:password",
        resultClass = User.class)

@NamedNativeQuery(name = "update_subscription_by_api_key",
        query = "UPDATE {h-schema}api_user SET subscription_id = :subid WHERE name=:name AND password=:password") 
@Entity
@Table(name = "api_user")
public class User implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "subscription_id", nullable = false)
    private int subscriptionId;
    
    @Column(name = "superuser", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean superuser;
    
    @Column(name = "created", nullable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created;
    
    @Column(name = "updated", nullable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updated;
    
    @Column(name = "updated_plan", nullable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Timestamp updatedPlan;
    
    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    public Timestamp getUpdatedPlan() {
        return updatedPlan;
    }

    public void setUpdatedPlan(Timestamp updatedPlan) {
        this.updatedPlan = updatedPlan;
    }
    
    public boolean isSuperuser() {
        return superuser;
    }

    public void setSuperuser(boolean superuser) {
        this.superuser = superuser;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", password=" + password
                + ", subscriptionId=" + subscriptionId + ", superuser=" + superuser
                + ", created=" + created + ", updated=" + updated + ", updatedPlan=" + updatedPlan + '}';
    }

    
}
