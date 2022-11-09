package com.osa.stocky.util;

import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


/**
 *
 * @author oleksii
 * @since 6 Nov 2022
 */
public class StockyUtilsTest {
    
    private static final String VALID_AUTHORIZATION = "Basic YWxleDpwYXNz";
    
    @Test
    public void testGetApiKey() {
        final String result = StockyUtils.getApiKey(VALID_AUTHORIZATION);
        assertNotNull(result);
        assertEquals("YWxleDpwYXNz", result);
        
        assertNull(StockyUtils.getApiKey(null));
        assertNull(StockyUtils.getApiKey(""));
        assertNull(StockyUtils.getApiKey(" YWxleDpwYXNz"));
        assertNull(StockyUtils.getApiKey("Basic "));
        assertNull(StockyUtils.getApiKey("  scd"));
        assertNotNull(StockyUtils.getApiKey("Basic 2"));
        assertEquals("2", StockyUtils.getApiKey("Basic 2"));
        
        assertNull(StockyUtils.getUserCredentials(null));
        assertNull(StockyUtils.getUserCredentials(""));
        assertNull(StockyUtils.getUserCredentials(Base64.getEncoder().encodeToString((":pass").getBytes())));
        assertNull(StockyUtils.getUserCredentials(Base64.getEncoder().encodeToString(("name:").getBytes())));
        assertNull(StockyUtils.getUserCredentials(Base64.getEncoder().encodeToString((":").getBytes())));
        assertNull(StockyUtils.getUserCredentials(Base64.getEncoder().encodeToString(("sdkcjnscd").getBytes())));
        
        assertNull(StockyUtils.getUserCredentials(Base64.getEncoder().encodeToString(("::::").getBytes())));
    }
}
