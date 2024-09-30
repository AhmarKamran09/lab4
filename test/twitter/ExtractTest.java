/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.Collections;
import org.junit.Test;



public class ExtractTest {

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
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    // Test strategy:
    // 1. Test with an empty list of tweets
    // 2. Test with a list containing a single tweet
    // 3. Test with multiple tweets with different timestamps
    // 4. Test with multiple tweets with identical timestamps

    @Test(expected = IllegalArgumentException.class)
    public void testGetTimespanEmptyList() {
        // Case 1: Empty list
        Extract.getTimespan(Collections.emptyList());
    }

    @Test
    public void testGetTimespanSingleTweet() {
        // Case 2: One tweet
        Instant timestamp = Instant.parse("2024-01-01T12:00:00Z");
        Tweet tweet = new Tweet(1, "user", "Hello world", timestamp);
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet));
        
        assertEquals(timestamp, timespan.getStart());
        assertEquals(timestamp, timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleTweetsDifferentTimes() {
        // Case 3: Multiple tweets with different timestamps
        Instant t1 = Instant.parse("2024-01-01T12:00:00Z");
        Instant t2 = Instant.parse("2024-01-01T14:00:00Z");
        Instant t3 = Instant.parse("2024-01-01T13:00:00Z");
        
        Tweet tweet1 = new Tweet(1, "user1", "First tweet", t1);
        Tweet tweet2 = new Tweet(2, "user2", "Second tweet", t2);
        Tweet tweet3 = new Tweet(3, "user3", "Third tweet", t3);
        
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));
        
        assertEquals(t1, timespan.getStart()); // Earliest tweet
        assertEquals(t2, timespan.getEnd());   // Latest tweet
    }

    @Test
    public void testGetTimespanMultipleTweetsSameTime() {
        // Case 4: Multiple tweets with the same timestamp
        Instant timestamp = Instant.parse("2024-01-01T12:00:00Z");
        Tweet tweet1 = new Tweet(1, "user1", "Tweet one", timestamp);
        Tweet tweet2 = new Tweet(2, "user2", "Tweet two", timestamp);
        
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals(timestamp, timespan.getStart());
        assertEquals(timestamp, timespan.getEnd());
    }
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }


    @Test
    public void testGetMentionedUsersNoMentions() {
        // Case 1: No mentions in tweets
        Tweet tweet1 = new Tweet(1, "user1", "Hello world", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Just another day", Instant.now());
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));
        
        assertTrue(mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSingleMention() {
        // Case 2: Single valid mention
        Tweet tweet = new Tweet(1, "user", "Hello @user1", Instant.now());
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        
        assertEquals(1, mentionedUsers.size());
        assertTrue(mentionedUsers.contains("user1"));
    }

    @Test
    public void testGetMentionedUsersMultipleMentions() {
        // Case 3: Multiple mentions
        Tweet tweet1 = new Tweet(1, "user1", "Hello @user1 @user2", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "@user2 you should meet @user3", Instant.now());
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));
        
        assertEquals(3, mentionedUsers.size());
        assertTrue(mentionedUsers.contains("user1"));
        assertTrue(mentionedUsers.contains("user2"));
        assertTrue(mentionedUsers.contains("user3"));
    }

    @Test
    public void testGetMentionedUsersCaseInsensitive() {
        // Case 4: Case insensitivity of mentions
        Tweet tweet1 = new Tweet(1, "user1", "Hello @USER1", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "@User1 meet @user2", Instant.now());
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));
        
        assertEquals(2, mentionedUsers.size());
        assertTrue(mentionedUsers.contains("user1"));
        assertTrue(mentionedUsers.contains("user2"));
    }

    @Test
    public void testGetMentionedUsersInvalidMentions() {
        // Case 5: Invalid mentions (e.g., email addresses)
        Tweet tweet = new Tweet(1, "user", "Contact me at bitdiddle@mit.edu", Instant.now());
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        
        assertTrue(mentionedUsers.isEmpty()); // Should not extract "mit"
    }

    @Test
    public void testGetMentionedUsersEdgeCases() {
        // Case 6: Mentions with punctuation around them, at start or end of text
        Tweet tweet1 = new Tweet(1, "user", "@user1, how are you?", Instant.now());
        Tweet tweet2 = new Tweet(2, "user", "Check this out, @user2!", Instant.now());
        Tweet tweet3 = new Tweet(3, "user", "@user3 is great.", Instant.now());
        Tweet tweet4 = new Tweet(4, "user", "Great job, @user4.", Instant.now());
        
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2, tweet3, tweet4));
        
        assertEquals(4, mentionedUsers.size());
        assertTrue(mentionedUsers.contains("user1"));
        assertTrue(mentionedUsers.contains("user2"));
        assertTrue(mentionedUsers.contains("user3"));
        assertTrue(mentionedUsers.contains("user4"));
    }
    
    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
