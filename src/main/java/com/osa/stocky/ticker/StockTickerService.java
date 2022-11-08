package com.osa.stocky.ticker;

import com.osa.stocky.util.StockyException;

/**
 * Main Service that provides prediction of stock price for some stock ticker at some point of time.
 * @author oleksii
 * @since Nov 4, 2022
 */

public interface StockTickerService {
    
    /**
     * Get Stock ticker prediction.
     * 
     * @param ticker
     * @param timeFrame
     * @return {@code double} value of stock ticker price, {@code null} in case of no
     * stock ticker was found, or price was not found.
     * 
     * @throws StockyException
     */
    Double getPrediction(final String ticker, long timeFrame) throws StockyException;
}
