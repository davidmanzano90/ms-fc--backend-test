package com.scmspain.validators;

import com.scmspain.entities.Tweet;
import com.scmspain.utils.MatcherUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class TweetValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetValidator.class);

    private static final int TWEET_MAX_CHARACTERS = 140;

    private MatcherUtils matcherUtils;

    public TweetValidator(MatcherUtils matcherUtils) {
        this.matcherUtils = matcherUtils;
    }

    /**
     * /**
     * Perform some checks to confirm that the tweet is valid, if it is not correct, throws an exception
     *
     * @param tweet - The tweet to validate
     */
    public void validate(Tweet tweet) {
        LOGGER.debug("Validating tweet {}", tweet);
        if (StringUtils.isEmpty(tweet.getPublisher())) {
            throw new IllegalArgumentException("Tweet must have a publisher");
        }
        if (StringUtils.isEmpty(tweet.getTweet())) {
            throw new IllegalArgumentException("Tweet must not be empty or null");
        } else if (tweet.getTweet().length() - countListCharacters(matcherUtils.matchLinks(tweet.getTweet()))
                > TWEET_MAX_CHARACTERS) {
            throw new IllegalArgumentException("Tweet must not be greater than 140 characters");
        }
    }

    private int countListCharacters(List<String> list) {
        int count = 0;
        for (String str : list) {
            count += str.length();
        }
        return count;
    }
}
