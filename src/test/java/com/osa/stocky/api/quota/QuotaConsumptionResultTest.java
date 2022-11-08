package com.osa.stocky.api.quota;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author oleksii
 * @since 8 Nov 2022
 */
public class QuotaConsumptionResultTest {
    
    @Test
    public void testQuota() {
        QuotaConsumptionResult res = new QuotaConsumptionResult();
        res.setConsumed(true);
        res.setRemaining(1);
        assertEquals(1, res.getRemaining());
        assertTrue(res.isConsumed());
    }
}
