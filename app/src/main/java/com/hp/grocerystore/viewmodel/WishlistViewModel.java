package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.repository.WishlistRepository;
import com.hp.grocerystore.utils.PagedResult;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

public class WishlistViewModel extends ViewModel {
    private final WishlistRepository wishlistRepository;

    public WishlistViewModel(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public LiveData<ApiResponse<Void>> getAddWishlistResult() {
        return wishlistRepository.getAddWishlistResult();
    }

    public LiveData<Resource<List<Wishlist>>> getWishlistLiveData() {
        return wishlistRepository.getWishlistLiveData();
    }

    public void addWishlist(long productId) {
        wishlistRepository.addWishlist(productId);
    }

    public void fetchWishlist(int page, int size) {
        wishlistRepository.getWishlist(page, size);  // Gọi method void, dữ liệu trả về qua LiveData trong repository
    }
}
