package com.hp.grocerystore.model.order;

import com.google.gson.annotations.SerializedName;
import com.hp.grocerystore.model.cart.CartItem;

import java.util.List;

public class CheckoutRequest {

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("items")
    private List<CartItem> items;

    // Constructors, getters, setters
    public CheckoutRequest(String address, String phone, String paymentMethod, double totalPrice, List<CartItem> items) {
        this.address = address;
        this.phone = phone;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalPrice() { return totalPrice; }
    public List<CartItem> getItems() { return items; }
}
