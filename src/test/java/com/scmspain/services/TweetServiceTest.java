package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.repositories.TweetRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class TweetServiceTest {

    @Autowired
    private TweetService tweetService;
    @Mock
    private TweetRepository tweetRepository;
    @Mock
    private MetricWriter metricWriter;

    @Test
    public void shouldInsertANewTweet() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");

        verify(tweetRepository).save(any(Tweet.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionWhenTweetLengthIsInvalid() throws Exception {
        tweetService.publishTweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
    }
}
