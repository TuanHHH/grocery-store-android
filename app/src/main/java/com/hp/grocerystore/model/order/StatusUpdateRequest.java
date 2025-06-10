package com.hp.grocerystore.model.order;

public class StatusUpdateRequest {
    private int status;

    public StatusUpdateRequest(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
