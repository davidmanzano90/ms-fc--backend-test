package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.repositories.TweetRepository;
import com.scmspain.validators.TweetValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TweetServiceImpl implements TweetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetServiceImpl.class);

    private TweetRepository tweetRepository;
    private TweetValidator tweetValidator;
    private MetricWriter metricWriter;

    public TweetServiceImpl(TweetRepository tweetRepository, TweetValidator tweetValidator, MetricWriter metricWriter) {
        this.tweetRepository = tweetRepository;
        this.tweetValidator = tweetValidator;
        this.metricWriter = metricWriter;
    }

    @Override
    public void publishTweet(String publisher, String text) {
        Tweet tweet = new Tweet(publisher, text);
        tweet.setPublicationDate(new Date());
        this.tweetValidator.validate(tweet);

        this.tweetRepository.save(tweet);
        this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
        LOGGER.debug("Tweet published!");
    }

    @Override
    public Tweet getTweet(Long id) {
        LOGGER.debug("Getting tweet with id '{}'", id);
        return tweetRepository.findOne(id);
    }

    @Override
    public void discardTweet(Long id) {
        Tweet tweet = getTweet(id);
        if (tweet != null && !tweet.getDiscarded()) {
            tweet.setDiscarded(Boolean.TRUE);
            tweet.setDiscardedDate(new Date());

            this.tweetRepository.save(tweet);
            this.metricWriter.increment(new Delta<Number>("discarded-tweets", 1));
            LOGGER.debug("Tweet with id '{}' discarded!", id);

        } else if (tweet != null) {
            LOGGER.warn("Tweet with id '{}' has already been discarded", id);
            throw new IllegalArgumentException("Tweet with id '" + id + "' has already been discarded");
        } else {
            LOGGER.warn("There is not any tweet with id '{}'", id);
            throw new NoSuchElementException("There is not any tweet with id '" + id + "'");
        }
    }

    @Override
    public List<Tweet> listAllTweets() {
        List<Tweet> tweets = tweetRepository.findAllByDiscardedFalseOrderByPublicationDateDesc();
        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        return tweets;
    }

    @Override
    public List<Tweet> listAllDiscardedTweets() {
        List<Tweet> discardedTweets = tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc();
        this.metricWriter.increment(new Delta<Number>("times-queried-discarded-tweets", 1));
        return discardedTweets;
    }
}
