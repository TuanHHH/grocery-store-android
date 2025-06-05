package com.hp.grocerystore.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.network.api.CategoryApi;
import com.hp.grocerystore.model.category.Category;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.network.api.UserApi;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private static volatile CategoryRepository INSTANCE;
    private final CategoryApi categoryApi;

    private CategoryRepository(CategoryApi categoryApi) {
        this.categoryApi = categoryApi;
    }

    public static CategoryRepository getInstance(CategoryApi categoryApi) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CategoryRepository(categoryApi);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<Category>>> getAllCategories() {
        MutableLiveData<Resource<List<Category>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        categoryApi.getAllCategories().enqueue(new Callback<ApiResponse<PaginationResponse<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaginationResponse<Category>>> call, @NonNull Response<ApiResponse<PaginationResponse<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData().getResult();
                    liveData.setValue(Resource.success(categories));
                } else {
                    liveData.setValue(Resource.error("Lỗi khi tải danh mục sản phẩm"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PaginationResponse<Category>>> call, @NonNull Throwable t) {
                liveData.setValue(Resource.error(t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<Category>> getCategoryById(long categoryId) {
        MutableLiveData<Resource<Category>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        categoryApi.getCategoryById(categoryId).enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Category>> call, @NonNull Response<ApiResponse<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Category category = response.body().getData();
                    liveData.setValue(Resource.success(category));
                } else {
                    liveData.setValue(Resource.error("Không thể lấy dữ liệu danh mục"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Category>> call, @NonNull Throwable t) {
                liveData.setValue(Resource.error(t.getMessage()));
            }
        });

        return liveData;
    }

}