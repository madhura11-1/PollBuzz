package com.PollBuzz.pollbuzz.objects;

public class VoteDetails {
    private String UserId, option, username, PollId, profileUrl;
    private long timestamp;

    public VoteDetails() {
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public VoteDetails(String PollId, String option, String username, String UserId, String profileUrl, long timestamp) {
        this.PollId = PollId;
        this.UserId = UserId;
        this.option = option;
        this.username = username;
        this.profileUrl = profileUrl;
        this.timestamp = timestamp;
    }

    public String getProgfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPollId() {
        return PollId;
    }

    public void setPollId(String pollId) {
        PollId = pollId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return UserId;
    }

    public String getOption() {
        return option;
    }

    public String getUsername() {
        return username;
    }

}
