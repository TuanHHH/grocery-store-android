package com.hp.grocerystore.view.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.hp.grocerystore.R;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.feedback.Feedback;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.utils.AuthPreferenceManager;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.utils.LoadingUtil;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.utils.UserSession;
import com.hp.grocerystore.view.adapter.FeedbackAdapter;
import com.hp.grocerystore.viewmodel.CartViewModel;
import com.hp.grocerystore.viewmodel.ProductViewModel;
import com.hp.grocerystore.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class ProductDetailActivity extends AppCompatActivity {
    private ProductViewModel viewModel;
    private CartViewModel cartViewModel;
    private WishlistViewModel wishlistViewModel;
    private Product currentProduct;
    private boolean isWishlisted = false;
    private RecyclerView recyclerFeedback;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;
    private FrameLayout loadingOverlay;
    private ProgressBar progressBar;
    private ImageButton wishlistBtn;

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
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        setupRecyclerView();
        Intent intent = getIntent();
        long productId;
        if (intent.hasExtra("product_id")) {
            Object extra = intent.getSerializableExtra("product_id");
            if (extra != null) {
                productId = (long) extra;
            } else {
                Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }
        observeProduct(productId);
    }


    private void observeProduct(long productId) {
        viewModel.getProduct(productId).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                LoadingUtil.showLoading(loadingOverlay, progressBar);
            } else if (resource.status == Resource.Status.SUCCESS) {
                showProductDetails(resource.data);
                LoadingUtil.hideLoading(loadingOverlay, progressBar);
                observeFeedback(productId);
            } else {
                LoadingUtil.hideLoading(loadingOverlay, progressBar);
                if (resource.message != null
                        && resource.message.contains("404")) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Lỗi")
                            .setMessage("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh")
                            .setPositiveButton("OK", (d, which) -> {
                                finish();
                            })
                            .setCancelable(false)
                            .create();
                    dialog.show();
                    Button btnOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    if (btnOk != null) {
                        btnOk.setTextColor(ContextCompat.getColor(this, R.color.primary));
                    }
                } else {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void observeFeedback(long productId) {
        viewModel.getFeedback(productId).observe(this, resource -> {
            switch (resource.status){
                case LOADING:
                    break;
                case SUCCESS:
                    feedbackAdapter.setFeedbackList(resource.data);
                    showSummaryFeedback(productId);
                    break;
                case ERROR:
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showSummaryFeedback(long productId) {
        ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        TextView aiSummary = findViewById(R.id.text_ai_summary);
        viewModel.getSummaryFeedback(productId).observe(this, resource->{
            if (resource.status == Resource.Status.LOADING) {
                shimmerFrameLayout.startShimmer();
            } else if (resource.status == Resource.Status.SUCCESS) {
                aiSummary.setText("✨ Tóm tắt bởi AI\n\n" + resource.data);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setShimmer(null);
            } else {
                aiSummary.setText("✨ Tóm tắt bởi AI\n\nKhông lấy được thông tin từ server");
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setShimmer(null);
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

    @SuppressLint("DefaultLocale")
    private void showProductDetails(Product product) {
        currentProduct = product;
        TextView name = findViewById(R.id.text_title);
        TextView price = findViewById(R.id.text_price);
        TextView description = findViewById(R.id.text_description);
        ImageView image = findViewById(R.id.image_product);
        TextView unit = findViewById(R.id.text_unit);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        MaterialButton addToCartBtn = findViewById(R.id.button_add_to_cart);
        wishlistBtn = findViewById(R.id.button_wishlist);
        if (product.getQuantity() <= 0) {
            addToCartBtn.setAlpha(0.5f);
            addToCartBtn.setOnClickListener(v ->
                    Toast.makeText(this, "Sản phẩm đang tạm hết hàng", Toast.LENGTH_SHORT).show()
            );
        } else {
            addToCartBtn.setAlpha(1.0f);
            addToCartBtn.setOnClickListener(this::addToCart);
        }
        name.setText(product.getProductName());
        price.setText(Extensions.formatCurrency(product.getPrice()));
        description.setText(product.getDescription());
        unit.setText((product.getUnit() == null || product.getUnit().isEmpty()) ? "Không có đơn vị" : product.getUnit());
        Glide.with(this)
                .load(product.getImageUrl())
                .timeout(10000)
                .placeholder(R.drawable.product_placeholder)
                .into(image);
        ratingBar.setRating(product.getRating());

        if (UserSession.getInstance().isLoggedIn() && AuthPreferenceManager.getInstance(GRCApplication.getAppContext()).isUserLoggedIn()) {
            viewModel.getWishlistStatus(product.getId()).observe(this, resource -> {
                if (resource.status == Resource.Status.LOADING) {
                    // pass
                } else if (resource.status == Resource.Status.SUCCESS) {
                    isWishlisted = resource.data.getWishlisted();
                    updateWishlistUI(wishlistBtn);
                } else {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            isWishlisted = false;
            updateWishlistUI(wishlistBtn);
        }

        wishlistBtn.setOnClickListener(this::handleWishlist);
    }

    private void updateWishlistUI(ImageButton wishlistBtn) {
        if (isWishlisted) {
            wishlistBtn.setImageResource(R.drawable.ic_heart_filled);
        } else {
            wishlistBtn.setImageResource(R.drawable.ic_favorite);
        }
    }
    private void handleWishlist(View view) {
        if (currentProduct == null) {
            Toast.makeText(this, "Đang tải sản phẩm, vui lòng chờ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Extensions.isLoggedIn(this)) {
            return;
        }
        wishlistBtn.setEnabled(false);
        if (isWishlisted){
            wishlistViewModel.deleteWishlist(currentProduct.getId()).observe(this, resource ->{
                wishlistBtn.setEnabled(true);
                if (resource.status == Resource.Status.LOADING) {
                    // pass
                } else if (resource.status == Resource.Status.SUCCESS) {
                    isWishlisted = false;
                    updateWishlistUI(wishlistBtn);
                    Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            wishlistViewModel.addWishlist(currentProduct.getId()).observe(this, resource ->{
                wishlistBtn.setEnabled(true);
                if (resource.status == Resource.Status.LOADING) {
                    // pass
                } else if (resource.status == Resource.Status.SUCCESS) {
                    isWishlisted = true;
                    updateWishlistUI(wishlistBtn);
                    Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void addToCart(View view) {
        if (currentProduct == null) {
            Toast.makeText(this, "Đang tải sản phẩm, vui lòng chờ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Extensions.isLoggedIn(this)) {
            return;
        }
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_quantity_selector, null);
        dialog.setContentView(dialogView);
        MaterialButton confirmBtn = dialogView.findViewById(R.id.button_confirm_quantity);
        TextView stockInDialog = dialogView.findViewById(R.id.stock_available);
        stockInDialog.setText(
                String.format(Locale.getDefault(), "Kho: %d", currentProduct.getQuantity())
        );
        MaterialButton minusBtn = dialogView.findViewById(R.id.button_minus);
        MaterialButton plusBtn = dialogView.findViewById(R.id.button_plus);
        TextView quantityView = dialogView.findViewById(R.id.text_quantity);
        quantityView.setText("1");
        minusBtn.setOnClickListener(v -> {
            int q = Integer.parseInt(quantityView.getText().toString());
            if (q > 1) quantityView.setText(String.valueOf(q - 1));
        });
        plusBtn.setOnClickListener(v -> {
            int q = Integer.parseInt(quantityView.getText().toString());
            if (q < currentProduct.getQuantity())
                quantityView.setText(String.valueOf(q + 1));
        });

        confirmBtn.setOnClickListener(v -> {
            int q = Integer.parseInt(quantityView.getText().toString());
            cartViewModel.addOrUpdateCart(currentProduct.getId(), q).observe(this, resource -> {
                if (resource.status == Resource.Status.LOADING) {
                    LoadingUtil.showLoading(loadingOverlay, progressBar);
                } else if (resource.status == Resource.Status.SUCCESS) {
                    LoadingUtil.hideLoading(loadingOverlay, progressBar);
                    Toast.makeText(this, "Thêm giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    LoadingUtil.hideLoading(loadingOverlay, progressBar);
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    public void showFeedbackDialog(View view) {
        if (currentProduct == null) {
            Toast.makeText(this, "Đang tải sản phẩm, vui lòng chờ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Extensions.isLoggedIn(this)) {
            return;
        }
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
                viewModel.addFeedBack(
                        currentProduct.getId(),
                        rating,
                        comment
                ).observe(this, resource -> {
                    if (resource.status == Resource.Status.LOADING) {
                        LoadingUtil.showLoading(loadingOverlay, progressBar);
                    } else if (resource.status == Resource.Status.SUCCESS) {
                        Feedback fb = resource.data;
                        int existingIndex = IntStream.range(0, feedbackList.size())
                                .filter(i -> feedbackList.get(i).getId() == fb.getId())
                                .findFirst()
                                .orElse(-1);
                        if (existingIndex >= 0) {
                            feedbackList.set(existingIndex, fb);
                            feedbackAdapter.notifyItemChanged(existingIndex);
                        } else {
                            feedbackList.add(0, fb);
                            feedbackAdapter.notifyItemInserted(0);
                            recyclerFeedback.scrollToPosition(0);
                        }
                        LoadingUtil.hideLoading(loadingOverlay, progressBar);
                        Toast.makeText(this, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        LoadingUtil.hideLoading(loadingOverlay, progressBar);
                        Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    }
                });
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


}
