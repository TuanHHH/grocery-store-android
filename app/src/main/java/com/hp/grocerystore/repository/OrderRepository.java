//package com.hp.grocerystore.repository;
//
//import android.util.Log;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.hp.grocerystore.model.base.ApiResponse;
//import com.hp.grocerystore.model.base.PaginationResponse;
//import com.hp.grocerystore.model.order.Order;
//import com.hp.grocerystore.model.product.Product;
//import com.hp.grocerystore.network.api.OrderApi;
//import com.hp.grocerystore.utils.PagedResult;
//import com.hp.grocerystore.utils.Resource;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class OrderRepository {
//    private final OrderApi orderApi;
////    private final MutableLiveData<Resource<List<Order>>> ordersLiveData = new MutableLiveData<>();
////    private final MutableLiveData<Resource<Integer>> totalOrders = new MutableLiveData<>();
//    private final MutableLiveData<Resource<List<Order>>> ordersLiveData;
//    private final MutableLiveData<Resource<Integer>> totalOrders;
//    private int currentPage = 1;
//    private int pageSize = 15;
//    private int status = 1;
//    private boolean isLoading;
//    private boolean hasMoreData;
//
//
//    public OrderRepository(OrderApi orderApi) {
//        this.hasMoreData = true;
//        this.isLoading = false;
////        this.status = 1;
//        this.pageSize = 15;
//        this.currentPage = 1;
//        this.totalOrders  = new MutableLiveData<>();
//        this.ordersLiveData  = new MutableLiveData<>();
//        this.orderApi = orderApi;
//    }
//
//
//
////    public MutableLiveData<Resource<List<Order>>> getOrders() {
////        return ordersLiveData;
////    }
//
//    public LiveData<Resource<List<Order>>> getOrdersByStatus(int status) {
//        this.status = status;
////        currentPage = 1;
//        if (!isLoading && hasMoreData) {
//            currentPage += 1;
//            loadOrdersByStatus(status);
//        }
//        return ordersLiveData;
//    }
//
//
//    public LiveData<Resource<Integer>> getTotalOrders() {
//        return totalOrders;
//    }
//
//    public void fetchOrdersByStatus(int status) {
//        this.status = status;
//        this.currentPage = 1;
////        loadOrdersByStatus(status);
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrdersByStatus(status);
//        }
//    }
//
//    public void loadMoreOrders() {
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrders();
//        }
//    }
//    public void loadMoreOrdersByStatus(int status) {
//        this.status = status;
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrdersByStatus(status);
//        }
//    }
//
//
//
//    public void refreshOrders() {
//        currentPage = 1;
//        hasMoreData = true;
//        loadOrders();
//    }
//    public void refreshOrdersByStatus(int status) {
//        this.status = status;
//        currentPage = 1;
//        hasMoreData = true;
//        loadOrdersByStatus(status);
//    }
//
////    private void loadOrders() {
////        isLoading = true;
////        ordersLiveData.setValue(Resource.loading());
////        orderApi.getOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<List<Order>>>() {
////            @Override
////            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
////                if (response.isSuccessful() && response.body() != null) {
////                    ApiResponse<List<Order>> apiResponse = response.body();
////                    if (apiResponse.getStatusCode() == 200) {
////                        List<Order> orders = apiResponse.getData();
////                        if (orders != null) {
////                            if (orders.isEmpty()) {
////                                hasMoreData = false;
////                            }
////                            ordersLiveData.setValue(Resource.success(orders));
////                        } else {
////                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
////                        }
////                    }else {
////                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
////                    }
////                } else {
////                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
////                }
////                isLoading = false;
////            }
////
////            @Override
////            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
////                isLoading = false;
////                ordersLiveData.setValue(Resource.error(t.getMessage()));
////            }
////        });
////    }
////    Callback<ApiResponse<PaginationResponse<Order>>>
//private void loadOrders() {
//    isLoading = true;
//    ordersLiveData.setValue(Resource.loading());
//    orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//        @Override
//        public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//            if (response.isSuccessful() && response.body() != null) {
//                ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                if (apiResponse.getStatusCode() == 200) {
//                    PagedResult<Order> pagedResult = apiResponse.getData();
//
//                    List<Order> orders = pagedResult.getResult();
//                    if (orders != null) {
//                        if (orders.isEmpty()) {
//                            hasMoreData = false;
//                        }
//                        ordersLiveData.setValue(Resource.success(orders));
//                    } else {
//                        ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
//                    }
//                }else {
//                    ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                }
//            } else {
//                ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
//            }
//            isLoading = false;
//        }
//
//
//
//        @Override
//        public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//            isLoading = false;
//            ordersLiveData.setValue(Resource.error(t.getMessage()));
//        }
//    });
//}
//    private void loadOrdersByStatus(int status) {
//        isLoading = true;
//        ordersLiveData.setValue(Resource.loading());
//        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        PagedResult<Order> pagedResult = apiResponse.getData();
//
//                        List<Order> orders = pagedResult.getResult();
//                        Log.d("OrderRepository", "Received orders: " + (orders != null ? orders.size() : "null"));
//                        if (orders != null) {
//                            if (orders.isEmpty()) {
//                                hasMoreData = false;
//                            }
//                            ordersLiveData.setValue(Resource.success(orders));
//                        } else {
//                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
//                        }
//                    }else {
//                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
//                }
//                isLoading = false;
//            }
//
//
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//                isLoading = false;
//                ordersLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//
////    ApiResponse<PaginationResponse<Order>>
//}


