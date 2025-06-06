package com.hp.grocerystore.model.auth;

public class GoogleCredentialRequest {
    private String credential;

    public GoogleCredentialRequest(String credential) {
        this.credential = credential;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
