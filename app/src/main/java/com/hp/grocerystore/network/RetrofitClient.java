package com.hp.grocerystore.network;

import android.content.Context;

import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.network.api.FeedbackApi;
import com.hp.grocerystore.network.api.ProductApi;
import com.hp.grocerystore.network.interceptor.AuthInterceptor;
import com.hp.grocerystore.network.interceptor.UserAgentInterceptor;
import com.hp.grocerystore.utils.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit createRetrofit(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new UserAgentInterceptor())
                .addInterceptor(new AuthInterceptor(context)) // Gửi access token nếu có
                .build();

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ProductApi getProductApi(Context context) {
        return createRetrofit(context).create(ProductApi.class);
    }

    public static FeedbackApi getFeedbackApi(Context context) {
        return createRetrofit(context).create(FeedbackApi.class);
    }

    public static AuthApi getAuthApi(Context context) {
        return createRetrofit(context).create(AuthApi.class);
    }
}
