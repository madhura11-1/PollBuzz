package com.PollBuzz.pollbuzz;

import java.util.Date;
import java.util.Map;

public class PollDetails {

    private String question;
    private String poll_type;
    private String author;
    private String UID;
    private String authorUID;
    private String username;
    private String pic;

    public String getAuthor_lc() {
        return author_lc;
    }

    public void setAuthor_lc(String author_lc) {
        this.author_lc = author_lc;
    }

    private String author_lc;
    Date created_date, expiry_date;
    private Map<String, Integer> map;
    private Integer pollcount=0;


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUsername() {
        return username;
    }

    public String getPic() {
        return pic;
    }


    public Integer getPollcount() {
        return pollcount;
    }

    public void setPollcount(Integer pollcount) {
        this.pollcount = pollcount;
    }
    private long timestamp;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    public String getAuthorUID() {
        return authorUID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public Date getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(Date expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getPoll_type() {
        return poll_type;
    }

    public void setPoll_type(String poll_type) {
        this.poll_type = poll_type;
    }
}