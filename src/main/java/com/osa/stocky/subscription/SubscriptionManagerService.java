package com.osa.stocky.subscription;

/**
 *
 * @author oleksii
 * @since 6 Nov 2022
 */
public interface SubscriptionManagerService {

    public SubscriptionPlan get(int id);
    
    public SubscriptionPlan get(String name);
}
