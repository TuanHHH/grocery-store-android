package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.category.Category;
import com.hp.grocerystore.utils.PagedResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApi {
    @GET("categories")
    Call<ApiResponse<PagedResult<Category>>> getAllCategories();
}
