package com.hp.grocerystore.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.utils.LoadingUtil;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.utils.UserSession;
import com.hp.grocerystore.viewmodel.PersonalViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonalActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_IMAGE = 1001;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;
    private ImageView profileImage;
    private ImageButton backButton;
    private EditText nameEdit, emailEdit, phoneEdit, addressEdit;
    private PersonalViewModel viewModel;
    private Button submitButton;
    private FrameLayout loadingOverlay;
    private ProgressBar progressBar;

    // Lưu trữ thông tin ban đầu để so sánh
    private String originalName = "";
    private String originalEmail = "";
    private String originalPhone = "";
    private String originalAddress = "";
    private String originalAvatarUrl = "";
    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.personal_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModel();
        loadUserData();
        setupImagePicker();
        setupClickListeners();
        setupTextWatchers();

        // Ẩn nút submit ban đầu
        submitButton.setVisibility(View.GONE);
    }

    private void initViews() {
        loadingOverlay = findViewById(R.id.loading_overlay);
        progressBar = findViewById(R.id.progress_bar);
        profileImage = findViewById(R.id.profileImage);
        nameEdit = findViewById(R.id.name);
        emailEdit = findViewById(R.id.email);
        phoneEdit = findViewById(R.id.phoneNumber);
        addressEdit = findViewById(R.id.address);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PersonalViewModel.class);
    }

    private void loadUserData() {
        User user = UserSession.getInstance().getUser();
        if (user != null) {
            originalName = user.getName() != null ? user.getName() : "";
            originalEmail = user.getEmail() != null ? user.getEmail() : "";
            originalPhone = user.getPhone() != null ? user.getPhone() : "";
            originalAddress = user.getAddress() != null ? user.getAddress() : "";
            originalAvatarUrl = user.getAvatarUrl() != null ? user.getAvatarUrl() : "";

            nameEdit.setText(originalName);
            emailEdit.setText(originalEmail);
            phoneEdit.setText(originalPhone);
            addressEdit.setText(originalAddress);

            if (!originalAvatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(originalAvatarUrl)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .circleCrop()
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_user);
            }
        }
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imageChanged = true;
                        Glide.with(this)
                                .load(selectedImageUri)
                                .circleCrop()
                                .into(profileImage);
                        checkForChanges();
                    }
                });
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> checkAndPickImage());
        submitButton.setOnClickListener(this::handleSubmit);
        backButton.setOnClickListener(v -> finish());
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkForChanges();
            }
        };

        nameEdit.addTextChangedListener(textWatcher);
        phoneEdit.addTextChangedListener(textWatcher);
        addressEdit.addTextChangedListener(textWatcher);
    }

    private void checkForChanges() {
        String currentName = nameEdit.getText().toString().trim();
        String currentPhone = phoneEdit.getText().toString().trim();
        String currentAddress = addressEdit.getText().toString().trim();

        boolean hasChanges = imageChanged ||
                !currentName.equals(originalName) ||
                !currentPhone.equals(originalPhone) ||
                !currentAddress.equals(originalAddress);

        if (hasChanges) {
            submitButton.setVisibility(View.VISIBLE);
        } else {
            submitButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryToPickImage();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkAndPickImage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_READ_IMAGE);
                return;
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_IMAGE);
                return;
            }
        }
        openGalleryToPickImage();
    }

    private void openGalleryToPickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void handleSubmit(View view) {
        String name = nameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();
        String address = addressEdit.getText().toString().trim();
        User user = UserSession.getInstance().getUser();

        if (user == null) return;

        String namePattern = "^[\\p{L} ]+$";
        if (name.isEmpty() || !name.matches(namePattern)) {
            Toast.makeText(this, "Tên chỉ được chứa chữ và khoảng trắng", Toast.LENGTH_SHORT).show();
            return;
        }

        String phonePattern = "^0\\d{9}$";
        if (!phone.isEmpty() && !phone.matches(phonePattern)) {
            Toast.makeText(this, "Số điện thoại phải gồm 10 số và bắt đầu bằng 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null && imageChanged) {
            File file;
            try {
                file = createTempFileFromUri(selectedImageUri);
                if (file == null) {
                    return;
                }

                Log.d("UploadFileSize", "File size: " + file.length());
                if (file.length() > 5 * 1024 * 1024) {
                    Toast.makeText(this, "Ảnh quá lớn. Vui lòng chọn ảnh dưới 5MB.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                viewModel.uploadFile(filePart).observe(this, resource -> {
                    if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                        String avatarUrl = resource.data.getFileName();
                        updateUser(name, phone, address, avatarUrl);
                    } else if (resource.status == Resource.Status.ERROR) {
                        LoadingUtil.hideLoading(loadingOverlay, progressBar);
                        Toast.makeText(this, "Có lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                Toast.makeText(this, "Không thể đọc file ảnh. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        } else {
            String avatarUrl = user.getAvatarUrl();
            updateUser(name, phone, address, avatarUrl);
        }
    }

    private void updateUser(String name, String phone, String address, String avatarUrl) {
        viewModel.updateUser(name, phone, address, avatarUrl).observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    LoadingUtil.showLoading(loadingOverlay, progressBar);
                    break;

                case SUCCESS:
                    UserSession.getInstance().setUser(resource.data);
                    LoadingUtil.hideLoading(loadingOverlay, progressBar);
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại thông tin gốc sau khi cập nhật thành công
                    updateOriginalData();

                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;

                case ERROR:
                    LoadingUtil.hideLoading(loadingOverlay, progressBar);
                    Toast.makeText(this, "Có lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void updateOriginalData() {
        User user = UserSession.getInstance().getUser();
        if (user != null) {
            originalName = user.getName() != null ? user.getName() : "";
            originalPhone = user.getPhone() != null ? user.getPhone() : "";
            originalAddress = user.getAddress() != null ? user.getAddress() : "";
            originalAvatarUrl = user.getAvatarUrl() != null ? user.getAvatarUrl() : "";
            imageChanged = false;
            submitButton.setVisibility(View.GONE);
        }
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        String mimeType = getContentResolver().getType(uri);
        Log.d("FileType", "MIME type: " + mimeType);

        if (!isValidImageMimeType(mimeType)) {
            String fileName = getFileName(uri);
            String fileExtension = getFileExtensionFromFileName(fileName);

            Log.d("FileType", "File name: " + fileName + ", Extension: " + fileExtension);

            if (!isValidImageExtension(fileExtension)) {
                Toast.makeText(this, "Tệp không hợp lệ. Vui lòng chọn ảnh có định dạng PNG, JPEG hoặc JPG.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            Toast.makeText(this, "Không thể đọc file ảnh.", Toast.LENGTH_SHORT).show();
            return null;
        }

        String extension = getAppropriateExtension(mimeType, uri);
        File tempFile = File.createTempFile("upload_", extension, getCacheDir());

        OutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        return tempFile;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getPath();
            if (fileName != null) {
                int cut = fileName.lastIndexOf('/');
                if (cut != -1) {
                    fileName = fileName.substring(cut + 1);
                }
            }
        }
        return fileName;
    }

    private String getFileExtensionFromFileName(String fileName) {
        if (fileName != null && fileName.lastIndexOf('.') != -1) {
            return fileName.substring(fileName.lastIndexOf('.'));
        }
        return "";
    }

    private String getAppropriateExtension(String mimeType, Uri uri) {
        if (mimeType != null) {
            switch (mimeType) {
                case "image/png":
                    return ".png";
                case "image/jpeg":
                case "image/jpg":
                    return ".jpg";
                default:
                    break;
            }
        }

        String fileName = getFileName(uri);
        String extension = getFileExtensionFromFileName(fileName);
        if (isValidImageExtension(extension)) {
            return extension;
        }

        return ".jpg";
    }

    private boolean isValidImageMimeType(String mimeType) {
        return mimeType != null && (
                mimeType.equals("image/png") ||
                        mimeType.equals("image/jpeg") ||
                        mimeType.equals("image/jpg")
        );
    }

    private boolean isValidImageExtension(String extension) {
        return extension != null && (
                extension.equalsIgnoreCase(".png") ||
                        extension.equalsIgnoreCase(".jpeg") ||
                        extension.equalsIgnoreCase(".jpg")
        );
    }
}