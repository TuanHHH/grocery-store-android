package com.hp.grocerystore.model.cart;

public class CartItem {
    private String productName;
    private String imageUrl;
    private int inventoryQuantity;
    private int quantity;
    private double price;
    private boolean selected = false;

    public CartItem(String productName, String imageUrl, int inventoryQuantity, int quantity, double price) {
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.inventoryQuantity = inventoryQuantity;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
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
