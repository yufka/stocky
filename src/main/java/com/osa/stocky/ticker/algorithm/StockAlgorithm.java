package com.osa.stocky.ticker.algorithm;

import org.springframework.stereotype.Service;

/**
 * Implementation of algorithm described in the task. Returns some random value [-100,100].
 * 
 * @author oleksii
 * @since 6 Nov 2022
 */
@Service
public class StockAlgorithm implements StockAlgorithmService {

    @Override
    public double getPrediction(long timePoint) {
        return Math.random() * 200.0 - 100.0;
    }

}
