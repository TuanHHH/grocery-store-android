package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.category.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryApi {
    @GET("categories")
    Call<ApiResponse<PaginationResponse<Category>>> getAllCategories();
    @GET("categories/{categoryId}")
    Call<ApiResponse<Category>> getCategoryById(@Path("categoryId") long categoryId);
}
