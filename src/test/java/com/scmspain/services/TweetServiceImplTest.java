package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.repositories.TweetRepository;
import com.scmspain.validators.TweetValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import java.util.NoSuchElementException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TweetServiceImplTest {

    private TweetRepository tweetRepository;
    private TweetValidator tweetValidator;
    private MetricWriter metricWriter;
    private TweetServiceImpl tweetServiceImpl;

    @Before
    public void setUp() {
        this.tweetRepository = mock(TweetRepository.class);
        this.tweetValidator = mock(TweetValidator.class);
        this.metricWriter = mock(MetricWriter.class);

        this.tweetServiceImpl = new TweetServiceImpl(tweetRepository, tweetValidator, metricWriter);
    }

    @Test
    public void shouldInsertANewTweet() throws Exception {
        this.tweetServiceImpl.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        verify(this.tweetRepository).save(any(Tweet.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionWhenValidationFails() {
        doThrow(new IllegalArgumentException()).when(this.tweetValidator).validate(any(Tweet.class));
        this.tweetServiceImpl.publishTweet("Pirate", "LeChuck? He's the guy that went to the Governor's for " +
                "dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. " +
                "So he did. Then things really got ugly.");
    }

    @Test
    public void shouldGetTweetById() {
        this.tweetServiceImpl.getTweet(1L);
        verify(this.tweetRepository).findOne(any(Long.class));
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowAnExceptionWithTweetDoesNotExist() {
        when(this.tweetRepository.findOne(1L)).thenReturn(null);
        this.tweetServiceImpl.discardTweet(1L);
    }

    @Test
    public void shouldDiscardTweetById() {
        when(this.tweetRepository.findOne(1L)).thenReturn(new Tweet("David", "My first tweet"));
        this.tweetServiceImpl.discardTweet(1L);
        verify(this.tweetRepository).save(any(Tweet.class));
    }

}