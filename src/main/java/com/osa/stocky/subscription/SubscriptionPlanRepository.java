package com.osa.stocky.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Subscription plan repository
 * @author oleksii
 * @since 5 Nov 2022
 */
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
        
    @Query(nativeQuery = true, name = "find_by_name")
    public SubscriptionPlan getByName(@Param(value = "name") String planName);
}
