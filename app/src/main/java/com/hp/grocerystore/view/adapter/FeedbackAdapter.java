package com.hp.grocerystore.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hp.grocerystore.R;

import java.util.List;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;
import com.hp.grocerystore.model.Feedback;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        Glide.with(holder.imageAvatar.getContext())
                .load(feedback.getUserAvatar()) // Giả sử `getAvatarUrl()` trả về URL của ảnh
                .placeholder(R.drawable.ic_avatar_placeholder)  // Ảnh placeholder khi đang tải
                .error(R.drawable.ic_avatar_placeholder)  // Ảnh lỗi khi tải thất bại
                .into(holder.imageAvatar);
        holder.textName.setText(feedback.getName());
        holder.ratingBar.setRating(feedback.getRating());
        holder.textDescription.setText(feedback.getDescription());
        holder.textTime.setText(feedback.getTime());
    }

    @Override
    public int getItemCount() {
        return feedbackList != null ? feedbackList.size() : 0;
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar;
        TextView textName, textDescription, textTime;
        RatingBar ratingBar;
        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            textName = itemView.findViewById(R.id.text_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            textDescription = itemView.findViewById(R.id.text_description);
            textTime = itemView.findViewById(R.id.text_time);
        }
    }
}
