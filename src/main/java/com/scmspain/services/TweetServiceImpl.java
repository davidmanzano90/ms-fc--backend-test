package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.repositories.TweetRepository;
import com.scmspain.validators.TweetValidator;
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
    }

    @Override
    public Tweet getTweet(Long id) {
        return tweetRepository.findOne(id);
    }

    @Override
    public void discardTweet(Long id) {
        Tweet tweet = getTweet(id);
        if (tweet != null) {
            tweet.setDiscarded(Boolean.TRUE);
            tweet.setDiscardedDate(new Date());

            this.tweetRepository.save(tweet);
            this.metricWriter.increment(new Delta<Number>("published-tweets", -1));
        } else {
            throw new NoSuchElementException("There is not any tweet with id " + id);
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
