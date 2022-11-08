package com.osa.stocky.api;

import com.osa.stocky.ticker.StockTickerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author oleksii
 * @since Nov 4, 2022
 */
@RestController
@RequestMapping(path = "/stock")
public class StockResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StockResource.class);

    @Autowired
    StockTickerService stockTickerService;

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStockTickerPrediction(@RequestBody Ticker ticker) {
        try {
            Double prediction = stockTickerService.getPrediction(ticker.getName(), ticker.getFrame().getTime());
            if (prediction == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(String.valueOf(prediction));
        } catch (Throwable th) {
            LOGGER.error("Could not retrieve stock ticker information for : " + ticker, th);
            return ResponseEntity.internalServerError().build();
        }
    }
}