//package com.hp.grocerystore.repository;
//import android.util.Log;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import com.hp.grocerystore.model.base.ApiResponse;
//import com.hp.grocerystore.model.order.Order;
//import com.hp.grocerystore.model.product.ProductOrder;
//import com.hp.grocerystore.network.api.OrderApi;
//import com.hp.grocerystore.utils.PagedResult;
//import com.hp.grocerystore.utils.Resource;
//import java.util.List;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class OrderRepository {
//    private final OrderApi orderApi;
//    private final MutableLiveData<Resource<List<Order>>> ordersLiveData;
//    private final MutableLiveData<Resource<Integer>> totalOrders;
//    private int currentPage;
//    private int pageSize;
//    private int status;
//    private boolean isLoading;
//    private boolean hasMoreData;
//
//    private final MutableLiveData<Resource<List<ProductOrder>>> productLiveData;
//
//    private final MutableLiveData<Resource<Order>> orderInfo ;
//
//    public OrderRepository(OrderApi orderApi) {
//        this.orderApi = orderApi;
////        this.productLiveData = null;
////        this.orderInfo = null;
//        this.productLiveData = new MutableLiveData<>();
//        this.orderInfo = new MutableLiveData<>();
//        this.ordersLiveData = new MutableLiveData<>();
//        this.totalOrders = new MutableLiveData<>();
//        this.currentPage = 1;
//        this.pageSize = 15;
//        this.status = 1; // Default status (e.g., pending orders)
//        this.isLoading = false;
//        this.hasMoreData = true;
//    }
//
//    public LiveData<Resource<List<Order>>> getOrdersByStatus(int status) {
//        this.status = status;
//        if (!isLoading && hasMoreData) {
//            loadOrdersByStatus(status);
//        }
//        return ordersLiveData;
//    }
//
//
//    public LiveData<Resource<Integer>> getTotalOrders() {
//        return totalOrders;
//    }
//
//    public void fetchOrdersByStatus(int status) {
//        this.status = status;
//        this.currentPage = 1;
//        if (!isLoading && hasMoreData) {
//            loadOrdersByStatus(status);
//        }
//    }
//
//    public void loadMoreOrders() {
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrders();
//        }
//    }
//
//    public void loadMoreOrdersByStatus(int status) {
//        this.status = status;
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrdersByStatus(status);
//        }
//    }
//
//    public void refreshOrders() {
//        currentPage = 1;
//        hasMoreData = true;
//        loadOrders();
//    }
//
//    public void refreshOrdersByStatus(int status) {
//        this.status = status;
//        currentPage = 1;
//        hasMoreData = true;
//        loadOrdersByStatus(status);
//    }
//    private void loadOrdersInformation(int orderId) {
////        loadOrders();
//    }
//
//    private void loadOrders() {
//        isLoading = true;
//        ordersLiveData.setValue(Resource.loading());
//        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        PagedResult<Order> pagedResult = apiResponse.getData();
//                        List<Order> orders = pagedResult.getResult();
//                        if (orders != null) {
//                            if (orders.isEmpty()) {
//                                hasMoreData = false;
//                            }
//                            ordersLiveData.setValue(Resource.success(orders));
//                        } else {
//                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
//                        }
//                    } else {
//                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//                isLoading = false;
//                ordersLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    private void loadOrdersByStatus(int status) {
//        isLoading = true;
//        ordersLiveData.setValue(Resource.loading());
//        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        PagedResult<Order> pagedResult = apiResponse.getData();
//                        List<Order> orders = pagedResult.getResult();
//                        Log.d("OrderRepository", "Received orders: " + (orders != null ? orders.size() : "null"));
//                        if (orders != null) {
//                            if (orders.isEmpty()) {
//                                hasMoreData = false;
//                            }
//                            ordersLiveData.setValue(Resource.success(orders));
//                        } else {
//                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
//                        }
//                    } else {
//                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//                isLoading = false;
//                ordersLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//    private void loadOrderInformationById(int orderId){
//
//    }
//
//    private void loadProductsInOrderDetailById(int orderId) {
//
//    }
//
//    public LiveData<Resource<Order>> getOrderById(int orderId) {
//        isLoading = true;
//        orderInfo.setValue(Resource.loading());
//        orderApi.getOrderInfo(orderId).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        PagedResult<Order> pagedResult = apiResponse.getData();
//                        List<Order> orders = pagedResult.getResult();
////                        List<Order> orders = apiResponse.getData().getResult();
//                        if (orders != null && !orders.isEmpty()) {
//                            orderInfo.setValue(Resource.success(orders.get(0))); // Vì đây là info của 1 order
//                        } else {
//                            orderInfo.setValue(Resource.error("Không tìm thấy thông tin đơn hàng"));
//                        }
//                    } else {
//                        orderInfo.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    orderInfo.setValue(Resource.error("Lỗi khi tải thông tin đơn hàng"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//                isLoading = false;
//                orderInfo.setValue(Resource.error(t.getMessage()));
//            }
//        });
//
//        return orderInfo;
//    }
//
//    public LiveData<Resource<List<ProductOrder>>> getProductLiveData(int orderId) {
//        isLoading = true;
//        productLiveData.setValue(Resource.loading());
//        orderApi.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<PagedResult<ProductOrder>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<ProductOrder>>> call, Response<ApiResponse<PagedResult<ProductOrder>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<ProductOrder>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        List<ProductOrder> products = apiResponse.getData().getResult();
//                        productLiveData.setValue(Resource.success(products));
//                    } else {
//                        productLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    productLiveData.setValue(Resource.error("Lỗi khi tải chi tiết đơn hàng"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<ProductOrder>>> call, Throwable t) {
//                isLoading = false;
//                productLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//
//        return productLiveData;
//    }
//}

