package com.hp.grocerystore.model.product;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    private long id;

    private String productName;

    @SerializedName("price")
    private double price;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("rating")
    private float rating;

    @SerializedName("sold")
    private int sold;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("unit")
    private String unit;

    @SerializedName("description")
    private String description;
    @SerializedName("category")
    private String category;

    public long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getRating() {
        return rating;
    }

    public int getSold() {
        return sold;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
