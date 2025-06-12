package com.hp.grocerystore.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.hp.grocerystore.R;

public class VnPayPaymentActivity extends AppCompatActivity {
    private WebView webView;
    private static final String RETURN_URL = "http://localhost:8080/api/v2/payment/vn-pay-callback";

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vnpay_payment);

        webView = findViewById(R.id.webViewPayment);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new VNPayWebViewClient());

        String paymentUrl = getIntent().getStringExtra("paymentUrl");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (paymentUrl != null && !paymentUrl.isEmpty()) {
            Log.d("VnPayPaymentActivity", "Loading URL: " + paymentUrl);
            webView.loadUrl(paymentUrl);
        } else {
            Log.e("VnPayPaymentActivity", "Payment URL is null or empty");
            Toast.makeText(this, "URL thanh toán không hợp lệ", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

private class VNPayWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("VnPayPaymentActivity", "Redirect URL: " + url);

        if (url.startsWith(RETURN_URL)) {
            String responseCode = parseResponseCode(url);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("vnp_ResponseCode", responseCode);
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;
        }

        if (url.contains("success")) {
            Log.d("VnPayPaymentActivity", "Payment successful - blocking URL load");
            navigateToMainWithResult(true, "Thanh toán thành công");
            return true;
        }

        if (url.contains("failure")) {
            Log.d("VnPayPaymentActivity", "Payment failed - blocking URL load");
            navigateToMainWithResult(false, "Thanh toán thất bại");
            return true;
        }

        return false;
    }


    private void navigateToMainWithResult(boolean isSuccess, String message) {
        Intent intent = new Intent(VnPayPaymentActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("payment_result", isSuccess);
        intent.putExtra("payment_message", message);

        startActivity(intent);
        finish();
    }

    private String parseResponseCode(String url) {
        try {
            Uri uri = Uri.parse(url);
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");
            return responseCode != null ? responseCode : "99";
        } catch (Exception e) {
            Log.e("VnPayPaymentActivity", "Error parsing response code", e);
            return "99";
        }
    }
}



}