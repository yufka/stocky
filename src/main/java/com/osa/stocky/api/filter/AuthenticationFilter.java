package com.osa.stocky.api.filter;

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
 * Filter executed first in the chain to filter out unauthorized requests. Filter checks if Authorization header is set
 * in request and if credentials match a known user from DB.
 *
 * @author oleksii
 * @since 5 Nov 2022
 */
@Component
@Order(1)
public class AuthenticationFilter implements Filter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private CachedUserManagementService userManagementService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            final String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String userApiKey = StockyUtils.getApiKey(authorizationHeaderValue);
            if (userApiKey == null || userApiKey.isEmpty()) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            User user = userManagementService.get(userApiKey);
            if (user == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            chain.doFilter(servletRequest, servletResponse);
        } catch (Throwable th) {
            LOGGER.error("Error occured during Authentication.", th);
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
