package com.scmspain.controller;

import com.scmspain.controller.command.DiscardTweetCommand;
import com.scmspain.controller.command.PublishTweetCommand;
import com.scmspain.entities.Tweet;
import com.scmspain.services.TweetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@RestController
public class TweetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetController.class);

    private TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/tweet")
    public List<Tweet> listAllTweets() {
        LOGGER.debug("Listing all non discarded tweets");
        return this.tweetService.listAllTweets();
    }

    @PostMapping("/tweet")
    @ResponseStatus(CREATED)
    public void publishTweet(@RequestBody PublishTweetCommand publishTweetCommand) {
        LOGGER.debug("Publishing tweet with publisher '{}' and text '{}'", publishTweetCommand.getPublisher(),
                publishTweetCommand.getTweet());
        this.tweetService.publishTweet(publishTweetCommand.getPublisher(), publishTweetCommand.getTweet());
    }

    @GetMapping("/discarded")
    public List<Tweet> listAllDiscarded() {
        LOGGER.debug("Listing all discarded tweets");
        return this.tweetService.listAllDiscardedTweets();
    }

    @PostMapping("/discarded")
    @ResponseStatus(OK)
    public void discardTweet(@RequestBody DiscardTweetCommand discardTweetCommand) {
        LOGGER.debug("Discarding tweet with id '{}'", discardTweetCommand.getTweet());
        this.tweetService.discardTweet(discardTweetCommand.getTweet());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Object invalidArgumentException(IllegalArgumentException ex) {
        return handleException(ex);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Object invalidArgumentException(NoSuchElementException ex) {
        return handleException(ex);
    }

    private Object handleException(Exception ex) {
        return new Object() {
            public String message = ex.getMessage();
            public String exceptionClass = ex.getClass().getSimpleName();
        };
    }

}
