package com.hp.grocerystore.model.user;

public class DeviceInfoResponse {
    private String deviceInfo;
    private String loginTime;
    private String deviceHash;
    private Boolean isCalledDevice;

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getDeviceHash() {
        return deviceHash;
    }

    public void setDeviceHash(String deviceHash) {
        this.deviceHash = deviceHash;
    }

    public Boolean getCalledDevice() {
        return isCalledDevice;
    }

    public void setCalledDevice(Boolean calledDevice) {
        isCalledDevice = calledDevice;
    }
}
