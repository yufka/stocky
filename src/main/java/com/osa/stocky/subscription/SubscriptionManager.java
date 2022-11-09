package com.osa.stocky.subscription;

import com.osa.stocky.api.cache.ApiCacheService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class manages {@link SubscriptionPlan} object using DB({@link SubscriptionPlanRepository}) 
 * and Cache to avoid extra calls to DB. Since Subscription Plans do not change (assumption), they stay in Cache forever.
 * @author oleksii
 * @since 6 Nov 2022
 */
@Service
public class SubscriptionManager implements SubscriptionManagerService {

    @Autowired
    private ApiCacheService apiCacheService;
    
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;
    
    @Override
    public SubscriptionPlan get(int id) {
        SubscriptionPlan plan = apiCacheService.getSubscriptionPlan(id);
        if (plan == null) {
            Optional<SubscriptionPlan> optionalPlan = subscriptionPlanRepository.findById(id);
            if (!optionalPlan.isEmpty()) {
                plan = optionalPlan.get();
                apiCacheService.putSubscriptionPlan(plan);
                return plan;
            } else {
                return null; // nothing found in cache and in DB
            }
        }
        return plan;
    }
    
    @Override
    public SubscriptionPlan get(String name) {
        SubscriptionPlan plan = apiCacheService.getSubscriptionPlan(name);
        if (plan == null) {
            plan = subscriptionPlanRepository.getByName(name);
            if (plan != null) {
                apiCacheService.putSubscriptionPlan(plan);
                return plan;
            }
            return null;
        }
        return plan;
    }
}
