package com.hp.grocerystore.network;

import android.content.Context;
import android.util.Log;

import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.network.api.CartApi;
import com.hp.grocerystore.network.api.CategoryApi;
import com.hp.grocerystore.network.api.FeedbackApi;
import com.hp.grocerystore.network.api.OrderApi;
import com.hp.grocerystore.network.api.ProductApi;
import com.hp.grocerystore.network.api.UserApi;
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

    // Khởi tạo Retrofit một lần duy nhất
    private static Retrofit retrofit = null;

    // Phương thức này tạo Retrofit mà không cần Context
    private static Retrofit createRetrofit() {
        if (retrofit == null) {
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

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL_API)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    // Các API phương thức không còn cần Context nữa
    public static ProductApi getProductApi() {
        return createRetrofit().create(ProductApi.class);
    }

    public static FeedbackApi getFeedbackApi() {
        return createRetrofit().create(FeedbackApi.class);
    }

    public static AuthApi getAuthApi() {
        return createRetrofit().create(AuthApi.class);
    }

    public static CartApi getCartApi() {
        return createRetrofit().create(CartApi.class);
    }

    public static CategoryApi getCategoryApi() {
        return createRetrofit().create(CategoryApi.class);
    }

    public static WishlistApi getWishlistApi() {
        return createRetrofit().create(WishlistApi.class);
    }

    public static UserApi getUserApi() {
        return createRetrofit().create(UserApi.class);
    }
    public static OrderApi getOrderApi(Context context) {
        return createRetrofit(context).create(OrderApi.class);
    }
}
