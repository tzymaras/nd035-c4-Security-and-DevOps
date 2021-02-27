package com.udacity.ecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.udacity.ecommerce.model.persistence.*;
import com.udacity.ecommerce.model.requests.*;
import com.udacity.ecommerce.security.SecurityConstants;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;

import java.io.IOException;
import java.net.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class ApplicationIntegrationTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    JacksonTester<User> jsonUser;

    @Autowired
    JacksonTester<CreateUserRequest> jsonCreateUser;

    @Autowired
    JacksonTester<ModifyCartRequest> jsonModifyCart;

    private User user;
    private String token;

    @Before
    public void initializeTestData() throws Exception {
        this.user = this.createUser();
        this.token = this.logUserInAndGetToken(this.user);
    }

    @After
    public void resetTestData() {
        this.user = null;
        this.token = null;
    }

    @Test
    public void testAccessDeniedWithoutValidToken() throws Exception {
        this.mvc.perform(
            get(new URI("/api/user/id/" + 999))
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        this.mvc.perform(
            get(new URI("/api/user/username/nonExistentUsername"))
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        this.mvc.perform(
            post(new URI("/api/order/submit/nonExistentUsername"))
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        this.mvc.perform(
            get(new URI("/api/order/history/nonExistentUsername"))
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        this.mvc.perform(
            post(new URI("/api/cart/addToCart"))
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        this.mvc.perform(
            post(new URI("/api/cart/removeFromCart"))
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testAccessToPublicResources() throws Exception {
        this.mvc.perform(
            get(new URI("/api/item/1"))
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        this.mvc.perform(
            get(new URI(null, null, "/api/item/name/Round Widget", null))
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].description").value("A widget that is round"));
    }

    @Test
    public void testAccessUserDataWithValidToken() throws Exception {
        this.mvc.perform(
            get(new URI("/api/user/" + this.user.getUsername()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk());

        this.mvc.perform(
            get(new URI("/api/user/id/" + this.user.getId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk());

        this.mvc.perform(
            post(new URI("/api/order/submit/" + this.user.getUsername()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk());

        this.mvc.perform(
            get(new URI("/api/order/history/" + this.user.getUsername()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk());

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(1);
        cartRequest.setQuantity(5);
        cartRequest.setUsername(this.user.getUsername());

        this.mvc.perform(
            post(new URI("/api/cart/addToCart"))
                .content(jsonModifyCart.write(cartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(5))
            .andExpect(jsonPath("$.items[0].id").value(1));

        cartRequest.setQuantity(3);

        this.mvc.perform(
            post(new URI("/api/cart/removeFromCart"))
                .content(jsonModifyCart.write(cartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    public void testOrderHistoryReturnsSubmittedOrders() throws Exception {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(1);
        cartRequest.setQuantity(1);
        cartRequest.setUsername(this.user.getUsername());

        this.mvc.perform(
            post(new URI("/api/cart/addToCart"))
                .content(jsonModifyCart.write(cartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk());

        this.mvc.perform(
            post(new URI("/api/order/submit/" + this.user.getUsername()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].id").value(1));

        this.mvc.perform(
            get(new URI("/api/order/history/" + this.user.getUsername()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + this.token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].items[0].id").value(1));
    }

    private User createUser() throws Exception {
        String randomUsername = RandomString.make(15);
        String randomPassword = RandomString.make(10);

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(randomUsername);
        createUserRequest.setPassword(randomPassword);
        createUserRequest.setConfirmPassword(randomPassword);

        MvcResult result = this.mvc.perform(
            post(new URI("/api/user/create"))
                .content(jsonCreateUser.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(createUserRequest.getUsername()))
            .andReturn();

        User user = new User();
        user.setId(jsonUser.parse(result.getResponse().getContentAsString()).getObject().getId());
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(createUserRequest.getPassword());

        return user;
    }

    private String logUserInAndGetToken(User user) throws Exception {
        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(User.class, UnitTestUser.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        String userAsJson = mapper.writerFor(User.class).writeValueAsString(user);

        MvcResult mvcResult = this.mvc.perform(
            post(new URI("/login"))
                .content(userAsJson)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(header().exists(SecurityConstants.HEADER_STRING))
            .andReturn();

        String authorizationHeader = mvcResult
            .getResponse()
            .getHeader(SecurityConstants.HEADER_STRING);

        Assert.assertNotNull(authorizationHeader);
        return authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
    }
}