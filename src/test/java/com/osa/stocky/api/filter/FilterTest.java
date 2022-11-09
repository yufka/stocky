package com.osa.stocky.api.filter;

import com.osa.stocky.StockyApplication;
import com.osa.stocky.api.Ticker;
import com.osa.stocky.subscription.SubscriptionManagerService;
import com.osa.stocky.subscription.SubscriptionPlan;
import com.osa.stocky.user.CachedUserManagementService;
import com.osa.stocky.user.User;
import com.osa.stocky.util.StockyUtils;
import com.osa.stocky.util.TestUtil;
import java.util.Date;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author oleksii
 * @since 7 Nov 2022
 */
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes = StockyApplication.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class FilterTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private SubscriptionManagerService subscriptionManger;
    
    @Autowired
    private CachedUserManagementService userManagement;
    
    private static User testUser(String userName, String password) {
        User user = new User();
        user.setName(userName);
        user.setPassword(password);
        user.setSubscriptionId(1);
        return user;
    }
    
    @Test
    public void testAuthorizationFilter() throws Exception {
        mockMvc.perform(get("/user/subscription")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/user/subscription").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
        User realUser = testUser("admin", "admin");
        String realUserApiKey = StockyUtils.getApiKey(realUser.getName(), realUser.getPassword());
        mockMvc.perform(get("/user/subscription").contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Basic " + realUserApiKey)).andExpect(status().isOk());
        User fakeUser = testUser("admin", "notpassword");
        String fakeUserApiKey = StockyUtils.getApiKey(fakeUser.getName(), fakeUser.getPassword());
        mockMvc.perform(get("/user/subscription").contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Basic " + fakeUserApiKey)).andExpect(status().isUnauthorized());
    }
    
    /**
     * demo user can do 1000 calls per month.
     * 
     * Trigger 1000 calls to /user/subscription to trigger interceptor. and check headers.
     */
    @Test
    public void testDemoUserRateLimit() throws Exception { 
        testUserRateLimit("DEMO");
        testUserRateLimit("SILVER");
        testUserRateLimit("GOLD");
        testUserQuota("DEMO");
        
    }
    
    private MvcResult makeGetStockCall(User user, Ticker ticker) throws Exception {
        String userApiKey = StockyUtils.getApiKey(user.getName(), user.getPassword());
        return mockMvc.perform(post("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.asJsonString(ticker))
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userApiKey)
                
        ).andReturn();
    }
    
    public void testUserRateLimit(String subscriptionPlanName) throws Exception {
        User user = TestUtil.createUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        SubscriptionPlan plan = subscriptionManger.get(subscriptionPlanName);
        user.setSubscriptionId(plan.getId());
        userManagement.createUser(user);
        Ticker ticker = TestUtil.getTicker();
        for (int i = 0; i < plan.getCallsCount(); i++) {
            MvcResult result = makeGetStockCall(user, ticker);
            assertNotNull(result);
            assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
            String remaining = result.getResponse().getHeader("X-Rate-Limit-Remaining");
            int remainingInt = Integer.valueOf(remaining);
            assertTrue(remainingInt >= 0);
        }
        MvcResult result = makeGetStockCall(user, ticker);
        assertNotNull(result);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), result.getResponse().getStatus());
        String bucketReload = result.getResponse().getHeader("X-Rate-Limit-Retry-After-Milliseconds");
        assertNotNull(bucketReload);
        long waitPeriod = Long.valueOf(bucketReload);
        assertTrue(waitPeriod < plan.getCallsTimeLimit());
        
        // these plans have a short limit for calls, so let's test reset
        if ("SILVER".equals(subscriptionPlanName) || "GOLD".equals(subscriptionPlanName)) {
            Thread.sleep(plan.getCallsTimeLimit());
            result = makeGetStockCall(user, ticker);
            assertNotNull(result);
            assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
            String remaining = result.getResponse().getHeader("X-Rate-Limit-Remaining");
            int remainingInt = Integer.valueOf(remaining);
            assertTrue(remainingInt >= 0);
        }
    }
    
    public void testUserQuota(String subscriptionPlanName) throws Exception {
        User user = TestUtil.createUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        SubscriptionPlan plan = subscriptionManger.get(subscriptionPlanName);
        user.setSubscriptionId(plan.getId());
        userManagement.createUser(user);
        
        for (int i = 0; i < plan.getStocksCount(); i++) {
            MvcResult result = makeGetStockCall(user, TestUtil.getTicker());
            assertNotNull(result);
            assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
            String remaining = result.getResponse().getHeader("X-Quota-Limit-Remaining");
            int remainingInt = Integer.valueOf(remaining);
            assertTrue(remainingInt >= 0);
        }
        MvcResult result = makeGetStockCall(user, TestUtil.getTicker());
        assertNotNull(result);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), result.getResponse().getStatus());
    }
    
    @Test
    public void QuotaEmptyTickerTest() throws Exception {
        User user = TestUtil.createUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        SubscriptionPlan plan = subscriptionManger.get("DEMO");
        user.setSubscriptionId(plan.getId());
        userManagement.createUser(user);
        String userApiKey = StockyUtils.getApiKey(user.getName(), user.getPassword());
        MvcResult result = mockMvc.perform(post("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userApiKey)
        ).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
    
    @Test
    public void QuotaNullTickerTest() throws Exception {
        User user = TestUtil.createUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        SubscriptionPlan plan = subscriptionManger.get("DEMO");
        user.setSubscriptionId(plan.getId());
        userManagement.createUser(user);
        String userApiKey = StockyUtils.getApiKey(user.getName(), user.getPassword());
        MvcResult result = mockMvc.perform(post("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userApiKey)
        ).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
    
    @Test
    public void QuotaBadTickerTest() throws Exception {
        User user = TestUtil.createUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        SubscriptionPlan plan = subscriptionManger.get("DEMO");
        user.setSubscriptionId(plan.getId());
        userManagement.createUser(user);
        String userApiKey = StockyUtils.getApiKey(user.getName(), user.getPassword());
        MvcResult result = mockMvc.perform(post("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("scdsdcscdscd")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userApiKey)
        ).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
    
    @Test
    public void testInvalidTicker() throws Exception {
        Ticker ticker = TestUtil.getTicker();
        ticker.setName(null);
        testTicker(ticker);
        
        ticker.setName("");
        testTicker(ticker);
        
        ticker.setName(UUID.randomUUID().toString());
        ticker.setFrame(new Date(System.currentTimeMillis() - 100000));
        testTicker(ticker);
    }
    
    public void testTicker(Ticker ticker) throws Exception {
        User user = TestUtil.createUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        SubscriptionPlan plan = subscriptionManger.get("DEMO");
        user.setSubscriptionId(plan.getId());
        userManagement.createUser(user);
        String userApiKey = StockyUtils.getApiKey(user.getName(), user.getPassword());
        MvcResult result = mockMvc.perform(post("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.asJsonString(ticker))
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userApiKey)
        ).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
}
