package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.repository.ProductRepository;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private final ProductRepository productRepository;
    public SearchViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public LiveData<Resource<List<Product>>> searchProducts(int page, int size, String query) {
        return productRepository.searchProducts(page, size, query);
    }

    public LiveData<Resource<List<Product>>> searchAndFilterProducts(int page, int size,
                                                                     String filter1, String filter2,
                                                                     String filter3, String filter4,String sort) {
        return productRepository.searchAndFilterProducts(page, size, filter1, filter2, filter3, filter4,sort);
    }
}