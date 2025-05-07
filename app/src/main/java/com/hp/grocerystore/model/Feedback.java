package com.hp.grocerystore.model;

public class Feedback {
    private String userAvatar;
    private String name;
    private float rating;
    private String description;
    private String time;

    public Feedback(String userAvatar, String name, float rating, String description, String time) {
        this.userAvatar = userAvatar;
        this.name = name;
        this.rating = rating;
        this.description = description;
        this.time = time;
    }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
