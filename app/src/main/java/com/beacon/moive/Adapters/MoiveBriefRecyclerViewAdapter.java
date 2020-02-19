package com.beacon.moive.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.beacon.moive.R;

import java.io.File;
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
public class MoiveBriefRecyclerViewAdapter extends RecyclerView.Adapter<MoiveBriefRecyclerViewAdapter.MoiveViewHolder> {

    public void setMoiveList(List<MoiveBean> mMoiveList) {
        this.mMoiveList = mMoiveList;
    }

    private List<MoiveBean> mMoiveList;

    public MoiveBriefRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public MoiveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_moive_brief, viewGroup, false);
        MoiveViewHolder bleViewHolder = new MoiveViewHolder(view);
        return bleViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MoiveViewHolder viewHolder, int i) {
        viewHolder.mTvName.setText(mMoiveList.get(i).getMoiveName());
        viewHolder.mTvActor.setText("演员：" + mMoiveList.get(i).getMoiveActor());
        viewHolder.mTvType.setText("类型：" + mMoiveList.get(i).getMoiveType());
        File file = new File(mMoiveList.get(i).getMoivePic());
        Bitmap bm = BitmapFactory.decodeFile(Uri.fromFile(file).getPath());
        viewHolder.mIvPic.setImageBitmap(bm);
        viewHolder.mLayout.setOnClickListener(new MyOnClickListener(mMoiveList.get(i)));
    }

    @Override
    public int getItemCount() {
        return mMoiveList.size();
    }

    class MoiveViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_moive_brief_image)
        ImageView mIvPic;
        @BindView(R.id.tv_moive_brief_name)
        TextView mTvName;
        @BindView(R.id.tv_moive_brief_actor)
        TextView mTvActor;
        @BindView(R.id.tv_moive_brief_type)
        TextView mTvType;
        @BindView(R.id.ll_moive_brief)
        LinearLayout mLayout;

        MoiveViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private class MyOnClickListener implements View.OnClickListener {

        private MoiveBean mMoiveBean;

        public MyOnClickListener(MoiveBean MoiveBean) {
            this.mMoiveBean = MoiveBean;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.OnItemClick(v, mMoiveBean);
        }
    }

    public void setOnItemClickListener(MoiveBriefRecyclerViewAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private onItemClickListener onItemClickListener;

    public interface onItemClickListener {
        void OnItemClick(View view, MoiveBean MoiveBean);
    }
}
