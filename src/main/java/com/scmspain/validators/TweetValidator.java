package com.scmspain.validators;

import com.scmspain.entities.Tweet;
import com.scmspain.utils.MatcherUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        if (StringUtils.isEmpty(StringUtils.trimAllWhitespace(tweet.getPublisher()))) {
            LOGGER.warn("Tweet must have a publisher");
            throw new IllegalArgumentException("Tweet must have a publisher");
        }
        if (StringUtils.isEmpty(StringUtils.trimAllWhitespace(tweet.getTweet()))) {
            LOGGER.warn("Tweet must not be empty or null");
            throw new IllegalArgumentException("Tweet must not be empty or null");

        } else if (tweet.getTweet().length() - countListCharacters(this.matcherUtils.matchLinks(tweet.getTweet()))
                > TWEET_MAX_CHARACTERS) {
            LOGGER.warn("Tweet must not be greater than {} characters", TWEET_MAX_CHARACTERS);
            throw new IllegalArgumentException("Tweet must not be greater than " + TWEET_MAX_CHARACTERS + " characters");
        }
    }

    private int countListCharacters(List<String> list) {
        AtomicInteger counter = new AtomicInteger(0);
        list.forEach((str) -> {
            counter.addAndGet(str.length());
        });
        return counter.get();
    }
}
