package com.osa.stocky.user;

import com.osa.stocky.subscription.SubscriptionManagerService;
import com.osa.stocky.subscription.SubscriptionPlan;
import com.osa.stocky.util.StockyException;
import com.osa.stocky.util.StockyUtils;
import java.sql.Timestamp;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author oleksii
 * @since 7 Nov 2022
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class CachedUserManagementServiceTest {
    
    @Autowired
    CachedUserManagementService userManagementService;
    
    @Autowired
    SubscriptionManagerService subscriptionPlanManager;
    
    private static final String API_KEY = StockyUtils.getApiKey("admin", "admin");
    
    private static final String FAKE_API_KEY = StockyUtils.getApiKey("admin2", "admin");
    
    @Test
    public void testUser() {
        assertNotNull(userManagementService.get(API_KEY)); // get from DB and put in cache
        assertNotNull(userManagementService.get(API_KEY)); // get from Cache
        assertNull(userManagementService.get(API_KEY + "123")); // no such user
    }
    
    @Test
    public void testUserUpdateSubscription() {
        User user = userManagementService.get(API_KEY);
        assertNotNull(user);
        int userSubscription = user.getSubscriptionId();
        int newSubscriptionId = userSubscription + 1;
        userManagementService.updateSubscription(API_KEY, newSubscriptionId);
        user = userManagementService.get(API_KEY);
        assertEquals(newSubscriptionId, user.getSubscriptionId());
    }
    
    @Test
    public void testUserUpdateSubscriptionInvalidUser() {
        User user = userManagementService.get(API_KEY);
        assertNotNull(user);
        int userSubscription = user.getSubscriptionId();
        int newSubscriptionId = userSubscription + 1;
        StockyException ex = assertThrows(StockyException.class, () -> {
            userManagementService.updateSubscription(API_KEY + "12312", newSubscriptionId);});
        assertEquals("Failed to update user subscription", ex.getMessage());
    }
    
    @Test
    public void testUserUpdateSubscriptionInvalidUserNotFound() {
        User user = userManagementService.get(API_KEY);
        assertNotNull(user);
        int userSubscription = user.getSubscriptionId();
        int newSubscriptionId = userSubscription + 1;
        StockyException ex = assertThrows(StockyException.class, () -> {
            userManagementService.updateSubscription(FAKE_API_KEY, newSubscriptionId);});
        assertEquals("Failed to update user subscription", ex.getMessage());
    }
    
    @Test
    public void testUserUpdateSubscriptionInvalidSubscription() {
        User user = userManagementService.get(API_KEY);
        assertNotNull(user);
        int userSubscription = user.getSubscriptionId();
        int newSubscriptionId = userSubscription + 1;
        StockyException ex = assertThrows(StockyException.class, () -> {
            userManagementService.updateSubscription(API_KEY, -10);});
        assertEquals("Failed to update user subscription", ex.getMessage());
    }
    
    
    
    @Test
    public void createUserTest() {
        User user = new User();
        user.setName(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());
        SubscriptionPlan plan = subscriptionPlanManager.get("DEMO");
        user.setSubscriptionId(plan.getId());
        user.setSuperuser(false);
        Timestamp timestmp = new Timestamp(System.currentTimeMillis());
        user.setCreated(timestmp);
        user.setUpdated(timestmp);
        user.setUpdatedPlan(timestmp);
        String createdUserApiKey = userManagementService.createUser(user);
        assertEquals(StockyUtils.getApiKey(user.getName(), user.getPassword()), createdUserApiKey);
        
        // not allowed to create user
        StockyException ex = assertThrows(StockyException.class, () -> {
            userManagementService.createUser(user);});
    }
}
