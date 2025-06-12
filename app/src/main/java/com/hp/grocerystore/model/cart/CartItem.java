package com.hp.grocerystore.model.cart;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class CartItem implements Serializable {
    private long id;
    private String productName;
    private String imageUrl;
    private int stock;
    private int quantity;
    private double price;
    private boolean selected;

    public CartItem() {
        this.selected = false;
    }

    public CartItem(long id, String productName, String imageUrl, int stock, int quantity, double price) {
        this.id = id;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.selected = false;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}