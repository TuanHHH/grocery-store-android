package com.hp.grocerystore.view.fragment;

import static com.hp.grocerystore.utils.Resource.Status.LOADING;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.grocerystore.R;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.view.activity.LoginActivity;
import com.hp.grocerystore.view.activity.ProductDetailActivity;
import com.hp.grocerystore.view.adapter.WishlistAdapter;
import com.hp.grocerystore.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WishlistFragment extends Fragment {

    private WishlistViewModel mViewModel;
    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private ProgressBar progressBarLoadMoreView;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView btnViewMore;
    private List<Wishlist> wishlist = new ArrayList<>();
    private int currentPage = 1;
    private int countLoad = 0;

    private LinearLayout loginRequiredLayout;
    private Button loginRequiredButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_wishlist);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new WishlistAdapter(getContext(), wishlist);
        recyclerView.setAdapter(adapter);
        progressBarLoadMoreView = view.findViewById(R.id.progress_bar_loadmore_view);
        btnViewMore = view.findViewById(R.id.btn_view_more_wishlist);
        nestedScrollView = view.findViewById(R.id.wislist_container);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_wishlist);

        loginRequiredLayout  = view.findViewById(R.id.login_required);
        loginRequiredButton  = view.findViewById(R.id.button_login_required);
        
        loginRequiredButton.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        });
        adapter.setOnWishlistItemClickListener(new WishlistAdapter.OnWishlistItemClickListener() {
            @Override
            public void onRemoveClick(int position) {
                deleteWishlistItem(position);
            }

            @Override
            public void onItemClick(int position) {
                if (position < 0 || position >= wishlist.size()) return;
                Wishlist clicked = wishlist.get(position);
                Long productId = clicked.getId();
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", productId);
                startActivity(intent);
            }
        });

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new WishlistViewModel();
            }
        }).get(WishlistViewModel.class);

        loadWishlist(1);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            countLoad = 0;
            loadWishlist(currentPage);
        });

        btnViewMore.setOnClickListener(v -> {
            currentPage++;
            loadWishlist(currentPage);
        });

        AtomicBoolean isCooldown = new AtomicBoolean(false);
        int delayMillis = 3000;


        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            Rect scrollBounds = new Rect();
            nestedScrollView.getHitRect(scrollBounds);

            if (btnViewMore.getLocalVisibleRect(scrollBounds)) {
                if (btnViewMore.getVisibility() == View.VISIBLE && !isCooldown.get() && countLoad > 1) {
                    btnViewMore.postDelayed(() -> {
                        btnViewMore.performClick();
                    }, 1000);
                    isCooldown.set(true);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        isCooldown.set(false);
                    }, delayMillis);
                }else {
                    countLoad++;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        isCooldown.set(false);
                    }, delayMillis);
                }
            }
        });

        handleLoginState();
    }

    private void handleLoginState() {
        if (Extensions.isLoggedIn(requireContext())) {
            showWishlistUI();
            if (wishlist.isEmpty()) {
                currentPage = 1;
                loadWishlist(currentPage);
            }
        } else {
            showLoginRequiredUI();
        }
    }

    private void showLoginRequiredUI() {
        loginRequiredLayout.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(false);
    }

    private void showWishlistUI() {
        loginRequiredLayout.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(true);
    }

    private void loadWishlist(int page){
        if (!Extensions.isLoggedIn(requireContext())) return;
        mViewModel.getWishlistLiveData(page, 10).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    if (page == 1 && !swipeRefreshLayout.isRefreshing()) {
                        recyclerView.setVisibility(View.GONE);
                    } else if (page > 1) {
                        progressBarLoadMoreView.setVisibility(View.VISIBLE);
                    }
                    break;
                case SUCCESS:
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBarLoadMoreView.setVisibility(View.GONE);
                    if (resource.data != null && !resource.data.isEmpty()) {

                        if (page == 1) {
                            wishlist = new ArrayList<>(resource.data);
                        } else {
                            wishlist.addAll(resource.data);
                        }
                        adapter.updateData(wishlist);
                    }

                    if (resource.data == null || resource.data.size() < 10) {
                        btnViewMore.setVisibility(View.GONE);
                    } else {
                        btnViewMore.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBarLoadMoreView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

    }

    private void deleteWishlistItem(int position) {
        if (position < 0 || position >= wishlist.size()) return;

        Long wishlistId = wishlist.get(position).getId();

        mViewModel.deleteWishlist(wishlistId).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    break;

                case SUCCESS:
                    Toast.makeText(getContext(), "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    wishlist.remove(position);
                    adapter.updateData(wishlist);

                    if (wishlist.isEmpty()) {
                        btnViewMore.setVisibility(View.GONE);
                    }

                    break;

                case ERROR:
                    Toast.makeText(getContext(), "Xóa thất bại: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void loadWishlist(int page, int size, WishlistLoadedCallback callback) {
        mViewModel.getWishlistLiveData(page, size).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        ArrayList<Wishlist> wishLists = new ArrayList<>(resource.data);
                        adapter.updateData(wishLists);
                    } else {
                        Toast.makeText(getContext(), "Danh sách yêu thích trống!", Toast.LENGTH_SHORT).show();
                    }
                    if (callback != null) callback.onWishlistLoaded();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onWishlistLoaded();
                    break;
                case LOADING:
                    break;
            }
        });
    }

    public interface WishlistLoadedCallback {
        void onWishlistLoaded();
    }

}