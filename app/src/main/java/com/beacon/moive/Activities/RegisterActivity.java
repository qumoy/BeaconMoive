package com.beacon.moive.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.beacon.moive.Dbs.MoiveDbUtil;
import com.beacon.moive.R;
import com.beacon.moive.Utils.DataTimeUtil;
import com.beacon.moive.Utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.ed_new_user)
    EditText mEdNewUser;
    @BindView(R.id.ed_new_password)
    EditText mEdNewPassword;
    @BindView(R.id.ed_new_password_again)
    EditText mEdNewPasswordAgain;
    @BindView(R.id.btn_register)
    Button mBtnRegister;
    private MoiveDbUtil moiveDbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //设置title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("用户注册");
        actionBar.setDisplayHomeAsUpEnabled(true);
        //初始化数据库
        moiveDbUtil = new MoiveDbUtil(RegisterActivity.this);
    }

    @OnClick(R.id.btn_register)
    public void onViewClicked() {
        //用户名密码有为输入的情况
        if (TextUtils.isEmpty(mEdNewUser.getText().toString()) || TextUtils.isEmpty(mEdNewPassword.getText().toString()) || TextUtils.isEmpty(mEdNewPasswordAgain.getText().toString())) {
            ToastUtil.showToast(RegisterActivity.this, "请输入用户名和密码");
        }
        //用户名和密码都不为空的情况
        if (!TextUtils.isEmpty(mEdNewUser.getText().toString()) && !TextUtils.isEmpty(mEdNewPassword.getText().toString()) && !TextUtils.isEmpty(mEdNewPasswordAgain.getText().toString())) {
            //两次输入的密码不同
            if (!TextUtils.equals(mEdNewPassword.getText().toString(), mEdNewPasswordAgain.getText().toString())) {
                ToastUtil.showToast(RegisterActivity.this, "两次输入的密码不同");
            } else {
                if (mEdNewPassword.getText().toString().length() < 6) {
                    ToastUtil.showToast(RegisterActivity.this, "密码不能小于6位");
                } else {
                    moiveDbUtil.insertUserDb(mEdNewUser.getText().toString(), mEdNewPassword.getText().toString(), DataTimeUtil.getCurrentDataTime(), DataTimeUtil.getCurrentDataTime(), 1);
                }
            }
        }
    }

    /**
     * ActionBar回退响应
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
