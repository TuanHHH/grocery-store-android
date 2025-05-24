package com.hp.grocerystore.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.model.product.ProductOrder;
import com.hp.grocerystore.network.api.OrderApi;
import com.hp.grocerystore.utils.Resource;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final OrderApi orderApi;
    private final MutableLiveData<Resource<List<Order>>> ordersLiveData;
    private final MutableLiveData<Resource<Integer>> totalOrders;
    private int currentPage;
    private int pageSize;
    private int status;
    private boolean isLoading;
    private boolean hasMoreData;

    private final MutableLiveData<Resource<List<ProductOrder>>> productLiveData;
    private final MutableLiveData<Resource<Order>> orderInfo;

    public OrderRepository(OrderApi orderApi) {
        this.orderApi = orderApi;
        this.productLiveData = new MutableLiveData<>();
        this.orderInfo = new MutableLiveData<>();
        this.ordersLiveData = new MutableLiveData<>();
        this.totalOrders = new MutableLiveData<>();
        this.currentPage = 1;
        this.pageSize = 15;
        this.status = 1;
        this.isLoading = false;
        this.hasMoreData = true;
    }

    public LiveData<Resource<List<Order>>> getOrdersByStatus(int status) {
        this.status = status;
        if (!isLoading && hasMoreData) {
            loadOrdersByStatus(status);
        }
        return ordersLiveData;
    }

    public LiveData<Resource<Integer>> getTotalOrders() {
        return totalOrders;
    }

    public void fetchOrdersByStatus(int status) {
        this.status = status;
        this.currentPage = 1;
        if (!isLoading && hasMoreData) {
            loadOrdersByStatus(status);
        }
    }

    public void loadMoreOrders() {
        if (!isLoading && hasMoreData) {
            currentPage++;
            loadOrders();
        }
    }

    public void loadMoreOrdersByStatus(int status) {
        this.status = status;
        if (!isLoading && hasMoreData) {
            currentPage++;
            loadOrdersByStatus(status);
        }
    }

    public void refreshOrders() {
        currentPage = 1;
        hasMoreData = true;
        loadOrders();
    }

    public void refreshOrdersByStatus(int status) {
        this.status = status;
        currentPage = 1;
        hasMoreData = true;
        loadOrdersByStatus(status);
    }

    private void loadOrders() {
        isLoading = true;
        ordersLiveData.setValue(Resource.loading());
        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PaginationResponse<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<Order>>> call, Response<ApiResponse<PaginationResponse<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaginationResponse<Order>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        PaginationResponse<Order> pagedResult = apiResponse.getData();
                        List<Order> orders = pagedResult.getResult();
                        Log.d("OrderRepository", "loadOrders: Orders received: " + (orders != null ? orders.size() : "null"));
                        if (orders != null) {
                            if (orders.isEmpty()) {
                                hasMoreData = false;
                            }
                            ordersLiveData.setValue(Resource.success(orders));
                        } else {
                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
                        }
                    } else {
                        Log.e("OrderRepository", "loadOrders: API error: " + apiResponse.getMessage());
                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    Log.e("OrderRepository", "loadOrders: Response failed: " + response.message());
                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<Order>>> call, Throwable t) {
                isLoading = false;
                Log.e("OrderRepository", "loadOrders: Failure: " + t.getMessage());
                ordersLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }

    private void loadOrdersByStatus(int status) {
        isLoading = true;
        ordersLiveData.setValue(Resource.loading());
        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PaginationResponse<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<Order>>> call, Response<ApiResponse<PaginationResponse<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaginationResponse<Order>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        PaginationResponse<Order> pagedResult = apiResponse.getData();
                        List<Order> orders = pagedResult.getResult();
                        Log.d("OrderRepository", "loadOrdersByStatus: Orders received: " + (orders != null ? orders.size() : "null"));
                        if (orders != null) {
                            if (orders.isEmpty()) {
                                hasMoreData = false;
                            }
                            ordersLiveData.setValue(Resource.success(orders));
                        } else {
                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
                        }
                    } else {
                        Log.e("OrderRepository", "loadOrdersByStatus: API error: " + apiResponse.getMessage());
                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    Log.e("OrderRepository", "loadOrdersByStatus: Response failed: " + response.message());
                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<Order>>> call, Throwable t) {
                isLoading = false;
                Log.e("OrderRepository", "loadOrdersByStatus: Failure: " + t.getMessage());
                ordersLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }
}