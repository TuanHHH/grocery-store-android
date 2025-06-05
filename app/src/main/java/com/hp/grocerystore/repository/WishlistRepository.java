package com.hp.grocerystore.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.network.api.WishlistApi;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistRepository {
    private static volatile WishlistRepository INSTANCE;
    private final WishlistApi wishlistApi;

    private WishlistRepository(WishlistApi wishlistApi) {
        this.wishlistApi = wishlistApi;
    }

    public static WishlistRepository getInstance(WishlistApi wishlistApi) {
        if (INSTANCE == null) {
            synchronized (WishlistRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WishlistRepository(wishlistApi);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<Void>> addWishlist(long productId) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());
        String json = "{\"productId\":" + productId + "}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        wishlistApi.addWishlist(body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(null));
                } else {
                    liveData.setValue(Resource.error("Thêm sản phẩm yêu thích thất bại"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                liveData.postValue(Resource.error(t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<List<Wishlist>>> getWishlist(int page, int size) {
        MutableLiveData<Resource<List<Wishlist>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        wishlistApi.getProductsInWishlist(page, size).enqueue(new Callback<ApiResponse<PaginationResponse<Wishlist>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaginationResponse<Wishlist>>> call,  @NonNull Response<ApiResponse<PaginationResponse<Wishlist>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Wishlist> wishlistItems = response.body().getData().getResult();
                    liveData.setValue(Resource.success(wishlistItems));
                } else {
                    liveData.setValue(Resource.error("Lỗi khi tải danh sách yêu thích"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PaginationResponse<Wishlist>>> call, @NonNull Throwable t) {
                liveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> deleteWishlist(Long id) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        wishlistApi.deleteWishlist(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatusCode() == 200) {
                    liveData.setValue(Resource.success(null));
                } else {
                    String errorMsg = "Xóa sản phẩm khỏi danh sách yêu thích thất bại";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    liveData.setValue(Resource.error(errorMsg));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                liveData.setValue(Resource.error(t.getMessage()));
            }
        });

        return liveData;
    }


}
