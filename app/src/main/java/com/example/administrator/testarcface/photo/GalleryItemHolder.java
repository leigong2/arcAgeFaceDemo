package com.example.administrator.testarcface.photo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.testarcface.R;

/**
 * Created by 王志龙 on 2018/11/9 009.
 */
public class GalleryItemHolder extends RecyclerView.ViewHolder{

    private TextView title;
    private String mCurData;

    public GalleryItemHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(mCurData);
                }
            }
        });
    }

    public void setValues(String content) {
        mCurData = content;
        title.setText(content);
    }

    public interface OnItemClickListener{
        void onItemClick(String content);
    }
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
