package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.network.api.ProductApi;
import com.hp.grocerystore.repository.ProductRepository;
import com.hp.grocerystore.utils.Resource;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private final ProductRepository productRepository;
    public SearchViewModel() {
        this.productRepository = ProductRepository.getInstance(RetrofitClient.getProductApi(), RetrofitClient.getFeedbackApi());;
    }
    public LiveData<Resource<List<Product>>> searchProducts(int page, int size, String query) {
        return productRepository.filterProducts(page, size, query);
    }

    public LiveData<Resource<List<Product>>> searchAndFilterProducts(int page, int size, String filter1,
                                                                     String filter2, String filter3, String filter4,
                                                                     String filter5, String filter6, String sort) {
        return productRepository.searchAndFilterProducts(page, size, filter1, filter2, filter3, filter4, filter5, filter6,sort);
    }


}