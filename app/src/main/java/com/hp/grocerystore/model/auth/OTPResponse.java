package com.hp.grocerystore.model.auth;

public class OTPResponse {
    private String tempToken;

    public OTPResponse(String tempToken) {
        this.tempToken = tempToken;
    }

    public String getTempToken() {
        return tempToken;
    }

    public void setTempToken(String tempToken) {
        this.tempToken = tempToken;
    }
}
