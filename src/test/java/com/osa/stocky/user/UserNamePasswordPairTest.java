package com.osa.stocky.user;

import com.osa.stocky.util.UserNamePasswordPair;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author oleksii
 * @since 8 Nov 2022
 */
public class UserNamePasswordPairTest {
    
    private static final String NAME = UUID.randomUUID().toString();
    
    private static final String PASSWORD = UUID.randomUUID().toString();
    
    @Test
    public void testUserNamePasswordPair() {
        UserNamePasswordPair pair = new UserNamePasswordPair();
        pair.setName(NAME);
        pair.setPassword(PASSWORD);
        assertEquals(NAME, pair.getName());
        assertEquals(PASSWORD, pair.getPassword());
        assertEquals("UserNamePasswordPair{" + "name=" + NAME + ", passwrod=" + PASSWORD + '}', pair.toString());
    }
}
