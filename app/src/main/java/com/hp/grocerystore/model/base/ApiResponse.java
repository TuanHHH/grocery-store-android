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

    public String getMessage() {
        return message;
    }
}
