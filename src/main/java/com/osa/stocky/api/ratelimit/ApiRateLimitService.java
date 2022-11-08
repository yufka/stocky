package com.osa.stocky.api.ratelimit;

import io.github.bucket4j.Bucket;

/**
 *
 * @author oleksii
 * @since Nov 3, 2022
 */
public interface ApiRateLimitService {
    
    Bucket resolve(String apiKey);
}
