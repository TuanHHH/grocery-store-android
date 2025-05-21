package com.hp.grocerystore.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.category.Category;
import com.hp.grocerystore.view.activity.MainActivity;
import com.hp.grocerystore.view.fragment.SearchFragment;
import com.hp.grocerystore.viewmodel.SharedViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter {
    private Context context;
    private List<Category> categoryList = new ArrayList<>();
    private View selectedCategoryView = null;
    private FrameLayout selectedCategoryFilter = null;
    private long selectedCategoryId = -1;
    private OnCategoryClickListener listener;


    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }


    public void setCategoryList(List<Category> categories) {
        this.categoryList.clear();
        this.categoryList.addAll(categories);
    }

    public void setSelectedCategoryId(long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }


    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public void populateHorizontalLinearLayout(LinearLayout linearLayout) {
        linearLayout.removeAllViews();

        for (Category category : categoryList) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.category_item, linearLayout, false);

            ImageView img = itemView.findViewById(R.id.imgCategory);
            TextView name = itemView.findViewById(R.id.txtCategoryName);

            name.setText(category.getName());

//            Glide.with(context)
//                    .load(category.getImageUrl())
//                    .placeholder(R.drawable.category_placeholder)
//                    .into(img);

            String imageUrl = category.getImageUrl();
            if (imageUrl != null && (imageUrl.endsWith(".jpg") || imageUrl.endsWith(".png") || imageUrl.endsWith(".jpeg"))) {
                Glide.with(context)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.category_placeholder)
                        .into(img);
            } else {
                // Có thể đặt placeholder mặc định hoặc lấy thumbnail nếu là video
                img.setImageResource(R.drawable.category_placeholder);
            }

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    (int) (context.getResources().getDisplayMetrics().density * 100),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            param.setMarginEnd((int) (context.getResources().getDisplayMetrics().density * 8));
            itemView.setLayoutParams(param);

            itemView.setTag(category);
            linearLayout.addView(itemView);
            itemView.setOnClickListener(v -> {
                Category selectedCategory = (Category) v.getTag();
                if(context instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.goToSearchWithCategory(selectedCategory.getId(),"category.slug~'" + selectedCategory.getSlug() + "'");
                }
            });
        }
    }

    public void populateGridLayoutForFilter(GridLayout gridLayout, long categoryId) {
        gridLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);

        for (Category category : categoryList) {
            FrameLayout item = (FrameLayout) inflater.inflate(R.layout.filter_item, gridLayout, false);
            TextView text = item.findViewById(R.id.filterText);
            text.setText(category.getName());
            if (categoryId != -1){
                boolean isSelected =  categoryId == category.getId();
                if (isSelected){
                    // Chọn mới
                    item.setSelected(true);
                    item.findViewById(R.id.checkmarkIcon).setVisibility(View.VISIBLE);
                    selectedCategoryFilter = item;

                    if (listener != null) {
                        listener.onCategoryClick(category);
                    }
                }
            }

            item.setOnClickListener(v -> {
                boolean isAlreadySelected = selectedCategoryFilter == item;

                // Bỏ chọn tất cả
                if (selectedCategoryFilter != null) {
                    selectedCategoryFilter.setSelected(false);
                    selectedCategoryFilter.findViewById(R.id.checkmarkIcon).setVisibility(View.GONE);
                    selectedCategoryFilter = null;
                }

                if (!isAlreadySelected) {
                    // Chọn mới
                    item.setSelected(true);
                    item.findViewById(R.id.checkmarkIcon).setVisibility(View.VISIBLE);
                    selectedCategoryFilter = item;

                    if (listener != null) {
                        listener.onCategoryClick(category);
                    }
                } else {
                    // Bỏ chọn nếu click lại
                    if (listener != null) {
                        listener.onCategoryClick(null);
                    }
                }
            });

            gridLayout.addView(item);
        }
    }
    public void setupCategorySelection(LinearLayout container, long selectedCategoryId, OnCategoryClickListener listener) {
        this.selectedCategoryId = selectedCategoryId;
        this.listener = listener;

        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            Category category = (Category) view.getTag();
            TextView name = view.findViewById(R.id.txtCategoryName);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(12f);

            if (category.getId() == selectedCategoryId) {
                drawable.setColor(Color.WHITE);
                drawable.setStroke(2, ContextCompat.getColor(context, R.color.color_variation));
                name.setTextColor(ContextCompat.getColor(context, R.color.color_variation));
            } else {
                drawable.setColor(Color.WHITE);
                name.setTextColor(Color.BLACK);
            }

            view.setBackground(drawable);

            view.setOnClickListener(v -> {
                Category clickedCategory = (Category) v.getTag();
                this.selectedCategoryId = clickedCategory.getId();

                // Cập nhật UI cho tất cả view
                for (int j = 0; j < container.getChildCount(); j++) {
                    View otherView = container.getChildAt(j);
                    Category otherCategory = (Category) otherView.getTag();
                    TextView otherText = otherView.findViewById(R.id.txtCategoryName);

                    GradientDrawable otherDrawable = new GradientDrawable();
                    otherDrawable.setCornerRadius(12f);
                    otherDrawable.setColor(Color.WHITE);

                    if (otherCategory.getId() == selectedCategoryId) {
                        otherDrawable.setStroke(2, ContextCompat.getColor(context, R.color.color_variation));
                        otherText.setTextColor(ContextCompat.getColor(context, R.color.color_variation));
                    } else {
                        otherText.setTextColor(Color.BLACK);
                    }

                    otherView.setBackground(otherDrawable);
                }

                if (this.listener != null) {
                    this.listener.onCategoryClick(clickedCategory);
                }
            });
        }
    }

}
