package com.hp.grocerystore.model.feedback;

import com.hp.grocerystore.utils.Extensions;

public class Feedback {
    private long id;
    private String userAvatarUrl;
    private String userName;
    private float ratingStar;
    private String description;
    private String updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(float rating) {
        this.ratingStar = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdatedAt() {
        return Extensions.showPrettyTime(updatedAt);
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
