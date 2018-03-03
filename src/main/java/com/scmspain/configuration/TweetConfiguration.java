package com.scmspain.configuration;

import com.scmspain.controller.TweetController;
import com.scmspain.repositories.TweetRepository;
import com.scmspain.services.TweetService;
import com.scmspain.services.TweetServiceImpl;
import com.scmspain.utils.MatcherUtils;
import com.scmspain.validators.TweetValidator;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TweetConfiguration {

    @Bean
    public TweetService getTweetService(TweetRepository tweetRepository, TweetValidator tweetValidator,
                                        MetricWriter metricWriter) {
        return new TweetServiceImpl(tweetRepository, tweetValidator, metricWriter);
    }

    @Bean
    public TweetController getTweetController(TweetService tweetService) {
        return new TweetController(tweetService);
    }

    @Bean
    public TweetValidator getTweetValidator(MatcherUtils matcherUtils) {
        return new TweetValidator(matcherUtils);
    }

    @Bean
    public MatcherUtils getMatcherUtils() {
        return new MatcherUtils();
    }

}
