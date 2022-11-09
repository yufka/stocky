package com.osa.stocky.api;

import com.osa.stocky.StockyApplication;
import com.osa.stocky.user.User;
import com.osa.stocky.util.TestUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author oleksii
 * @since Nov 4, 2022
 */
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, classes = StockyApplication.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StockTickerResourceTest {

    private static final String STOCKY_PATH = "/stock";
    
    private static final User USER = TestUtil.createUser("admin", "admin", true);

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getTickerInformation() throws Exception {
        Ticker ticker = new Ticker("AAPL", new Date(System.currentTimeMillis() + 10000000));
        MvcResult result = this.mockMvc
                .perform(
                        post(STOCKY_PATH)
                                .content(TestUtil.asJsonString(ticker))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                .andExpect(status().isOk()).andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertNotNull(resultString);
        Double value = Double.valueOf(resultString);
        assertTrue(value > -100.0);
    }
    
    @Test
    public void testNotFound() throws Exception {
        Ticker ticker = new Ticker(UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + 10000000));
        this.mockMvc
                .perform(
                        post(STOCKY_PATH)
                                .content(TestUtil.asJsonString(ticker))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testBadRequest() throws Exception {
        List<Ticker> badTickerList = new ArrayList<>();
        badTickerList.add(new Ticker(null, new Date(System.currentTimeMillis() + 10000000)));
        badTickerList.add(new Ticker("", new Date(System.currentTimeMillis() + 10000000)));
        badTickerList.add(new Ticker("AAPL", new Date(System.currentTimeMillis() - 10000000)));
        badTickerList.add(null);
        for (Ticker ticker : badTickerList) {
            this.mockMvc
                    .perform(
                            post(STOCKY_PATH)
                                    .content(TestUtil.asJsonString(ticker))
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                    .andExpect(status().isBadRequest());
        }
        
        this.mockMvc
                    .perform(
                            post(STOCKY_PATH)
                                    .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                    .andExpect(status().isBadRequest());
        
        this.mockMvc
                    .perform(
                            post(STOCKY_PATH)
                                    .content("")
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                    .andExpect(status().isBadRequest());
    }
    
}
