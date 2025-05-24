package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.auth.AuthResponse;
import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.AuthRepository;
import com.hp.grocerystore.utils.Resource;

public class LoginViewModel extends ViewModel {
    private final AuthRepository repository;

    public LoginViewModel() {
        AuthApi authApi = RetrofitClient.getAuthApi();
        this.repository = new AuthRepository(authApi);
    }

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        return repository.login(email, password);
    }

    public LiveData<Resource<User>> getUserInfo(){
        return repository.getUserInfo();
    }
}
