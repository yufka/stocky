package com.osa.stocky.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author oleksii
 * @since 8 Nov 2022
 */
public class UserNamePasswordPairTest {

    @Test
    public void test() {
        final String name = "name";
        final String password = "password";
        UserNamePasswordPair pair = new UserNamePasswordPair(name, password);
        assertEquals(name, pair.getName());
        assertEquals(password, pair.getPassword());
        assertEquals("UserNamePasswordPair{name=name, passwrod=password}", pair.toString());
    }
}
