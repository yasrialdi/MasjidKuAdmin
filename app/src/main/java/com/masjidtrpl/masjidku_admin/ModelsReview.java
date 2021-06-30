package com.masjidtrpl.masjidku_admin;

public class ModelsReview {
    private String key;
    private String user;
    private String review;

    public ModelsReview() {
    }

    public ModelsReview(String key, String user, String review) {
        this.key = key;
        this.user = user;
        this.review = review;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
