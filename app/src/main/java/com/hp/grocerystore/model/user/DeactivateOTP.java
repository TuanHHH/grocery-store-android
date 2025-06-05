package com.hp.grocerystore.model.user;

public class DeactivateOTP {
    private String otpCode;

    public DeactivateOTP(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
