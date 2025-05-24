package com.hp.grocerystore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hp.grocerystore.utils.AuthPreferenceManager;
import com.hp.grocerystore.view.activity.LoginActivity;

public class SessionExpiredReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AuthPreferenceManager pref = AuthPreferenceManager.getInstance(context);
        pref.clear();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent loginIntent = new Intent(context, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(loginIntent);
            Toast.makeText(context, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
        }, 100); // Delay 100ms SurfaceSyncGroup
    }
}
