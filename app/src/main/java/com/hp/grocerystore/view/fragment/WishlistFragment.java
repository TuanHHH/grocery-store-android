package com.hp.grocerystore.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hp.grocerystore.R;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.WishlistRepository;
import com.hp.grocerystore.view.adapter.WishlistAdapter;
import com.hp.grocerystore.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private WishlistViewModel mViewModel;
    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private List<Wishlist> wishlist = new ArrayList<>();

    public static WishlistFragment newInstance() {
        return new WishlistFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ RecyclerView
        recyclerView = view.findViewById(R.id.recycler_wishlist);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new WishlistAdapter(getContext(), wishlist);
        recyclerView.setAdapter(adapter);

        // Khởi tạo ViewModel
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                WishlistRepository wishlistRepo = new WishlistRepository(
                        RetrofitClient.getWishlistApi(GRCApplication.getAppContext())
                );
                return (T) new WishlistViewModel(wishlistRepo);
            }
        }).get(WishlistViewModel.class);


        // Load dữ liệu wishlist từ view model
        loadWishlist(1,10);

    }
    private void loadWishlist(int page, int size){
        // Khởi tạo observer 1 lần (ví dụ trong onViewCreated)
        mViewModel.getWishlistLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        adapter.updateData(resource.data);
                    } else {
                        Toast.makeText(getContext(), "Danh sách yêu thích trống!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;

                case LOADING:
                    // Hiển thị tiến trình nếu cần
                    break;
            }
        });

        // Khi cần load dữ liệu (ví dụ ở onViewCreated hoặc khi refresh)
        mViewModel.fetchWishlist(page, size);
    }
}