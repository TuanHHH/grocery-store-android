package com.hp.grocerystore.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hp.grocerystore.view.activity.OrderActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.user.DeviceInfoResponse;
import com.hp.grocerystore.model.user.UpdatePasswordRequest;
import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.utils.UserSession;
import com.hp.grocerystore.view.activity.LoginActivity;
import com.hp.grocerystore.view.activity.PersonalActivity;
import com.hp.grocerystore.view.adapter.DeviceInfoAdapter;
import com.hp.grocerystore.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    MaterialButton accountInfoBtn, logoutBtn, changePassBtn, showDeviceBtn, deactiveBtn;
    private TextView userName, userEmail;
    private ImageView profileImage;
    private ProfileViewModel viewModel;
    private List<DeviceInfoResponse> devices = new ArrayList<>();
    private ActivityResultLauncher<Intent> updateUserLauncher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        profileImage = view.findViewById(R.id.profileImage);
        LinearLayout pendingOrder = view.findViewById(R.id.pendingOrder);
        LinearLayout indeliveryOrder = view.findViewById(R.id.indeliveryOrder);
        LinearLayout successOrder = view.findViewById(R.id.successOrder);
        LinearLayout cancelOrder = view.findViewById(R.id.cancelOrder);
        loadUserInfo();

        accountInfoBtn = view.findViewById(R.id.btnAccountInfo);
        accountInfoBtn.setOnClickListener(this::navigateToAccountInfo);
        logoutBtn = view.findViewById(R.id.btnLogout);
        logoutBtn.setOnClickListener(this::logout);
        changePassBtn = view.findViewById(R.id.btnChangePassword);
        changePassBtn.setOnClickListener(this::showChangePasswordDialog);
        showDeviceBtn = view.findViewById(R.id.btnDevices);
        showDeviceBtn.setOnClickListener(this::showDevicesInfo);
        deactiveBtn = view.findViewById(R.id.btnDisableAccount);
        deactiveBtn.setOnClickListener(this::disableAccount);

        View.OnClickListener listener = v -> {
            int orderStatus = 0;
            if (v.getId() == R.id.pendingOrder) orderStatus = 0;
            else if (v.getId() == R.id.indeliveryOrder) orderStatus = 1;
            else if (v.getId() == R.id.successOrder) orderStatus = 2;
            else if (v.getId() == R.id.cancelOrder) orderStatus = 3;

            Intent intent = new Intent(getActivity(), OrderActivity.class);
            intent.putExtra("orderStatus", orderStatus);
            startActivity(intent);
        };

        updateUserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                       loadUserInfo();
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void disableAccount(View view) {
        Toast.makeText(requireContext(), UserSession.getInstance().getUser().getName(), Toast.LENGTH_LONG).show();
    }

    private void showDevicesInfo(View view) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_device_logger_in, null);

        RecyclerView devicesRv = dialogView.findViewById(R.id.device_recycler_view);
        DeviceInfoAdapter adapter = new DeviceInfoAdapter(requireContext(), devices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        devicesRv.setLayoutManager(layoutManager);
        devicesRv.setHasFixedSize(false);
        devicesRv.setAdapter(adapter);

        // Optional
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(devicesRv.getContext(),
                layoutManager.getOrientation());
        devicesRv.addItemDecoration(dividerItemDecoration);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        getDeviceList(adapter);

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            int dialogHeight = (int) (screenHeight * 0.7);

            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = dialogHeight;
            window.setAttributes(layoutParams);
        }
    }

    private void getDeviceList(DeviceInfoAdapter adapter) {
        viewModel.getLoggedInDevices().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.LOADING) {
                Toast.makeText(requireContext(), "Đang lấy thông tin", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.SUCCESS) {
                if (resource.data != null) {
                    devices.clear();
                    devices.addAll(resource.data);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.d("ProfileFragment", "Error message: " + resource.message);
                Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void logout(View view) {
        viewModel.logout().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.LOADING){
                Toast.makeText(requireContext(), "Đang đăng xuất", Toast.LENGTH_SHORT).show();
            }
            else if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadUserInfo() {
        User user = UserSession.getInstance().getUser();

        if (user != null) {
            userName.setText(user.getName());
            userEmail.setText(user.getEmail());

            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                Glide.with(this)
                        .load(user.getAvatarUrl())
                        .placeholder(R.drawable.ic_user)
                        .circleCrop()
                        .into(profileImage);
            }
        }
    }

    public void navigateToAccountInfo(View view) {
        Intent intent = new Intent(getActivity(), PersonalActivity.class);
        startActivity(intent);
    }

    private void showChangePasswordDialog(View view) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

        TextInputEditText etOld = dialogView.findViewById(R.id.etOldPassword);
        TextInputEditText etNew = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirm = dialogView.findViewById(R.id.etConfirmPassword);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirmPassword);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();

        dialog.show();

        btnConfirm.setOnClickListener(v -> {
            String oldPwd = Objects.requireNonNull(etOld.getText()).toString().trim();
            String newPwd = Objects.requireNonNull(etNew.getText()).toString().trim();
            String confirmPwd = Objects.requireNonNull(etConfirm.getText()).toString().trim();

            if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (oldPwd.length() < 6) {
                etOld.setError("Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }

            if (newPwd.length() < 6) {
                etNew.setError("Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }

            if (!newPwd.equals(confirmPwd)) {
                etConfirm.setError("Mật khẩu không khớp");
            }

            viewModel.updatePassword(new UpdatePasswordRequest(oldPwd, newPwd, confirmPwd))
                    .observe(getViewLifecycleOwner(), result -> {
                        if (result.status == Resource.Status.SUCCESS) {
                            Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else if (result.status == Resource.Status.ERROR) {
                            Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

}

