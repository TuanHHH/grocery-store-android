package com.hp.grocerystore.application;

import android.app.Application;
import android.content.Context;

public class GRCApplication extends Application {

    private static GRCApplication instance;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();  // Lưu Context ứng dụng
    }

    public static GRCApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
