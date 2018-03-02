package com.scmspain.validators;

import com.scmspain.entities.Tweet;
import com.scmspain.utils.MatcherUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TweetValidatorTest {

    private MatcherUtils matcherUtils;
    private TweetValidator tweetValidator;

    @Before
    public void setUp() {
        this.matcherUtils = mock(MatcherUtils.class);

        this.tweetValidator = new TweetValidator(matcherUtils);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowsAnExceptionWhenPublisherIsNull() {
        Tweet tweet = new Tweet(null, "tweet");
        this.tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowsAnExceptionWhenPublisherIsWhiteSpace() {
        Tweet tweet = new Tweet("    ", "tweet");
        this.tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowsAnExceptionWhenPublisherIsEmpty() {
        Tweet tweet = new Tweet("", "tweet");
        this.tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowsAnExceptionWhenTweetIsNull() {
        Tweet tweet = new Tweet("Publisher", null);
        this.tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowsAnExceptionWhenTweetIsWhiteSpace() {
        Tweet tweet = new Tweet("Publisher", "    ");
        this.tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowsAnExceptionWhenTweetIsWhiteEmpty() {
        Tweet tweet = new Tweet("Publisher", "");
        this.tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowsAnExceptionWhenTweetHasMoreThan140CharsWithoutLinks() {
        Tweet tweet = new Tweet("Publisher", "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim " +
                "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.");

        when(this.matcherUtils.matchLinks(tweet.getTweet())).thenReturn(new ArrayList<>());
        this.tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowsAnExceptionWhenTweetHasMoreThan140CharsAndTwoLinks() {
        Tweet tweet = new Tweet("Publisher", "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim " +
                "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.");

        List<String> links = new ArrayList<>(Arrays.asList("https://www.foogle.co", "http://www.foogle.co"));
        when(this.matcherUtils.matchLinks(tweet.getTweet())).thenReturn(links);
        this.tweetValidator.validate(tweet);
    }

    @Test
    public void shouldWorksWhenTweetHasLessThan140CharsWithoutLinks() {
        Tweet tweet = new Tweet("Publisher", "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        when(this.matcherUtils.matchLinks(tweet.getTweet())).thenReturn(new ArrayList<>());
        this.tweetValidator.validate(tweet);
    }

    @Test
    public void shouldWorksWhenTweetHasLessThan140CharsAndTwoLinks() {
        Tweet tweet = new Tweet("Publisher", "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        List<String> links = new ArrayList<>(Arrays.asList("https://www.foogle.co", "http://www.foogle.co"));
        when(this.matcherUtils.matchLinks(tweet.getTweet())).thenReturn(links);
        this.tweetValidator.validate(tweet);
    }

}