package com.scmspain.repositories;

import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@DataJpaTest
public class TweetRepositoryTest {

    @Autowired
    TweetRepository tweetRepository;

    @Test
    public void shouldFindAllNotDiscardedTweetsOrderedByPublicationDate() {
        // Saving 3 tweets
        Tweet tweet1 = new Tweet("publisher", "tweet1");
        tweet1.setPublicationDate(new Date());
        Tweet tweet2 = new Tweet("publisher", "tweet2");
        tweet2.setPublicationDate(subtractDaysCurrentDate(1));
        Tweet tweet3 = new Tweet("publisher", "tweet3");
        tweet3.setPublicationDate(subtractDaysCurrentDate(2));
        tweetRepository.save(tweet3);
        tweetRepository.save(tweet2);
        tweetRepository.save(tweet1);

        // Checking saved tweets are ordered by publication date
        List<Tweet> tweets = tweetRepository.findAllByDiscardedFalseOrderByPublicationDateDesc();
        Assert.assertEquals(tweets.get(0).getTweet(), "tweet1");
        Assert.assertEquals(tweets.get(1).getTweet(), "tweet2");
        Assert.assertEquals(tweets.get(2).getTweet(), "tweet3");
    }

    @Test
    public void shouldFindAllDiscardedTweetsOrderedByDiscardedDate() {
        // Saving 4 tweets
        Tweet tweet1 = new Tweet("publisher", "tweet1");
        tweet1.setPublicationDate(new Date());
        Tweet tweet2 = new Tweet("publisher", "tweet2");
        tweet2.setPublicationDate(subtractDaysCurrentDate(1));
        Tweet tweet3 = new Tweet("publisher", "tweet3");
        tweet3.setPublicationDate(subtractDaysCurrentDate(2));
        Tweet tweet4 = new Tweet("publisher", "tweet4");
        tweet4.setPublicationDate(subtractDaysCurrentDate(3));
        tweetRepository.save(tweet3);
        tweetRepository.save(tweet2);
        tweetRepository.save(tweet1);
        tweetRepository.save(tweet4);

        // Checking inserted tweets are not discarded
        List<Tweet> discardedTweets = tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc();
        Assert.assertEquals(discardedTweets.size(), 0);

        // Discarding tweet1 and tweet3
        tweet1.setDiscarded(true);
        tweet1.setDiscardedDate(new Date());
        tweetRepository.save(tweet1);
        tweet3.setDiscarded(true);
        tweet3.setDiscardedDate(subtractDaysCurrentDate(1));
        tweetRepository.save(tweet3);

        // Checking saved discarded tweets are discarded
        discardedTweets = tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc();
        Assert.assertEquals(discardedTweets.size(), 2);

        // Checking are ordered by discarded date
        Assert.assertEquals(discardedTweets.get(0).getTweet(), "tweet1");
        Assert.assertEquals(discardedTweets.get(1).getTweet(), "tweet3");
    }

    private Date subtractDaysCurrentDate(int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

}