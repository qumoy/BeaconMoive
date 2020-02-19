package com.beacon.moive.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
public class AdminPostRecyclerViewAdapter extends RecyclerView.Adapter<AdminPostRecyclerViewAdapter.PostViewHolder> {
    public void setPostList(List<String> mPostList) {
        this.mPostList = mPostList;
    }

    private List<String> mPostList;

    public AdminPostRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_admin_post, viewGroup, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPostRecyclerViewAdapter.PostViewHolder viewHolder, int i) {
        File file = new File(mPostList.get(i));
        Bitmap bm = BitmapFactory.decodeFile(Uri.fromFile(file).getPath());
        viewHolder.mIvPic.setImageBitmap(bm);
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_admin_post)
        ImageView mIvPic;

        PostViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
