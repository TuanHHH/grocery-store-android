package com.hp.grocerystore.model.cart;

public class AddCartResponse {
    private Info id;
    private int quantity;
    private String timestamp;

    public static class Info {
        private long productId;
        private long userId;

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }
    }

    public Info getId() {
        return id;
    }

    public void setId(Info id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
