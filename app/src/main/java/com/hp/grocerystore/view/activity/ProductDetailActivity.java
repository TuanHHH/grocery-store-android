package com.hp.grocerystore.view.activity;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.grocerystore.R;
import com.hp.grocerystore.model.Feedback;
import com.hp.grocerystore.view.adapter.FeedbackAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerFeedback;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.product_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerFeedback = findViewById(R.id.recycler_feedback);

        // Initialize feedback data (Mock Data)
        feedbackList = new ArrayList<>();
        feedbackList.add(new Feedback(null,"Nguyễn Văn A", 5.0f, "Sản phẩm rất tốt!", "2 ngày trước"));
        feedbackList.add(new Feedback(null,"Trần Thị B", 4.5f, "Giao hàng nhanh, đóng gói cẩn thận.", "1 ngày trước"));
        feedbackList.add(new Feedback(null,"Lê Văn C", 4.0f, "Chất lượng tuyệt vời, sẽ mua lại.", "3 giờ trước"));

        // Setup RecyclerView for Feedback List
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        recyclerFeedback.setAdapter(feedbackAdapter);
        recyclerFeedback.setLayoutManager(new LinearLayoutManager(this));

        // Setup Comment Section
        EditText editComment = findViewById(R.id.edit_comment);
        ImageButton buttonSendComment = findViewById(R.id.button_send_comment);

        buttonSendComment.setOnClickListener(v -> {
            String comment = editComment.getText().toString().trim();
            if (!comment.isEmpty()) {
                // Simulate adding comment to feedback list (For now, just adding it with default values)
                feedbackList.add(new Feedback(null,"Người dùng", 4.0f, comment, "Vừa xong"));
                feedbackAdapter.notifyItemInserted(feedbackList.size() - 1); // Notify adapter of new item
                editComment.setText(""); // Clear the comment input
                hideKeyboard();
            }
        });
    }

    // Method to hide keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        }
    }
}
