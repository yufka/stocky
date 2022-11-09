package com.osa.stocky.api.cache;

import com.osa.stocky.subscription.SubscriptionPlan;
import com.osa.stocky.user.User;
import com.osa.stocky.util.StockyUtils;
import java.time.Duration;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.stereotype.Service;

/**
 * Ehcache service provides methods to cache user API-keys (encoded <name:password>) for fast user search by API key
 * to avoid extra calls to DB and also cache {@link SubscriptionPlan}. In case of user API-key caching, a lifetime 
 * of 10 minutes is set to provide "soft" user deletion option from DB (if needed).
 * 
 * @author oleksii
 * @since 5 Nov 2022
 */
@Service
public class ApiCache implements ApiCacheService {
    
    private static final String API_USER_CACHE_NAME = "api_user_cache";
    
    private static final String API_NAME_SUBSCRIPTION_CACHE_NAME = "api_name_subscription_cache";
    
    private static final String API_ID_SUBSCRIPTION_CACHE_NAME = "api_id_subscription_cache";

    private final CacheManager cacheManager;
    
    public ApiCache() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        cacheManager.createCache(API_USER_CACHE_NAME,
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, User.class, ResourcePoolsBuilder.heap(100))
                        .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(10))));
        
        cacheManager.createCache(API_NAME_SUBSCRIPTION_CACHE_NAME,
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, SubscriptionPlan.class, ResourcePoolsBuilder.heap(10)));
        
        cacheManager.createCache(API_ID_SUBSCRIPTION_CACHE_NAME,
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(Integer.class, SubscriptionPlan.class, ResourcePoolsBuilder.heap(10)));
    }
    
    public Cache<String, User> getUserCache() {
        return cacheManager.getCache(API_USER_CACHE_NAME, String.class, User.class);
    }

    private Cache<String, SubscriptionPlan> getSubscriptionNameCache() {
        return cacheManager.getCache(API_NAME_SUBSCRIPTION_CACHE_NAME, String.class, SubscriptionPlan.class);
    }
    
    private Cache<Integer, SubscriptionPlan> getSubscriptionIdCache() {
        return cacheManager.getCache(API_ID_SUBSCRIPTION_CACHE_NAME, Integer.class, SubscriptionPlan.class);
    }
    
    

    @Override
    public SubscriptionPlan getSubscriptionPlan(String name) {
        Cache<String, SubscriptionPlan> cache = getSubscriptionNameCache();
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        return null;
    }

    @Override
    public void putSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        Cache<String, SubscriptionPlan> nameCache = getSubscriptionNameCache();
        nameCache.put(subscriptionPlan.getName(), subscriptionPlan);
        Cache<Integer, SubscriptionPlan> idCache = getSubscriptionIdCache();
        idCache.put(subscriptionPlan.getId(), subscriptionPlan);
    }

    @Override
    public SubscriptionPlan getSubscriptionPlan(int id) {
        Cache<Integer, SubscriptionPlan> cache = getSubscriptionIdCache();
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        return null;
    }

    @Override
    public User getUser(String apikey) {
        Cache<String, User> cache = getUserCache();
        if (cache.containsKey(apikey)) {
            return cache.get(apikey);
        }
        return null;
    }

    @Override
    public void putUser(User user) {
        Cache<String, User> cache = getUserCache();
        cache.put(StockyUtils.getApiKey(user.getName(), user.getPassword()), user);
    }
}
