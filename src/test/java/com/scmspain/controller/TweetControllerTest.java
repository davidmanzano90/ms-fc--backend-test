package com.scmspain.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.context).build();
    }


    @Test
    public void shouldReturn201WhenInsertingAValidTweet() throws Exception {
        mockMvc.perform(newTweet("Prospect", "Breaking the law"))
                .andExpect(status().is(201));
    }

    @Test
    public void shouldReturn400WhenInsertingAnInvalidTweet() throws Exception {
        mockMvc.perform(newTweet("Schibsted Spain", "Schibsted Media Group is an international media " +
                "group that owns the leading newspapers in Sweden and Norway. It is also one of the world’s leading " +
                "online classified ads businesses, and is active in 22 countries in Europe, Asia, Africa and America."))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturnAllPublishedTweets() throws Exception {
        mockMvc.perform(newTweet("Yo", "How are you?"))
                .andExpect(status().is(201));

        List<Tweet> insertedTweets = listTweets();
        Assert.assertEquals(insertedTweets.size(), 1);
    }

    @Test
    public void shouldReturn404WhenDiscardingTweetNotExist() throws Exception {
        mockMvc.perform(discardTweet("10"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldReturn400WhenDiscardingTweetIdIsNotANumber() throws Exception {
        mockMvc.perform(discardTweet("dummy"))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldDiscardAllTweetsAndReturnInAList() throws Exception {
        mockMvc.perform(newTweet("Yo", "How are you?"))
                .andExpect(status().is(201));

        mockMvc.perform(newTweet("Prospect", "Breaking the law"))
                .andExpect(status().is(201));

        List<Tweet> insertedTweets = listTweets();
        for (Tweet tweet : insertedTweets) {
            mockMvc.perform(discardTweet(tweet.getId().toString()))
                    .andExpect(status().is(200));
        }

        List<Tweet> discardedTweets = listDiscardedTweets();
        Assert.assertEquals(discardedTweets.size(), insertedTweets.size());
    }

    private MockHttpServletRequestBuilder newTweet(String publisher, String tweet) {
        return post("/tweet")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet));
    }

    private MockHttpServletRequestBuilder discardTweet(String tweet) {
        return post("/discarded")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"tweet\": \"%s\"}", tweet));
    }

    private List<Tweet> listTweets() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/tweet"))
                .andExpect(status().is(200)).andReturn();
        return new ObjectMapper().readValue(
                getResult.getResponse().getContentAsString(), new TypeReference<List<Tweet>>() {
                });
    }

    private List<Tweet> listDiscardedTweets() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/discarded"))
                .andExpect(status().is(200)).andReturn();
        return new ObjectMapper().readValue(
                getResult.getResponse().getContentAsString(), new TypeReference<List<Tweet>>() {
                });
    }

}
