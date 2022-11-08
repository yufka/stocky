package com.osa.stocky.ticker.external;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author oleksii
 * @since 8 Nov 2022
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ExternalStockInformationServiceTest {

    
    @Autowired
    ExternalStockInformationService service;
    
    private static final String APPLE_TICKER = "AAPL";
    
    @Test
    public void testApple() {
        Double price = service.getStockPrice(APPLE_TICKER);
        assertNotNull(price);
        assertTrue(price > 0.0);
    }
    
    @Test
    public void testNotExisting() {
        Double price = service.getStockPrice(UUID.randomUUID().toString());
        assertNull(price);
    }
}
