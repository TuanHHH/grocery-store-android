//package com.hp.grocerystore.view.activity;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.hp.grocerystore.R;
//import com.hp.grocerystore.model.product.Product;
//import com.hp.grocerystore.utils.Resource;
//import com.hp.grocerystore.view.adapter.ProductAdapter;
//import com.hp.grocerystore.viewmodel.ProductViewModel;
//
//import java.util.List;
//
//public class ProductListActivity extends AppCompatActivity {
//
//    private ProductViewModel viewModel;
////    private FrameLayout loadingOverlay;
////    private ProgressBar progressBar;
//
//    private RecyclerView recyclerProductList;
//    private List<Product> productsList;
//
////    private final Context context;
////    private final List<Product> productList;
////
////    public ProductAdapter(Context context, List<Product> productList) {
////        this.context = context;
////        this.productList = productList;
////    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_product_list);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerViewProducts), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
////        loadingOverlay = findViewById(R.id.loading_overlay);
////        progressBar = findViewById(R.id.progress_bar);
//        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
//        observeProductList();
////        setupRecyclerView();
//    }
//    private void observeProductList() {
//        viewModel.getProducts().observe(this, resource -> {
//            if (resource.status == Resource.Status.LOADING) {
//
//            } else if (resource.status == Resource.Status.SUCCESS) {
////                showProductDetails(resource.data);
////                ProductAdapter.set
//            } else {
//                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
////                hideLoading();
//            }
//        });
//    }
//
//}
package com.hp.grocerystore.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.grocerystore.R;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.view.adapter.ProductAdapter;
import com.hp.grocerystore.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ProductViewModel viewModel;
    private RecyclerView recyclerProductList;
    private ProductAdapter productAdapter;
    private final List<Product> productsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerViewProducts), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerProductList = findViewById(R.id.recyclerViewProducts);
        recyclerProductList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, productsList);
        recyclerProductList.setAdapter(productAdapter);

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        observeProductList();
    }

    private void observeProductList() {
        viewModel.getProducts().observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                // Show loading indicator if needed
            } else if (resource.status == Resource.Status.SUCCESS) {
                if (resource.data != null) {
                    productsList.clear();
                    productsList.addAll(resource.data);
                    Log.d("ProductListActivity", "Received products: " + resource.data.size());
                    productAdapter.notifyDataSetChanged();
                }
            } else {
                Log.d("ProductListActivity", "Received products: " + resource.data.size());
                Toast.makeText(this, resource.message != null ? resource.message : "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();

//                Toast.makeText(this, resource.message != null ? resource.message : "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
