package com.hp.grocerystore.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.model.order.StatusUpdateRequest;
import com.hp.grocerystore.model.product.ProductOrder;
import com.hp.grocerystore.network.api.OrderApi;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class OrderRepository {
    private static volatile OrderRepository INSTANCE;
    private final OrderApi orderApi;
    private final MutableLiveData<Resource<List<Order>>> ordersLiveData;
    private final MutableLiveData<Resource<Integer>> totalOrders;
    private int currentPage;
    private final int pageSize;
    private int currentStatus; // Thêm biến để track status hiện tại
    private boolean isLoading;
    private boolean hasMoreData;

    private final MutableLiveData<Resource<List<ProductOrder>>> productLiveData;
    private final MutableLiveData<Resource<Order>> orderInfo;
    private final MutableLiveData<Resource<Order>> updateStatusResult = new MutableLiveData<>();

    private OrderRepository(OrderApi orderApi) {
        this.orderApi = orderApi;
        this.productLiveData = new MutableLiveData<>();
        this.orderInfo = new MutableLiveData<>();
        this.ordersLiveData = new MutableLiveData<>();
        this.totalOrders = new MutableLiveData<>();
        this.currentPage = 1;
        this.pageSize = 15;
        this.currentStatus = -1; // Khởi tạo với giá trị không hợp lệ
        this.isLoading = false;
        this.hasMoreData = true;
    }
    public static OrderRepository getInstance(OrderApi orderApi) {
        if (INSTANCE == null) {
            synchronized (OrderRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OrderRepository(orderApi);
                }
            }
        }
        return INSTANCE;
    }


    public LiveData<Resource<List<Order>>> getOrdersByStatus(int status) {
        // Reset trạng thái khi status thay đổi
        if (this.currentStatus != status) {
            resetState();
            this.currentStatus = status;
        }

        if (!isLoading && hasMoreData) {
            loadOrdersByStatus(status);
        }
        return ordersLiveData;
    }

//    public void fetchOrdersByStatus(int status) {
//        // Reset trạng thái khi fetch status mới
//        if (this.currentStatus != status) {
//            resetState();
//            this.currentStatus = status;
//        }
//
//        if (!isLoading) {
//            loadOrdersByStatus(status);
//        }
//    }

    private void resetState() {
        this.currentPage = 1;
        this.isLoading = false;
        this.hasMoreData = true;
        this.ordersLiveData.setValue(Resource.loading());
    }

    public void refreshOrdersByStatus(int status) {
        this.currentStatus = status;
        resetState();
        loadOrdersByStatus(status);
    }


    public LiveData<Resource<Order>> updateOrderStatus(int orderId, int status) {
        updateStatusResult.setValue(Resource.loading());

        orderApi.updateOrderStatus(orderId, new StatusUpdateRequest(status)).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Order>> call, @NonNull Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Order> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        updateStatusResult.setValue(Resource.success(apiResponse.getData()));
                    } else {
                        updateStatusResult.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    updateStatusResult.setValue(Resource.error("Cập nhật trạng thái thất bại"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Order>> call, @NonNull Throwable t) {
                updateStatusResult.setValue(Resource.error(t.getMessage()));
            }
        });

        return updateStatusResult;
    }


    private void loadOrdersByStatus(int status) {
        isLoading = true;
        ordersLiveData.setValue(Resource.loading());
        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PaginationResponse<Order>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaginationResponse<Order>>> call, @NonNull Response<ApiResponse<PaginationResponse<Order>>> response) {
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
            public void onFailure(@NonNull Call<ApiResponse<PaginationResponse<Order>>> call, @NonNull Throwable t) {
                isLoading = false;
                Log.e("OrderRepository", "loadOrdersByStatus: Failure: " + t.getMessage());
                ordersLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }

    public LiveData<Resource<Order>> getOrderById(int orderId) {
        loadOrderInformationById(orderId);
        return orderInfo;
    }

    private void loadOrderInformationById(int orderId) {
        isLoading = true;
        orderInfo.setValue(Resource.loading());
        orderApi.getOrderInfo(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Order>> call, @NonNull Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Order> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        Order order = apiResponse.getData();
                        Log.d("OrderRepository", "loadOrderInformationById: Order received: " + (order != null ? order.toString() : "null"));
                        if (order != null) {
                            orderInfo.setValue(Resource.success(order));
                        } else {
                            orderInfo.setValue(Resource.error("Không tìm thấy thông tin đơn hàng"));
                        }
                    } else {
                        Log.e("OrderRepository", "loadOrderInformationById: API error: " + apiResponse.getMessage());
                        orderInfo.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    Log.e("OrderRepository", "loadOrderInformationById: Response failed: " + response.message());
                    orderInfo.setValue(Resource.error("Lỗi khi tải thông tin đơn hàng"));
                }
                isLoading = false;
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Order>> call, @NonNull Throwable t) {
                isLoading = false;
                Log.e("OrderRepository", "loadOrderInformationById: Failure: " + t.getMessage());
                orderInfo.setValue(Resource.error(t.getMessage()));
            }
        });
    }

    public LiveData<Resource<List<ProductOrder>>> getProductLiveData(int orderId) {
        loadProductsInOrderDetailById(orderId);
        return productLiveData;
    }

    private void loadProductsInOrderDetailById(int orderId) {
        isLoading = true;
        productLiveData.setValue(Resource.loading());
        orderApi.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<List<ProductOrder>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ProductOrder>>> call, @NonNull Response<ApiResponse<List<ProductOrder>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ProductOrder>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        List<ProductOrder> products = apiResponse.getData();
                        Log.d("OrderRepository", "loadProductsInOrderDetailById: Products received: " + (products != null ? products.size() : "null"));
                        productLiveData.setValue(Resource.success(products));
                    } else {
                        Log.e("OrderRepository", "loadProductsInOrderDetailById: API error: " + apiResponse.getMessage());
                        productLiveData.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    Log.e("OrderRepository", "loadProductsInOrderDetailById: Response failed: " + response.message());
                    productLiveData.setValue(Resource.error("Lỗi khi tải chi tiết đơn hàng"));
                }
                isLoading = false;
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ProductOrder>>> call, @NonNull Throwable t) {
                isLoading = false;
                Log.e("OrderRepository", "loadProductsInOrderDetailById: Failure: " + t.getMessage());
                productLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }
}
