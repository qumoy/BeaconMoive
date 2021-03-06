package com.beacon.moive.Activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beacon.moive.Adapters.AdminScanRecyclerViewAdapter;
import com.beacon.moive.Beans.BeaconDevice;
import com.beacon.moive.R;
import com.beacon.moive.Utils.ToastUtil;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AdminBeaconScanActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = AdminBeaconScanActivity.class.getSimpleName();
    protected static final String BEACON_MINOR = "Beacon_minor";
    private static final int BLE_REQUEST_CODE = 2;
    private static final long SCAN_PERIOD = Integer.MAX_VALUE;
    @BindView(R.id.rc_admin_beacon_scan)
    RecyclerView mRcBleScan;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler handler = new Handler();
    private AdminScanRecyclerViewAdapter mBleRecyclerViewAdapter;
    private List<BeaconDevice> mBleDeviceInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_beacon_scan);
        ButterKnife.bind(this);
        /* 显示App icon左侧的back键 */
        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        toolbar.setTitle("基站管理");
        setSupportActionBar(toolbar);
        //侧边栏监听响应
        DrawerLayout drawer = findViewById(R.id.admin_drawer_layout);
        NavigationView navigationView = findViewById(R.id.admin_nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //初始化view
        initViews();
        //蓝牙管理，这是系统服务可以通过getSystemService(BLUETOOTH_SERVICE)的方法获取实例
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        //通过蓝牙管理实例获取适配器，然后通过扫描方法（scan）获取设备(device)
        assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BLE_REQUEST_CODE);
        } else {
            //开启扫描
            startScan();
        }
    }

    /**
     * 初始化RecyclerView
     */
    @SuppressLint("WrongConstant")
    private void initViews() {
        mBleRecyclerViewAdapter = new AdminScanRecyclerViewAdapter(AdminBeaconScanActivity.this);
        mBleRecyclerViewAdapter.setBleDeviceList(mBleDeviceInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcBleScan.setLayoutManager(linearLayoutManager);
        mRcBleScan.setAdapter(mBleRecyclerViewAdapter);
        mBleRecyclerViewAdapter.setOnItemClickListener((view, BeaconDevice) -> {
            Intent intent = new Intent(AdminBeaconScanActivity.this, AdminBeaconDetailActivity.class);
            intent.putExtra(BEACON_MINOR, BeaconDevice.getMinor());
            startActivity(intent);
        });
    }

    /**
     * ActionBar中Scan和Stop的切换
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    /**
     * Scan和Stop的点击响应
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                startScan();
                Log.e(TAG, "onOptionsItemSelected scanLeDevice is true!!!");
                break;
            case R.id.menu_stop:
                stopScan();
                Log.e(TAG, "onOptionsItemSelected scanLeDevice is false!!!");
                break;
        }
        return true;
    }

    /**
     * 开启蓝牙扫描
     */
    private void startScan() {
        mBleDeviceInfoList.clear();
        scanLeDevice(true);
        notifyDataSetChanged();
    }

    /**
     * 停止蓝牙扫描
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopScan() {
        mScanning = false;
        scanLeDevice(false);
    }

    /**
     * 刷新RecyclerView中的列表数据
     */
    private void notifyDataSetChanged() {
        if (mBleRecyclerViewAdapter == null) {
            mBleRecyclerViewAdapter = new AdminScanRecyclerViewAdapter(AdminBeaconScanActivity.this);
            mBleRecyclerViewAdapter.setBleDeviceList(mBleDeviceInfoList);
            mRcBleScan.setAdapter(mBleRecyclerViewAdapter);
        }
        mBleRecyclerViewAdapter.setBleDeviceList(mBleDeviceInfoList);
        mBleRecyclerViewAdapter.notifyDataSetChanged();
    }

    private Runnable startScanRunnable = () -> {
        mScanning = false;
        stopScan();
    };

    /**
     * 开启/关闭蓝牙扫描
     *
     * @param enable true开启扫描 false关闭扫描
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(startScanRunnable, SCAN_PERIOD);

            mScanning = true;
            //指定需要识别到的蓝牙设备
            List<ScanFilter> scanFilterList = new ArrayList<>();
            ScanFilter.Builder builder = new ScanFilter.Builder();
            builder.setDeviceName("CKS_BLE(B615383F97D8)");
            ScanFilter scanFilter = builder.build();
            scanFilterList.add(scanFilter);
//
//            //指定蓝牙的方式，这里设置的ScanSettings.SCAN_MODE_LOW_LATENCY是比较高频率的扫描方式
            ScanSettings.Builder settingBuilder = new ScanSettings.Builder();
            settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
            settingBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
            settingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
            settingBuilder.setLegacy(true);//false10秒一次 true大约20s一次
            ScanSettings settings = settingBuilder.build();
            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilterList, settings, new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.e(TAG, "onScanResult: " + result.getDevice().getName());
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    for (ScanResult scanResult : results) {
                        Log.e(TAG, "onBatchScanResults: " + scanResult.getDevice().getName());
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e(TAG, "onScanFailed: " + errorCode);
                }
            });
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    /**
     * 蓝牙扫描回调
     */
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            //不能做耗时操作，特别是周围设备多的时候
            AdminBeaconScanActivity.this.runOnUiThread(() -> {
                if (!AdminBeaconScanActivity.this.deviceInfoExists(device.getAddress())) {
                    if (device.getName() != null) {
                        BeaconDevice beaconDeviceInfo = new BeaconDevice(device, rssi, scanRecord);
                        Log.e(TAG, "beaconDeviceInfo: " + beaconDeviceInfo.getName());
                        mBleDeviceInfoList.add(beaconDeviceInfo);
                        AdminBeaconScanActivity.this.runOnUiThread(AdminBeaconScanActivity.this::notifyDataSetChanged);
                    }
                } else {
//                 Already in list, update RSSI info
                    BeaconDevice deviceInfo = AdminBeaconScanActivity.this.findDeviceInfo(device);
                    assert deviceInfo != null;
                    deviceInfo.updateParameters(rssi, device.getName(), scanRecord);
                    AdminBeaconScanActivity.this.notifyDataSetChanged();
                }
            });
        }
    };


    /**
     * 从现有集合中获取设备
     */
    private BeaconDevice findDeviceInfo(BluetoothDevice device) {
        for (int i = 0; i < mBleDeviceInfoList.size(); i++) {
            if (mBleDeviceInfoList.get(i).getBluetoothDevice().getAddress()
                    .equals(device.getAddress())) {
                return mBleDeviceInfoList.get(i);
            }
        }
        return null;
    }

    /**
     * 筛选重复的设备地址
     */
    private boolean deviceInfoExists(String address) {
        for (int i = 0; i < mBleDeviceInfoList.size(); i++) {
            if (mBleDeviceInfoList.get(i).getBluetoothDevice().getAddress()
                    .equals(address)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLE_REQUEST_CODE && resultCode == RESULT_OK) {
            //第一次安装，蓝牙启动后开启服务
            ToastUtil.showToast(AdminBeaconScanActivity.this, "蓝牙已开启");
        } else {
            ToastUtil.showToast(AdminBeaconScanActivity.this, "蓝牙未开启");
        }
    }

    /**
     * 侧边栏回调响应
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.login) {
            // 登录操作
            startActivity(new Intent(AdminBeaconScanActivity.this, LoginActivity.class));
        }
        if (id == R.id.back) {
            // Handle the camera action
            finish();
        }
        //关闭Drawer
        DrawerLayout drawer = findViewById(R.id.admin_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
