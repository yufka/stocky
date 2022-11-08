package com.osa.stocky.subscription;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

/**
 * Class represents rate + quota limit data and can be interpreted like this:
 * <br>
 * It is allowed to do {@link #callsCount} in {@link #callsTimeUnit}
 * <br>
 * In case if {@link #callsCount} is {@code null} there is no limit set on number of calls
 * <br>
 * In case if {@link #stocksCount} is {@code null} there is no limit set on number of stock tickers
 * 
 * @author oleksii
 * @since Nov 3, 2022
 */

@NamedNativeQuery(name = "find_by_name",
        query = "SELECT * FROM {h-schema}subscription_plan WHERE name = :name",
        resultClass = SubscriptionPlan.class)
@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "stock_count", nullable = true)
    private Long stocksCount;
    
    @Column(name = "stock_time_limit_millis", nullable = true)
    private Long stocksTimeLimit;
    
    @Column(name = "call_count", nullable = true)
    private Long callsCount;
    
    @Column(name = "call_time_limit_millis", nullable = true)
    private Long callsTimeLimit;
    
    public SubscriptionPlan() {
    }
    
    public SubscriptionPlan(final Long stocksCount, final Long stocksTimeLimit, 
            final Long callsCount, final Long callsTimeLimit) {
        this.stocksCount = stocksCount;
        this.stocksTimeLimit = stocksTimeLimit;
        this.callsCount = callsCount;
        this.callsTimeLimit = callsTimeLimit;
    }

    public Long getStocksCount() {
        return stocksCount;
    }

    public void setStocksCount(Long stocksCount) {
        this.stocksCount = stocksCount;
    }

    public Long getStocksTimeLimit() {
        return stocksTimeLimit;
    }

    public void setStocksTimeLimit(Long stocksTimeLimit) {
        this.stocksTimeLimit = stocksTimeLimit;
    }

    public Long getCallsCount() {
        return callsCount;
    }

    public void setCallsCount(Long callsCount) {
        this.callsCount = callsCount;
    }

    public Long getCallsTimeLimit() {
        return callsTimeLimit;
    }

    public void setCallsTimeLimit(Long callsTimeLimit) {
        this.callsTimeLimit = callsTimeLimit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SubscriptionPlan{" + "id=" + id + ", name=" + name + ", stocksCount=" + stocksCount
                + ", stocksTimeLimit=" + stocksTimeLimit + ", callsCount=" + callsCount
                + ", callsTimeLimit=" + callsTimeLimit + '}';
    }
    
    
}
