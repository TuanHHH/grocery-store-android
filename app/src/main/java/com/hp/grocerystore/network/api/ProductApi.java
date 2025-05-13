package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.product.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductApi {
    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") long productId);

    Call<ApiResponse<List<Product>>> getProducts(int currentPage, int i);
}
