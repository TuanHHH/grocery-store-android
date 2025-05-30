package com.hp.grocerystore.model.feedback;

public class CreateFeedbackRequest {
    private long productId;
    private int rating;
    private String description;

    public CreateFeedbackRequest(long productId, int rating, String description) {
        this.productId = productId;
        this.rating = rating;
        this.description = description;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
