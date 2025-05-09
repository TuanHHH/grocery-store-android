package com.hp.grocerystore.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.model.auth.LoginResponse;
import com.hp.grocerystore.repository.AuthRepository;
import com.hp.grocerystore.utils.Resource;

public class LoginViewModel extends ViewModel {
    private AuthRepository repository;
    public LoginViewModel() {
        repository = new AuthRepository();
    }
    public LiveData<Resource<LoginResponse>> login(String email, String password) {
        return repository.login(email, password);
    }
}
