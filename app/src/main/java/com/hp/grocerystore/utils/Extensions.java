package com.hp.grocerystore.utils;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Extensions {
    public static String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(amount) + " đ";
    }

    public static String getText(TextInputEditText editText) {
        CharSequence text = editText.getText();
        return text == null ? "" : text.toString().trim();
    }

    public static String showPrettyTime(String isoTimeStr) {
        try {
            String cleanedIso = cleanIsoTime(isoTimeStr);
            Date inputDate;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0+
                inputDate = Date.from(Instant.parse(cleanedIso));
                PrettyTime prettyTime = new PrettyTime();
                return prettyTime.format(inputDate);
            } else {
                return new SimpleDateFormat("HH:mm dd:MM:yy", Locale.getDefault())
                        .format(Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(cleanedIso)));
            }
        } catch (Exception e) {
            return "Time undefined";
        }
    }

    private static String cleanIsoTime(String iso) {
        if (iso.contains(".")) return iso.split("\\.")[0] + "Z";
        return iso;
    }

    public static boolean isLoggedIn(Context context) {
        if (!UserSession.getInstance().isLoggedIn() && !AuthPreferenceManager.getInstance(context).isUserLoggedIn()) {
            Toast.makeText(context, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
