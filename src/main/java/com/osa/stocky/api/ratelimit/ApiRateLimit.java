package com.osa.stocky.api.ratelimit;

import com.osa.stocky.subscription.SubscriptionManagerService;
import com.osa.stocky.subscription.SubscriptionPlan;
import com.osa.stocky.user.User;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.osa.stocky.user.CachedUserManagementService;

/**
 * This Service keeps track of a {@link io.github.bucket4j.Bucket} for each API-key to control the rate limits
 * for API calls.
 * 
 * @author oleksii
 * @since Nov 3, 2022
 */
@Service
public class ApiRateLimit implements ApiRateLimitService {
    
    @Autowired 
    private CachedUserManagementService userManagement;
    
    @Autowired
    private SubscriptionManagerService subscriptionManager;
    
    /**
     * Each map entry represents a Bucket used for rate limit control associated with a particular API-key. 
     */
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    @Override
    public Bucket resolve(final String apiKey) {
        return bucketCache.computeIfAbsent(apiKey, this::newBucket);
    }
    
    private Bucket newBucket(String apiKey) {
        User user = userManagement.get(apiKey); // should exist, since called from RateLimitFilter.
        SubscriptionPlan subscriptionPlan = getPlan(user.getSubscriptionId());
        return Bucket4j.builder().addLimit(Bandwidth.classic(subscriptionPlan.getCallsCount(),
                Refill.intervally(subscriptionPlan.getCallsCount(),
                        Duration.ofMillis(subscriptionPlan.getCallsTimeLimit())))).build();
    }
    
    private SubscriptionPlan getPlan(int id) {
        return subscriptionManager.get(id);
    }
}
