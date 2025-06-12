package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.model.payment.VNPayResponse;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.network.api.VNPayApi;
import com.hp.grocerystore.repository.VNPayRepository;
import com.hp.grocerystore.utils.Resource;

public class VNPayViewModel extends ViewModel {
    private final VNPayRepository vnPayRepository;

    public VNPayViewModel() {
        VNPayApi vnPayApi = RetrofitClient.getVNPayApi();
        this.vnPayRepository = VNPayRepository.getInstance(vnPayApi);
    }

    public LiveData<Resource<VNPayResponse>> getPaymentResponseLiveData() {
        return vnPayRepository.getPaymentResponseLiveData();
    }

    public void createPayment(long amount, String orderData) {
        vnPayRepository.createPayment(amount, orderData);
    }

}