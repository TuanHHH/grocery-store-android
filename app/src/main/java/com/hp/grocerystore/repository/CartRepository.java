package com.hp.grocerystore.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.network.api.CartApi;
import com.hp.grocerystore.utils.Resource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private final CartApi cartApi;
    private final MutableLiveData<Resource<List<CartItem>>> cartItemsLiveData;
    private final MutableLiveData<Resource<Boolean>> selectAllLiveData;
    private int currentPage;
    private boolean isLoading;
    private boolean hasMoreData;
    private static final int PAGE_SIZE = 10;
    private List<CartItem> allItems;

    public CartRepository(CartApi cartApi) {
        this.cartApi = cartApi;
        this.cartItemsLiveData = new MutableLiveData<>();
        this.selectAllLiveData = new MutableLiveData<>();
        this.currentPage = 1;
        this.isLoading = false;
        this.hasMoreData = true;
        this.allItems = new ArrayList<>();
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

    private void loadCartItems(String loadType) {
        if (isLoading) return;
        
        isLoading = true;
        Log.d("CartRepository", "Bắt đầu " + loadType + " trang: " + currentPage);
        
        if ("init".equals(loadType) || "refresh".equals(loadType)) {
            cartItemsLiveData.setValue(Resource.loading());
        }

        cartApi.getCartItems(currentPage, PAGE_SIZE).enqueue(new Callback<ApiResponse<PaginationResponse<CartItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<CartItem>>> call, Response<ApiResponse<PaginationResponse<CartItem>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaginationResponse<CartItem>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        PaginationResponse<CartItem> paginationResponse = apiResponse.getData();
                        List<CartItem> newItems = paginationResponse.getResult();
                        
                        if (newItems != null && !newItems.isEmpty()) {
                            hasMoreData = currentPage < paginationResponse.getMeta().getPages();
                            Log.d("CartRepository", String.format("Trang %d/%d, Số sản phẩm: %d, Còn dữ liệu: %s", 
                                currentPage, 
                                paginationResponse.getMeta().getPages(),
                                newItems.size(),
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
            public void onFailure(Call<ApiResponse<PaginationResponse<CartItem>>> call, Throwable t) {
                isLoading = false;
                cartItemsLiveData.setValue(Resource.error(t.getMessage()));
                Log.e("CartRepository", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
