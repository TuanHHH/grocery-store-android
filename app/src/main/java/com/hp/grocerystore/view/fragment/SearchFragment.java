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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
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
import com.hp.grocerystore.view.adapter.CategoryAdapter;
import com.hp.grocerystore.view.adapter.ProductAdapter;
import com.hp.grocerystore.viewmodel.HomeViewModel;
import com.hp.grocerystore.viewmodel.SearchViewModel;
import com.hp.grocerystore.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchFragment extends Fragment {
    // View Model
    private HomeViewModel homeViewModel;

    private SearchViewModel mViewModel;
    private WishlistViewModel wishlistViewModel;
    // List Data
    private List<Product> productList = new ArrayList<>();
    private List<Product> originalProductList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    //Adapter
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    // Components
    private RecyclerView recyclerView;
    private LinearLayout linearCategoryContainer;
    private TextView selectedSortView = null;
    private TextView[] filters;
    // Filter Args
    private long selectedCategoryId = -1; // Không lọc
    private String selectedSort = "";    // Không sắp xếp
    private int minPrice = 0;
    private int maxPrice = 500000;


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

        // Danh sách nút sắp xếp sản phẩm
        filters = new TextView[]{
                view.findViewById(R.id.filter_best_seller),
                view.findViewById(R.id.filter_name),
                view.findViewById(R.id.filter_price_low),
                view.findViewById(R.id.filter_price_high)
        };

        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

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

        productAdapter = new ProductAdapter(getContext(), productList,wishlistViewModel);


        // Test mẫu product results
        loadProducts(1, 6, "category.slug~'sua'");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(productAdapter);

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        loadCategories();
        addSortOptions();

    }


    private void loadProducts(int page, int size, String filter) {
        homeViewModel.getProducts(page, size, filter).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    // TODO: Hiển thị loading nếu muốn
                    break;

                case SUCCESS:
                    productList = resource.data;
                    if (originalProductList.size() == 0) {
                        originalProductList = new ArrayList<>(productList);
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

    private void loadCategories() {
        homeViewModel.getAllCategories().observe(getViewLifecycleOwner(), resource -> {

            switch (resource.status){
                case LOADING:
                    // TODO: Hiển thị loading nếu muốn
                    break;

                case SUCCESS:
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
        List<Product> filtered = new ArrayList<>(originalProductList);

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
        if(minPrice != 0 || maxPrice != 500000){
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