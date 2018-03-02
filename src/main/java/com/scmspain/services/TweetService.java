package com.scmspain.services;

import com.scmspain.entities.Tweet;

import java.util.List;


public interface TweetService {

    /**
     * Push tweet to repository
     *
     * @param publisher - Creator of the Tweet
     * @param text      - Content of the Tweet
     */
    void publishTweet(String publisher, String text);

    /**
     * Recover tweet from repository
     *
     * @param id - Id of the Tweet to retrieve
     * @return Tweet - Retrieved Tweet
     */
    Tweet getTweet(Long id);

    /**
     * Mark as discarded the tweet with the specified id, if the tweet is not found it throws an exception
     *
     * @param id - The id of the tweet
     */
    void discardTweet(Long id);

    /**
     * Retrieve a list of tweets that has not been discarded, sorted in descending order on the day of publication
     *
     * @return List<Tweet> - No discarded tweets
     */
    List<Tweet> listAllTweets();

    /**
     * Retrieve a list of discarded tweets sorted in descending order on the day they were discarded
     *
     * @return List<Tweet> - Discarded tweets
     */
    List<Tweet> listAllDiscardedTweets();

}
