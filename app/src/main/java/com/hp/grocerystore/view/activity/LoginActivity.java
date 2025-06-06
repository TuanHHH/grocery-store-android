package com.hp.grocerystore.view.activity;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hp.grocerystore.R;
import com.hp.grocerystore.utils.Constants;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.viewmodel.LoginViewModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String WEB_CLIENT_ID = Constants.GOOGLE_CLIENT_ID;
    private static final String TAG = "LoginActivity";
    private LoginViewModel viewModel;
    MaterialButton loginButton, googleLoginButton;
    TextView registerText, forgotPasswordText, homeText;
    TextInputEditText emailEditText, passwordEditText;

    private FrameLayout loadingOverlay;
    private ProgressBar progressBar;

    // Google Sign-In
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> oneTapLauncher;

    // Legacy Google Sign-In (fallback)
    private GoogleSignInClient legacyGoogleSignInClient;
    private ActivityResultLauncher<Intent> legacySignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        initGoogleSignIn();
        initLegacyGoogleSignIn();

        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            emailEditText.setText(email);
            passwordEditText.requestFocus();
        }
    }

    private void initLegacyGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build();


        legacyGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        legacySignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            String idToken = account.getIdToken();

                            Log.d(TAG, "Legacy Google Sign-In successful");
                            Log.d(TAG, "Display Name: " + account.getDisplayName());
                            Log.d(TAG, "Email: " + account.getEmail());

                            if (idToken != null) {
                                Log.d(TAG, "Got ID token from legacy: " + idToken);
                                sendCredentialToBackend(idToken);
                            } else {
                                Log.e(TAG, "Legacy ID Token is null");
                                hideLoading();
                                Toast.makeText(this, "Không thể lấy token từ Google", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ApiException e) {
                            Log.e(TAG, "Legacy Google Sign-In failed", e);
                            Log.e(TAG, "Error code: " + e.getStatusCode());
                            hideLoading();
                            Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Legacy Google Sign-In canceled");
                        hideLoading();
                    }
                }
        );
    }

    private void initViews() {
        loadingOverlay = findViewById(R.id.loading_overlay);
        progressBar = findViewById(R.id.progress_bar);
        loginButton = findViewById(R.id.loginButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        registerText = findViewById(R.id.registerText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        homeText = findViewById(R.id.homeText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        googleLoginButton.setOnClickListener(v -> startGoogleSignIn());
    }
    private void initGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(this);

        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(WEB_CLIENT_ID)
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .setAutoSelectEnabled(false)
                .build();


        // Register ActivityResultLauncher for One Tap sign-in
        oneTapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                sendCredentialToBackend(idToken);
                            } else {
                                hideLoading();
                                Toast.makeText(this, "Không thể lấy token từ Google", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ApiException e) {
                            Log.e(TAG, "Google Sign-In failed", e);
                            Log.e(TAG, "Error code: " + e.getStatusCode());
                            hideLoading();
                            Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        hideLoading();
                        startLegacyGoogleSignIn();
                    } else {
                        Log.d(TAG, "One Tap sign-in failed with code: " + result.getResultCode());
                        hideLoading();
                        startLegacyGoogleSignIn();
                    }
                }
        );
    }

    private void startLegacyGoogleSignIn() {
        Log.d(TAG, "Starting legacy Google Sign-In as fallback");
        showLoading();
        Intent signInIntent = legacyGoogleSignInClient.getSignInIntent();
        legacySignInLauncher.launch(signInIntent);
    }

    private void sendCredentialToBackend(String idToken) {
        Log.d(TAG, idToken);
        viewModel.loginGoogle(idToken).observe(this, resource ->{
            if (resource == null) return;
            switch (resource.status) {
                case SUCCESS:
                    hideLoading();
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    viewModel.getUserInfo();
                    break;
                case ERROR:
                    hideLoading();
                    String errorMessage = resource.message != null ? resource.message : "Đăng nhập thất bại. Vui lòng thử lại.";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    showLoading();
                    break;
            }
        });
    }

    private void startGoogleSignIn() {
        Log.d(TAG, "Starting Google One Tap Sign-In");
        showLoading();

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    Log.d(TAG, "One Tap Sign-In request successful");
                    try {
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        oneTapLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        hideLoading();
                        startLegacyGoogleSignIn();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "One Tap Sign-In request failed: " + e.getLocalizedMessage());
                    Log.e(TAG, "Error class: " + e.getClass().getSimpleName());
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        Log.e(TAG, "Status code: " + apiException.getStatusCode());
                    }
                    hideLoading();
                    startLegacyGoogleSignIn();
                });
    }

    public void processLogin(View view) {
        String email = Extensions.getText(emailEditText);
        String password = Extensions.getText(passwordEditText);

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email không hợp lệ");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        viewModel.login(email, password).observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case SUCCESS:
                    hideLoading();
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    viewModel.getUserInfo();
                    break;
                case ERROR:
                    hideLoading();
                    String errorMessage = resource.message != null ? resource.message : "Đăng nhập thất bại. Vui lòng thử lại.";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    showLoading();
                    break;
            }
        });
    }

    public void navigateToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void navigateToForgotPassword(View view) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);

        TextInputEditText etEmail = dialog.findViewById(R.id.email);
        Button btnSendOtp = dialog.findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(v -> {
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Vui lòng nhập email");
                etEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email không hợp lệ");
                etEmail.requestFocus();
                return;
            }

            viewModel.sendOTPForgotPassword(email).observe(this, resource ->{
                if (resource == null) return;

                switch (resource.status) {
                    case SUCCESS:
                        hideLoading();
                        Toast.makeText(this, "Đã gửi mã OTP tới: " + email, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        showOtpDialog(email);
                        break;
                    case ERROR:
                        hideLoading();
                        String errorMessage = resource.message != null ? resource.message : "Gửi OTP thất bại. Vui lòng thử lại.";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        showLoading();
                        break;
                }
            });
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showOtpDialog(String email) {
        Dialog otpDialog = new Dialog(this);
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.setContentView(R.layout.dialog_input_otp);
        TextInputEditText etOtp = otpDialog.findViewById(R.id.otp);
        Button btnVerifyOtp = otpDialog.findViewById(R.id.btnVerifyOtp);

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = Objects.requireNonNull(etOtp.getText()).toString().trim();
            if (otp.isEmpty()) {
                etOtp.setError("Vui lòng nhập mã OTP");
                etOtp.requestFocus();
                return;
            }

            if (otp.length() != 6) {
                etOtp.setError("Mã OTP có 6 chữ số");
                etOtp.requestFocus();
                return;
            }

            viewModel.verifyOTP(email, otp).observe(this, resource ->{
                if (resource == null) return;
                switch (resource.status) {
                    case SUCCESS:
                        hideLoading();
                        Toast.makeText(this, "Xác thực thành công", Toast.LENGTH_SHORT).show();
                        otpDialog.dismiss();
                        showResetPasswordDialog(resource.data.getTempToken());
                        break;
                    case ERROR:
                        hideLoading();
                        String errorMessage = resource.message != null ? resource.message : "Xác thực OTP thất bại. Vui lòng thử lại.";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        showLoading();
                        break;
                }
            });
        });

        otpDialog.show();
        Window window = otpDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showResetPasswordDialog(String tempToken) {
        Dialog resetDialog = new Dialog(this);
        resetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resetDialog.setContentView(R.layout.dialog_reset_password);

        TextInputEditText etNewPassword = resetDialog.findViewById(R.id.newPassword);
        TextInputEditText etConfirmPassword = resetDialog.findViewById(R.id.confirmPassword);
        Button btnSubmitNewPassword = resetDialog.findViewById(R.id.btnSubmitNewPassword);

        btnSubmitNewPassword.setOnClickListener(v -> {
            String newPassword = Objects.requireNonNull(etNewPassword.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

            if (newPassword.isEmpty() || newPassword.length() < 6) {
                etNewPassword.setError("Mật khẩu phải từ 6 ký tự");
                etNewPassword.requestFocus();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                etConfirmPassword.requestFocus();
                return;
            }

            viewModel.resetPassword(tempToken, newPassword, confirmPassword).observe(this, resource ->{
                if (resource == null) return;

                switch (resource.status) {
                    case SUCCESS:
                        hideLoading();
                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        resetDialog.dismiss();
                        break;
                    case ERROR:
                        hideLoading();
                        String errorMessage = resource.message != null ? resource.message : "Đổi mật khẩu thất bại. Vui lòng thử lại.";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        showLoading();
                        break;
                }
            });
        });

        resetDialog.show();
        Window window = resetDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void navigateToHome(View view) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    private void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
}