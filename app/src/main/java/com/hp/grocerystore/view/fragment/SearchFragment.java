package com.hp.grocerystore.view.fragment;

import static android.content.Intent.getIntent;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
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
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Comparator;
import java.util.List;

public class SearchFragment extends Fragment {
    // View Model
    private HomeViewModel homeViewModel;

    private SearchViewModel mViewModel;
    private WishlistViewModel wishlistViewModel;
    private SharedViewModel sharedViewModel;
    // List Data
    private List<Product> productList = new ArrayList<>();
    private List<Product> originalProductList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    //Adapter
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    // Components
    private RecyclerView recyclerView;
    private LinearLayout linearCategoryContainer, btnFilter;
    private TextView selectedSortView = null;
    private TextView[] filters;
    private ProgressBar progressBarSearchView;
    // Filter Args
    private long selectedCategoryId = -1; // Không lọc
    private String selectedSort = "";    // Không sắp xếp
    private int minPrice = 0;
    private int maxPrice = 500000;
    private String searchText = "";


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
        btnFilter = view.findViewById(R.id.btn_filter);
        // Danh sách nút sắp xếp sản phẩm
        filters = new TextView[]{
                view.findViewById(R.id.filter_best_seller),
                view.findViewById(R.id.filter_name),
                view.findViewById(R.id.filter_price_low),
                view.findViewById(R.id.filter_price_high)
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

        productAdapter = new ProductAdapter(getContext(), productList,wishlistViewModel);


        // Nếu chưa có giá trị được share thì load mặc định
        if (sharedViewModel.getFilterData().getValue() == null) {
            searchProducts(1, 10, "");
        }


//        sharedViewModel.getSharedText().observe(getViewLifecycleOwner(), slug -> {
//            if (slug != null && slug.contains("category.slug")) {
//                loadProducts(1, 10, slug, true);
//            }
//            else if(slug != null && slug.contains("productName")){
//                searchText = slug;
//                searchProducts(1, 10, slug);
//            }
//            if(selectedSortView != null){
//                for (TextView filter: filters){
//                    selectedSortView.setSelected(false);
//                    selectedSortView.setTextColor(Color.BLACK);
//                }
//                selectedSortView = null;
//                selectedSort = "";
//            }
//        });

        sharedViewModel.getFilterData().observe(getViewLifecycleOwner(), filterData  -> {
            if (filterData != null) {
                selectedCategoryId = filterData.getSelectedCategoryId();
                String selectedCategorySlug = filterData.getSelectedCategorySlug();
                selectedSort = filterData.getSelectedSort() == null ? "": filterData.getSelectedSort();
                minPrice = filterData.getMinPrice();
                maxPrice = filterData.getMaxPrice();
                searchText = filterData.getSearchText() == null ? "": filterData.getSearchText();
                String sortQuery = "";
                for (TextView filter: filters){
                    if (filter.getText().equals(selectedSort)){
                        selectedSortView = filter;
                        filter.setSelected(true);
                        filter.setTextColor(ContextCompat.getColor(getContext(), R.color.color_variation));
                        selectedSort = filter.getText().toString();
                    }
                }
                switch (selectedSort) {
                    case "Tên sản phẩm":
                        sortQuery = "productName,asc";
                        break;
                    case "Giá thấp đến cao":
                        sortQuery = "price,asc";
                        break;
                    case "Giá cao đến thấp":
                        sortQuery = "price,desc";
                        break;
                    default:
                        sortQuery = "";
                        break;
                }

                if(selectedCategorySlug != null && selectedCategorySlug.contains("category.slug")
                && selectedSort.isEmpty() && minPrice == 0 && maxPrice == 0 && searchText.isEmpty()){
                    // Cập nhật lại giao diện category list
                    categoryAdapter.setupCategorySelection(linearCategoryContainer, selectedCategoryId, this::onCategoryClick);
                    addSortOptions();
                    loadProducts(1, 10, selectedCategorySlug, true);
                }else if(selectedCategorySlug != null && selectedCategorySlug.contains("productName")
                && selectedSort.isEmpty() && minPrice == 0 && maxPrice == 0 && searchText.isEmpty()){
                    searchText = selectedCategorySlug;
                    categoryAdapter.setupCategorySelection(linearCategoryContainer, selectedCategoryId, this::onCategoryClick);
                    addSortOptions();
                    searchProducts(1, 10, selectedCategorySlug);
                }else{
                    if (searchText.contains("productName~"))
                        searchAndFilterProducts(1,10, "category.slug~'"+selectedCategorySlug+"'",
                                searchText, "price > " + minPrice, "price < "+maxPrice, sortQuery);
                    else
                        searchAndFilterProducts(1,10, "category.slug~'"+selectedCategorySlug+"'",
                            "productName~'"+searchText+"'", "price > "+minPrice, "price < "+maxPrice, sortQuery);
                }
            }
        });



        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(productAdapter);

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        loadCategories();
        addSortOptions();

        btnFilter.setOnClickListener(v -> {
            Intent filterIntent = new Intent(getActivity(), FilterActivity.class);
            filterIntent.putExtra("selected_categoryId", (Serializable) selectedCategoryId);
            filterIntent.putExtra("selected_sort", (Serializable) selectedSort);
            filterIntent.putExtra("min_price", (Serializable) minPrice);
            filterIntent.putExtra("max_price", (Serializable) maxPrice);
            filterIntent.putExtra("search_text", (Serializable) searchText);
            startActivity(filterIntent);
        });
    }


