package com.hp.grocerystore.model.product;

public class WishlistStatusResponse {
    private long productId;
    private Boolean wishlisted;

    public WishlistStatusResponse(long productId, Boolean wishlisted) {
        this.productId = productId;
        this.wishlisted = wishlisted;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public Boolean getWishlisted() {
        return wishlisted;
    }

    public void setWishlisted(Boolean wishlisted) {
        this.wishlisted = wishlisted;
    }
}