//package com.hp.grocerystore.repository;
//
//import android.util.Log;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import com.hp.grocerystore.model.base.ApiResponse;
//import com.hp.grocerystore.model.order.Order;
//import com.hp.grocerystore.model.product.ProductOrder;
//import com.hp.grocerystore.network.api.OrderApi;
//import com.hp.grocerystore.utils.PagedResult;
//import com.hp.grocerystore.utils.Resource;
//import java.util.List;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class OrderRepository {
//    private final OrderApi orderApi;
//    private final MutableLiveData<Resource<List<Order>>> ordersLiveData;
//    private final MutableLiveData<Resource<Integer>> totalOrders;
//    private int currentPage;
//    private int pageSize;
//    private int status;
//    private boolean isLoading;
//    private boolean hasMoreData;
//
//    private final MutableLiveData<Resource<List<ProductOrder>>> productLiveData;
//    private final MutableLiveData<Resource<Order>> orderInfo;
//
//    public OrderRepository(OrderApi orderApi) {
//        this.orderApi = orderApi;
//        this.productLiveData = new MutableLiveData<>();
//        this.orderInfo = new MutableLiveData<>();
//        this.ordersLiveData = new MutableLiveData<>();
//        this.totalOrders = new MutableLiveData<>();
//        this.currentPage = 1;
//        this.pageSize = 15;
//        this.status = 1; // Default status (e.g., pending orders)
//        this.isLoading = false;
//        this.hasMoreData = true;
//    }
//
//    public LiveData<Resource<List<Order>>> getOrdersByStatus(int status) {
//        this.status = status;
//        if (!isLoading && hasMoreData) {
//            loadOrdersByStatus(status);
//        }
//        return ordersLiveData;
//    }
//
//    public LiveData<Resource<Integer>> getTotalOrders() {
//        return totalOrders;
//    }
//
//    public void fetchOrdersByStatus(int status) {
//        this.status = status;
//        this.currentPage = 1;
//        if (!isLoading && hasMoreData) {
//            loadOrdersByStatus(status);
//        }
//    }
//
//    public void loadMoreOrders() {
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrders();
//        }
//    }
//
//    public void loadMoreOrdersByStatus(int status) {
//        this.status = status;
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrdersByStatus(status);
//        }
//    }
//
//    public void refreshOrders() {
//        currentPage = 1;
//        hasMoreData = true;
//        loadOrders();
//    }
//
//    public void refreshOrdersByStatus(int status) {
//        this.status = status;
//        currentPage = 1;
//        hasMoreData = true;
//        loadOrdersByStatus(status);
//    }
//
//    private void loadOrders() {
//        isLoading = true;
//        ordersLiveData.setValue(Resource.loading());
//        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        PagedResult<Order> pagedResult = apiResponse.getData();
//                        List<Order> orders = pagedResult.getResult();
//                        if (orders != null) {
//                            if (orders.isEmpty()) {
//                                hasMoreData = false;
//                            }
//                            ordersLiveData.setValue(Resource.success(orders));
//                        } else {
//                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
//                        }
//                    } else {
//                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//                isLoading = false;
//                ordersLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    private void loadOrdersByStatus(int status) {
//        isLoading = true;
//        ordersLiveData.setValue(Resource.loading());
//        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        PagedResult<Order> pagedResult = apiResponse.getData();
//                        List<Order> orders = pagedResult.getResult();
//                        Log.d("OrderRepository", "Received orders: " + (orders != null ? orders.size() : "null"));
//                        if (orders != null) {
//                            if (orders.isEmpty()) {
//                                hasMoreData = false;
//                            }
//                            ordersLiveData.setValue(Resource.success(orders));
//                        } else {
//                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
//                        }
//                    } else {
//                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//                isLoading = false;
//                ordersLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    private void loadOrderInformationById(int orderId) {
//        isLoading = true;
//        orderInfo.setValue(Resource.loading());
//        orderApi.getOrderInfo(orderId).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        List<Order> orders = apiResponse.getData().getResult();
//                        if (orders != null && !orders.isEmpty()) {
//                            orderInfo.setValue(Resource.success(orders.get(0)));
//                        } else {
//                            orderInfo.setValue(Resource.error("Không tìm thấy thông tin đơn hàng"));
//                        }
//                    } else {
//                        orderInfo.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    orderInfo.setValue(Resource.error("Lỗi khi tải thông tin đơn hàng"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//                isLoading = false;
//                orderInfo.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    private void loadProductsInOrderDetailById(int orderId) {
//        isLoading = true;
//        productLiveData.setValue(Resource.loading());
//        orderApi.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<PagedResult<ProductOrder>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<ProductOrder>>> call, Response<ApiResponse<PagedResult<ProductOrder>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<ProductOrder>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        List<ProductOrder> products = apiResponse.getData().getResult();
//                        productLiveData.setValue(Resource.success(products));
//                    } else {
//                        productLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    productLiveData.setValue(Resource.error("Lỗi khi tải chi tiết đơn hàng"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<ProductOrder>>> call, Throwable t) {
//                isLoading = false;
//                productLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    public LiveData<Resource<Order>> getOrderById(int orderId) {
//        loadOrderInformationById(orderId); // Gọi phương thức đã triển khai
//        return orderInfo;
//    }
//
//    public LiveData<Resource<List<ProductOrder>>> getProductLiveData(int orderId) {
//        loadProductsInOrderDetailById(orderId); // Gọi phương thức đã triển khai
//        return productLiveData;
//    }
//}

