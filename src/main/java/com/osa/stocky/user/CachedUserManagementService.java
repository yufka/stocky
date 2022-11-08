package com.osa.stocky.user;

import com.osa.stocky.util.StockyException;

/**
 *
 * @author oleksii
 * @since 7 Nov 2022
 */
public interface CachedUserManagementService {
    
    User get(String apiKey);
    
    void updateSubscription(String apiKey, int subscriptionId) throws StockyException;

    String createUser(User user) throws StockyException;
}
