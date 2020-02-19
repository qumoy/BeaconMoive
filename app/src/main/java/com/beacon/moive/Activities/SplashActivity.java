package com.beacon.moive.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.beacon.moive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splash_btn_skip)
    Button mSplashBtnSkip;
    private Handler mHandler = new Handler();
    private Runnable mLoginRunnable = this::jumpLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        //延时3秒跳转
        mHandler.postDelayed(mLoginRunnable, 3 * 1000);
    }

    /**
     * 跳转登录界面
     */
    private void jumpLogin() {
        startActivity(new Intent(SplashActivity.this, UserMoiveBriefActivity.class));
    }


    @OnClick(R.id.splash_btn_skip)
    public void onViewClicked() {
        //点击跳转，先移除runnable，然后直接跳转
        mHandler.removeCallbacks(mLoginRunnable);
        jumpLogin();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
