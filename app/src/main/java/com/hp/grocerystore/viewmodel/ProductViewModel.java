package com.hp.grocerystore.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.model.feedback.Feedback;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.repository.ProductRepository;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private ProductRepository repository;

    public ProductViewModel() {
        repository = new ProductRepository();
    }

    public LiveData<Resource<Product>> getProduct(long productId) {
        return repository.getProductById(productId);
    }

    public LiveData<Resource<List<Feedback>>> getFeedback(long productId) {
        return repository.getFeedbacksByProductId(productId);
    }
}