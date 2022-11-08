package com.osa.stocky.api.cache;

import com.osa.stocky.subscription.SubscriptionPlan;
import com.osa.stocky.user.User;

/**
 *
 * @author oleksii
 * @since 5 Nov 2022
 */
public interface ApiCacheService {

    /**
     * 
     * @param id
     * @return {@link SubscriptionPlan} or {@code null} if nothing found.
     */
    public SubscriptionPlan getSubscriptionPlan(int id);
    /**
     * @param name name of subscription plan
     * @return {@link SubscriptionPlan} or {@code null} if nothing found
     */
    public SubscriptionPlan getSubscriptionPlan(String name);
    
    /**
     * @param subscriptionPlan 
     */
    void putSubscriptionPlan(SubscriptionPlan subscriptionPlan);
    
    public User getUser(String apikey);
    
    public void putUser(User user);
    
}
