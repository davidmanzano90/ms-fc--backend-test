package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.repositories.TweetRepository;
import com.scmspain.validators.TweetValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import java.util.NoSuchElementException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class TweetServiceImplTest {

    private TweetRepository tweetRepository;
    private TweetValidator tweetValidator;
    private MetricWriter metricWriter;
    private TweetServiceImpl tweetServiceImpl;

    @Captor
    private ArgumentCaptor<Delta<Number>> deltaMetricsCaptor;

    @Before
    public void setUp() {
        this.tweetRepository = mock(TweetRepository.class);
        this.tweetValidator = mock(TweetValidator.class);
        this.metricWriter = mock(MetricWriter.class);

        this.tweetServiceImpl = new TweetServiceImpl(tweetRepository, tweetValidator, metricWriter);
    }

    @Test
    public void shouldPublishTweetInsertANewTweet() throws Exception {
        this.tweetServiceImpl.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        verify(this.tweetRepository).save(any(Tweet.class));
        verify(metricWriter).increment(deltaMetricsCaptor.capture());
        Assert.assertEquals(deltaMetricsCaptor.getValue().getName(), "published-tweets");
        Assert.assertEquals(deltaMetricsCaptor.getValue().getValue(), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPublishTweetThrowAnExceptionWhenValidationFails() {
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
    public void shouldDiscardTweetThrowAnExceptionWhenTweetDoesNotExist() {
        when(this.tweetRepository.findOne(1L)).thenReturn(null);
        this.tweetServiceImpl.discardTweet(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDiscardTweetThrowAnExceptionWhenTweetIsDiscarded() {
        Tweet tweet = new Tweet("publisher", "tweet");
        tweet.setDiscarded(true);
        when(this.tweetRepository.findOne(1L)).thenReturn(tweet);
        this.tweetServiceImpl.discardTweet(1L);
    }

    @Test
    public void shouldDiscardTweetById() {
        when(this.tweetRepository.findOne(1L)).thenReturn(new Tweet("David", "My first tweet"));
        this.tweetServiceImpl.discardTweet(1L);
        verify(this.tweetRepository).save(any(Tweet.class));
        verify(metricWriter).increment(deltaMetricsCaptor.capture());
        Assert.assertEquals(deltaMetricsCaptor.getValue().getName(), "discarded-tweets");
        Assert.assertEquals(deltaMetricsCaptor.getValue().getValue(), 1);
    }

    @Test
    public void shouldListAllTweetsOrderedByPublicationDateDesc() {
        this.tweetServiceImpl.listAllTweets();
        verify(this.tweetRepository).findAllByDiscardedFalseOrderByPublicationDateDesc();
        verify(metricWriter).increment(deltaMetricsCaptor.capture());
        Assert.assertEquals(deltaMetricsCaptor.getValue().getName(), "times-queried-tweets");
        Assert.assertEquals(deltaMetricsCaptor.getValue().getValue(), 1);
    }

    @Test
    public void shouldListAllDiscardedTweetsOrderedByDiscardedDateDesc() {
        this.tweetServiceImpl.listAllDiscardedTweets();
        verify(this.tweetRepository).findAllByDiscardedTrueOrderByDiscardedDateDesc();
        verify(metricWriter).increment(deltaMetricsCaptor.capture());
        Assert.assertEquals(deltaMetricsCaptor.getValue().getName(), "times-queried-discarded-tweets");
        Assert.assertEquals(deltaMetricsCaptor.getValue().getValue(), 1);
    }

}