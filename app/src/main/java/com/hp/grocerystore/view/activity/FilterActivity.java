package com.hp.grocerystore.view.activity;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.RangeSlider;
import com.hp.grocerystore.R;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.category.Category;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.repository.CategoryRepository;
import com.hp.grocerystore.repository.ProductRepository;
import com.hp.grocerystore.view.adapter.CategoryAdapter;
import com.hp.grocerystore.viewmodel.HomeViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    private HomeViewModel homeViewModel;
    private CategoryAdapter categoryAdapter;
    private ImageButton btnClose;
    private GridLayout sortGrid, sortFilterCategoryGrid;
    private Button btnApply,btnReset;
    private String[] sortOptions = {
            "Mới nhất",
            "Bán chạy",
            "Tên sản phẩm",
            "Giá thấp đến cao",
            "Giá cao đến thấp",
            "Rating thấp đến cao",
            "Rating cao đến thấp"
    };
    private ProgressBar progressBar;
    private RangeSlider priceRangeSlider, ratingRangeSlider;
    private TextView tvMinPrice, tvMaxPrice, tvMinRating, tvMaxRating;
    private List<Category> categoryList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private long selectedCategoryId = -1; // Không lọc
    private String selectedCategorySlug = "";
    private String selectedSort = "";    // Không sắp xếp
    private int minPrice = 0;
    private int maxPrice = 500000;
    private float minRating = 0;
    private float maxRating = 5;
    private String searchText = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.filter_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnClose = findViewById(R.id.btnClose);
        btnReset = findViewById(R.id.btnReset);
        sortGrid = findViewById(R.id.sortGrid);
        sortFilterCategoryGrid = findViewById(R.id.filterCategoryGrid);
        btnApply = findViewById(R.id.btnApply);
        priceRangeSlider = findViewById(R.id.priceRangeSlider);
        ratingRangeSlider = findViewById(R.id.ratingRangeSlider);
        tvMinPrice = findViewById(R.id.tvMinPrice);
        tvMaxPrice = findViewById(R.id.tvMaxPrice);
        tvMinRating = findViewById(R.id.tvMinRating);
        tvMaxRating = findViewById(R.id.tvMaxRating);
        progressBar = findViewById(R.id.progress_bar_category_view_filter);

        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

                return (T) new HomeViewModel();
            }
        }).get(HomeViewModel.class);

        Intent intent = getIntent();

        categoryAdapter = new CategoryAdapter(this, categoryList);
        categoryAdapter.setOnCategoryClickListener(category -> {
            if (category != null) {
                selectedCategoryId = category.getId();
                selectedCategorySlug = category.getSlug();
            } else {
                selectedCategoryId = -1;
                selectedCategorySlug = "";
            }
        });



        selectedCategoryId = (long) intent.getSerializableExtra("selected_categoryId");
        selectedSort = (String) intent.getSerializableExtra("selected_sort");
        if (intent.hasExtra("min_price")){
            minPrice = (int) intent.getSerializableExtra("min_price");
        }
        if (intent.hasExtra("max_price")){
            maxPrice = (int) intent.getSerializableExtra("max_price");
        }
        if (intent.hasExtra("min_rating")){
            minRating = (float) intent.getSerializableExtra("min_rating");
        }
        if (intent.hasExtra("max_rating")){
            maxRating = (float) intent.getSerializableExtra("max_rating");
        }
        if (intent.hasExtra("search_text")){
            searchText = (String) intent.getSerializableExtra("search_text");
        }

