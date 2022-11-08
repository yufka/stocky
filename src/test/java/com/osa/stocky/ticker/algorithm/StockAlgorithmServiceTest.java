package com.osa.stocky.ticker.algorithm;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author oleksii
 * @since 7 Nov 2022
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StockAlgorithmServiceTest {

    @Autowired
    private StockAlgorithmService service;
    
    @Test
    public void testAlgorithm() {
        for (int i = 0; i < 1000; i++) {
            double result = service.getPrediction(new Random().nextLong());
            assertTrue(result >= -100.0);
            assertTrue(result <= 100.0);
        }
    }
}
