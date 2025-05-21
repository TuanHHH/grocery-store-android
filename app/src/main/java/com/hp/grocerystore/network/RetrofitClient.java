package com.hp.grocerystore.network;

import android.content.Context;
import android.util.Log;

import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.network.api.CartApi;
import com.hp.grocerystore.network.api.CategoryApi;
import com.hp.grocerystore.network.api.FeedbackApi;
import com.hp.grocerystore.network.api.ProductApi;
import com.hp.grocerystore.network.api.WishlistApi;
import com.hp.grocerystore.network.authenticator.TokenAuthenticator;
import com.hp.grocerystore.network.interceptor.AuthInterceptor;
import com.hp.grocerystore.network.interceptor.UserAgentInterceptor;
import com.hp.grocerystore.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit createRetrofit(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (message.startsWith(">") || message.startsWith("<")) {
                    return;
                }
                Log.d("Retrofit Request", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new UserAgentInterceptor())
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(loggingInterceptor)
                .authenticator(new TokenAuthenticator())
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

    public static CartApi getCartApi(Context context) {
        return createRetrofit(context).create(CartApi.class);
    }
    public static CategoryApi getCategoryApi(Context context) {
        return createRetrofit(context).create(CategoryApi.class);
    }
    public static WishlistApi getWishlistApi(Context context) {
        return createRetrofit(context).create(WishlistApi.class);
    }
}
