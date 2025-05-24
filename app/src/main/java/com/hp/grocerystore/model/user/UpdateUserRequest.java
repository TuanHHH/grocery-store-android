package com.hp.grocerystore.model.user;

public class UpdateUserRequest {
    private String name;
    private String phone;
    private String address;
    private String avatarUrl;

    public UpdateUserRequest(String name, String phone, String address, String avatarUrl) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        if (avatarUrl != null) {
            this.avatarUrl = avatarUrl;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
