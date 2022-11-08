package com.osa.stocky.ticker.external;

import com.osa.stocky.util.StockyException;

/**
 * Interface for stock ticker providers
 * @author oleksii
 * @since Nov 4, 2022
 */
public interface ExternalStockInformationService {

    /**
     * Retrieve stock price from endpoint
     * 
     * @param ticker name of stock ticker
     * @return price in USD of stock ticker, {@code null} in case if ticker was not found or price was not found.
     */
    public Double getStockPrice(final String ticker) throws StockyException;
}
