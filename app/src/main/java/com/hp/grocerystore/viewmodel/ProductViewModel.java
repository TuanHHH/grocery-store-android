package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.feedback.CreateFeedbackRequest;
import com.hp.grocerystore.model.feedback.Feedback;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.network.api.FeedbackApi;
import com.hp.grocerystore.network.api.ProductApi;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.ProductRepository;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private final ProductRepository repository;

    public ProductViewModel() {
        ProductApi productApi = RetrofitClient.getProductApi();
        FeedbackApi feedbackApi = RetrofitClient.getFeedbackApi();
        this.repository = new ProductRepository(productApi, feedbackApi);
    }

    public LiveData<Resource<Product>> getProduct(long productId) {
        return repository.getProduct(productId);
    }

    public LiveData<Resource<List<Feedback>>> getFeedback(long productId) {
        return repository.getFeedback(productId);
    }

    public LiveData<Resource<List<Product>>> getProducts() {
        return repository.getProducts();
    }

    public void loadMoreProducts() {
        repository.loadMoreProducts();
    }

    public void refreshProducts() {
        repository.refreshProducts();
    }

    public LiveData<Resource<Feedback>> addFeedBack(long productId, int rating, String description){
        return repository.addFeedback(new CreateFeedbackRequest(productId, rating, description));
    }
}