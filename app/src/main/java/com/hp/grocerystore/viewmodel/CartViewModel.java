package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.cart.AddToCartRequest;
import com.hp.grocerystore.model.order.CheckoutRequest;
import com.hp.grocerystore.network.api.CartApi;
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.CartRepository;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

public class CartViewModel extends ViewModel {
    private final CartRepository repository;

    public CartViewModel() {
        CartApi cartApi = RetrofitClient.getCartApi();
        this.repository = CartRepository.getInstance(cartApi);
    }

    public LiveData<Resource<List<CartItem>>> getCartItems() {
        return repository.getCartItems();
    }

    public LiveData<Resource<Boolean>> getSelectAllState() {
        return repository.getSelectAllState();
    }

    public LiveData<Resource<Integer>> getTotalItems() {
        return repository.getTotalItems();
    }

    public void selectAll(boolean isSelected) {
        repository.selectAll(isSelected);
    }

    public void loadMoreItems() {
        repository.loadMoreCartItems();
    }

    public void refresh() {
        repository.refreshCartItems();
    }

    public void removeCartItem(long productId) {
        repository.removeCartItem(productId);
    }

    public LiveData<Resource<Void>> addOrUpdateCart(long productId, int quantity){
        return repository.addOrUpdateCart(new AddToCartRequest(productId, quantity));
    }

    // LiveData để quan sát kết quả checkout từ Activity
//    public LiveData<Resource<Void>> getCheckoutResult() {
//        return checkoutResult;
//    }
//
//    public void checkout(CheckoutRequest request) {
//        // Gọi hàm checkout từ repository và post giá trị vào LiveData
//        repository.checkoutOrder(request).observeForever(checkoutResult::setValue);
//    }

    public LiveData<Resource<Void>> getCheckoutResult() {
        return repository.getCheckoutResult();
    }

    public void checkout(CheckoutRequest request) {
        repository.checkoutOrder(request);
    }
//    public LiveData<Resource<String>> createVnPayPayment(int amount, String bankCode) {
//        return repository.createVnPayPayment(amount, bankCode);
//    }
//    public LiveData<Resource<String>> getVnPayBaseUrl() {
//        return repository.getVnPayBaseUrl();
//    }


}
