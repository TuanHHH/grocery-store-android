package com.hp.grocerystore.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.network.api.WishlistApi;
import com.hp.grocerystore.utils.PagedResult;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.utils.SingleLiveEvent;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistRepository {
    private final WishlistApi wishlistApi;
//    private final MutableLiveData<ApiResponse<Void>> addWishlistResult;
    private final SingleLiveEvent<ApiResponse<Void>> addWishlistResult;

    private final MutableLiveData<Resource<List<Wishlist>>> wishlistLiveData;

    public WishlistRepository(WishlistApi wishlistApi) {
        this.wishlistApi = wishlistApi;
        addWishlistResult = new SingleLiveEvent<>();
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
     * @return LiveData<Resource<List<Wishlist>>>
     */

    public LiveData<Resource<List<Wishlist>>> getWishlist(int page, int size) {
        MutableLiveData<Resource<List<Wishlist>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        wishlistApi.getProductsInWishlist(page, size).enqueue(new Callback<ApiResponse<PagedResult<Wishlist>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResult<Wishlist>>> call, Response<ApiResponse<PagedResult<Wishlist>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Wishlist> wishlistItems = response.body().getData().getResult();
                    liveData.setValue(Resource.success(wishlistItems));
                } else {
                    liveData.setValue(Resource.error("Lỗi khi tải danh sách yêu thích"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResult<Wishlist>>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return liveData;
    }

    /**
     * Gửi yêu cầu xóa một sản phẩm khỏi danh sách yêu thích theo ID.
     *
     * Phương thức này sử dụng Retrofit để gọi API xóa wishlist từ server.
     * Trạng thái của yêu cầu được phản ánh qua LiveData dưới dạng đối tượng {@link Resource}.
     *
     * @param id ID của sản phẩm trong danh sách yêu thích cần xóa.
     * @return {@link LiveData} chứa đối tượng {@link Resource<Void>} đại diện cho trạng thái:
     *         - {@code loading}: khi yêu cầu đang được thực hiện.
     *         - {@code success}: khi xóa thành công.
     *         - {@code error}: khi có lỗi xảy ra trong quá trình xóa.
     */
    public LiveData<Resource<Void>> deleteWishlist(Long id) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        wishlistApi.deleteWishlist(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
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
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage()));
            }
        });

        return liveData;
    }


    // Getter LiveData cho ViewModel hoặc UI quan sát
    public LiveData<ApiResponse<Void>> getAddWishlistResult() {
        return addWishlistResult;
    }

    public LiveData<Resource<List<Wishlist>>> getWishlistLiveData() {
        return wishlistLiveData;
    }
}
