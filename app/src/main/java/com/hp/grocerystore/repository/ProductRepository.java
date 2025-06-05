package com.hp.grocerystore.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.feedback.CreateFeedbackRequest;
import com.hp.grocerystore.model.feedback.Feedback;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.model.product.WishlistStatusResponse;
import com.hp.grocerystore.network.api.FeedbackApi;
import com.hp.grocerystore.network.api.ProductApi;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static volatile ProductRepository INSTANCE;
    private final ProductApi productApi;
    private final FeedbackApi feedbackApi;
    MutableLiveData<Resource<List<Product>>> productsLiveData;
    private int currentPage;
    private boolean isLoading;
    private boolean hasMoreData;

    private ProductRepository(ProductApi productApi, FeedbackApi feedbackApi) {
        this.productApi = productApi;
        this.feedbackApi = feedbackApi;
        this.productsLiveData = new MutableLiveData<>();
        this.currentPage = 1;
        this.isLoading = false;
        this.hasMoreData = true;
    }

    public static ProductRepository getInstance(ProductApi productApi, FeedbackApi feedbackApi) {
        if (INSTANCE == null) {
            synchronized (ProductRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProductRepository(productApi, feedbackApi);
                }
            }
        }
        return INSTANCE;
    }


    public LiveData<Resource<Product>> getProduct(long productId) {
        MutableLiveData<Resource<Product>> productLiveData = new MutableLiveData<>();
        productLiveData.setValue(Resource.loading());
        productApi.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Product>> call, @NonNull Response<ApiResponse<Product>> response) {
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
            public void onFailure(@NonNull Call<ApiResponse<Product>> call, @NonNull Throwable t) {
                productLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return productLiveData;
    }

    public LiveData<Resource<List<Feedback>>> getFeedback(long productId) {
        MutableLiveData<Resource<List<Feedback>>> feedbackLiveData = new MutableLiveData<>();
        feedbackLiveData.setValue(Resource.loading());
        feedbackApi.getFeedbacksByProductId(productId).enqueue(new Callback<ApiResponse<PaginationResponse<Feedback>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaginationResponse<Feedback>>> call, @NonNull Response<ApiResponse<PaginationResponse<Feedback>>> response) {
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
            public void onFailure(@NonNull Call<ApiResponse<PaginationResponse<Feedback>>> call, @NonNull Throwable t) {
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
            public void onResponse(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Response<ApiResponse<List<Product>>> response) {
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
            public void onFailure(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Throwable t) {
                isLoading = false;
                productsLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }


    public LiveData<Resource<List<Product>>> getHomeProducts(int page, int size, String filter) {
        MutableLiveData<Resource<List<Product>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        productApi.getProductsPaginated(page, size, filter).enqueue(new Callback<ApiResponse<PaginationResponse<Product>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaginationResponse<Product>>> call, @NonNull Response<ApiResponse<PaginationResponse<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData().getResult();
                    liveData.setValue(Resource.success(products));
                } else {
                    liveData.setValue(Resource.error("Lỗi khi tải danh sách sản phẩm"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PaginationResponse<Product>>> call, @NonNull Throwable t) {
                liveData.setValue(Resource.error(t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<List<Product>>> searchProducts(int page, int size, String filter) {
        MutableLiveData<Resource<List<Product>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());
        productApi.searchProducts(page, size, filter).enqueue(new Callback<ApiResponse<PaginationResponse<Product>>>() {


            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaginationResponse<Product>>> call, @NonNull Response<ApiResponse<PaginationResponse<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData().getResult();
                    liveData.setValue(Resource.success(products));
                } else {
                    liveData.setValue(Resource.error("Lỗi khi tải danh sách sản phẩm"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PaginationResponse<Product>>> call, @NonNull Throwable throwable) {
                liveData.setValue(Resource.error(throwable.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<List<Product>>> searchAndFilterProducts(int page, int size, String filter1,
                                                                     String filter2, String filter3, String filter4,
                                                                     String filter5, String filter6, String sort) {
        MutableLiveData<Resource<List<Product>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));
        productApi.searchAndFilterProducts(page, size, filter1, filter2, filter3, filter4, filter5, filter6, sort).enqueue(new Callback<ApiResponse<PaginationResponse<Product>>>() {


            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaginationResponse<Product>>> call, @NonNull Response<ApiResponse<PaginationResponse<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData().getResult();
                    liveData.setValue(Resource.success(products));
                } else {
                    liveData.setValue(Resource.error("Lỗi khi tải danh sách sản phẩm", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PaginationResponse<Product>>> call, @NonNull Throwable throwable) {
                liveData.setValue(Resource.error(throwable.getMessage(), null));
            }
        });

        return liveData;
    }

    public LiveData<Resource<Feedback>> addFeedback(CreateFeedbackRequest request) {
        MutableLiveData<Resource<Feedback>> addFeedbackLiveData = new MutableLiveData<>();
        addFeedbackLiveData.setValue(Resource.loading());
        feedbackApi.addFeedback(request).enqueue(new Callback<ApiResponse<Feedback>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Feedback>> call, @NonNull Response<ApiResponse<Feedback>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Feedback> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 201) {
                        Feedback feedback = apiResponse.getData();
                        addFeedbackLiveData.setValue(Resource.success(feedback));
                    } else {
                        addFeedbackLiveData.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    addFeedbackLiveData.setValue(Resource.error("Không thể tạo đánh giá"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Feedback>> call, @NonNull Throwable t) {
                addFeedbackLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return addFeedbackLiveData;
    }

    public LiveData<Resource<WishlistStatusResponse>> getWishlistStatus(long productId) {
        MutableLiveData<Resource<WishlistStatusResponse>> wishlistStatusLiveData = new MutableLiveData<>();
        wishlistStatusLiveData.setValue(Resource.loading());
        productApi.getWishlistStatus(productId).enqueue(new Callback<ApiResponse<WishlistStatusResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<WishlistStatusResponse>> call, @NonNull Response<ApiResponse<WishlistStatusResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<WishlistStatusResponse> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        wishlistStatusLiveData.setValue(Resource.success(apiResponse.getData()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<WishlistStatusResponse>> call, @NonNull Throwable t) {
                wishlistStatusLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return wishlistStatusLiveData;
    }
    public LiveData<Resource<String>> getSummaryFeedback(long productId) {
        MutableLiveData<Resource<String>> summaryFeedbackLiveData = new MutableLiveData<>();
        summaryFeedbackLiveData.setValue(Resource.loading());
        productApi.getSummaryFeedback(productId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                        summaryFeedbackLiveData.setValue(Resource.success(response.body()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                summaryFeedbackLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return summaryFeedbackLiveData;
    }

}
