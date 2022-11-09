package com.osa.stocky.ticker.external;

import com.osa.stocky.util.StockyException;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Implementation of service that uses Yahoo Finance API to get stock ticker prices using a third-party client.
 * <br>
 * In case of manual implementation:
 * Client calls : "https://query1.finance.yahoo.com/v7/finance/quote?symbols=<ticker_name>";
 * according to result JSON it can be done via a simple client with price extraction from returned JSON.
 * 
 * @author oleksii
 * @since 6 Nov 2022
 */
@Service
public class YahooFinanceStock implements ExternalStockInformationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(YahooFinanceStock.class);

    @Override
    public Double getStockPrice(String ticker) throws StockyException {
        try {
            Stock stock = YahooFinance.get(ticker);
            if (stock == null || stock.getQuote() == null || stock.getQuote().getPrice() == null) {
                return null;
            }
            BigDecimal price = stock.getQuote().getPrice();
            return price.doubleValue();
        } catch (Exception e) {
            LOGGER.error("Filed to get stock information for ticker: " + ticker, e);
            throw new StockyException("Filed to get stock information for ticker: " + ticker, e);
        }
    }
}
