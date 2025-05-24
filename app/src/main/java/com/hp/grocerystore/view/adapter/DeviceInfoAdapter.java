package com.hp.grocerystore.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.grocerystore.R;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.user.DeviceInfoResponse;
import com.hp.grocerystore.utils.AuthPreferenceManager;
import com.hp.grocerystore.utils.Extensions;

import java.util.List;

public class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoAdapter.DeviceViewHolder> {
    private Context context;
    private List<DeviceInfoResponse> devices;
    private int layout;

    public DeviceInfoAdapter(Context context, List<DeviceInfoResponse> devices) {
        this.context = context;
        this.devices = devices;
        this.layout = R.layout.device_item;
    }

    public DeviceInfoAdapter(Context context, int layout, List<DeviceInfoResponse> devices) {
        this.context = context;
        this.layout = layout;
        this.devices = devices;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceInfoResponse deviceInfoResponse = devices.get(position);
        holder.bind(deviceInfoResponse);
    }

    @Override
    public int getItemCount() {
        return devices != null ? devices.size() : 0;
    }

    public void updateData(List<DeviceInfoResponse> newDevices) {
        this.devices = newDevices;
        notifyDataSetChanged();
    }

    public void addDevice(DeviceInfoResponse device) {
        if (devices != null) {
            devices.add(device);
            notifyItemInserted(devices.size() - 1);
        }
    }

    public void clearData() {
        if (devices != null) {
            int size = devices.size();
            devices.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDeviceName;
        private TextView txtDeviceTime;
        private com.google.android.material.chip.Chip chipDeviceCurrent;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDeviceName = itemView.findViewById(R.id.device_name);
            txtDeviceTime = itemView.findViewById(R.id.device_time);
            chipDeviceCurrent = itemView.findViewById(R.id.device_current);
        }

        public void bind(DeviceInfoResponse deviceInfoResponse) {
            txtDeviceName.setText(deviceInfoResponse.getDeviceInfo());
            txtDeviceTime.setText(Extensions.showPrettyTime(deviceInfoResponse.getLoginTime()));

            if (deviceInfoResponse.getCalledDevice()) {
                chipDeviceCurrent.setVisibility(View.VISIBLE);
            } else {
                chipDeviceCurrent.setVisibility(View.GONE);
            }
        }
    }
}