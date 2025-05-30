package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.feedback.CreateFeedbackRequest;
import com.hp.grocerystore.model.feedback.Feedback;
import com.hp.grocerystore.model.base.PaginationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FeedbackApi {
    @GET("product/{productId}/ratings")
    Call<ApiResponse<PaginationResponse<Feedback>>> getFeedbacksByProductId(@Path("productId") long productId);

    @POST("product/ratings")
    Call<ApiResponse<Feedback>> addFeedback(@Body CreateFeedbackRequest request);
}
