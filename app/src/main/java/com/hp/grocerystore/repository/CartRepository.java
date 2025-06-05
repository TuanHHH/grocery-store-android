package com.hp.grocerystore.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.cart.AddCartResponse;
import com.hp.grocerystore.model.cart.AddToCartRequest;
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.network.api.CartApi;
import com.hp.grocerystore.utils.Resource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private static volatile CartRepository INSTANCE;
    private final CartApi cartApi;
    private final MutableLiveData<Resource<List<CartItem>>> cartItemsLiveData;
    private final MutableLiveData<Resource<Boolean>> selectAllLiveData;
    private final MutableLiveData<Resource<Integer>> totalItemsLiveData;

    private int currentPage;
    private boolean isLoading;
    private boolean hasMoreData;
    private static final int PAGE_SIZE = 10;
    private List<CartItem> allItems;

    private CartRepository(CartApi cartApi) {
        this.cartApi = cartApi;
        this.cartItemsLiveData = new MutableLiveData<>();
        this.selectAllLiveData = new MutableLiveData<>();
        this.totalItemsLiveData = new MutableLiveData<>();
        this.currentPage = 1;
        this.isLoading = false;
        this.hasMoreData = true;
        this.allItems = new ArrayList<>();
    }

    public static CartRepository getInstance(CartApi cartApi) {
        if (INSTANCE == null) {
            synchronized (CartRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CartRepository(cartApi);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<CartItem>>> getCartItems() {
        if (!isLoading && hasMoreData) {
            loadCartItems("init");
        }
        return cartItemsLiveData;
    }

    public LiveData<Resource<Boolean>> getSelectAllState() {
        return selectAllLiveData;
    }

    public LiveData<Resource<Integer>> getTotalItems() {
        return totalItemsLiveData;
    }

    public void selectAll(boolean isSelected) {
        if (cartItemsLiveData.getValue() != null && cartItemsLiveData.getValue().data != null) {
            List<CartItem> items = cartItemsLiveData.getValue().data;
            for (CartItem item : items) {
                if (item.getStock() > 0) {
                    item.setSelected(isSelected);
                }
            }
            cartItemsLiveData.setValue(Resource.success(new ArrayList<>(items)));
            selectAllLiveData.setValue(Resource.success(isSelected));
        }
    }

    public void updateSelectAllState() {
        if (cartItemsLiveData.getValue() != null && cartItemsLiveData.getValue().data != null) {
            List<CartItem> items = cartItemsLiveData.getValue().data;
            boolean allSelected = !items.isEmpty() &&
                    items.stream()
                            .filter(item -> item.getStock() > 0)
                            .allMatch(CartItem::isSelected);
            selectAllLiveData.setValue(Resource.success(allSelected));
        }
    }

    public void loadMoreCartItems() {
        if (!isLoading && hasMoreData) {
            currentPage++;
            loadCartItems("loadMore");
        }
    }

    public void refreshCartItems() {
        currentPage = 1;
        hasMoreData = true;
        allItems.clear();
        loadCartItems("refresh");
    }

    public void removeCartItem(long productId) {
        Log.d("CartRepository", "Bắt đầu xóa sản phẩm: " + productId);
        cartApi.removeCartItem(productId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        Log.d("CartRepository", "Xóa sản phẩm thành công, cập nhật lại dữ liệu");
                        refreshCartItems();
                    } else {
                        Log.e("CartRepository", "Lỗi khi xóa sản phẩm: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e("CartRepository", "Lỗi khi xóa sản phẩm: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Log.e("CartRepository", "Lỗi kết nối khi xóa sản phẩm: " + t.getMessage());
            }
        });
    }

    private void loadCartItems(String loadType) {
        if (isLoading) return;

        isLoading = true;
        Log.d("CartRepository", "Bắt đầu " + loadType + " trang: " + currentPage);

        if ("init".equals(loadType) || "refresh".equals(loadType)) {
            cartItemsLiveData.setValue(Resource.loading());
        }

        cartApi.getCartItems(currentPage, PAGE_SIZE).enqueue(new Callback<ApiResponse<PaginationResponse<CartItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaginationResponse<CartItem>>> call, @NonNull Response<ApiResponse<PaginationResponse<CartItem>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaginationResponse<CartItem>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        PaginationResponse<CartItem> paginationResponse = apiResponse.getData();
                        List<CartItem> newItems = paginationResponse.getResult();

                        totalItemsLiveData.setValue(Resource.success(paginationResponse.getMeta().getTotal()));

                        if (newItems != null && !newItems.isEmpty()) {
                            hasMoreData = currentPage < paginationResponse.getMeta().getPages();
                            Log.d("CartRepository", String.format("Trang %d/%d, Số sản phẩm: %d, Tổng sản phẩm: %d, Còn dữ liệu: %s",
                                    currentPage,
                                    paginationResponse.getMeta().getPages(),
                                    newItems.size(),
                                    paginationResponse.getMeta().getTotal(),
                                    hasMoreData));

                            if ("loadMore".equals(loadType)) {
                                allItems.addAll(newItems);
                                cartItemsLiveData.setValue(Resource.success(new ArrayList<>(allItems)));
                            } else {
                                allItems.clear();
                                allItems.addAll(newItems);
                                cartItemsLiveData.setValue(Resource.success(new ArrayList<>(allItems)));
                            }

                            updateSelectAllState();
                        } else {
                            if ("loadMore".equals(loadType)) {
                                hasMoreData = false;
                            } else {
                                allItems.clear();
                                cartItemsLiveData.setValue(Resource.success(new ArrayList<>()));
                            }
                        }
                    } else {
                        cartItemsLiveData.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    cartItemsLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PaginationResponse<CartItem>>> call, @NonNull Throwable t) {
                isLoading = false;
                cartItemsLiveData.setValue(Resource.error(t.getMessage()));
                Log.e("CartRepository", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public LiveData<Resource<Void>> addOrUpdateCart(AddToCartRequest request) {
        MutableLiveData<Resource<Void>> addCartLiveData = new MutableLiveData<>();
        Log.d("API", "call cart");
        addCartLiveData.setValue(Resource.loading());
        cartApi.addOrUpdateCart(request).enqueue(new Callback<ApiResponse<AddCartResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AddCartResponse>> call, @NonNull Response<ApiResponse<AddCartResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AddCartResponse> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 201) {
                        addCartLiveData.setValue(Resource.success(null));
                    } else {
                        Log.d("API", "call cart error");
                        addCartLiveData.setValue(Resource.error("Thêm vào giỏ hàng thất bại"));
                    }
                } else {
                    String errorMessage = "Thêm vào giỏ hàng thất bại";
                    try {
                        Log.d("API", "call cart error");
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(response.errorBody().charStream(), ApiResponse.class);
                            if (errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    addCartLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AddCartResponse>> call, @NonNull Throwable throwable) {
                Log.d("API", "call cart error");
                addCartLiveData.setValue(Resource.error(throwable.getMessage()));
            }
        });
        return addCartLiveData;
    }
}
