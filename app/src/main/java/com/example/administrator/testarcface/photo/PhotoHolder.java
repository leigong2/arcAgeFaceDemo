package com.example.administrator.testarcface.photo;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.testarcface.Application;
import com.example.administrator.testarcface.R;

/**
 * Created by 王志龙 on 2018/11/8 008.
 */
public class PhotoHolder extends RecyclerView.ViewHolder {

    private ImageView photo;

    public PhotoHolder(View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        photo.getLayoutParams().width = ScreenUtils.getScreenWidth(Application.getApp()) / 3 - 6;
        photo.getLayoutParams().height = ScreenUtils.getScreenWidth(Application.getApp()) / 3 - 6;
    }

    public void setValues(final MediaBean media) {
        Glide.with(photo).load(media.path).into(photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(media);
                }
            }
        });
    }

    public interface OnItemClickListener{
        void onItemClickListener(MediaBean mediaBean);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
