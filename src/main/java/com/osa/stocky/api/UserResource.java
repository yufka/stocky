package com.osa.stocky.api;

import com.osa.stocky.subscription.SubscriptionManagerService;
import com.osa.stocky.subscription.SubscriptionPlan;
import com.osa.stocky.user.CachedUserManagementService;
import com.osa.stocky.user.User;
import com.osa.stocky.util.StockyUtils;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint of user resource
 *
 * @author oleksii
 * @since Nov 4, 2022
 */
@RestController
@RequestMapping(path = "/user")
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private CachedUserManagementService userManagementService;

    @Autowired
    private SubscriptionManagerService subscriptionManager;

    @GetMapping(path = "/subscription")
    public ResponseEntity<String> getUserSubscriptionPlan(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String autorizationHeader) {
        final String apiKey = StockyUtils.getApiKey(autorizationHeader);
        User user = userManagementService.get(apiKey);
        return ResponseEntity.ok(subscriptionManager.get(user.getSubscriptionId()).getName());
    }

    @PostMapping()
    public ResponseEntity<String> createUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String autorizationHeader,
            @RequestBody UserDTO userDto) {
        try {
            // check if superuser is calling method
            final String apiKey = StockyUtils.getApiKey(autorizationHeader);
            User superUser = userManagementService.get(apiKey);
            if (!superUser.isSuperuser()) {
                return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }
            if (!isValid(userDto)) {
                return ResponseEntity.badRequest().build();
            }
            User user = new User();
            user.setName(userDto.getName());
            user.setPassword(userDto.getPassword());

            SubscriptionPlan subscriptionPlan = subscriptionManager.get(userDto.getSubscriptionPlan());
            if (subscriptionPlan == null) {
                logUnrecognizedPlan(userDto.getSubscriptionPlan());
                return ResponseEntity.badRequest().build();
            }
            user.setSubscriptionId(subscriptionPlan.getId());
            user.setSuperuser(false);
            Timestamp creationTimestamp = new Timestamp(System.currentTimeMillis());
            user.setCreated(creationTimestamp);
            user.setUpdated(creationTimestamp);
            user.setUpdatedPlan(creationTimestamp);
            
            String userApiKey = userManagementService.createUser(user);
            return new ResponseEntity<>(userApiKey, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Failed to create user", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean isValid(final UserDTO user) {
        return user != null && isValidCredential(user.getName()) && isValidCredential(user.getPassword());
    }

    private boolean isValidCredential(final String name) {
        return name != null && !name.isEmpty() && !name.contains(":");
    }

    @PutMapping("/subscription")
    public ResponseEntity<String> updateUserSubscriptionPlan(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String autorizationHeader,
            @RequestBody String userPlan) {
        try {
            if (userPlan == null || userPlan.isEmpty()) {
                logUnrecognizedPlan(userPlan);
                return ResponseEntity.badRequest().build();
            }
            SubscriptionPlan newSubscriptionPlan = subscriptionManager.get(userPlan);
            if (newSubscriptionPlan == null) {
                logUnrecognizedPlan(userPlan);
                return ResponseEntity.badRequest().build();
            }

            final String apiKey = StockyUtils.getApiKey(autorizationHeader);
            User user = userManagementService.get(apiKey);
            SubscriptionPlan userSubscriptionPlan = subscriptionManager.get(user.getSubscriptionId());

            if (userSubscriptionPlan.getId() == newSubscriptionPlan.getId()) {
                LOGGER.warn("User provided the same subscription plan to override: " + newSubscriptionPlan.getName());
                return ResponseEntity.ok(userSubscriptionPlan.getName()); // nothing was done, thus not a problem.
            }
            
            if (System.currentTimeMillis() - StockyUtils.MONTH_MILLIS < user.getUpdatedPlan().getTime()) {
                LOGGER.warn("Failed to override user subscription plan, due to update time constraint");
                return ResponseEntity.badRequest().build();
            }

            userManagementService.updateSubscription(apiKey, newSubscriptionPlan.getId());
            user = userManagementService.get(apiKey);
            return ResponseEntity.ok(subscriptionManager.get(user.getSubscriptionId()).getName());
        } catch (Throwable e) {
            LOGGER.error("Failed to update user subscription plan", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private static final String UNRECOGNIZED_MESSAGE = "Unrecognized user subscription plan: ";
    
    private void logUnrecognizedPlan(String planName) {
        LOGGER.error(UNRECOGNIZED_MESSAGE + planName);
    }
}
