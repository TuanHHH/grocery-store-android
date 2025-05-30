package com.hp.grocerystore.model.product;

import com.google.gson.annotations.SerializedName;
import com.hp.grocerystore.model.cart.CartItem;

import java.util.List;

public class ProductOrder {

    @SerializedName("productId")
    private int productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("quantity")
    private int quantity;

    private double unitPrice;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Constructor
    public ProductOrder(int productId, String productName, int quantity, double unitPrice, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getImageUrl() { return imageUrl; }

}
