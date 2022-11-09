package com.osa.stocky.api.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.stocky.api.Ticker;
import com.osa.stocky.api.quota.QuotaConsumptionResult;
import com.osa.stocky.api.quota.StockTickerQuotaService;
import com.osa.stocky.subscription.SubscriptionManagerService;
import com.osa.stocky.subscription.SubscriptionPlan;
import com.osa.stocky.user.CachedUserManagementService;
import com.osa.stocky.user.User;
import com.osa.stocky.util.StockyUtils;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Quota Filer is run third in the chain and checks if user already expired quota on unique stock tickers defined in
 * subscription plan.
 *
 * @author oleksii
 * @since 5 Nov 2022
 */
@Component
@Order(3)
public class QuotaFilter implements Filter {

    private static final String NO_TICKER_PROVIDED_STRING = "No stock ticker provided";

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotaFilter.class);

    @Autowired
    private CachedUserManagementService userManagementService;

    @Autowired
    private StockTickerQuotaService stockTickerQuotaService;

    @Autowired
    private SubscriptionManagerService subscriptionManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            final String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String valueBase64 = StockyUtils.getApiKey(authorizationHeaderValue);
            User user = userManagementService.get(valueBase64);
            SubscriptionPlan rateLimit = subscriptionManager.get(user.getSubscriptionId());
            // extract ticker information
            CachedBodyHttpRequest cachedBodyHttpServletRequest = new CachedBodyHttpRequest(request);
            final String resourceRelativePath = request.getRequestURI().substring(request.getContextPath().length());
            
            // quota is checked only for stock resource
            if (!"/stock".equals(resourceRelativePath)) {
                chain.doFilter(cachedBodyHttpServletRequest, servletResponse);
                return;
            }

            Ticker ticker = getTicker(cachedBodyHttpServletRequest.getInputStream().readAllBytes());
            if (ticker == null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), NO_TICKER_PROVIDED_STRING);
                return;
            }
            QuotaConsumptionResult result = stockTickerQuotaService.resolve(valueBase64, ticker.getName(), rateLimit);
            if (result.isConsumed()) {
                if (result.getRemaining() != Integer.MAX_VALUE) {
                    response.addHeader("X-Quota-Limit-Remaining", String.valueOf(result.getRemaining()));
                }
            } else {
                response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Quota");
                return;
            }
            chain.doFilter(cachedBodyHttpServletRequest, servletResponse);
        } catch (Throwable th) {
            LOGGER.error("Error occured during Quta filtering", th);
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }
    
    /**
     * Extract ticker information form request body.
     * 
     * @param requestBodyBytes
     * @return 
     */
    private Ticker getTicker(byte[] requestBodyBytes) {
        String requestBodyString = new String(requestBodyBytes);
        if (requestBodyString.isEmpty()) {
            return null;
        }
        Ticker ticker = getTicker(requestBodyString);
        if (ticker == null) {
            return null;
        }
        if (ticker.getName() == null || ticker.getName().isEmpty() || ticker.getFrame() == null
                || ticker.getFrame().getTime() < System.currentTimeMillis()) {
            return null;
        }
        return ticker;
    }

    private Ticker getTicker(String body) {
        try {
            return new ObjectMapper().readValue(body, Ticker.class);
        } catch (Exception e) {
            LOGGER.warn("Failed to convert Request Body to Stock Ticker : " + body, e);
        }
        return null;
    }
}
