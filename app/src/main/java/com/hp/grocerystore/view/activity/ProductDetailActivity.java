package com.hp.grocerystore.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.feedback.Feedback;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.view.adapter.FeedbackAdapter;
import com.hp.grocerystore.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    private ProductViewModel viewModel;
    private RecyclerView recyclerFeedback;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;
    private FrameLayout loadingOverlay;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.product_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadingOverlay = findViewById(R.id.loading_overlay);
        progressBar = findViewById(R.id.progress_bar);
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        setupRecyclerView();
        long productId = 222;
        observeProduct(productId);
        observeFeedback(productId);
    }

    private void observeProduct(long productId) {
        viewModel.getProduct(productId).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                showLoading();
            } else if (resource.status == Resource.Status.SUCCESS) {
                showProductDetails(resource.data);
                hideLoading();
            } else {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }

    private void observeFeedback(long productId) {
        viewModel.getFeedback(productId).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                showLoading();
            } else if (resource.status == Resource.Status.SUCCESS) {
                feedbackAdapter.setFeedbackList(resource.data);
                hideLoading();
            } else {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerFeedback = findViewById(R.id.recycler_feedback);
        recyclerFeedback.setLayoutManager(new LinearLayoutManager(this));
        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        recyclerFeedback.setAdapter(feedbackAdapter);
    }

    private void showProductDetails(Product product) {
        TextView name = findViewById(R.id.text_title);
        TextView price = findViewById(R.id.text_price);
        TextView description = findViewById(R.id.text_description);
        ImageView image = findViewById(R.id.image_product);
        RatingBar ratingBar = findViewById(R.id.rating_bar);

        name.setText(product.getProductName());
        price.setText(Extensions.formatCurrency(product.getPrice()));
        description.setText(product.getDescription());
        Glide.with(this)
                .load(product.getImageUrl())
                .timeout(10000)
                .placeholder(R.drawable.product_placeholder)
                .into(image);
        ratingBar.setRating(product.getRating());
    }

    public void showFeedbackDialog(View view) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_review, null);
        dialog.setContentView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.rating_selector);
        EditText editComment = dialogView.findViewById(R.id.edit_comment);
        Button sendButton = dialogView.findViewById(R.id.button_send_review);

        sendButton.setOnClickListener(v -> {
            String comment = editComment.getText().toString().trim();
            int rating = (int) ratingBar.getRating();

            if (comment.isEmpty() || rating == 0) {
                Toast.makeText(this, "Vui lòng nhập nội dung và chọn sao", Toast.LENGTH_SHORT).show();
            } else {
                //Call api
                Log.d("SendReview", "Rating: " + rating + " | Comment: " + comment);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void redirectToCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void navigateBack(View view) {
        finish();
    }

    private void showLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    private void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
