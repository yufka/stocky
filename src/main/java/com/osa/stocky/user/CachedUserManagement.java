package com.osa.stocky.user;

import com.osa.stocky.api.cache.ApiCacheService;
import com.osa.stocky.util.StockyException;
import com.osa.stocky.util.StockyUtils;
import com.osa.stocky.util.UserNamePasswordPair;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manage user. This class combines information from DB({@link UserRepository}) and puts in into Cache for 10 minutes
 * to avoid extra calls to DB. In case if user has to be deleted manually in DB, User will be deleted from cache automatically
 * 10 minutes.
 * 
 * @author oleksii
 * @since 7 Nov 2022
 */
@Service
public class CachedUserManagement implements CachedUserManagementService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedUserManagement.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApiCacheService apiCacheService;

    @Override
    public User get(String apiKey) {
        User user = apiCacheService.getUser(apiKey);
        if (user == null) {
            UserNamePasswordPair credentials = StockyUtils.getUserCredentials(apiKey);
            if (credentials == null) {
                return null;
            }
            user = userRepository.get(credentials.getName(), credentials.getPassword());
            if (user != null) {
                apiCacheService.getUser(apiKey);
                return user;
            }
        }
        return user;
    }

    @Override
    public void updateSubscription(String apiKey, int subscriptionId) throws StockyException {
        try {
            UserNamePasswordPair credentials = StockyUtils.getUserCredentials(apiKey);
            if (credentials == null) {
                throw new IllegalArgumentException("User with API key not found: " + apiKey);
            }
            User user = userRepository.get(credentials.getName(), credentials.getPassword());
            if (user == null) {
                throw new IllegalArgumentException("User with API key not found: " + apiKey);
            }
            userRepository.updateSubscription(credentials.getName(), credentials.getPassword(),
                    subscriptionId, new Timestamp(System.currentTimeMillis()));
            user = userRepository.get(credentials.getName(), credentials.getPassword());
            apiCacheService.putUser(user); // update user in chache
        } catch (Exception e) {
            LOGGER.error("Failed to update user subscription", e);
            throw new StockyException("Failed to update user subscription", e);
        }
    }

    @Override
    public String createUser(User user) throws StockyException {
        try {
            String apiKey = StockyUtils.getApiKey(user.getName(), user.getPassword());
            if (apiCacheService.getUser(apiKey) != null ||
                userRepository.get(user.getName(), user.getPassword()) != null) {
                throw new StockyException("User already exists!");
            }
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            user.setCreated(currentTimestamp);
            user.setUpdated(currentTimestamp);
            user.setUpdatedPlan(null);
            User savedUser = userRepository.save(user);
            apiCacheService.putUser(savedUser);
            return StockyUtils.getApiKey(savedUser.getName(), savedUser.getPassword());
        } catch (Exception e) {
            if (e instanceof StockyException) {
                throw e;
            }
            LOGGER.error("Failed to create user", e);
            throw new StockyException("Failed to create user", e);
        }
    }
}
