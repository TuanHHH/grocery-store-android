package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.model.product.ProductOrder;
import com.hp.grocerystore.network.api.OrderApi;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.OrderRepository;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

public class OrderViewModel extends ViewModel {
    private final OrderRepository repository;

    public OrderViewModel() {
        OrderApi orderApi = RetrofitClient.getOrderApi(GRCApplication.getAppContext());
        this.repository = new OrderRepository(orderApi);
    }

    public LiveData<Resource<List<Order>>> getOrdersByStatus(int status) {
        return repository.getOrdersByStatus(status);
    }

    public LiveData<Resource<Integer>> getTotalOrders() {
        return repository.getTotalOrders();
    }

    public LiveData<Resource<Order>> getOrderById(int orderId) {
        return repository.getOrderById(orderId);
    }

    public LiveData<Resource<List<ProductOrder>>> getProductLiveData(int orderId) {
        return repository.getProductLiveData(orderId);
    }

    public void loadMoreOrders() {
        repository.loadMoreOrders();
    }

    public void refresh() {
        repository.refreshOrders();
    }

    public void loadMoreOrdersByStatus(int status) {
        repository.loadMoreOrdersByStatus(status);
    }

    public void fetchOrdersByStatus(int status) {
        repository.fetchOrdersByStatus(status);
    }
}