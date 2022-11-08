package com.osa.stocky.user;

import java.sql.Timestamp;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author oleksii
 * @since 7 Nov 2022
 */
public class UserTest {
    
    private static final int ID = 1;
    
    private static final int SUBSCRIPTION_ID = 1;
    
    private static final String NAME = UUID.randomUUID().toString();
    
    private static final String PASSWORD = UUID.randomUUID().toString();
    
    private static final long TIME = System.currentTimeMillis();

    @Test
    public void testUser() {
        User user = new User();
        user.setId(ID);
        user.setName(NAME);
        user.setPassword(PASSWORD);
        user.setSubscriptionId(SUBSCRIPTION_ID);
        user.setUpdated(new Timestamp(TIME));
        user.setCreated(new Timestamp(TIME));
        user.setUpdatedPlan(new Timestamp(TIME));
        user.setSuperuser(false);
        
        assertEquals(ID, user.getId());
        assertEquals(SUBSCRIPTION_ID, user.getSubscriptionId());
        assertEquals(NAME, user.getName());
        assertEquals(PASSWORD, user.getPassword());
        assertEquals(new Timestamp(TIME), user.getCreated());
        assertEquals(new Timestamp(TIME), user.getUpdated());
        assertEquals(new Timestamp(TIME), user.getUpdatedPlan());
        assertNotNull(user.toString());
        assertFalse(user.isSuperuser());
    }
}
