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
public class TweetService {

    private TweetRepository tweetRepository;
    private TweetValidator tweetValidator;
    private MetricWriter metricWriter;

    public TweetService(TweetRepository tweetRepository, TweetValidator tweetValidator, MetricWriter metricWriter) {
        this.tweetRepository = tweetRepository;
        this.tweetValidator = tweetValidator;
        this.metricWriter = metricWriter;
    }

    /**
     * Push tweet to repository
     * Parameter - publisher - creator of the Tweet
     * Parameter - text - Content of the Tweet
     * Result - recovered Tweet
     */
    public void publishTweet(String publisher, String text) {
        Tweet tweet = new Tweet();
        tweet.setTweet(text);
        tweet.setPublisher(publisher);
        tweet.setPublicationDate(new Date());
        tweetValidator.validate(tweet);

        tweetRepository.save(tweet);
        metricWriter.increment(new Delta<Number>("published-tweets", 1));
    }

    /**
     * Recover tweet from repository
     * Parameter - id - id of the Tweet to retrieve
     * Result - retrieved Tweet
     */
    public Tweet getTweet(Long id) {
        return tweetRepository.findOne(id);
    }

    public void discardTweet(Long id) throws NoSuchElementException {
        Tweet tweet = tweetRepository.findOne(id);
        if (tweet != null) {
            tweet.setDiscarded(Boolean.TRUE);
            tweet.setDiscardedDate(new Date());

            tweetRepository.save(tweet);
            metricWriter.increment(new Delta<Number>("published-tweets", -1));
        } else {
            throw new NoSuchElementException("There is not any tweet with id " + id);
        }
    }

    /**
     * Recover tweet from repository
     * Parameter - id - id of the Tweet to retrieve
     * Result - retrieved Tweet
     */
    public List<Tweet> listAllTweets() {
        List<Tweet> tweets = tweetRepository.findAllByDiscardedFalseOrderByPublicationDateDesc();
        metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        return tweets;
    }


    public List<Tweet> listAllDiscardedTweets() {
        List<Tweet> discardedTweets = tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc();
        metricWriter.increment(new Delta<Number>("times-queried-discarded-tweets", 1));
        return discardedTweets;
    }
}
