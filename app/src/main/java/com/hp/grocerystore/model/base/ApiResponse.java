package com.hp.grocerystore.model.base;

public class ApiResponse<T> {
    private int statusCode;

    private T data;

    private Object error;

    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public T getData() {
        return data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