//        categoryAdapter.populateGridLayoutForFilter(sortFilterCategoryGrid, selectedCategoryId);
        loadCategories();
        addSortOptions();


        //Config Slider Price
        //Cài đặt giao diện thanh slider
        Drawable customThumb = ContextCompat.getDrawable(this, R.drawable.custom_thumb);
        priceRangeSlider.setCustomThumbDrawable(customThumb);
        priceRangeSlider.setHaloRadius(12);
        priceRangeSlider.setTrackHeight(12);
        priceRangeSlider.setThumbRadius(24);

        ratingRangeSlider.setCustomThumbDrawable(customThumb);
        ratingRangeSlider.setHaloRadius(6);
        ratingRangeSlider.setTrackHeight(12);
        ratingRangeSlider.setThumbRadius(24);

        float sliderPriceMin = priceRangeSlider.getValueFrom(); // 0
        float sliderPriceMax = priceRangeSlider.getValueTo();   // 500000

        float sliderRatingMin = ratingRangeSlider.getValueFrom(); // 0
        float sliderRatingMax = ratingRangeSlider.getValueTo();   // 5

        float leftThumbPrice = Math.max(sliderPriceMin, Math.min(minPrice, maxPrice));
        float rightThumbPrice = Math.min(sliderPriceMax, Math.max(minPrice, maxPrice));
        tvMinPrice.setText(String.valueOf(minPrice));
        tvMaxPrice.setText(String.valueOf(maxPrice));
        priceRangeSlider.setValues(leftThumbPrice, rightThumbPrice);

        float leftThumbRating = Math.max(sliderRatingMin, Math.min(minRating, maxRating));
        float rightThumbRating = Math.min(sliderRatingMax, Math.max(minRating, maxRating));
        tvMinRating.setText(String.valueOf(minRating));
        tvMaxRating.setText(String.valueOf(maxRating));
        ratingRangeSlider.setValues(leftThumbRating, rightThumbRating);

        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            minPrice = Math.round(values.get(0));
            maxPrice = Math.round(values.get(1));
            tvMinPrice.setText(String.valueOf(minPrice));
            tvMaxPrice.setText(String.valueOf(maxPrice));
        });

        ratingRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            minRating = values.get(0);
            maxRating = values.get(1);
            tvMinRating.setText(String.valueOf(minRating));
            tvMaxRating.setText(String.valueOf(maxRating));
        });




        btnClose.setOnClickListener(v -> finish()); // hoặc dismiss() nếu là dialog
        btnReset.setOnClickListener(view -> {
            // 1. Reset các biến lọc
            selectedCategoryId = -1;
            selectedCategorySlug = "";
            selectedSort = "";
            minPrice = 0;
            maxPrice = 500000;
            minRating = 0;
            maxRating = 5;

            // 2. Reset RangeSlider
            priceRangeSlider.setValues((float) minPrice, (float) maxPrice);
            tvMinPrice.setText(String.valueOf(minPrice));
            tvMaxPrice.setText(String.valueOf(maxPrice));

            ratingRangeSlider.setValues((float) minRating, (float) maxRating);
            tvMinRating.setText(String.valueOf(minRating));
            tvMaxRating.setText(String.valueOf(maxRating));

            // 3. Bỏ chọn các sort option
            for (int i = 0; i < sortGrid.getChildCount(); i++) {
                FrameLayout child = (FrameLayout) sortGrid.getChildAt(i);
                child.setSelected(false);
                child.findViewById(R.id.checkmarkIcon).setVisibility(View.GONE);
            }

            // 4. Bỏ chọn các category
            categoryAdapter.setSelectedCategoryId(-1);
            categoryAdapter.populateGridLayoutForFilter(sortFilterCategoryGrid, -1);
            cofigHeigthGridLayout();
        });
        btnApply.setOnClickListener(view -> {
            Intent filterIntent = new Intent(FilterActivity.this, MainActivity.class);
            filterIntent.putExtra("selected_categoryId", (Serializable) selectedCategoryId);
            filterIntent.putExtra("selected_categorySlug", (Serializable) selectedCategorySlug);
            filterIntent.putExtra("selected_sort", (Serializable) selectedSort);
            filterIntent.putExtra("min_price", (Serializable) minPrice);
            filterIntent.putExtra("max_price", (Serializable) maxPrice);
            filterIntent.putExtra("min_rating", (Serializable) minRating);
            filterIntent.putExtra("max_rating", (Serializable) maxRating);
            filterIntent.putExtra("search_text", (Serializable) searchText);
            startActivity(filterIntent);
        });
    }
    private void loadCategories() {
        homeViewModel.getAllCategories().observe(this, resource -> {

            switch (resource.status){
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    sortFilterCategoryGrid.setVisibility(View.GONE);
                    break;


                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    sortFilterCategoryGrid.setVisibility(View.VISIBLE);

                    categoryList = resource.data;
                    categoryAdapter.setCategoryList(categoryList);
                    categoryAdapter.populateGridLayoutForFilter(sortFilterCategoryGrid, selectedCategoryId);
                    cofigHeigthGridLayout();
                    break;
                case ERROR:
                    Toast.makeText(this, "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void addSortOptions() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (String option : sortOptions) {
            FrameLayout item = (FrameLayout) inflater.inflate(R.layout.filter_item, sortGrid, false);

            TextView text = item.findViewById(R.id.filterText);
            text.setText(option);
            if (!selectedSort.equals("")){
                boolean isSelected = option.equals(selectedSort);
                if(isSelected){
                    item.setSelected(true);
                    item.findViewById(R.id.checkmarkIcon).setVisibility(View.VISIBLE);
                }
            }

            item.setOnClickListener(v -> {
                boolean isAlreadySelected = option.equals(selectedSort);

                // Bỏ chọn tất cả
                for (int i = 0; i < sortGrid.getChildCount(); i++) {
                    FrameLayout child = (FrameLayout) sortGrid.getChildAt(i);
                    child.setSelected(false);
                    child.findViewById(R.id.checkmarkIcon).setVisibility(View.GONE);
                }

                if (isAlreadySelected) {
                    // Nếu đã chọn rồi và click lại -> bỏ chọn
                    selectedSort = "";
                } else {
                    // Chọn mới
                    item.setSelected(true);
                    item.findViewById(R.id.checkmarkIcon).setVisibility(View.VISIBLE);
                    selectedSort = option;
                }
            });

            sortGrid.addView(item);
        }
    }

    private void cofigHeigthGridLayout(){
        //Thiết lập chiều cao cho grid view category
        sortFilterCategoryGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sortFilterCategoryGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int columnCount = 2; // hoặc 3 tùy bạn định số cột là bao nhiêu
                int childCount = sortFilterCategoryGrid.getChildCount();

                for (int i = 0; i < childCount; i += columnCount) {
                    int maxHeight = 0;

                    // Tìm chiều cao lớn nhất trong 1 hàng
                    for (int j = 0; j < columnCount; j++) {
                        if (i + j < childCount) {
                            View child = sortFilterCategoryGrid.getChildAt(i + j);
                            child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            int height = child.getMeasuredHeight();
                            if (height > maxHeight) {
                                maxHeight = height;
                            }
                        }
                    }

                    // Set lại chiều cao của từng item trong hàng
                    for (int j = 0; j < columnCount; j++) {
                        if (i + j < childCount) {
                            View child = sortFilterCategoryGrid.getChildAt(i + j);
                            GridLayout.LayoutParams params = (GridLayout.LayoutParams) child.getLayoutParams();
                            params.height = maxHeight;
                            child.setLayoutParams(params);
                        }
                    }
                }
            }
        });

    }
}
