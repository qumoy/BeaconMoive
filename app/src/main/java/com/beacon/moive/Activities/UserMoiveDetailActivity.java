package com.beacon.moive.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.beacon.moive.Beans.MoiveBean;
import com.beacon.moive.Dbs.MoiveDbUtil;
import com.beacon.moive.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.beacon.moive.Activities.AdminBeaconScanActivity.BEACON_MINOR;

public class UserMoiveDetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_moive_detail_pic)
    ImageView mIvMoiveDetailPic;
    @BindView(R.id.tv_moive_detail_name)
    TextView mTvMoiveDetailName;
    @BindView(R.id.tv_moive_detail_time)
    TextView mTvMoiveDetailTime;
    @BindView(R.id.tv_moive_detail_actor)
    TextView mTvMoiveDetailActor;
    @BindView(R.id.tv_moive_detail_type)
    TextView mTvMoiveDetailType;
    @BindView(R.id.tv_moive_detail_description)
    TextView mTvMoiveDetailDescription;
    private int mBeaconMinor;
    private MoiveDbUtil mMoiveDbUtil;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_moive_detail);
        ButterKnife.bind(this);
        /* 显示App icon左侧的back键 */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("电影详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        //获取设备minor
        mBeaconMinor = getIntent().getIntExtra(BEACON_MINOR, 0);
        //实例化数据库工具类
        mMoiveDbUtil = new MoiveDbUtil(UserMoiveDetailActivity.this);
        //查询数据库，显示该设备对应的信息
        MoiveBean moiveBean = mMoiveDbUtil.queryMoiveDb(mBeaconMinor);
        if (moiveBean != null) {
            mTvMoiveDetailName.setText("电影名称: "+moiveBean.getMoiveName());
            mTvMoiveDetailTime.setText("上映时间: "+moiveBean.getMoiveTime());
            mTvMoiveDetailActor.setText("演员名称: "+moiveBean.getMoiveActor());
            mTvMoiveDetailType.setText("电影类型: "+moiveBean.getMoiveType());
            mTvMoiveDetailDescription.setText("电影详情: "+moiveBean.getMoiveDescription());
            File file = new File(moiveBean.getMoivePic());
            Bitmap bm = BitmapFactory.decodeFile(Uri.fromFile(file).getPath());
            mIvMoiveDetailPic.setImageBitmap(bm);
        }
    }

    @OnClick(R.id.iv_moive_detail_pic)
    public void onViewClicked() {
        Intent intent = new Intent(UserMoiveDetailActivity.this, UserPostActivity.class);
        intent.putExtra(BEACON_MINOR,mBeaconMinor);
        startActivity(intent);
    }
    /**
     * ActionBar回退响应*/
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
