package com.hp.grocerystore.model.category;


import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("imageUrl")
    private String imageUrl;

    // --- Getter methods ---
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

