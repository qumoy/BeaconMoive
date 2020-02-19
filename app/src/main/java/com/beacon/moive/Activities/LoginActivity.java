package com.beacon.moive.Activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.beacon.moive.Beans.UserBean;
import com.beacon.moive.Dbs.MoiveDbUtil;
import com.beacon.moive.R;
import com.beacon.moive.Utils.DataTimeUtil;
import com.beacon.moive.Utils.DialogUtil;
import com.beacon.moive.Utils.PermissionHelper;
import com.beacon.moive.Utils.PermissionInterface;
import com.beacon.moive.Utils.ToastUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements PermissionInterface {
    //    @BindView(R.id.btn)
//    Button btn;
//    @BindView(R.id.btn_user)
//    Button btnUser;
    @BindView(R.id.login_name)
    EditText mLoginName;
    @BindView(R.id.login_password)
    EditText mLoginPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.btn_register)
    Button mBtnRegister;
    @BindView(R.id.login_frame)
    FrameLayout loginFrame;
    private int requestCode;
    private PermissionHelper mPermissionHelper;
    private MoiveDbUtil moiveDbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //登录界面设置全屏
//        Objects.requireNonNull(getSupportActionBar()).hide();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //权限动态请求
        mPermissionHelper = new PermissionHelper(this, this);
        requestCode = 1;
        mPermissionHelper.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION});
        //初始化数据库
        moiveDbUtil = new MoiveDbUtil(LoginActivity.this);
        //手动添加管理员
        moiveDbUtil.insertUserDb("root", "123456", "", "", 0);
    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //用户名和密码为空时
                if (TextUtils.isEmpty(mLoginName.getText().toString()) || TextUtils.isEmpty(mLoginPassword.getText().toString())) {
                    ToastUtil.showToast(LoginActivity.this, "请输入用户名和密码");
                }
                //用户名密码都不为空时
                if (!TextUtils.isEmpty(mLoginName.getText().toString()) && !TextUtils.isEmpty(mLoginPassword.getText().toString())) {
                    //从数据中查询是否有该用户名
                    UserBean userBean = moiveDbUtil.queryUserDb(mLoginName.getText().toString());
                    if (userBean != null) {
                        //若存在该用户名继续验证密码
                        if (TextUtils.equals(userBean.getPasswod(), mLoginPassword.getText().toString())) {
                            //isUser为0跳转管理员界面，为1跳转用户界面
                            if (userBean.getIsUser() == 0) {
                                startActivity(new Intent(LoginActivity.this, AdminBeaconScanActivity.class));
                            }
                            if (userBean.getIsUser() == 1) {
                                startActivity(new Intent(LoginActivity.this, UserMoiveBriefActivity.class));
                            }
                            moiveDbUtil.updateUserDb(mLoginName.getText().toString(), DataTimeUtil.getCurrentDataTime());
                        } else {
                            ToastUtil.showToast(LoginActivity.this, "密码错误，请重新输入");
                        }
                    } else {
                        //不存在该用户
                        ToastUtil.showToast(LoginActivity.this, "用户名错误，请重新输入");
                    }

                }
                break;
            case R.id.btn_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
//            case R.id.btn:
//                startActivity(new Intent(LoginActivity.this, AdminBeaconScanActivity.class));
//                break;
//            case R.id.btn_user:
//                startActivity(new Intent(LoginActivity.this, UserMoiveBriefActivity.class));
//                break;
        }
    }


    @Override
    public int getPermissionsRequestCode() {
        return requestCode;
    }

    @Override
    public void requestPermissionsSuccess() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void requestPermissionsFail() {
        if (requestCode == 1) {
            //如果拒绝授予权限,且勾选了再也不提醒
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                DialogUtil.showSelectDialog(this, "说明", "需要使用位置权限，进行蓝牙操作", "取消", "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        //用于在用户勾选“不再提示”并且拒绝时，再次提示用户
                        DialogUtil.showSelectDialog(LoginActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                            @Override
                            public void confirm() {
                                goToAppSetting();
                            }

                            @Override
                            public void cancel() {

                            }
                        }).show();
                    }

                    @Override
                    public void cancel() {

                    }

                }).show();
            } else {
                DialogUtil.showSelectDialog(LoginActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        goToAppSetting();
                    }

                    @Override
                    public void cancel() {

                    }

                }).show();
            }
            //如果拒绝授予权限,且勾选了再也不提醒
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                DialogUtil.showSelectDialog(this, "说明", "需要使用位置权限，进行蓝牙操作", "取消", "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        //用于在用户勾选“不再提示”并且拒绝时，再次提示用户
                        DialogUtil.showSelectDialog(LoginActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                            @Override
                            public void confirm() {
                                goToAppSetting();
                            }

                            @Override
                            public void cancel() {

                            }


                        }).show();
                    }

                    @Override
                    public void cancel() {

                    }


                }).show();
            } else {
                DialogUtil.showSelectDialog(LoginActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        goToAppSetting();
                    }

                    @Override
                    public void cancel() {

                    }


                }).show();
            }
            //如果拒绝授予权限,且勾选了再也不提醒
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                DialogUtil.showSelectDialog(this, "说明", "需要使用位置权限，进行蓝牙操作", "取消", "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        //用于在用户勾选“不再提示”并且拒绝时，再次提示用户
                        DialogUtil.showSelectDialog(LoginActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                            @Override
                            public void confirm() {
                                goToAppSetting();
                            }

                            @Override
                            public void cancel() {

                            }


                        }).show();
                    }

                    @Override
                    public void cancel() {

                    }


                }).show();
            } else {
                DialogUtil.showSelectDialog(LoginActivity.this, "写入权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        goToAppSetting();
                    }

                    @Override
                    public void cancel() {

                    }


                }).show();
            }
        }
    }

    /**
     * 打开Setting
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            //Todo show setting success
        }
    }
}
