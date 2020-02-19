package com.beacon.moive.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beacon.moive.Adapters.UserPostRecyclerViewAdapter;
import com.beacon.moive.Beans.MoiveBean;
import com.beacon.moive.Dbs.MoiveDbUtil;
import com.beacon.moive.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.beacon.moive.Activities.AdminBeaconScanActivity.BEACON_MINOR;

public class UserPostActivity extends AppCompatActivity {

    @BindView(R.id.rc_user_post)
    RecyclerView mRcUserPost;
    private List<String> postList = new ArrayList<>();
    private int mBeaconMinor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);
        ButterKnife.bind(this);
        /* 显示App icon左侧的back键 */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("海报展示");
        actionBar.setDisplayHomeAsUpEnabled(true);
        //获取设备minor
        mBeaconMinor = getIntent().getIntExtra(BEACON_MINOR, 0);
        //初始化数据
        initData();
        //初始化Recyclerview
        initView();
    }

    private void initData() {
        MoiveDbUtil moiveDbUtil = new MoiveDbUtil(UserPostActivity.this);
        MoiveBean moiveBean = moiveDbUtil.queryMoiveDb(mBeaconMinor);
        postList = moiveBean.getMoivePost();

    }

    private void initView() {
        UserPostRecyclerViewAdapter adapter = new UserPostRecyclerViewAdapter();
        if (postList != null) {
            adapter.setPostList(postList);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(UserPostActivity.this, 2);
        mRcUserPost.setLayoutManager(gridLayoutManager);
        mRcUserPost.setAdapter(adapter);
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
