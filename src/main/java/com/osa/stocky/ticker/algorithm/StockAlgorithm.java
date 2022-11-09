package com.osa.stocky.ticker.algorithm;

import java.security.SecureRandom;
import org.springframework.stereotype.Service;

/**
 * Implementation of algorithm described in the task. Returns some random value [-100,100].
 * 
 * @author oleksii
 * @since 6 Nov 2022
 */
@Service
public class StockAlgorithm implements StockAlgorithmService {
    
    private static final double MAX = 100.0;

    final SecureRandom random = new SecureRandom();
    
    @Override
    public double getPrediction(long timePoint) {
        return random.nextDouble(MAX * 2) - MAX; // scale to [-100;100]
    }

}
