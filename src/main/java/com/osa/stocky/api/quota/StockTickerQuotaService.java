package com.osa.stocky.api.quota;

import com.osa.stocky.subscription.SubscriptionPlan;

/**
 *
 * @author oleksii
 * @since Nov 4, 2022
 */
public interface StockTickerQuotaService {

    /**
     * @param apiKey API-key
     * @param stockTicker String defining stock ticker
     * @param limits subscription limits
     * @return number of elements that still can be queried.
     */
    public QuotaConsumptionResult resolve(final String apiKey, final String stockTicker, SubscriptionPlan limits);
}
