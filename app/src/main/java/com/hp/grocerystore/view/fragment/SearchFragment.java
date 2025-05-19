package com.hp.grocerystore.view.fragment;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
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
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.grocerystore.R;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.category.Category;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.CategoryRepository;
import com.hp.grocerystore.repository.ProductRepository;
import com.hp.grocerystore.repository.WishlistRepository;
import com.hp.grocerystore.view.activity.FilterActivity;
import com.hp.grocerystore.view.adapter.CategoryAdapter;
import com.hp.grocerystore.view.adapter.ProductAdapter;
import com.hp.grocerystore.viewmodel.HomeViewModel;
import com.hp.grocerystore.viewmodel.SearchViewModel;
import com.hp.grocerystore.viewmodel.SharedViewModel;
import com.hp.grocerystore.viewmodel.WishlistViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchFragment extends Fragment {
    // View Model
    private HomeViewModel homeViewModel;

    private SearchViewModel mViewModel;
    private WishlistViewModel wishlistViewModel;
    private SharedViewModel sharedViewModel;
    // List Data
    private List<Product> productList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    //Adapter
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    // Components
    private RecyclerView recyclerView;
    private LinearLayout linearCategoryContainer, btnFilter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView nestedScrollView;
    private TextView selectedSortView = null;
    private TextView btnViewMore;
    private TextView[] filters;
    private ProgressBar progressBarSearchView, progressBarLoadmoreView;
    // Filter Args
    private long selectedCategoryId = -1; // Không lọc
    private String selectedCategorySlug = "";
    private String selectedSort = "";    // Không sắp xếp
    private int minPrice = 0;
    private int maxPrice = 500000;
    private float minRating = 0;
    private float maxRating = 5;
    private String searchText = "";
    // load more product
    private boolean isLoading, isLastPage, isLoadMore;
    private int currentPage = 1;
    private int countLoad = 0;
    private int[] lastScrollY = {0};

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.search_result_products);
        linearCategoryContainer = view.findViewById(R.id.category_container);
        progressBarSearchView = view.findViewById(R.id.progress_bar_search_view);
        progressBarLoadmoreView = view.findViewById(R.id.progress_bar_loadmore_view);
        btnFilter = view.findViewById(R.id.btn_filter);
        btnViewMore = view.findViewById(R.id.btn_view_more_product);
        nestedScrollView = view.findViewById(R.id.search_result_container); // thêm ID nếu cần
        swipeRefreshLayout = view.findViewById(R.id.search_swipe_refresh_layout);
        // Danh sách nút sắp xếp sản phẩm
        filters = new TextView[]{
                view.findViewById(R.id.filter_best_seller),
                view.findViewById(R.id.filter_name),
                view.findViewById(R.id.filter_price_low),
                view.findViewById(R.id.filter_price_high),
                view.findViewById(R.id.filter_rating_low),
                view.findViewById(R.id.filter_rating_high)
        };

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                ProductRepository productRepo = new ProductRepository(RetrofitClient.getProductApi(GRCApplication.getAppContext())); // Đảm bảo constructor đúng
                return (T) new SearchViewModel(productRepo);
            }
        }).get(SearchViewModel.class);

        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                ProductRepository productRepo = new ProductRepository(RetrofitClient.getProductApi(GRCApplication.getAppContext())); // Đảm bảo constructor đúng
                CategoryRepository categoryRepo = new CategoryRepository(RetrofitClient.getCategoryApi(GRCApplication.getAppContext()));
                return (T) new HomeViewModel(productRepo, categoryRepo);
            }
        }).get(HomeViewModel.class);

        wishlistViewModel =  new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                WishlistRepository wishlistRepo = new WishlistRepository(RetrofitClient.getWishlistApi(GRCApplication.getAppContext()));  // Đảm bảo constructor đúng
                return (T) new WishlistViewModel(wishlistRepo);
            }
        }).get(WishlistViewModel.class);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        productAdapter = new ProductAdapter(getContext(), productList,wishlistViewModel,getViewLifecycleOwner());


        // Nếu chưa có giá trị được share thì load mặc định
        if (sharedViewModel.getFilterData().getValue() == null) {
            searchProducts(1, 10, "");
        }

        sharedViewModel.getFilterData().observe(getViewLifecycleOwner(), filterData  -> {
            if (filterData != null) {
                selectedCategoryId = filterData.getSelectedCategoryId();
                selectedCategorySlug = filterData.getSelectedCategorySlug();
                selectedSort = filterData.getSelectedSort() == null ? "": filterData.getSelectedSort();
                minPrice = filterData.getMinPrice();
                maxPrice = filterData.getMaxPrice() == 0 ? 500000 : filterData.getMaxPrice();
                minRating = filterData.getMinRating();
                maxRating = filterData.getMaxRating() == 0 ? 5 : filterData.getMaxRating();
                searchText = filterData.getSearchText() == null ? "": filterData.getSearchText();
                String sortQuery = getSortQuery(selectedSort);
                isLoadMore = false;
                lastScrollY[0] = 0;
                currentPage = 1;
                // Cập nhật lại giao diện sort
                for (TextView filter: filters){
                    if (filter.getText().equals(selectedSort)){
                        selectedSortView = filter;
                        filter.setSelected(true);
                        filter.setTextColor(ContextCompat.getColor(getContext(), R.color.color_variation));
                        selectedSort = filter.getText().toString();
                    }
                }

                if(selectedCategorySlug != null && selectedCategorySlug.contains("category.slug")
                        && selectedSort.isEmpty() && minPrice == 0 && maxPrice == 500000
                        && minRating == 0 && maxRating == 5.0 && searchText.isEmpty()){
                    // Cập nhật lại giao diện category list
                    categoryAdapter.setupCategorySelection(linearCategoryContainer, selectedCategoryId, this::onCategoryClick);
                    addSortOptions();
                    loadProducts(currentPage, 10, selectedCategorySlug, true);
                }else if(selectedCategorySlug != null && selectedCategorySlug.contains("productName")
                        && selectedSort.isEmpty() && minPrice == 0 && maxPrice == 500000
                        && minRating == 0 && maxRating == 5 && searchText.isEmpty()){
                    categoryAdapter.setupCategorySelection(linearCategoryContainer, selectedCategoryId, this::onCategoryClick);
                    addSortOptions();
                    searchText = selectedCategorySlug;
                    searchProducts(currentPage, 10, selectedCategorySlug);
                }else{
                    if (searchText.contains("productName~")) {
                        searchAndFilterProducts(currentPage, 10, "category.slug~'" + selectedCategorySlug + "'",
                                searchText, "price > " + minPrice, "price < " + maxPrice,
                                "rating > "+minRating, "rating < "+maxRating, sortQuery);
                    }
                    else if(selectedCategorySlug.contains("category.slug"))
                    {
                        searchAndFilterProducts(currentPage,10, selectedCategorySlug,
                                "productName~'"+searchText+"'", "price > "+minPrice, "price < "+maxPrice,
                                "rating > "+minRating, "rating < "+maxRating, sortQuery);
                    }else{
                        searchAndFilterProducts(1,10, "category.slug~'"+selectedCategorySlug+"'",
                                "productName~'"+searchText+"'", "price > "+minPrice, "price < "+maxPrice,
                                "rating > "+minRating, "rating < "+maxRating, sortQuery);
                    }
                }
            }
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(productAdapter);

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        loadCategories();
        addSortOptions();
        btnViewMore.setOnClickListener(v -> {
            currentPage++;
            setSortAndFilterProduct(currentPage);
        });

        swipeRefreshLayout.setOnRefreshListener(this::refreshSearchData);


        // Biến cờ kiểm soát click
        AtomicBoolean isCooldown = new AtomicBoolean(false);
        // Thời gian delay giữa các lần click (ms)
        int delayMillis = 2000; // 3 giây




        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int currentScrollY = nestedScrollView.getScrollY();

            // Chỉ xử lý nếu scrollY thay đổi, tức là scroll theo chiều dọc
            if (currentScrollY != lastScrollY[0]) {
                lastScrollY[0] = currentScrollY;

                Rect scrollBounds = new Rect();
                nestedScrollView.getHitRect(scrollBounds);

                if (btnViewMore.getLocalVisibleRect(scrollBounds)) {
                    if (btnViewMore.getVisibility() == View.VISIBLE && !isCooldown.get() && countLoad > 1) {
                        btnViewMore.postDelayed(() -> btnViewMore.performClick(), 1000);
                        isCooldown.set(true);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            isCooldown.set(false);
                        }, delayMillis);
                    } else {
                        countLoad++;
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            isCooldown.set(false);
                        }, delayMillis);
                    }
                }
            }
        });

        //Gửi dữ liệu cho filter activity
        btnFilter.setOnClickListener(v -> {
            Intent filterIntent = new Intent(getActivity(), FilterActivity.class);
            filterIntent.putExtra("selected_categoryId", (Serializable) selectedCategoryId);
            filterIntent.putExtra("selected_sort", (Serializable) selectedSort);
            filterIntent.putExtra("min_price", (Serializable) minPrice);
            filterIntent.putExtra("max_price", (Serializable) maxPrice);
            filterIntent.putExtra("min_rating", (Serializable) minRating);
            filterIntent.putExtra("max_rating", (Serializable) maxRating);
            filterIntent.putExtra("search_text", (Serializable) searchText);
            startActivity(filterIntent);
        });
    }


    private void loadProducts(int page, int size, String filter, boolean hasFilterCategory) {
        homeViewModel.getProducts(page, size, filter).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    if(page==1 && !swipeRefreshLayout.isRefreshing()){
                        progressBarSearchView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else if(page > 1){
                        progressBarLoadmoreView.setVisibility(View.VISIBLE);
                    }
                    break;

                case SUCCESS:
                    swipeRefreshLayout.setRefreshing(false); // Dừng refresh
                    progressBarSearchView.setVisibility(View.GONE);
                    progressBarLoadmoreView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    if (resource.data != null && !resource.data.isEmpty()) {

                        if (page == 1) {
                            productList = new ArrayList<>(resource.data);
                        } else {
                            // Load thêm -> nối thêm vào danh sách cũ
                            productList.addAll(resource.data);
                        }
                        productAdapter.setProductList(productList);
                    }

                    if (resource.data == null || resource.data.size() < size) {
                        btnViewMore.setVisibility(View.GONE);
                    } else {
                        btnViewMore.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    swipeRefreshLayout.setRefreshing(false); // Dừng refresh
                    progressBarSearchView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
    private void searchProducts(int page, int size, String filter) {
        mViewModel.searchProducts(page, size, filter).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    if(page==1 && !swipeRefreshLayout.isRefreshing()){
                        progressBarSearchView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else{
                        progressBarLoadmoreView.setVisibility(View.VISIBLE);
                    }
                    break;

                case SUCCESS:
                    swipeRefreshLayout.setRefreshing(false); // Dừng refresh
                    progressBarSearchView.setVisibility(View.GONE);
                    progressBarLoadmoreView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    //  productList = resource.data;
//                    if (originalProductList.size() == 0) {
//                        originalProductList = new ArrayList<>(productList);
//                    }
//                    if (productList != null) {
//                        productAdapter.setProductList(productList); // cập nhật danh sách sản phẩm
//                    }
//                    if (resource.data != null && !resource.data.isEmpty()) {
//                        productList = resource.data;
//                        productAdapter.setProductList(productList);
//                    }

                    if (resource.data != null && !resource.data.isEmpty()) {
                        if (page == 1) {
                            productList = new ArrayList<>(resource.data);
                        } else {
                            // Load thêm -> nối thêm vào danh sách cũ
                            productList.addAll(resource.data);
                        }
                        productAdapter.setProductList(productList);
                    }

                    if (resource.data == null || resource.data.size() < size) {
                        btnViewMore.setVisibility(View.GONE);
                    } else {
                        btnViewMore.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    swipeRefreshLayout.setRefreshing(false); // Dừng refresh
                    progressBarSearchView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
    private void searchAndFilterProducts(int page, int size, String filter1,
                                         String filter2, String filter3, String filter4,
                                         String filter5,String filter6,String sort) {
        mViewModel.searchAndFilterProducts(page, size, filter1, filter2, filter3, filter4, filter5, filter6, sort)
                .observe(getViewLifecycleOwner(), resource -> {
                    switch (resource.status) {
                        case LOADING:
                            if(page==1 && !swipeRefreshLayout.isRefreshing()){
                                progressBarSearchView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else{
                                progressBarLoadmoreView.setVisibility(View.VISIBLE);
                            }
                            break;

                        case SUCCESS:
                            swipeRefreshLayout.setRefreshing(false); // Dừng refresh
                            progressBarSearchView.setVisibility(View.GONE);
                            progressBarLoadmoreView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            if (resource.data != null && !resource.data.isEmpty()) {
//                                 Nếu đang load trang đầu -> khởi tạo
                                if (page == 1) {
                                    productList = new ArrayList<>(resource.data);
                                } else {
                                    // Load thêm -> nối thêm vào danh sách cũ
                                    productList.addAll(resource.data);
                                }
//                                productList = resource.data;
                                productAdapter.setProductList(productList);
                            }

//                            // Ẩn nút nếu số sản phẩm trả về < size mỗi trang (không còn để load)
                            if (resource.data == null || resource.data.size() < size) {
                                btnViewMore.setVisibility(View.GONE);
                            } else {
                                btnViewMore.setVisibility(View.VISIBLE);
                            }
                            break;

                        case ERROR:
                            swipeRefreshLayout.setRefreshing(false); // Dừng refresh
                            progressBarSearchView.setVisibility(View.GONE);
                            progressBarLoadmoreView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
    }
    private void loadCategories() {
        homeViewModel.getAllCategories().observe(getViewLifecycleOwner(), resource -> {

            switch (resource.status){
                case LOADING:
                    progressBarSearchView.setVisibility(View.VISIBLE);
                    linearCategoryContainer.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    progressBarSearchView.setVisibility(View.GONE);
                    linearCategoryContainer.setVisibility(View.VISIBLE);

                    categoryList = resource.data;
                    categoryAdapter.setCategoryList(categoryList);
                    categoryAdapter.populateHorizontalLinearLayout(linearCategoryContainer);
                    categoryAdapter.setupCategorySelection(linearCategoryContainer, selectedCategoryId, this::onCategoryClick);

                    // Đợi layout hoàn tất để đo setting chiều cao cho item trong linearlayout
                    linearCategoryContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            linearCategoryContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            int maxHeight = 0;

                            // Bước 1: Tìm chiều cao lớn nhất
                            for (int i = 0; i < linearCategoryContainer.getChildCount(); i++) {
                                View view = linearCategoryContainer.getChildAt(i);
                                int height = view.getHeight();
                                if (height > maxHeight) {
                                    maxHeight = height;
                                }
                            }

                            // Bước 2: Gán chiều cao lớn nhất cho tất cả các item
                            for (int i = 0; i < linearCategoryContainer.getChildCount(); i++) {
                                View view = linearCategoryContainer.getChildAt(i);
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                                params.height = maxHeight;
                                view.setLayoutParams(params);
                            }
                        }
                    });
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void onCategoryClick(Category category) {
        selectedCategorySlug = "";
        if (selectedCategoryId == category.getId()) {
            // Nếu click lại category đang chọn thì bỏ lọc
            currentPage = 1;
            lastScrollY[0] = 0;
            selectedCategoryId = -1;
            selectedCategorySlug = "";
            categoryAdapter.setSelectedCategoryId(-1);
        } else {
            // Cập nhật category mới được chọn
            currentPage = 1;
            lastScrollY[0] = 0;
            selectedCategoryId = category.getId();
            selectedCategorySlug = category.getSlug();
            categoryAdapter.setSelectedCategoryId(selectedCategoryId);
        }

        setSortAndFilterProduct(currentPage);
        // Cập nhật giao diện selection
        categoryAdapter.setupCategorySelection(
                (LinearLayout) getView().findViewById(R.id.category_container),
                selectedCategoryId,
                this::onCategoryClick // Gán lại listener
        );
    }

    private void addSortOptions(){
        // Xử lý sắp xếp sản phẩm
        for (TextView filter : filters) {
            if (!selectedSort.equals("")){
                boolean isSelected = filter.getText().equals(selectedSort);
                if(isSelected){
                    selectedSortView = filter;
                    filter.setSelected(true);
                    filter.setTextColor(ContextCompat.getColor(getContext(), R.color.color_variation));
                }
            }else{
                if (selectedSortView!=null){
                    selectedSortView.setSelected(false);
                    selectedSortView.setTextColor(Color.BLACK);
                    selectedSortView = null;
                }
            }
            filter.setOnClickListener(v -> {
                TextView clickedFilter = (TextView) v;
                isLoadMore = false;
                // Nếu click lại chính filter đang chọn => hủy sắp xếp
                if (selectedSortView == clickedFilter) {
                    selectedSortView.setSelected(false);
                    selectedSortView.setTextColor(Color.BLACK);
                    selectedSortView = null;
                    selectedSort = "";
                } else {
                    // Reset view trước đó
                    if (selectedSortView != null) {
                        selectedSortView.setSelected(false);
                        selectedSortView.setTextColor(Color.BLACK);
                    }

                    // Chọn mới
                    selectedSortView = clickedFilter;
                    clickedFilter.setSelected(true);
                    clickedFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.color_variation));
                    selectedSort = clickedFilter.getText().toString();
                }

                setSortAndFilterProduct(currentPage);
            });
        }
    }

    private String getSortQuery(String selectedSort){
        String sortQuery = "";
        switch (selectedSort) {
            case "Bán chạy":
                sortQuery = "sold,desc";
                break;
            case "Tên sản phẩm":
                sortQuery = "productName,asc";
                break;
            case "Giá thấp đến cao":
                sortQuery = "price,asc";
                break;
            case "Giá cao đến thấp":
                sortQuery = "price,desc";
                break;
            case "Rating cao đến thấp":
                sortQuery = "rating,desc";
                break;
            case "Rating thấp đến cao":
                sortQuery = "rating,asc";
                break;
            default:
                sortQuery = "";
                break;
        }
        return sortQuery;
    }
    private void setSortAndFilterProduct(int page){
        // Lọc lại danh sách sản phẩm
        if(selectedCategorySlug != null && selectedCategorySlug.contains("category.slug")
                && selectedSort.isEmpty() &&  searchText.isEmpty()){
            loadProducts(page, 10, selectedCategorySlug, true);
        }else if(selectedCategorySlug != null && selectedCategorySlug.contains("productName")
                && selectedSort.isEmpty()){
            searchProducts(currentPage, 10, searchText);
        }else{
            if (searchText.contains("productName~")){
//                currentPage = 1;
                searchAndFilterProducts(currentPage, 10, "category.slug~'" + selectedCategorySlug + "'",
                        searchText, "price > " + minPrice, "price < " + maxPrice,
                        "rating > "+minRating, "rating < "+maxRating, getSortQuery(selectedSort));
            }
            else if(selectedCategorySlug.contains("category.slug"))
            {
//                currentPage = 1;
                searchAndFilterProducts(currentPage,10, selectedCategorySlug,
                        "productName~'"+searchText+"'", "price > "+minPrice, "price < "+maxPrice,
                        "rating > "+minRating, "rating < "+maxRating, getSortQuery(selectedSort));
            }
            else if(selectedCategorySlug.contains("productName") && searchText.contains("productName~")){
//                currentPage = 1;
                searchAndFilterProducts(currentPage,10, selectedCategorySlug,
                        searchText, "price > "+minPrice, "price < "+maxPrice,
                        "rating > "+minRating, "rating < "+maxRating, getSortQuery(selectedSort));
            }
            else
            {
//                currentPage = 1;
                searchAndFilterProducts(currentPage,10, "category.slug~'"+selectedCategorySlug+"'",
                        "productName~'"+searchText+"'", "price > "+minPrice, "price < "+maxPrice,
                        "rating > "+minRating, "rating < "+maxRating, getSortQuery(selectedSort));
            }
        }

    }

    private void refreshSearchData() {
        // Nếu đang load more, thì không refresh lại dữ liệu
        if (isLoadMore) return;

        // Cuộn lên đầu
        nestedScrollView.scrollTo(0, 0);
        currentPage = 1;  // Reset lại trang
        isLastPage = false;
        isLoading = false;
        countLoad = 0;
        lastScrollY[0] = 0;
        progressBarSearchView.setVisibility(View.VISIBLE);  // Show progress bar chính

        setSortAndFilterProduct(currentPage);
    }

}