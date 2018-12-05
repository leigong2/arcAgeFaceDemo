package com.example.administrator.testarcface.photo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.testarcface.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 王志龙 on 2018/11/8 008.
 */
public class GallaryActivity extends Activity {

    private RecyclerView photos;
    private RecyclerView.Adapter photoAdapter;
    private List<MediaBean> mediaBeen;
    private TextView choice;
    private HashMap<String, List<MediaBean>> allPhotosTemp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int requestCode = getIntent().getIntExtra("Key1", 0);
        setContentView(R.layout.activity_gallary);
        photos = findViewById(R.id.photo_wall);
        choice = findViewById(R.id.choice);
        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoicePop();
            }
        });
        photos.setLayoutManager(new GridLayoutManager(this, 3
                , LinearLayoutManager.VERTICAL, false));
        photos.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 3;
                outRect.right = 3;
                outRect.top = 3;
                outRect.bottom = 3;
            }
        });
        mediaBeen = new ArrayList<>();
        photoAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View inflate = LayoutInflater.from(GallaryActivity.this).inflate(R.layout.item_photo, viewGroup, false);
                return new PhotoHolder(inflate);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder instanceof PhotoHolder) {
                    ((PhotoHolder)viewHolder).setValues(mediaBeen.get(i));
                    ((PhotoHolder)viewHolder).setOnItemClickListener(new PhotoHolder.OnItemClickListener() {
                        @Override
                        public void onItemClickListener(MediaBean mediaBean) {
                            if (requestCode > 0) {
                                Intent intent = getIntent();
                                intent.putExtra("Key1", mediaBean);
                                setResult(RESULT_OK, intent);
                                onBackPressed();
                            } else {
                                Intent intent = new Intent(GallaryActivity.this, ImageActivity.class);
                                intent.putExtra("Media", mediaBean);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }

            @Override
            public int getItemCount() {
                return mediaBeen.size();
            }
        };
        photos.setAdapter(photoAdapter);
        getAllPhotoInfo();
    }

    private void showChoicePop() {
        GalleryPop pop = new GalleryPop(this);
        pop.setValues(allPhotosTemp);
        pop.setMyClickListener(new GalleryPop.MyClickListener() {
            @Override
            public void onClickListener(String content) {
                choice.setText(content);
                mediaBeen.clear();
                mediaBeen.addAll(allPhotosTemp.get(content));
                photoAdapter.notifyDataSetChanged();
            }
        });
        pop.show(choice, Gravity.BOTTOM, 0, 0);
    }

    private void getAllPhotoInfo() {
        allPhotosTemp = new HashMap<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projImage = { MediaStore.Images.Media._ID
                        , MediaStore.Images.Media.DATA
                        ,MediaStore.Images.Media.SIZE
                        ,MediaStore.Images.Media.DISPLAY_NAME};
                Cursor mCursor = getContentResolver().query(mImageUri,
                        projImage,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED+" desc");

                if(mCursor!=null){
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE))/1024;
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        MediaBean bean = new MediaBean(path, size, displayName);
                        mediaBeen.add(bean);
                        getFile(allPhotosTemp, bean);
                    }
                    mCursor.close();
                }
                //更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        photoAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void getFile(HashMap<String, List<MediaBean>> allPhotosTemp, MediaBean mediaBean) {
        // 获取该图片的父路径名
        String dirPath = new File(mediaBean.path).getParentFile().getAbsolutePath();
        File rootFile = Environment.getExternalStoragePublicDirectory("");
        String absolutePath = rootFile.getAbsolutePath();
        dirPath = dirPath.replace(absolutePath, "");
        //存储对应关系
        if (allPhotosTemp.containsKey(dirPath)) {
            List<MediaBean> data = allPhotosTemp.get(dirPath);
            data.add(mediaBean);
        } else {
            List<MediaBean> data = new ArrayList<>();
            data.add(mediaBean);
            allPhotosTemp.put(dirPath,data);
        }
    }
}
