package com.hp.grocerystore.view.activity;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;


import android.annotation.SuppressLint;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import android.view.View;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.utils.FilterData;
import com.hp.grocerystore.view.adapter.ViewpageAdapter;
import com.hp.grocerystore.viewmodel.SharedViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager2 mViewPager;
    BottomNavigationView mBottomNavigationView;
    Toolbar toolbar;
    private ImageButton btnSearch, btnClose;
    private EditText searchBar;
    private SharedViewModel sharedViewModel;
    private long selectedCategoryId = -1;

    private String selectedCategorySlug = ""; // Không lọc
    private String selectedSort = "";    // Không sắp xếp
    private int minPrice = 0;
    private int maxPrice = 500000;
    private float minRating = 0;
    private float maxRating = 5;
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });
        toolbar = findViewById(R.id.toolbar);
        searchBar = findViewById(R.id.search_bar);
        btnSearch = findViewById(R.id.btn_search);
        btnClose = findViewById(R.id.btn_close);

        setSupportActionBar(toolbar);
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setVisibility(View.GONE);
        }
        mViewPager = findViewById(R.id.view_pager);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        ViewpageAdapter viewpagerAdater = new ViewpageAdapter(this);;

        mViewPager.setAdapter(viewpagerAdater);
        mViewPager.setCurrentItem(0);

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        break;
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.navigation_category).setChecked(true);
                        break;
                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.navigation_heart).setChecked(true);
                        break;
                }
            }
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(new  BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {  switch (item.getItemId()){
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0);break;
                case R.id.navigation_category:
                    mViewPager.setCurrentItem(1); break;
                case R.id.navigation_heart:
                    mViewPager.setCurrentItem(2); break;
            }
                return true;
            }
        });

        settingSearchFunc();

        Intent intent = getIntent();
        if (intent.hasExtra("selected_categoryId")){
            selectedCategoryId = (long) intent.getSerializableExtra("selected_categoryId");
        }
        if (intent.hasExtra("selected_categorySlug")){
            selectedCategorySlug = (String) intent.getSerializableExtra("selected_categorySlug");
        }
        if (intent.hasExtra("selected_sort")){
            selectedSort = (String) intent.getSerializableExtra("selected_sort");
        }
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
            if (searchText.length() > 0) {
                String searchBarText = searchText.replace("productName~'", "");
                searchBar.setText(searchBarText.subSequence(0,searchBarText.length()-1));
            }else{
                searchBar.setText("");
            }
        }

        if((intent.hasExtra("selected_categorySlug") && intent.hasExtra("selected_categoryId"))
                || intent.hasExtra("selected_sort")
                || intent.hasExtra("min_price")
                || intent.hasExtra("max_price")
                || intent.hasExtra("search_text")
                || intent.hasExtra("min_rating")
                || intent.hasExtra("max_rating")) {
            goToSearchFromFilter(selectedCategoryId, selectedCategorySlug,
                    selectedSort, minPrice, maxPrice,minRating, maxRating, searchText);
        }
    }

    public void goToSearchWithCategory(long categoryId,String categorySlug) {
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        FilterData data = new FilterData(categorySlug);
        data.setSelectedCategoryId(categoryId);
        sharedViewModel.setFilterData(data);
        mViewPager.setCurrentItem(1, true);
    }
    public void goToSearchWithKeyword(String keyword) {
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        FilterData data = new FilterData(keyword);
        sharedViewModel.setFilterData(data);
        mViewPager.setCurrentItem(1, true);
    }
    public void goToSearchFromFilter(long selectedCategoryId, String selectedCategorySlug, String selectedSort, int minPrice,
                                     int maxPrice, float minRating, float maxRating, String searchText){

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        FilterData data = new FilterData(selectedCategoryId, selectedCategorySlug,
                selectedSort, minPrice, maxPrice,minRating, maxRating, searchText);
        sharedViewModel.setFilterData(data);
        mViewPager.setCurrentItem(1, true);
    }
    private void settingSearchFunc(){
        // Khi nhấn Enter trên bàn phím
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch(searchBar.getText().toString().trim());
                return true;
            }
            return false;
        });

        // Khi nhấn icon kính lúp (đã tách riêng)
        btnSearch.setOnClickListener(v -> {
            performSearch(searchBar.getText().toString().trim());
        });

        // Khi nhấn nút close
        btnClose.setOnClickListener(v -> {
            searchBar.setText("");
        });
    }
    private void performSearch(String keyword) {
        if (keyword.contains("productName~")) goToSearchWithKeyword(keyword);
        else goToSearchWithKeyword("productName~'"+keyword+"'");
    }
}