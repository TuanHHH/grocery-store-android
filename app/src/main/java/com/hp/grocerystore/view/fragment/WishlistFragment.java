package com.hp.grocerystore.view.fragment;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.grocerystore.R;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.WishlistRepository;
import com.hp.grocerystore.view.adapter.WishlistAdapter;
import com.hp.grocerystore.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WishlistFragment extends Fragment {

    private WishlistViewModel mViewModel;
    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private ProgressBar progressBar,progressBarLoadmoreView;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView btnViewMore;
    private List<Wishlist> wishlist = new ArrayList<>();
    private int currentPage = 1;
    private int countLoad = 0;


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
        progressBar = view.findViewById(R.id.progress_bar_wishlist_view);
        progressBarLoadmoreView = view.findViewById(R.id.progress_bar_loadmore_view);
        btnViewMore = view.findViewById(R.id.btn_view_more_wishlist);
        nestedScrollView = view.findViewById(R.id.wislist_container);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_wishlist);


        adapter.setOnWishlistItemClickListener(position -> {
            deleteWishlistItem(position); // ← gọi hàm bạn đã viết
        });


        // Khởi tạo ViewModel
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new WishlistViewModel();
            }
        }).get(WishlistViewModel.class);


        // Load dữ liệu wishlist từ view model
        loadWishlist(1,10);
        // Thiết lập lắng nghe pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            countLoad = 0;
            loadWishlist(currentPage, 10);
        });

        btnViewMore.setOnClickListener(v -> {
            currentPage++;
            loadWishlist(currentPage,10);
        });


        // Biến cờ kiểm soát click
        AtomicBoolean isCooldown = new AtomicBoolean(false);
        // Thời gian delay giữa các lần click (ms)
        int delayMillis = 3000; // 3 giây


        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            Rect scrollBounds = new Rect();
            nestedScrollView.getHitRect(scrollBounds);

            if (btnViewMore.getLocalVisibleRect(scrollBounds)) {
                if (btnViewMore.getVisibility() == View.VISIBLE && !isCooldown.get() && countLoad > 1) {
                    btnViewMore.postDelayed(() -> {
                        btnViewMore.performClick();
                    }, 1000);
                    isCooldown.set(true);

                    // Đặt lại sau khoảng thời gian delay
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        isCooldown.set(false);
                    }, delayMillis);
                }else {
                    countLoad++;
                    // Đặt lại sau khoảng thời gian delay
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        isCooldown.set(false);
                    }, delayMillis);
                }
            }
        });


    }
    private void loadWishlist(int page, int size){
        // Khởi tạo observer 1 lần (ví dụ trong onViewCreated)
        mViewModel.getWishlistLiveData(page,size).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    if (page == 1 && !swipeRefreshLayout.isRefreshing()) {
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else if (page > 1) {
                        progressBarLoadmoreView.setVisibility(View.VISIBLE);
                    }
                    break;
                case SUCCESS:
                    swipeRefreshLayout.setRefreshing(false); // Dừng refresh
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBarLoadmoreView.setVisibility(View.GONE);
                    if (resource.data != null && !resource.data.isEmpty()) {

                        if (page == 1) {
                            wishlist = new ArrayList<>(resource.data);
                        } else {
                            // Load thêm -> nối thêm vào danh sách cũ
                            wishlist.addAll(resource.data);
                        }
                        adapter.updateData(wishlist);
                    }

                    if (resource.data == null || resource.data.size() < size) {
                        btnViewMore.setVisibility(View.GONE);
                    } else {
                        btnViewMore.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    swipeRefreshLayout.setRefreshing(false); // Dừng refresh
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBarLoadmoreView.setVisibility(View.GONE);
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

}