package com.osa.stocky.ticker;

import com.osa.stocky.ticker.algorithm.StockAlgorithmService;
import com.osa.stocky.ticker.external.ExternalStockInformationService;
import com.osa.stocky.util.StockyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Stock Ticker Implementation of required algorithm that takes a value from an external API
 * 
 * @author oleksii
 * @since Nov 4, 2022
 */
@Service
public class StockTicker implements StockTickerService {

    /**
     * External provider of stock information
     */
    @Autowired
    ExternalStockInformationService externalStockTickerService; 
    
    /**
     * Algorithm of prediction
     */
    @Autowired
    StockAlgorithmService algorithmService;
    
    @Override
    public Double getPrediction(final String ticker, long timeFrame) throws StockyException {
        Double stockTickerPrice = externalStockTickerService.getStockPrice(ticker);
        if (stockTickerPrice == null) {
            return null;
        }
        return stockTickerPrice + algorithmService.getPrediction(timeFrame);
    }
}
