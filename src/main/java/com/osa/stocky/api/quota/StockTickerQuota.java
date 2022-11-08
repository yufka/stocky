package com.osa.stocky.api.quota;

import com.osa.stocky.subscription.SubscriptionPlan;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 *
 * @author oleksii
 * @since Nov 4, 2022
 */
@Service
public class StockTickerQuota implements StockTickerQuotaService {
    
    private static final Map<String, Map<String, Long>> apiTickersMap = new ConcurrentHashMap<>();

    @Override
    public QuotaConsumptionResult resolve(String apiKey, String stockTicker, SubscriptionPlan limits) {
        if (limits == null) {
            throw new IllegalArgumentException("Subscription plan limits object is null");
        }
        if (limits.getStocksCount() == null) {
            // there is no limit on number of stock tickers in subscription plan, return -1 for processing
            return new QuotaConsumptionResult(true, Integer.MAX_VALUE);
        }
        long tickersCount = limits.getStocksCount();
        long lifetime = limits.getStocksTimeLimit();
        
        if (!apiTickersMap.containsKey(apiKey)) {
            Map<String, Long> tickerMap = new ConcurrentHashMap<>();
            apiTickersMap.put(apiKey, tickerMap);
        }
        Map<String, Long> tickerMap = apiTickersMap.get(apiKey);
        clearOldEntries(tickerMap, lifetime); // remove expired values.
        if (tickerMap.containsKey(stockTicker)) {
            return new QuotaConsumptionResult(true, limits.getStocksCount().intValue() - tickerMap.size());
        }
        if (tickerMap.size() >= tickersCount) {
            return new QuotaConsumptionResult(false, 0);
        }
        tickerMap.put(stockTicker, System.currentTimeMillis());
        return new QuotaConsumptionResult(true, limits.getStocksCount().intValue() - tickerMap.size());
    }
    
    private void clearOldEntries(Map<String, Long> tickerMap, long lifetime) {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : tickerMap.entrySet()) {
            if (currentTime - entry.getValue() > lifetime) {
                tickerMap.remove(entry.getKey());
            }
        }
    }
}
