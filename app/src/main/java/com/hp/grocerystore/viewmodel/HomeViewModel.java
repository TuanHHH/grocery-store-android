package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.model.category.Category;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.CategoryRepository;
import com.hp.grocerystore.repository.ProductRepository;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public HomeViewModel() {
        this.productRepository = ProductRepository.getInstance(
                RetrofitClient.getProductApi(),
                RetrofitClient.getFeedbackApi()
        );;
        this.categoryRepository = CategoryRepository.getInstance(RetrofitClient.getCategoryApi());
    }

    public LiveData<Resource<List<Product>>> getProducts(int page, int size, String filter) {
        return productRepository.filterProducts(page, size, filter);
    }
    public LiveData<Resource<List<Category>>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }
}