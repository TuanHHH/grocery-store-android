//package com.hp.grocerystore.model.payment;
//
//public class VNPayResponse {
//    public String code;
//    public String message;
//    public String paymentUrl;
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public String getPaymentUrl() {
//        return paymentUrl;
//    }
//
//    public void setPaymentUrl(String paymentUrl) {
//        this.paymentUrl = paymentUrl;
//    }
//}
package com.hp.grocerystore.model.payment;

import com.google.gson.annotations.SerializedName;

public class VNPayResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("data")
    private ResponseData data;

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

    // Getters and Setters
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public ResponseData getData() { return data; }
    public void setData(ResponseData data) { this.data = data; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static class ResponseData {
        @SerializedName("code")
        private int code;

        @SerializedName("message")
        private String message;

        @SerializedName("data")
        private PaymentData paymentData;

        // Getters and Setters
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public PaymentData getPaymentData() { return paymentData; }
        public void setPaymentData(PaymentData paymentData) { this.paymentData = paymentData; }
    }

    public static class PaymentData {
        @SerializedName("code")
        private String code;

        @SerializedName("message")
        private String message;

        @SerializedName("paymentUrl")
        private String paymentUrl;

        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getPaymentUrl() { return paymentUrl; }
        public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    }
}