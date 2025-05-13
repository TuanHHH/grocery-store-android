package com.hp.grocerystore.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.feedback.Feedback;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.network.api.FeedbackApi;
import com.hp.grocerystore.network.api.ProductApi;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private final ProductApi productApi;
    private final FeedbackApi feedbackApi;
    private final MutableLiveData<Resource<Product>> productLiveData;
    private final MutableLiveData<Resource<List<Feedback>>> feedbackLiveData;
    private final MutableLiveData<Resource<List<Product>>> productsLiveData;
    private int currentPage;
    private boolean isLoading;
    private boolean hasMoreData;

    public ProductRepository(ProductApi productApi, FeedbackApi feedbackApi) {
        this.productApi = productApi;
        this.feedbackApi = feedbackApi;
        this.productLiveData = new MutableLiveData<>();
        this.feedbackLiveData = new MutableLiveData<>();
        this.productsLiveData = new MutableLiveData<>();
        this.currentPage = 1;
        this.isLoading = false;
        this.hasMoreData = true;
    }

    public LiveData<Resource<Product>> getProduct(long productId) {
        productLiveData.setValue(Resource.loading());
        productApi.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        productLiveData.setValue(Resource.success(apiResponse.getData()));
                    } else {
                        productLiveData.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    productLiveData.setValue(Resource.error("Không thể tải thông tin sản phẩm"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                productLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return productLiveData;
    }

    public LiveData<Resource<List<Feedback>>> getFeedback(long productId) {
        feedbackLiveData.setValue(Resource.loading());
        feedbackApi.getFeedbacksByProductId(productId).enqueue(new Callback<ApiResponse<PaginationResponse<Feedback>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<Feedback>>> call, Response<ApiResponse<PaginationResponse<Feedback>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaginationResponse<Feedback>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        PaginationResponse<Feedback> paginationResponse = apiResponse.getData();
                        feedbackLiveData.setValue(Resource.success(paginationResponse.getResult()));
                    } else {
                        feedbackLiveData.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    feedbackLiveData.setValue(Resource.error("Không thể tải đánh giá"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<Feedback>>> call, Throwable t) {
                feedbackLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return feedbackLiveData;
    }

    public LiveData<Resource<List<Product>>> getProducts() {
        if (!isLoading && hasMoreData) {
            loadProducts();
        }
        return productsLiveData;
    }

    public void loadMoreProducts() {
        if (!isLoading && hasMoreData) {
            currentPage++;
            loadProducts();
        }
    }

    public void refreshProducts() {
        currentPage = 1;
        hasMoreData = true;
        loadProducts();
    }

    private void loadProducts() {
        isLoading = true;
        productsLiveData.setValue(Resource.loading());

        productApi.getProducts(currentPage, 15).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        List<Product> products = apiResponse.getData();
                        if (products != null) {
                            if (products.isEmpty()) {
                                hasMoreData = false;
                            }
                            productsLiveData.setValue(Resource.success(products));
                        } else {
                            productsLiveData.setValue(Resource.error("Không có dữ liệu"));
                        }
                    } else {
                        productsLiveData.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    productsLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading = false;
                productsLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }
}
