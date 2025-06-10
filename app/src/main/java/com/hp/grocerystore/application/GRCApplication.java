package com.hp.grocerystore.application;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class GRCApplication extends Application {
    private static GRCApplication instance;
    private static Context appContext;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isConnected = false;
    private boolean hasLostBefore = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isConnected = true;
                Log.d("GRCNetwork", "Network connected");

                if (hasLostBefore) {
                    showToast("Đã kết nối mạng trở lại");
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                isConnected = false;
                hasLostBefore = true;
                Log.d("GRCNetwork", "Network disconnected");
                showToast("Mất kết nối mạng");
            }
        };

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    private void showToast(final String message) {
        android.os.Handler handler = new android.os.Handler(getMainLooper());
        handler.post(() -> Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public static GRCApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
