package com.hp.grocerystore.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.auth.RegisterResponse;
import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.AuthRepository;
import com.hp.grocerystore.utils.Resource;

public class RegisterViewModel extends ViewModel {
    private final AuthRepository repository;

    public RegisterViewModel() {
        AuthApi authApi = RetrofitClient.getAuthApi();
        this.repository = AuthRepository.getInstance(authApi);
    }

    public LiveData<Resource<RegisterResponse>> register(String name, String email, String password) {
        return repository.register(name, email, password);
    }
}
