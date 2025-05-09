package com.hp.grocerystore.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.feedback.Feedback;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private final Context context;

    public ProductRepository() {
        this.context = GRCApplication.getAppContext();
    }
    public LiveData<Resource<Product>> getProductById(long productId) {
        MutableLiveData<Resource<Product>> productLiveData = new MutableLiveData<>();
        productLiveData.setValue(Resource.loading());

        RetrofitClient.getProductApi(context).getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productLiveData.setValue(Resource.success(response.body().getData()));
                } else {
                    productLiveData.setValue(Resource.error("Lỗi khi load thông tin sản phẩm"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                productLiveData.setValue(Resource.error("Có lỗi khi gọi API: " + t.getMessage()));
            }
        });

        return productLiveData;
    }

    public LiveData<Resource<List<Feedback>>> getFeedbacksByProductId(long productId) {
        MutableLiveData<Resource<List<Feedback>>> feedbackLiveData = new MutableLiveData<>();
        feedbackLiveData.setValue(Resource.loading());

        RetrofitClient.getFeedbackApi(context).getFeedbacksByProductId(productId).enqueue(new Callback<ApiResponse<PaginationResponse<Feedback>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<Feedback>>> call, Response<ApiResponse<PaginationResponse<Feedback>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    feedbackLiveData.setValue(Resource.success(response.body().getData().getResult()));
                } else {
                    feedbackLiveData.setValue(Resource.error("Lỗi khi load feedback"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<Feedback>>> call, Throwable t) {
                feedbackLiveData.setValue(Resource.error("Lỗi: " + t.getMessage()));
            }
        });

        return feedbackLiveData;
    }
}
