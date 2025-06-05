package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.WishlistRepository;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

public class WishlistViewModel extends ViewModel {
    private final WishlistRepository wishlistRepository;

    public WishlistViewModel() {
        this.wishlistRepository = WishlistRepository.getInstance(RetrofitClient.getWishlistApi());
    }

    public LiveData<Resource<Void>> getAddWishlistResult(long productId) {
        return wishlistRepository.addWishlist(productId);
    }

    public LiveData<Resource<List<Wishlist>>> getWishlistLiveData(int page, int size) {
        return wishlistRepository.getWishlist(page, size);
    }

    public LiveData<Resource<Void>> addWishlist(long productId) {
        return wishlistRepository.addWishlist(productId);
    }
    public LiveData<Resource<Void>> deleteWishlist(Long id) {
        return wishlistRepository.deleteWishlist(id);
    }
}
