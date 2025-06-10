package com.hp.grocerystore.view.fragment;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.category.Category;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.utils.GridSpacingItemDecoration;
import com.hp.grocerystore.view.activity.MainActivity;
import com.hp.grocerystore.view.adapter.CategoryAdapter;
import com.hp.grocerystore.view.adapter.ProductAdapter;
import com.hp.grocerystore.view.adapter.WishlistAdapter;
import com.hp.grocerystore.viewmodel.HomeViewModel;
import com.hp.grocerystore.viewmodel.SharedViewModel;
import com.hp.grocerystore.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    //View Model
    private HomeViewModel homeViewModel;
    private WishlistViewModel wishlistViewModel;
    private SharedViewModel sharedViewModel;
    private LinearLayout linearCategoryContainer, linearCategoryBlockContainer;
    private ProgressBar progressBarCategoryView;
    //List
    private List<Category> categoryList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<Wishlist> wishLists = new ArrayList<>();
    //Adapter
    private ProductAdapter adapter;
    private CategoryAdapter categoryAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearCategoryContainer = view.findViewById(R.id.category_container);
        linearCategoryBlockContainer = view.findViewById(R.id.category_block_container);
        progressBarCategoryView = view.findViewById(R.id.progress_bar_category_view);

        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new HomeViewModel();
            }
        }).get(HomeViewModel.class);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        adapter = new ProductAdapter(getContext(), productList, getViewLifecycleOwner());
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);

        loadCategories();
    }

    private void loadProducts(int page, int size, String filter) {
        homeViewModel.getProducts(page, size, filter).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    List<Product> products = resource.data;
                    if (products != null) {
                        adapter.setProductList(products);
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

            switch (resource.status) {
                case LOADING:
                    progressBarCategoryView.setVisibility(View.VISIBLE);
                    linearCategoryContainer.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    progressBarCategoryView.setVisibility(View.GONE);
                    linearCategoryContainer.setVisibility(View.VISIBLE);

                    categoryList = resource.data;
                    categoryAdapter.setCategoryList(categoryList);
                    categoryAdapter.populateHorizontalLinearLayout(linearCategoryContainer);
                    for (Category category : categoryList) {
                        addCategoryBlock(category);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void addCategoryBlock(Category category) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View categoryView = inflater.inflate(R.layout.item_category_block, linearCategoryBlockContainer, false);
        TextView categoryName = categoryView.findViewById(R.id.category_name);
        TextView sectionTitle = categoryView.findViewById(R.id.section_title);
        ImageView categoryImage = categoryView.findViewById(R.id.category_image);
        RecyclerView recyclerView = categoryView.findViewById(R.id.recyclerView_products);
        TextView btnViewMore = categoryView.findViewById(R.id.btn_view_more);

        categoryName.setText(category.getName());
        String imageUrl = category.getImageUrl();
        if (imageUrl != null && (imageUrl.endsWith(".jpg") || imageUrl.endsWith(".png") || imageUrl.endsWith(".jpeg"))) {
            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.category_placeholder)
                    .into(categoryImage);
        } else {
            categoryImage.setImageResource(R.drawable.category_placeholder);
        }
        sectionTitle.setText(category.getName().toUpperCase());

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin_product_grid);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, true));

        ProductAdapter productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), getViewLifecycleOwner());
        recyclerView.setAdapter(productAdapter);

        String filter = "category.slug~'" + category.getSlug() + "'";
        homeViewModel.getProducts(1, 6, filter).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    linearCategoryBlockContainer.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    linearCategoryBlockContainer.setVisibility(View.VISIBLE);
                    productAdapter.setProductList(resource.data);
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        btnViewMore.setText("Xem thêm sản phẩm " + category.getName() + " >");
        btnViewMore.setTag(category);
        btnViewMore.setOnClickListener(v -> {
            Category selectedCategory = (Category) v.getTag();
            if (getContext() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getContext();
                mainActivity.goToSearchWithCategory(category.getId(), "category.slug~'" + selectedCategory.getSlug() + "'");
            }
        });

        linearCategoryBlockContainer.addView(categoryView);
    }
}