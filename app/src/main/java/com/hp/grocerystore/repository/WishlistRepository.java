package com.hp.grocerystore.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.network.api.WishlistApi;
import com.hp.grocerystore.utils.PagedResult;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistRepository {
    private final WishlistApi wishlistApi;
    private final MutableLiveData<ApiResponse<Void>> addWishlistResult;
    private final MutableLiveData<Resource<List<Wishlist>>> wishlistLiveData;

    public WishlistRepository(WishlistApi wishlistApi) {
        this.wishlistApi = wishlistApi;
        addWishlistResult = new MutableLiveData<>();
        wishlistLiveData = new MutableLiveData<>();
    }

    /**
     * Gọi API để thêm sản phẩm vào wishlist
     * @param productId ID sản phẩm cần thêm
     * @return LiveData<ApiResponse<Void>> phản hồi từ server
     */
    public void addWishlist(long productId) {
        String json = "{\"productId\":" + productId + "}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        wishlistApi.addWishlist(body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addWishlistResult.postValue(response.body());
                } else {
                    ApiResponse<Void> error = new ApiResponse<>();
                    error.setMessage("Add wishlist failed: " + response.code());
                    addWishlistResult.postValue(error);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                ApiResponse<Void> error = new ApiResponse<>();
                error.setMessage("Add wishlist error: " + t.getMessage());
                addWishlistResult.postValue(error);
            }
        });
    }

    /**
     * Gọi API để lấy danh sách sản phẩm trong wishlist
     * @return LiveData<ApiResponse<PagedResult<Product>>>
     */

        public void getWishlist(int page, int size) {
            wishlistLiveData.postValue(Resource.loading());

            wishlistApi.getProductsInWishlist(page, size).enqueue(new Callback<ApiResponse<PagedResult<Wishlist>>>() {
                @Override
                public void onResponse(Call<ApiResponse<PagedResult<Wishlist>>> call, Response<ApiResponse<PagedResult<Wishlist>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Wishlist> wishlistItems = response.body().getData().getResult();
                        wishlistLiveData.postValue(Resource.success(wishlistItems));
                    } else {
                        wishlistLiveData.postValue(Resource.error("Lỗi khi tải danh sách yêu thích"));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<PagedResult<Wishlist>>> call, Throwable t) {
                    wishlistLiveData.postValue(Resource.error(t.getMessage()));
                }
            });
        }

        // Getter LiveData cho ViewModel hoặc UI quan sát
        public LiveData<ApiResponse<Void>> getAddWishlistResult() {
            return addWishlistResult;
        }

        public LiveData<Resource<List<Wishlist>>> getWishlistLiveData() {
            return wishlistLiveData;
        }
}