package com.hp.grocerystore.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.model.product.ProductOrder;
import com.hp.grocerystore.network.api.OrderApi;
import com.hp.grocerystore.utils.PagedResult;
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
        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        PagedResult<Order> pagedResult = apiResponse.getData();
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
            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
                isLoading = false;
                Log.e("OrderRepository", "loadOrders: Failure: " + t.getMessage());
                ordersLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }

    private void loadOrdersByStatus(int status) {
        isLoading = true;
        ordersLiveData.setValue(Resource.loading());
        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        PagedResult<Order> pagedResult = apiResponse.getData();
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
            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
                isLoading = false;
                Log.e("OrderRepository", "loadOrdersByStatus: Failure: " + t.getMessage());
                ordersLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }

//    private void loadOrderInformationById(int orderId) {
//        isLoading = true;
//        orderInfo.setValue(Resource.loading());
//        orderApi.getOrderInfo(orderId).enqueue(new Callback<ApiResponse<PagedResult<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<Order>>> call, Response<ApiResponse<PagedResult<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        List<Order> orders = apiResponse.getData().getResult();
//                        Log.d("OrderRepository", "loadOrderInformationById: Order received: " + (orders != null && !orders.isEmpty() ? orders.get(0).toString() : "null"));
//                        if (orders != null && !orders.isEmpty()) {
//                            orderInfo.setValue(Resource.success(orders.get(0)));
//                        } else {
//                            orderInfo.setValue(Resource.error("Không tìm thấy thông tin đơn hàng"));
//                        }
//                    } else {
//                        Log.e("OrderRepository", "loadOrderInformationById: API error: " + apiResponse.getMessage());
//                        orderInfo.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    Log.e("OrderRepository", "loadOrderInformationById: Response failed: " + response.message());
//                    orderInfo.setValue(Resource.error("Lỗi khi tải thông tin đơn hàng"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<Order>>> call, Throwable t) {
//                isLoading = false;
//                Log.e("OrderRepository", "loadOrderInformationById: Failure: " + t.getMessage());
//                orderInfo.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    private void loadProductsInOrderDetailById(int orderId) {
//        isLoading = true;
//        productLiveData.setValue(Resource.loading());
//        orderApi.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<PagedResult<ProductOrder>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<PagedResult<ProductOrder>>> call, Response<ApiResponse<PagedResult<ProductOrder>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<PagedResult<ProductOrder>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        List<ProductOrder> products = apiResponse.getData().getResult();
//                        Log.d("OrderRepository", "loadProductsInOrderDetailById: Products received: " + (products != null ? products.size() : "null"));
//                        productLiveData.setValue(Resource.success(products));
//                    } else {
//                        Log.e("OrderRepository", "loadProductsInOrderDetailById: API error: " + apiResponse.getMessage());
//                        productLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    Log.e("OrderRepository", "loadProductsInOrderDetailById: Response failed: " + response.message());
//                    productLiveData.setValue(Resource.error("Lỗi khi tải chi tiết đơn hàng"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<PagedResult<ProductOrder>>> call, Throwable t) {
//                isLoading = false;
//                Log.e("OrderRepository", "loadProductsInOrderDetailById: Failure: " + t.getMessage());
//                productLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    public LiveData<Resource<Order>> getOrderById(int orderId) {
//        loadOrderInformationById(orderId);
//        return orderInfo;
//    }
//
//    public LiveData<Resource<List<ProductOrder>>> getProductLiveData(int orderId) {
//        loadProductsInOrderDetailById(orderId);
//        return productLiveData;
//    }
private void loadOrderInformationById(int orderId) {
    isLoading = true;
    orderInfo.setValue(Resource.loading());
    orderApi.getOrderInfo(orderId).enqueue(new Callback<ApiResponse<Order>>() {
        @Override
        public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
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
        public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
            isLoading = false;
            Log.e("OrderRepository", "loadOrderInformationById: Failure: " + t.getMessage());
            orderInfo.setValue(Resource.error(t.getMessage()));
        }
    });
}

    private void loadProductsInOrderDetailById(int orderId) {
        isLoading = true;
        productLiveData.setValue(Resource.loading());
        orderApi.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<List<ProductOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductOrder>>> call, Response<ApiResponse<List<ProductOrder>>> response) {
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
            public void onFailure(Call<ApiResponse<List<ProductOrder>>> call, Throwable t) {
                isLoading = false;
                Log.e("OrderRepository", "loadProductsInOrderDetailById: Failure: " + t.getMessage());
                productLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }

    public LiveData<Resource<Order>> getOrderById(int orderId) {
        loadOrderInformationById(orderId);
        return orderInfo;
    }

    public LiveData<Resource<List<ProductOrder>>> getProductLiveData(int orderId) {
        loadProductsInOrderDetailById(orderId);
        return productLiveData;
    }
}


