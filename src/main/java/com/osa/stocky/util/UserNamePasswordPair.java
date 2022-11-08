package com.osa.stocky.util;

/**
 *
 * @author oleksii
 * @since 8 Nov 2022
 */
public class UserNamePasswordPair {
    
    private String name;
    
    private String password;
    
    public UserNamePasswordPair() {
    }
    
    public UserNamePasswordPair(String name, String password) {
        this.name = name;
        this.password = password;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserNamePasswordPair{" + "name=" + name + ", passwrod=" + password + '}';
    }
    
    
}
