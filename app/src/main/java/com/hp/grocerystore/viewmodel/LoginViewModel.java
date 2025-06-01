package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.auth.AuthResponse;
import com.hp.grocerystore.model.auth.ForgotPasswordRequest;
import com.hp.grocerystore.model.auth.OTPRequest;
import com.hp.grocerystore.model.auth.OTPResponse;
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

    public LiveData<Resource<User>> getUserInfo() {
        return repository.getUserInfo();
    }

    public LiveData<Resource<Void>> sendOTPForgotPassword(String email) {
        return repository.sendOTPForgotPassword(new ForgotPasswordRequest(email));
    }

    public LiveData<Resource<OTPResponse>> verifyOTP(String email, String otp) {
        return repository.verifyOTP(new OTPRequest(email, otp));
    }
}
