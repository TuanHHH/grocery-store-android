package com.hp.grocerystore.utils;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class LoadingUtil {
    public static void hideLoading(FrameLayout loadingOverlay, ProgressBar progressBar) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public static void showLoading(FrameLayout loadingOverlay, ProgressBar progressBar) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
