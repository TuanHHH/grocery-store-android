package com.hp.grocerystore.model.wishlist;

import com.google.gson.annotations.SerializedName;

public class Wishlist {
    @SerializedName("id")
    private long id;
    @SerializedName("productName")
    private String productName;
    @SerializedName("price")
    private double price;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("category")
    private String category;
    @SerializedName("active")
    private boolean active;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
