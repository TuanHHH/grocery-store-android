
package com.hp.grocerystore.model.order;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.hp.grocerystore.model.cart.CartItem;

import java.util.ArrayList;
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

    private List<Item> items = new ArrayList<>();

    public static class Item {
        @SerializedName("productId")
        private long productId;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("productName")
        private String productName;

        @SerializedName("unitPrice")
        private double unitPrice;

        public Item(long productId, int quantity, String productName, double unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.productName = productName;
            this.unitPrice = unitPrice;
        }

        public long getProductId() { return productId; }
        public void setProductId(long productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    }

    // Constructors
    public CheckoutRequest(String address, String phone, String paymentMethod, double totalPrice, List<Item> items) {
        this.address = address;
        this.phone = phone;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public CheckoutRequest() {}

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public List<Item> getItems() { return items; }
//    public void setItems(List<Item> items) { this.items = items; }
    public void setItems(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Item item = new Item(
                    cartItem.getId(),
                    cartItem.getQuantity(),
                    cartItem.getProductName(),
                    cartItem.getPrice()
            );
//            Log.d("CheckoutRequest", "Item added: " + item.getProductName() + ", Quantity: " + item.getQuantity() + ", Price: " + item.getUnitPrice() + ", Product ID: " + item.getProductId());
            items.add(item);
        }
    }

    public static CheckoutRequest fromCartItems(String address, String phone, String paymentMethod, double totalPrice, List<CartItem> cartItems) {
        List<Item> items = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Item item = new Item(
                    cartItem.getId(),
                    cartItem.getQuantity(),
                    cartItem.getProductName(),
                    cartItem.getPrice()
            );
            items.add(item);
        }
        return new CheckoutRequest(address, phone, paymentMethod, totalPrice, items);
    }
}