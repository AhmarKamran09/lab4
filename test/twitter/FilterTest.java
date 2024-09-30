/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import java.util.Set;
import java.util.Collections;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
   
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
   
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    // Test strategy:
    // 1. No matching tweets
    // 2. All tweets match the username
    // 3. Some tweets match the username
    // 4. Case insensitivity of username
    // 5. Empty list of tweets

   @Test
    public void testWrittenByNoMatchingTweets() {
        // Case 1: No tweets match the username
        Tweet tweet1 = new Tweet(1, "user1", "Tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", Instant.now());
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "user3");
        
        assertTrue(result.isEmpty());
    }

    @Test
    public void testWrittenByAllMatchingTweets() {
        // Case 2: All tweets match the username
        Tweet tweet1 = new Tweet(1, "user1", "Tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user1", "Another tweet", Instant.now());
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "user1");
        
        assertEquals(2, result.size());
        assertTrue(result.contains(tweet1));
        assertTrue(result.contains(tweet2));
    }

    @Test
    public void testWrittenBySomeMatchingTweets() {
        // Case 3: Some tweets match the username
        Tweet tweet1 = new Tweet(1, "user1", "Tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", Instant.now());
        Tweet tweet3 = new Tweet(3, "user1", "Yet another tweet", Instant.now());
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "user1");
        
        assertEquals(2, result.size());
        assertTrue(result.contains(tweet1));
        assertTrue(result.contains(tweet3));
        assertFalse(result.contains(tweet2));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        // Case 4: Case insensitivity of username
        Tweet tweet1 = new Tweet(1, "User1", "Tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user1", "Another tweet", Instant.now());
        Tweet tweet3 = new Tweet(3, "USER1", "Yet another tweet", Instant.now());
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "user1");
        
        assertEquals(3, result.size());
        assertTrue(result.contains(tweet1));
        assertTrue(result.contains(tweet2));
        assertTrue(result.contains(tweet3));
    }

    @Test
    public void testWrittenByEmptyList() {
        // Case 5: Empty list of tweets
        List<Tweet> result = Filter.writtenBy(Collections.emptyList(), "user1");
        
        assertTrue(result.isEmpty());
    }
    
    

    // Test strategy:
    // 1. No tweets match the timespan
    // 2. All tweets match the timespan
    // 3. Some tweets match the timespan
    // 4. Tweets match exactly on the start or end time of the timespan
    // 5. Empty list of tweets

    @Test
    public void testInTimespanNoMatchingTweets() {
        // Case 1: No tweets fall within the timespan
        Instant time1 = Instant.parse("2020-01-01T00:00:00Z");
        Instant time2 = Instant.parse("2020-01-02T00:00:00Z");
        Tweet tweet1 = new Tweet(1, "user1", "Tweet content", time1);
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", time2);
        
        Timespan timespan = new Timespan(Instant.parse("2020-01-03T00:00:00Z"), Instant.parse("2020-01-04T00:00:00Z"));
        
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2), timespan);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInTimespanAllMatchingTweets() {
        // Case 2: All tweets fall within the timespan
        Instant time1 = Instant.parse("2020-01-01T10:00:00Z");
        Instant time2 = Instant.parse("2020-01-01T12:00:00Z");
        Tweet tweet1 = new Tweet(1, "user1", "Tweet content", time1);
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", time2);
        
        Timespan timespan = new Timespan(Instant.parse("2020-01-01T00:00:00Z"), Instant.parse("2020-01-02T00:00:00Z"));
        
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2), timespan);
        assertEquals(2, result.size());
        assertTrue(result.contains(tweet1));
        assertTrue(result.contains(tweet2));
    }

    @Test
    public void testInTimespanSomeMatchingTweets() {
        // Case 3: Some tweets fall within the timespan
        Instant time1 = Instant.parse("2020-01-01T10:00:00Z");
        Instant time2 = Instant.parse("2020-01-03T10:00:00Z");
        Tweet tweet1 = new Tweet(1, "user1", "Tweet content", time1);
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", time2);
        
        Timespan timespan = new Timespan(Instant.parse("2020-01-01T00:00:00Z"), Instant.parse("2020-01-02T00:00:00Z"));
        
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2), timespan);
        assertEquals(1, result.size());
        assertTrue(result.contains(tweet1));
        assertFalse(result.contains(tweet2));
    }

    @Test
    public void testInTimespanEdgeCaseMatching() {
        // Case 4: Tweets match exactly on the start or end time of the timespan
        Instant time1 = Instant.parse("2020-01-01T00:00:00Z");
        Instant time2 = Instant.parse("2020-01-02T00:00:00Z");
        Tweet tweet1 = new Tweet(1, "user1", "Tweet content", time1);
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", time2);
        
        Timespan timespan = new Timespan(time1, time2);
        
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2), timespan);
        assertEquals(2, result.size());
        assertTrue(result.contains(tweet1));
        assertTrue(result.contains(tweet2));
    }

    @Test
    public void testInTimespanEmptyList() {
        // Case 5: Empty list of tweets
        Timespan timespan = new Timespan(Instant.parse("2020-01-01T00:00:00Z"), Instant.parse("2020-01-02T00:00:00Z"));
        
        List<Tweet> result = Filter.inTimespan(Collections.emptyList(), timespan);
        assertTrue(result.isEmpty());
    }

    
    
    // Test strategy:
    // 1. No matching words found in any tweet.
    // 2. All tweets contain at least one word from the list.
    // 3. Some tweets contain at least one word from the list.
    // 4. Word matching should be case-insensitive.
    // 5. Empty words list.
    // 6. Empty tweet list.

    @Test
    public void testContainingNoMatchingWords() {
        // Case 1: No tweets match the words list
        Tweet tweet1 = new Tweet(1, "user1", "Tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", Instant.now());
        
        List<String> words = Arrays.asList("hello", "world");
        
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), words);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testContainingAllMatchingTweets() {
        // Case 2: All tweets contain at least one word from the list
        Tweet tweet1 = new Tweet(1, "user1", "Hello tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet world", Instant.now());
        
        List<String> words = Arrays.asList("hello", "world");
        
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), words);
        assertEquals(2, result.size());
        assertTrue(result.contains(tweet1));
        assertTrue(result.contains(tweet2));
    }

    @Test
    public void testContainingSomeMatchingTweets() {
        // Case 3: Some tweets contain at least one word from the list
        Tweet tweet1 = new Tweet(1, "user1", "Hello tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", Instant.now());
        
        List<String> words = Arrays.asList("hello", "world");
        
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), words);
        assertEquals(1, result.size());
        assertTrue(result.contains(tweet1));
        assertFalse(result.contains(tweet2));
    }

    @Test
    public void testContainingCaseInsensitive() {
        // Case 4: Word matching is case-insensitive
        Tweet tweet1 = new Tweet(1, "user1", "HELLO tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet WORLD", Instant.now());
        
        List<String> words = Arrays.asList("hello", "world");
        
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), words);
        assertEquals(2, result.size());
        assertTrue(result.contains(tweet1));
        assertTrue(result.contains(tweet2));
    }

    @Test
    public void testContainingEmptyWordsList() {
        // Case 5: Empty words list
        Tweet tweet1 = new Tweet(1, "user1", "Hello tweet content", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet", Instant.now());
        
        List<String> words = Collections.emptyList();
        
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), words);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testContainingEmptyTweetList() {
        // Case 6: Empty tweet list
        List<String> words = Arrays.asList("hello", "world");
        
        List<Tweet> result = Filter.containing(Collections.emptyList(), words);
        assertTrue(result.isEmpty());
    }
    
    
    
    
    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
