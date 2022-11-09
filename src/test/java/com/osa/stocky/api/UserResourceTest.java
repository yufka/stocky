package com.osa.stocky.api;

import com.osa.stocky.StockyApplication;
import com.osa.stocky.user.CachedUserManagementService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
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
    
    @Autowired
    private CachedUserManagementService userService;

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
        User user = TestUtil.createUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        userService.createUser(user);
        updatePlanAndExpect(user,"SILVER", status().isOk());
        updatePlanAndExpect(user, "SILVER", status().isOk());
        updatePlanAndExpect(user, "DEMO", status().isBadRequest());
        updatePlanAndExpect(user, "", status().isBadRequest());
        updatePlanAndExpect(user, UUID.randomUUID().toString(), status().isBadRequest());
    }
    
    private void updatePlanAndExpect(User user, String planName, ResultMatcher resultMatcher) throws Exception {
        this.mockMvc
                .perform(
                        put(USER_SUBSCRIPTION_PATH)
                                .content(planName)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(user)))
                .andExpect(resultMatcher);
    }
    
    @Test
    public void updateNullPlanAndExpect() throws Exception {
        this.mockMvc
                .perform(
                        put(USER_SUBSCRIPTION_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(USER)))
                .andExpect(status().isBadRequest());
    }
    
    
    @Test
    public void testCreaterUser() throws Exception {
        
        UserDTO newUser = createUserDTO();
        MvcResult result = createUser(USER, newUser);
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        
        String resultString = result.getResponse().getContentAsString();
        assertNotNull(resultString);
        assertEquals(resultString, StockyUtils.getApiKey(newUser.getName(), newUser.getPassword()));
    }
    
    private MvcResult createUser(User caller, UserDTO newUser) throws Exception {
        return this.mockMvc.perform(
                post(USER_PATH)
                        .content(TestUtil.asJsonString(newUser))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestUtil.getAuthorizationHeader(caller)))
                .andReturn();
    }
    
    @Test
    public void testUnauthorizedCreaterUser() throws Exception {
        User user = TestUtil.createUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        userService.createUser(user);
        UserDTO newUser = createUserDTO();
        MvcResult result = createUser(user, newUser);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }
    
    @Test
    public void testUserBadName() throws Exception {
        testUserWithBadName(null);
        testUserWithBadName("");
        testUserWithBadName(":");
        testUserWithBadName("test:test");
    }
    
    @Test
    public void testUserBadPassword() throws Exception {
        testUserWithBadName(null);
        testUserWithBadName("");
        testUserWithBadName(":");
        testUserWithBadName("test:test");
        
        testUserWithBadPassowrd(null);
        testUserWithBadPassowrd("");
        testUserWithBadPassowrd(":");
        testUserWithBadPassowrd("test:test");
        
        testUserWithBadSubscriptionPlan(null);
        testUserWithBadSubscriptionPlan("");
        testUserWithBadSubscriptionPlan(UUID.randomUUID().toString());
    }
    
    private void testUserWithBadSubscriptionPlan(String name) throws Exception {
        UserDTO newUser = createUserDTO();
        newUser.setSubscriptionPlan(name);
        MvcResult result = createUser(USER, newUser);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
    
    private void testUserWithBadName(String name) throws Exception {
        UserDTO newUser = createUserDTO();
        newUser.setName(name);
        MvcResult result = createUser(USER, newUser);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
    
    private void testUserWithBadPassowrd(String password) throws Exception {
        UserDTO newUser = createUserDTO();
        newUser.setPassword(password);
        MvcResult result = createUser(USER, newUser);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
    
    private UserDTO createUserDTO() {
        UserDTO newUser = new UserDTO();
        newUser.setName(UUID.randomUUID().toString());
        newUser.setPassword(UUID.randomUUID().toString());
        newUser.setSubscriptionPlan("SILVER");
        return newUser;
    }
    
}