    private void loadProducts(int page, int size, String filter, boolean hasFilterCategory) {
        homeViewModel.getProducts(page, size, filter).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    progressBarSearchView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    progressBarSearchView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    productList = resource.data;
                    if (!hasFilterCategory && originalProductList.size() == 0) {
                        originalProductList = new ArrayList<>(productList);
                    }
                    if (hasFilterCategory && originalProductList.size() != 0){
                        originalProductList.clear();
                    }
                    if (productList != null) {
                        productAdapter.setProductList(productList); // cập nhật danh sách sản phẩm
                    }
                    break;

                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void searchProducts(int page, int size, String filter) {
        mViewModel.searchProducts(page, size, filter).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    progressBarSearchView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    progressBarSearchView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    productList = resource.data;
//                    if (originalProductList.size() == 0) {
                        originalProductList = new ArrayList<>(productList);
//                    }
                    if (productList != null) {
                        productAdapter.setProductList(productList); // cập nhật danh sách sản phẩm
                    }
                    break;

                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void searchAndFilterProducts(int page, int size,
                                         String filter1, String filter2,
                                         String filter3, String filter4,String sort) {
        mViewModel.searchAndFilterProducts(page, size, filter1, filter2, filter3, filter4,sort)
                .observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    progressBarSearchView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    progressBarSearchView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    productList = resource.data;
//                    if (originalProductList.size() == 0) {
//                        originalProductList = new ArrayList<>(productList);
//                    }
                    if (productList != null) {
                        productAdapter.setProductList(productList); // cập nhật danh sách sản phẩm
                    }
                    break;

                case ERROR:
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
        if (selectedCategoryId == category.getId()) {
            // Nếu click lại category đang chọn thì bỏ lọc
            selectedCategoryId = -1;
            categoryAdapter.setSelectedCategoryId(-1);
        } else {
            // Cập nhật category mới được chọn
            selectedCategoryId = category.getId();
            categoryAdapter.setSelectedCategoryId(selectedCategoryId);
        }

        // Lọc lại danh sách sản phẩm
        filterProducts();

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
                filterProducts();
            });
        }
    }

    private void filterProducts() {
        List<Product> filtered;
        if (originalProductList.size() != 0){
            filtered = new ArrayList<>(originalProductList);
        }else {
            filtered = new ArrayList<>(productList);
        }
//         Sửa thêm
        if (selectedCategoryId != -1) {

            Category selectedCategory = null;
            for (Category c : categoryList) {
                if (c.getId() == selectedCategoryId) {
                    selectedCategory = c;
                    break;
                }
            }

            if (selectedCategory != null) {
                String selectedSlug = selectedCategory.getSlug();
                filtered.removeIf(p -> !p.getCategory().equals(selectedSlug));
            }

        }
        if((minPrice > 0 || maxPrice < 500000) & maxPrice!=0 & minPrice != 0){
            filtered.removeIf(p -> p.getPrice() < minPrice || p.getPrice() > maxPrice);
        }

        switch (selectedSort) {
            case "Tên sản phẩm":
                filtered.sort(Comparator.comparing(Product::getProductName));
                break;
            case "Giá thấp đến cao":
                filtered.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case "Giá cao đến thấp":
                filtered.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
        }

        productAdapter.updateData(filtered);
    }
}