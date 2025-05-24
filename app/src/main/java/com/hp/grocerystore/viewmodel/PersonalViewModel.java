package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.base.UploadFileResponse;
import com.hp.grocerystore.model.user.UpdateUserRequest;
import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.UserRepository;
import com.hp.grocerystore.utils.Resource;

import okhttp3.MultipartBody;

public class PersonalViewModel extends ViewModel {
    private final UserRepository userRepository;

    public PersonalViewModel() {
        this.userRepository = new UserRepository(RetrofitClient.getUserApi());
    }

    public LiveData<Resource<UploadFileResponse>> uploadFile(MultipartBody.Part filePart) {
        return userRepository.uploadFile(filePart, "user");
    }

    public LiveData<Resource<User>> updateUser(String name, String phone, String address, String avatarUrl) {
        return userRepository.updateUser(new UpdateUserRequest(name, phone, address, avatarUrl));
    }
}
