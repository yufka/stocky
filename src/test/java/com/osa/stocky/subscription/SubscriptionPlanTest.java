package com.osa.stocky.subscription;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author oleksii
 * @since 8 Nov 2022
 */
public class SubscriptionPlanTest {

    @Test
    public void testEmpty() {
        SubscriptionPlan plan = new SubscriptionPlan();
        assertNull(plan.getCallsCount());
        assertNull(plan.getCallsTimeLimit());
        assertNull(plan.getStocksCount());
        assertNull(plan.getStocksTimeLimit());
    }
    
    @Test
    public void testSetters() {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setCallsCount(1L);
        plan.setCallsTimeLimit(1L);
        plan.setStocksCount(1L);
        plan.setStocksTimeLimit(1L);
        plan.setName("demo");
        assertEquals(1L, plan.getCallsCount());
        assertEquals(1L, plan.getCallsTimeLimit());
        assertEquals(1L, plan.getStocksCount());
        assertEquals(1L, plan.getStocksTimeLimit());
        assertEquals("demo", plan.getName());
    }
    
    @Test
    public void testConstructor() {
        SubscriptionPlan plan = new SubscriptionPlan(1L, 1L, 1L, 1L);
        assertEquals(1L, plan.getCallsCount());
        assertEquals(1L, plan.getCallsTimeLimit());
        assertEquals(1L, plan.getStocksCount());
        assertEquals(1L, plan.getStocksTimeLimit());
        plan.setId(1);
        assertEquals(1, plan.getId());
        assertEquals("SubscriptionPlan{id=1, name=null, stocksCount=1, stocksTimeLimit=1, callsCount=1, callsTimeLimit=1}",
                plan.toString());
    }
}
