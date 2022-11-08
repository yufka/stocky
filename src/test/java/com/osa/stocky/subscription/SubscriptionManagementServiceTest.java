package com.osa.stocky.subscription;

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
//@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SubscriptionManagementServiceTest {

    @Autowired
    SubscriptionManagerService managementService;
    
    @Test
    public void getSubscriptionPlan() {
        assertNotNull(managementService.get("DEMO")); // get from DB, put in cache
        assertNotNull(managementService.get("DEMO")); // get from cache
        
        // do not change lines, since calls put stuff in cache
        assertNotNull(managementService.get(2)); // get from DB, put in cache
        assertNotNull(managementService.get(3)); // get from DB, put in cache
        assertNotNull(managementService.get("SILVER")); // get from DB, put in cache
        assertNotNull(managementService.get("GOLD")); // get from DB, put in cache
        // from here you can change lines
        assertNotNull(managementService.get(1)); // get from DB, put in cache
        assertNull(managementService.get(UUID.randomUUID().toString()));
        assertNull(managementService.get(-1));
    }
    
    @Test
    public void getCheckSubscriptionParameters() {
        SubscriptionPlan demo = managementService.get("DEMO");
        assertEquals(1000, demo.getCallsCount());
        assertEquals(2629800000L, demo.getCallsTimeLimit());
        assertEquals(10, demo.getStocksCount());
        assertEquals(2629800000L, demo.getStocksTimeLimit());
        
        SubscriptionPlan silver = managementService.get("SILVER");
        assertEquals(1, silver.getCallsCount());
        assertEquals(60000L, silver.getCallsTimeLimit());
        assertEquals(100, silver.getStocksCount());
        assertEquals(2629800000L, silver.getStocksTimeLimit());
        
        SubscriptionPlan gold = managementService.get("GOLD");
        assertNull(gold.getStocksCount());
        assertNull(gold.getStocksTimeLimit());
        assertEquals(1, gold.getCallsCount());
        assertEquals(10000L, gold.getCallsTimeLimit());
    }
}
