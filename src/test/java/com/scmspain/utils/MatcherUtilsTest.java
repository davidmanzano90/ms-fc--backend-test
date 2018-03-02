package com.scmspain.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MatcherUtilsTest {

    private MatcherUtils matcherUtils;

    @Before
    public void setUp() {
        this.matcherUtils = new MatcherUtils();
    }

    @Test
    public void shouldMatchTheLinkWhenHasBeginningText() {
        String text = "Hey http://foogle.co ";
        List<String> links = matcherUtils.matchLinks(text);
        Assert.assertEquals(links.size(), 1);
        Assert.assertEquals(links.get(0), "http://foogle.co ");
    }

    @Test
    public void shouldNotMatchTheLinkWhenNotFinishWithSpace() {
        String text = "Hey http://foogle.co";
        List<String> links = matcherUtils.matchLinks(text);
        Assert.assertEquals(links.size(), 0);
    }

    @Test
    public void shouldMatchTheTwoLinksWhenHasBeginningText() {
        String text = "Hey http://foogle.co http://foogle.es ";
        List<String> links = matcherUtils.matchLinks(text);
        Assert.assertEquals(links.size(), 2);
        Assert.assertEquals(links.get(0), "http://foogle.co ");
        Assert.assertEquals(links.get(1), "http://foogle.es ");
    }

    @Test
    public void shouldMatchTheTwoLinksWhenIsBetweenTwoTexts() {
        String text = "Hey http://foogle.co http://foogle.es Hey";
        List<String> links = matcherUtils.matchLinks(text);
        Assert.assertEquals(links.size(), 2);
        Assert.assertEquals(links.get(0), "http://foogle.co ");
        Assert.assertEquals(links.get(1), "http://foogle.es ");
    }

    @Test
    public void shouldMatchTheTwoLinksWhenHasFinalText() {
        String text = "http://foogle.co http://foogle.es Hey";
        List<String> links = matcherUtils.matchLinks(text);
        Assert.assertEquals(links.size(), 2);
        Assert.assertEquals(links.get(0), "http://foogle.co ");
        Assert.assertEquals(links.get(1), "http://foogle.es ");
    }


}