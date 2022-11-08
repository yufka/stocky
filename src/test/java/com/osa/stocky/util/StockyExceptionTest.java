package com.osa.stocky.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
/**
 *
 * @author oleksii
 * @since 7 Nov 2022
 */
public class StockyExceptionTest {
    
    @Test
    public void testExceptionCreation() {
        assertNotNull(new StockyException());
        assertNotNull(new StockyException("message"));
        assertNotNull(new StockyException("message", new Exception()));
        assertNotNull(new StockyException(new Exception()));
    }
}
