package com.osa.stocky.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osa.stocky.StockyApplication;
import com.osa.stocky.user.User;
import com.osa.stocky.util.StockyUtils;
import com.osa.stocky.util.TestUtil;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author oleksii
 * @since 8 Nov 2022
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = StockyApplication.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserResourceTest {

    private static final String USER_SUBSCRIPTION_PATH = "/user/subscription";
    
    private static final String USER_PATH = "/user";

    private static final User USER = TestUtil.createUser("admin", "admin", true);

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetUserSubscription() throws Exception {
        MvcResult result = this.mockMvc
                .perform(
                        get(USER_SUBSCRIPTION_PATH)
                                .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                .andExpect(status().isOk())
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertEquals("DEMO", resultString);
    }

    @Test
    public void testUpdateUserSubscriptionFail() throws Exception {
        this.mockMvc
                .perform(
                        put(USER_SUBSCRIPTION_PATH)
                                .content("SILVER")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testCreaterUser() throws Exception {
        
        UserDTO newUser = new UserDTO();
        newUser.setName(UUID.randomUUID().toString());
        newUser.setPassword(UUID.randomUUID().toString());
        newUser.setSubscriptionPlan("SILVER");
        
        MvcResult result = this.mockMvc
                .perform(
                        post(USER_PATH)
                                .content(asJsonString(newUser))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                .andExpect(status().isCreated()).andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertNotNull(resultString);
        assertEquals(resultString, StockyUtils.getApiKey(newUser.getName(), newUser.getPassword()));
        
        
    }
    
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
