package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.cart.AddToCartRequest;
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
}
