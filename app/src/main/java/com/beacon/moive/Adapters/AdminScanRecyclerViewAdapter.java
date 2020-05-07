package com.beacon.moive.Adapters;

import android.annotation.SuppressLint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beacon.moive.Beans.BeaconDevice;
import com.beacon.moive.Beans.MoiveBean;
import com.beacon.moive.Dbs.MoiveDbUtil;
import com.beacon.moive.R;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author Qumoy
 * Create Date 2020/2/3
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class AdminScanRecyclerViewAdapter extends RecyclerView.Adapter<AdminScanRecyclerViewAdapter.BleViewHolder> {

    private final MoiveDbUtil moiveDbUtil;

    public void setBleDeviceList(List<BeaconDevice> mBleDeviceList) {
        //对数组进行排序根据rssi值
        Collections.sort(mBleDeviceList);
        this.mBleDeviceList = mBleDeviceList;
    }
    private List<BeaconDevice> mBleDeviceList;

    public AdminScanRecyclerViewAdapter(Context context) {
        moiveDbUtil = new MoiveDbUtil(context);
    }

    @NonNull
    @Override
    public BleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_device, viewGroup, false);
        return new BleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BleViewHolder viewHolder, int i) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        viewHolder.mTvName.setText("设备名称：" + mBleDeviceList.get(i).getBluetoothDevice().getName());
        viewHolder.mTvRssi.setText(String.valueOf(mBleDeviceList.get(i).getRssi()));
        viewHolder.mTvMinor.setText("设备Minor：" + mBleDeviceList.get(i).getMinor());
        viewHolder.mTvDistance.setText("设备距离：" + decimalFormat.format(mBleDeviceList.get(i).getmDistance()) + "m");
        viewHolder.mLayout.setOnClickListener(new MyOnClickListener(mBleDeviceList.get(i)));
        MoiveBean moiveBean = moiveDbUtil.queryMoiveDb(mBleDeviceList.get(i).getMinor());
        if (moiveBean != null) {
            viewHolder.mTvIsDb.setText("海报录入情况：已录入");
        } else {
            viewHolder.mTvIsDb.setText("海报录入情况：未录入");
        }
    }

    @Override
    public int getItemCount() {
        return mBleDeviceList.size();
    }

    class BleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView mIvRssi;
        @BindView(R.id.tv_rssi)
        TextView mTvRssi;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_isDb)
        TextView mTvIsDb;
        @BindView(R.id.tv_minor)
        TextView mTvMinor;
        @BindView(R.id.tv_distance)
        TextView mTvDistance;
        @BindView(R.id.layout)
        LinearLayout mLayout;

        BleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private class MyOnClickListener implements View.OnClickListener {

        private BeaconDevice mBeaconDevice;

        public MyOnClickListener(BeaconDevice BeaconDevice) {
            this.mBeaconDevice = BeaconDevice;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.OnItemClick(v, mBeaconDevice);
        }
    }

    public void setOnItemClickListener(AdminScanRecyclerViewAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private onItemClickListener onItemClickListener;

    public interface onItemClickListener {
        void OnItemClick(View view, BeaconDevice BeaconDevice);
    }
}
