package com.osa.stocky.util;

import java.util.Base64;

/**
 *
 * @author oleksii
 * @since Nov 4, 2022
 */
public final class StockyUtils {
    
    public static final String BASIC_SRTING = "Basic ";
    
    public static final Long SECOND_MILLIS = 1000L;
    
    public static final Long MINUTE_MILLIS = 60000L;
    
    public static final Long MONTH_MILLIS = 2629800000L;
    
    /**
     * Extract API key from Authorization.
     * @param authorizationHeader
     * @return User API-key, or {@code null} if can not be extracted.
     */
    public static String getApiKey(final String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BASIC_SRTING)) {
            String apiKey = authorizationHeader.substring(StockyUtils.BASIC_SRTING.length());
            if (apiKey != null && apiKey.length() > 0) {
                return apiKey;
            }
        }
        return null;
    }
    
    public static UserNamePasswordPair getUserCredentials(String apiKey) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return null;
            }
            String decodedApiKey = new String(Base64.getDecoder().decode(apiKey));
            String[] split = decodedApiKey.split(":");
            if (split.length != 2 || split[0].length() == 0 || split[1].length() == 0) {
                return null;
            }
            return new UserNamePasswordPair(split[0], split[1]);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getApiKey(final String name, final String password) {
        return new String(Base64.getEncoder().encode((name + ":" + password).getBytes()));
    }
}
