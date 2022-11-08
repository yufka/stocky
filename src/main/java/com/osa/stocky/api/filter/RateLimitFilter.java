package com.osa.stocky.api.filter;

import com.osa.stocky.api.ratelimit.ApiRateLimitService;
import com.osa.stocky.util.StockyUtils;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
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
 * Filter runs second in the chain and checks the rate limit condition on number of calls per time unit depending on
 * subscription models:
 *
 * <ul>
 * <li>DEMO: 1000 / month</li>
 * <li>SILVER 1/ minute</li>
 * <li>GOLD 1 / 10 sec</li>
 * </ul>
 *
 * This filter is called after the {@link AuthenticationFilter} so it can be assumed that the call is authorized and
 * Authorization header is set.
 *
 * @author oleksii
 * @since 5 Nov 2022
 */
@Component
@Order(2)
public class RateLimitFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    @Autowired
    private ApiRateLimitService rateLimitService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            // Since authentication interceptor checked that API-key is available in the headers of request
            // there is no need to do any checks here.
            final String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String valueBase64 = authorizationHeaderValue.substring(StockyUtils.BASIC_SRTING.length());
            // we track number of accesses only to stock resource
            if ("/stock".equals(request.getRequestURI().substring(request.getContextPath().length()))) {
                Bucket bucket = rateLimitService.resolve(valueBase64);

                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                if (probe.isConsumed()) {
                    response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
                    chain.doFilter(servletRequest, servletResponse);
                } else {
                    long waitForRefill = probe.getNanosToWaitForRefill() / 1000000; // convert to millis
                    String millisecondsToWait = String.valueOf(waitForRefill);
                    response.addHeader("X-Rate-Limit-Retry-After-Milliseconds", millisecondsToWait);
                    response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
                    response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Limit");
                }
            } else {
                chain.doFilter(servletRequest, servletResponse);
            }
        } catch (Throwable th) {
            LOGGER.error("Error occured during Rate limit filtering", th);
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }

}
