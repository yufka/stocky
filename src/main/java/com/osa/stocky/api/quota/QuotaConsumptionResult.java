package com.osa.stocky.api.quota;

/**
 *
 * @author oleksii
 * @since 8 Nov 2022
 */
public class QuotaConsumptionResult {

    private boolean consumed;
    
    private int remaining;
    
    public QuotaConsumptionResult() {
        
    }
    
    public QuotaConsumptionResult(final boolean consumed, final int remaining) {
        this.consumed = consumed;
        this.remaining = remaining;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
