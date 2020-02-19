package com.beacon.moive.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beacon.moive.Adapters.AdminPostRecyclerViewAdapter;
import com.beacon.moive.Adapters.UserPostRecyclerViewAdapter;
import com.beacon.moive.Beans.MoiveBean;
import com.beacon.moive.R;
import com.beacon.moive.Dbs.MoiveDbUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.beacon.moive.Activities.AdminBeaconScanActivity.BEACON_MINOR;

/**
 * Author Qumoy
 * Create Date 2020/2/3
 * Description：set moive infomation
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */

public class AdminBeaconDetailActivity extends AppCompatActivity {

    private static final int REQUEST_MOIVE_ICON = 1;
    private static final int REQUEST_MOIVE_POST = 2;
    @BindView(R.id.iv_pic)
    ImageView mIvPic;
    @BindView(R.id.iv_default)
    ImageView mIvDefault;
    @BindView(R.id.re_pic)
    RelativeLayout mRePic;
    @BindView(R.id.ed_name)
    EditText mEdName;
    @BindView(R.id.ed_time)
    EditText mEdTime;
    @BindView(R.id.ed_actor)
    EditText mEdActor;
    @BindView(R.id.ed_type)
    EditText mEdType;
    @BindView(R.id.ed_description)
    EditText mEdDescription;
    @BindView(R.id.rc_admin_detail_post)
    RecyclerView mRcPost;
    @BindView(R.id.re_post)
    RelativeLayout mPostLayout;
    @BindView(R.id.btn_write_into_db)
    Button mBtnWriteIntoDb;
    @BindView(R.id.btn_update_into_db)
    Button mBtnUpdateIntoDb;
    @BindView(R.id.btn_delete_into_db)
    Button mBtnDeleteIntoDb;
    private MoiveDbUtil mMoiveDbUtil;
    private int mBeaconMinor;
    private String mPiclPath = "";
    private List<String> mPostPathList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_beacon_detail);
        ButterKnife.bind(this);
        /* 显示App icon左侧的back键 */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("海报配置");
        actionBar.setDisplayHomeAsUpEnabled(true);
        //获取设备minor
        mBeaconMinor = getIntent().getIntExtra(BEACON_MINOR, 0);
        //实例化数据库工具类
        mMoiveDbUtil = new MoiveDbUtil(AdminBeaconDetailActivity.this);
        //查询数据库，显示该设备对应的信息
        MoiveBean moiveBean = mMoiveDbUtil.queryMoiveDb(mBeaconMinor);
        if (moiveBean != null) {
            mEdName.setText(moiveBean.getMoiveName());
            mEdTime.setText(moiveBean.getMoiveTime());
            mEdActor.setText(moiveBean.getMoiveActor());
            mEdType.setText(moiveBean.getMoiveType());
            mEdDescription.setText(moiveBean.getMoiveDescription());
            Log.e("test", "moiveBean.getMoivePic(): " + moiveBean.getMoivePic());
            if (!TextUtils.isEmpty(moiveBean.getMoivePic())) {
                File file = new File(moiveBean.getMoivePic());
                Bitmap bm = BitmapFactory.decodeFile(Uri.fromFile(file).getPath());
                mIvPic.setImageBitmap(bm);
            } else {
                mIvPic.setImageResource(R.mipmap.icon_default);
                Log.e("test", "onCreate: ");
            }
            //展示选中的图片
            if (moiveBean.getMoivePost() == null) {
                mIvDefault.setVisibility(View.VISIBLE);
                mRcPost.setVisibility(View.GONE);
            } else {
                mIvDefault.setVisibility(View.GONE);
                mRcPost.setVisibility(View.VISIBLE);
                initPostRc(moiveBean.getMoivePost());
            }
        }else{
            mIvPic.setImageResource(R.mipmap.icon_default);
            mIvDefault.setVisibility(View.VISIBLE);
            mRcPost.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.iv_pic, R.id.re_post, R.id.btn_write_into_db, R.id.btn_update_into_db, R.id.btn_delete_into_db})
    public void onViewClicked(View view) {
        //进入相册选择电影头像
        if (view.getId() == R.id.iv_pic) {
            pickUpMoiveIcon();
        }
        //进入相册选择多组电影海报
        if (view.getId() == R.id.re_post) {
            pickUpMoivePost();
        }
        //录入海报信息
        if (view.getId() == R.id.btn_write_into_db) {
            mMoiveDbUtil.insertMoiveDb(mBeaconMinor, mEdName.getText().toString(), mPiclPath, mEdActor.getText().toString(), mEdTime.getText().toString(), mEdType.getText().toString(), mEdDescription.getText().toString(), mPostPathList);
        }
        //编辑海报信息
        if (view.getId() == R.id.btn_update_into_db) {
            mMoiveDbUtil.updateMoiveDb(mBeaconMinor, mEdName.getText().toString(), mPiclPath, mEdActor.getText().toString(), mEdTime.getText().toString(), mEdType.getText().toString(), mEdDescription.getText().toString(), mPostPathList);
        }
        //删除海报信息
        if (view.getId() == R.id.btn_delete_into_db) {
            mMoiveDbUtil.deleteMoiveDb(mBeaconMinor);
            //删除完信息置空
            mEdName.setText("");
            mEdTime.setText("");
            mEdActor.setText("");
            mEdType.setText("");
            mEdDescription.setText("");
            mIvPic.setImageResource(R.mipmap.icon_default);
            mIvDefault.setVisibility(View.VISIBLE);
            mRcPost.setVisibility(View.GONE);
        }
    }

    /**
     * 打开相册选择多组海报图片
     */
    public void pickUpMoivePost() {
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        startActivityForResult(intent, REQUEST_MOIVE_POST);
    }

    /**
     * 打开相册选择头像
     */
    private void pickUpMoiveIcon() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "图像选择..."), REQUEST_MOIVE_ICON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MOIVE_ICON && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mPiclPath = getRealPath(uri);
            File f = new File(mPiclPath);
            Bitmap bm = BitmapFactory.decodeFile(Uri.fromFile(f).getPath());
            mIvPic.setImageBitmap(bm);
        }
        if (requestCode == REQUEST_MOIVE_POST) {
            try {
                // 获取返回的图片列表(存放的是图片路径)
                List<String> mPostPathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                this.mPostPathList = mPostPathList;
                mIvDefault.setVisibility(View.GONE);
                mRcPost.setVisibility(View.VISIBLE);
                //展示选中的图片
                initPostRc(mPostPathList);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化PostRecyclerView
     */
    private void initPostRc(List<String> list) {
        AdminPostRecyclerViewAdapter adapter = new AdminPostRecyclerViewAdapter();
        adapter.setPostList(list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AdminBeaconDetailActivity.this, 5);
        mRcPost.setLayoutManager(gridLayoutManager);
        mRcPost.setAdapter(adapter);
    }


    /**
     * 获取照片路径，uri转string
     */
    private String getRealPath(Uri uri) {
        String filePath = null;
        //4.4及以上
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                    sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        } else {//4.4以下，即4.4以上获取路径的方法
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(column_index);
        }
        return filePath;
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
