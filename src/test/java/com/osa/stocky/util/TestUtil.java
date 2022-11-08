package com.osa.stocky.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.stocky.api.Ticker;
import com.osa.stocky.subscription.SubscriptionPlan;
import com.osa.stocky.user.User;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author oleksii
 * @since 7 Nov 2022
 */
public class TestUtil {

    public static User createUser(final String userName, final String password, boolean superUser) {
        User user = new User();
        user.setName(userName);
        user.setPassword(password);
        user.setSubscriptionId(1);
        user.setSuperuser(superUser);
        return user;
    }
    
    public static String getAuthorizationHeader(final User user) {
        return "Basic " + StockyUtils.getApiKey(user.getName(), user.getPassword());
    }
    
    public static SubscriptionPlan getSubscriptionPlan(String name) {
        switch (name) {
            case "DEMO":
                return new SubscriptionPlan(10L, 2629800000L, 1000L, 2629800000L);
            case "SILVER":
                return new SubscriptionPlan(100L, 2629800000L, 1L, 60000L);
            case "GOLD":
                return new SubscriptionPlan(null, null, 1L, 10000L);   
            default:
                throw new AssertionError();
        }
    }
    
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Ticker getTicker() {
        return new Ticker(UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + 10000000));
    }
    
    public static final SubscriptionPlan DEMO = getSubscriptionPlan("DEMO");
    
    public static final SubscriptionPlan SILVER = getSubscriptionPlan("SILVER");
    
    public static final SubscriptionPlan GOLD = getSubscriptionPlan("GOLD");
    
}
