/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.List;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
    	  if (tweets.isEmpty()) {
              throw new IllegalArgumentException("Tweet list cannot be empty");
          }

          // Initialize min and max time with the first tweet's timestamp
          Instant minTime = tweets.get(0).getTimestamp();
          Instant maxTime = minTime;

          // Iterate through tweets to find the minimum and maximum timestamps
          for (Tweet tweet : tweets) {
              Instant timestamp = tweet.getTimestamp();
              if (timestamp.isBefore(minTime)) {
                  minTime = timestamp;
              }
              if (timestamp.isAfter(maxTime)) {
                  maxTime = timestamp;
              }
          }

          // Return the timespan
          return new Timespan(minTime, maxTime);
          }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
    	   Set<String> mentionedUsers = new HashSet<>();
           // Pattern to match valid Twitter username mentions
           String usernameRegex = "(?<=^|[^a-zA-Z0-9_])@([a-zA-Z0-9_]+)(?=[^a-zA-Z0-9_]|$)";
           Pattern pattern = Pattern.compile(usernameRegex);

           for (Tweet tweet : tweets) {
               Matcher matcher = pattern.matcher(tweet.getText());
               while (matcher.find()) {
                   // Add username to the set, converting to lowercase (since mentions are case-insensitive)
                   mentionedUsers.add(matcher.group(1).toLowerCase());
               }
           }
           
           return mentionedUsers;    }

}
