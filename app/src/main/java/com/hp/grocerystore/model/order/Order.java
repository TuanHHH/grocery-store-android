package com.hp.grocerystore.model.order;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("id")
    private int id;

    @SerializedName("orderTime")
    private String orderTime;

    @SerializedName("deliveryTime")
    private String deliveryTime;

    @SerializedName("status")
    private int status;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userId")
    private int userId;

    private double totalPrice;

    // Getters
    public int getId() { return id; }
    public String getOrderTime() { return orderTime; }
    public String getDeliveryTime() { return deliveryTime; }
    public int getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getUserEmail() { return userEmail; }
    public String getUserName() { return userName; }
    public int getUserId() { return userId; }
    public double getTotalPrice() { return totalPrice; }
}