package com.scmspain.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MatcherUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatcherUtils.class);

    private static final Pattern linkPattern = Pattern.compile("(http|https):[^\\s]+(\\s)");

    /**
     * Checks that the text contains links and returns them within a list
     *
     * @param text - The text to be treated
     * @return List<String> - List of the links found in the text
     */
    public List<String> matchLinks(String text) {
        List<String> links = new ArrayList<>();
        if (!StringUtils.isEmpty(text)) {
            Matcher matcher = linkPattern.matcher(text);
            while (matcher.find()) {
                links.add(matcher.group());
            }
        }
        return links;
    }

}
