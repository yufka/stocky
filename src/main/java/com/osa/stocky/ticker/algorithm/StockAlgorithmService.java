package com.osa.stocky.ticker.algorithm;

/**
 * Interface exposes methods obligatory for all "Algorithms" that manipulate stock data.
 * 
 * @author oleksii
 * @since 6 Nov 2022
 */
public interface StockAlgorithmService {

    double getPrediction(long timePoint);
}
