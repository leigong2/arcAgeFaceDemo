package com.example.administrator.testarcface.photo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.administrator.testarcface.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;

/**
 * Created by 王志龙 on 2018/11/9 009.
 */
public class GalleryPop extends PopupWindow{

    private Context mContext;
    private View rootView;
    private RecyclerView content;
    private final List<String> dataList;
    private final RecyclerView.Adapter adapter;

    public GalleryPop(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.pop_gallery, null);
        content = rootView.findViewById(R.id.content);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(0x34000000);
        this.setBackgroundDrawable(dw);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (Build.VERSION.SDK_INT >= M) {
            setClippingEnabled(false);
        }
        dataList = new ArrayList<>();
        content.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, viewGroup, false);
                return new GalleryItemHolder(inflate);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ((GalleryItemHolder) viewHolder).setValues(dataList.get(i));
                ((GalleryItemHolder) viewHolder).setOnItemClickListener(new GalleryItemHolder.OnItemClickListener() {
                    @Override
                    public void onItemClick(String content) {
                        confirm(content);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }
        };
        content.setAdapter(adapter);
    }

    private void confirm(String content) {
        dismiss();
        if (myClickListener != null) {
            myClickListener.onClickListener(content);
        }
    }

    public void setValues(HashMap<String, List<MediaBean>> allPhotosTemp) {
        dataList.clear();
        Set<String> strings = allPhotosTemp.keySet();
        dataList.addAll(strings);
        adapter.notifyDataSetChanged();
    }

    public interface MyClickListener {
        void onClickListener(String content);
    }

    private MyClickListener myClickListener;

    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void show(View view, int gravity, int xoff, int yoff) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if(Build.VERSION.SDK_INT == N) {
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    int h = view.getResources().getDisplayMetrics().heightPixels - rect.bottom;
                    setHeight(h);
                }
                showAsDropDown(view, xoff, yoff, gravity);
            } else {
                showAtLocation(view, Gravity.BOTTOM, 0, 50);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