//package com.hp.grocerystore.repository;
//
//import android.util.Log;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import com.hp.grocerystore.model.base.ApiResponse;
//import com.hp.grocerystore.model.order.Order;
//import com.hp.grocerystore.model.product.ProductOrder;
//import com.hp.grocerystore.network.api.OrderApi;
//import com.hp.grocerystore.utils.Resource;
//import java.util.List;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class OrderRepository {
//    private final OrderApi orderApi;
//    private final MutableLiveData<Resource<List<Order>>> ordersLiveData;
//    private final MutableLiveData<Resource<Integer>> totalOrders;
//    private int currentPage;
//    private int pageSize;
//    private int status;
//    private boolean isLoading;
//    private boolean hasMoreData;
//
//    private final MutableLiveData<Resource<List<ProductOrder>>> productLiveData;
//    private final MutableLiveData<Resource<Order>> orderInfo;
//
//    public OrderRepository(OrderApi orderApi) {
//        this.orderApi = orderApi;
//        this.productLiveData = new MutableLiveData<>();
//        this.orderInfo = new MutableLiveData<>();
//        this.ordersLiveData = new MutableLiveData<>();
//        this.totalOrders = new MutableLiveData<>();
//        this.currentPage = 1;
//        this.pageSize = 15;
//        this.status = 1;
//        this.isLoading = false;
//        this.hasMoreData = true;
//    }
//
//    public LiveData<Resource<List<Order>>> getOrdersByStatus(int status) {
//        this.status = status;
//        if (!isLoading && hasMoreData) {
//            loadOrdersByStatus(status);
//        }
//        return ordersLiveData;
//    }
//
//    public LiveData<Resource<Integer>> getTotalOrders() {
//        return totalOrders;
//    }
//
//    public void fetchOrdersByStatus(int status) {
//        this.status = status;
//        this.currentPage = 1;
//        if (!isLoading && hasMoreData) {
//            loadOrdersByStatus(status);
//        }
//    }
//
//    public void loadMoreOrders() {
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrders();
//        }
//    }
//
//    public void loadMoreOrdersByStatus(int status) {
//        this.status = status;
//        if (!isLoading && hasMoreData) {
//            currentPage++;
//            loadOrdersByStatus(status);
//        }
//    }
//
//    public void refreshOrders() {
//        currentPage = 1;
//        hasMoreData = true;
//        loadOrders();
//    }
//
//    public void refreshOrdersByStatus(int status) {
//        this.status = status;
//        currentPage = 1;
//        hasMoreData = true;
//        loadOrdersByStatus(status);
//    }
//
//    private void loadOrders() {
//        isLoading = true;
//        ordersLiveData.setValue(Resource.loading());
//        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<List<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<List<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        List<Order> orders = apiResponse.getData();
//                        Log.d("OrderRepository", "loadOrders: Orders received: " + (orders != null ? orders.size() : "null"));
//                        if (orders != null) {
//                            if (orders.isEmpty()) {
//                                hasMoreData = false;
//                            }
//                            ordersLiveData.setValue(Resource.success(orders));
//                        } else {
//                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
//                        }
//                    } else {
//                        Log.e("OrderRepository", "loadOrders: API error: " + apiResponse.getMessage());
//                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    Log.e("OrderRepository", "loadOrders: Response failed: " + response.message());
//                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
//                isLoading = false;
//                Log.e("OrderRepository", "loadOrders: Failure: " + t.getMessage());
//                ordersLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    private void loadOrdersByStatus(int status) {
//        isLoading = true;
//        ordersLiveData.setValue(Resource.loading());
//        orderApi.getMyOrders(status, currentPage, pageSize).enqueue(new Callback<ApiResponse<List<Order>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<List<Order>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        List<Order> orders = apiResponse.getData();
//                        Log.d("OrderRepository", "loadOrdersByStatus: Orders received: " + (orders != null ? orders.size() : "null"));
//                        if (orders != null) {
//                            if (orders.isEmpty()) {
//                                hasMoreData = false;
//                            }
//                            ordersLiveData.setValue(Resource.success(orders));
//                        } else {
//                            ordersLiveData.setValue(Resource.error("Không có dữ liệu"));
//                        }
//                    } else {
//                        Log.e("OrderRepository", "loadOrdersByStatus: API error: " + apiResponse.getMessage());
//                        ordersLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    Log.e("OrderRepository", "loadOrdersByStatus: Response failed: " + response.message());
//                    ordersLiveData.setValue(Resource.error("Lỗi khi tải dữ liệu"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
//                isLoading = false;
//                Log.e("OrderRepository", "loadOrdersByStatus: Failure: " + t.getMessage());
//                ordersLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    private void loadOrderInformationById(int orderId) {
//        isLoading = true;
//        orderInfo.setValue(Resource.loading());
//        orderApi.getOrderInfo(orderId).enqueue(new Callback<ApiResponse<Order>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<Order> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        Order order = apiResponse.getData();
//                        Log.d("OrderRepository", "loadOrderInformationById: Order received: " + (order != null ? order.toString() : "null"));
//                        if (order != null) {
//                            orderInfo.setValue(Resource.success(order));
//                        } else {
//                            orderInfo.setValue(Resource.error("Không tìm thấy thông tin đơn hàng"));
//                        }
//                    } else {
//                        Log.e("OrderRepository", "loadOrderInformationById: API error: " + apiResponse.getMessage());
//                        orderInfo.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    Log.e("OrderRepository", "loadOrderInformationById: Response failed: " + response.message());
//                    orderInfo.setValue(Resource.error("Lỗi khi tải thông tin đơn hàng"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
//                isLoading = false;
//                Log.e("OrderRepository", "loadOrderInformationById: Failure: " + t.getMessage());
//                orderInfo.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    private void loadProductsInOrderDetailById(int orderId) {
//        isLoading = true;
//        productLiveData.setValue(Resource.loading());
//        orderApi.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<List<ProductOrder>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<List<ProductOrder>>> call, Response<ApiResponse<List<ProductOrder>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<List<ProductOrder>> apiResponse = response.body();
//                    if (apiResponse.getStatusCode() == 200) {
//                        List<ProductOrder> products = apiResponse.getData();
//                        Log.d("OrderRepository", "loadProductsInOrderDetailById: Products received: " + (products != null ? products.size() : "null"));
//                        productLiveData.setValue(Resource.success(products));
//                    } else {
//                        Log.e("OrderRepository", "loadProductsInOrderDetailById: API error: " + apiResponse.getMessage());
//                        productLiveData.setValue(Resource.error(apiResponse.getMessage()));
//                    }
//                } else {
//                    Log.e("OrderRepository", "loadProductsInOrderDetailById: Response failed: " + response.message());
//                    productLiveData.setValue(Resource.error("Lỗi khi tải chi tiết đơn hàng"));
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<List<ProductOrder>>> call, Throwable t) {
//                isLoading = false;
//                Log.e("OrderRepository", "loadProductsInOrderDetailById: Failure: " + t.getMessage());
//                productLiveData.setValue(Resource.error(t.getMessage()));
//            }
//        });
//    }
//
//    public LiveData<Resource<Order>> getOrderById(int orderId) {
//        loadOrderInformationById(orderId);
//        return orderInfo;
//    }
//
//    public LiveData<Resource<List<ProductOrder>>> getProductLiveData(int orderId) {
//        loadProductsInOrderDetailById(orderId);
//        return productLiveData;
//    }
//}