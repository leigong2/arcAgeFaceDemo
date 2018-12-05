package com.example.administrator.testarcface.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;
import com.bumptech.glide.Glide;
import com.example.administrator.testarcface.Application;
import com.example.administrator.testarcface.FaceDB;
import com.example.administrator.testarcface.R;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.java.ExtByteArrayOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王志龙 on 2018/11/8 008.
 */
public class ImageActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();
    ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
    ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();

    ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
    ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();

    List<ASAE_FSDKAge> ages = new ArrayList<>();
    List<ASGE_FSDKGender> genders = new ArrayList<>();

    AFT_FSDKEngine at_engine = new AFT_FSDKEngine();
    AFT_FSDKVersion at_version = new AFT_FSDKVersion();

    AFR_FSDKVersion ar_version = new AFR_FSDKVersion();
    AFR_FSDKEngine ar_engine = new AFR_FSDKEngine();
    AFR_FSDKFace ar_result = new AFR_FSDKFace();

    AFD_FSDKEngine ad_engine = new AFD_FSDKEngine();
    AFD_FSDKVersion ad_version = new AFD_FSDKVersion();
    List<AFD_FSDKFace> ad_result = new ArrayList<AFD_FSDKFace>();
    AFD_FSDKFace mAFD_FSDKFace = null;

    List<FaceDB.FaceRegist> mResgist = (Application.getApp()).mFaceDB.mRegister;
    List<ASAE_FSDKFace> face1 = new ArrayList<>();
    List<ASGE_FSDKFace> face2 = new ArrayList<>();
    byte[] mImageNV21 = null;
    AFT_FSDKFace mAFT_FSDKFace = null;
    List<AFT_FSDKFace> results = new ArrayList<>();
    private ImageView large;
    private int mWidth;
    private int mHeight;
    private int mFormat;
    private TextView mTextView;
    private TextView mTextView1;
    private TextView mTextView2;
    private ImageView ivSmall;
    private AFT_FSDKError error1;
    private ASAE_FSDKError error2;
    private ASGE_FSDKError error3;
    private AFR_FSDKError error4;
    private Bitmap mBitmap;
    private AFD_FSDKError error0;
    private List<AFD_FSDKFace> ad_results = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1001) {
                dispatchUI();
            }
        }
    };
    private MediaBean mMedia;
    private String age;
    private String age2;
    private String gender;
    private String gender2;
    private TextView mTextView11;
    private TextView mTextView12;
    private TextView mTextView13;
    private ImageView ivSmall2;
    private ViewGroup secondPerson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mMedia = (MediaBean) getIntent().getSerializableExtra("Media");
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView1 = (TextView) findViewById(R.id.textView1);
        mTextView2 = (TextView) findViewById(R.id.textView2);
        mTextView11 = (TextView) findViewById(R.id.textView11);
        mTextView12 = (TextView) findViewById(R.id.textView12);
        mTextView13 = (TextView) findViewById(R.id.textView13);
        large = findViewById(R.id.iv_large);
        ivSmall = findViewById(R.id.iv_small);
        ivSmall2 = findViewById(R.id.iv_small2);
        secondPerson = findViewById(R.id.second_person);
        if (mMedia != null && mMedia.path != null) {
            Glide.with(this).load(mMedia.path).into(large);
        }
        large.setImageBitmap(mBitmap);
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (mMedia != null && mMedia.path != null) {
                    mBitmap = Application.decodeImage(mMedia.path);
                    if (mBitmap != null) {
                        dispatchBitmap(mBitmap);
                    }
                }
            }
        }.start();
    }

    private void dispatchBitmap(Bitmap bitmap) {
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
        mFormat = ImageFormat.NV21;
        mImageNV21 = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
        try {
            ImageConverter convert = new ImageConverter();
            convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
            if (convert.convert(mBitmap, mImageNV21)) {
                Log.d(TAG, "convert ok!");
            }
            convert.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();
        load();
    }

    private void dispatchUI() {
        if (ad_results != null && !ad_results.isEmpty()) {
            for (int i = 0; i < ad_results.size(); i++) {
                ad_result.add(ad_results.get(i).clone());
            }
        } else {
            mTextView.setText("未识别");
            mTextView1.setText("未识别");
            mTextView2.setText("未识别");
            return;
        }
        if (mImageNV21 != null) {
            AFR_FSDKMatching score = new AFR_FSDKMatching();

            if (ad_result.size() >= 1) {
                //age & gender

                float max = 0.0f;
                String name = null;
                face1.clear();
                face2.clear();
                face1.add(new ASAE_FSDKFace(ad_result.get(0).getRect(), ad_result.get(0).getDegree()));
                face2.add(new ASGE_FSDKFace(ad_result.get(0).getRect(), ad_result.get(0).getDegree()));

                error2 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, ASAE_FSDKEngine.CP_PAF_NV21, face1, ages);
                error3 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, ASGE_FSDKEngine.CP_PAF_NV21, face2, genders);
                if (ad_result.get(0) != null) {
                    error4 = ar_engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21
                            , ad_result.get(0).getRect(), ad_result.get(0).getDegree(), ar_result);
                }
                for (FaceDB.FaceRegist fr : mResgist) {
                    for (AFR_FSDKFace face : fr.mFaceList.values()) {
                        AFR_FSDKError error = ar_engine.AFR_FSDK_FacePairMatching(ar_result, face, score);
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = fr.mName;
                        }
                    }
                }
                if (!ages.isEmpty()) {
                    age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
                }
                if (!genders.isEmpty()) {
                    gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");
                }

                //crop
                YuvImage yuv = new YuvImage(mImageNV21, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                yuv.compressToJpeg(ad_result.get(0).getRect(), 80, ops);
                Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
                ivSmall.setImageBitmap(bmp);
                try {
                    ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (max > 0.4f) {
                    //fr success.
                    mTextView.setText(name);
                    mTextView1.setText("置信度：" + (float) ((int) (max * 1000)) / 10.0 + "%");
                    mTextView2.setText(age +"  "+ gender);
                } else {
                    final String mNameShow = "未识别";
                    mTextView.setText(mNameShow);
                    mTextView1.setText(gender);
                    mTextView2.setText(age);
                }
            }
            if (ad_result.size() >= 2) {
                secondPerson.setVisibility(View.VISIBLE);
                //age & gender
                float max = 0.0f;
                String name = null;
                face1.clear();
                face2.clear();
                face1.add(new ASAE_FSDKFace(ad_result.get(1).getRect(), ad_result.get(1).getDegree()));
                face2.add(new ASGE_FSDKFace(ad_result.get(1).getRect(), ad_result.get(1).getDegree()));

                error2 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, ASAE_FSDKEngine.CP_PAF_NV21, face1, ages);
                error3 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, ASGE_FSDKEngine.CP_PAF_NV21, face2, genders);
                if (ad_result.get(1) != null) {
                    error4 = ar_engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21
                            , ad_result.get(1).getRect(), ad_result.get(1).getDegree(), ar_result);
                }
                for (FaceDB.FaceRegist fr : mResgist) {
                    for (AFR_FSDKFace face : fr.mFaceList.values()) {
                        AFR_FSDKError error = ar_engine.AFR_FSDK_FacePairMatching(ar_result, face, score);
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = fr.mName;
                        }
                    }
                }
                if (ages.size() > 1) {
                    age2 = ages.get(1).getAge() == 0 ? "年龄未知" : ages.get(1).getAge() + "岁";
                }
                if (genders.size() > 1) {
                    gender2 = genders.get(1).getGender() == -1 ? "性别未知" : (genders.get(1).getGender() == 0 ? "男" : "女");
                }

                //crop
                YuvImage yuv = new YuvImage(mImageNV21, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                yuv.compressToJpeg(ad_result.get(1).getRect(), 80, ops);
                Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
                ivSmall2.setImageBitmap(bmp);
                try {
                    ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (max > 0.4f) {
                    //fr success.
                    mTextView11.setText(name);
                    mTextView12.setText("置信度：" + (float) ((int) (max * 1000)) / 10.0 + "%");
                    mTextView13.setText(age2 +"  "+ gender2);
                } else {
                    String mNameShow = "未识别";
                    mTextView11.setText(mNameShow);
                    mTextView12.setText(gender2);
                    mTextView13.setText(age2);
                }
            }
            mImageNV21 = null;
        }
    }

    private void init() {
        error0 = ad_engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        error0 = ad_engine.AFD_FSDK_GetVersion(ad_version);

        error1 = at_engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        error1 = at_engine.AFT_FSDK_GetVersion(at_version);

        error2 = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
        error2 = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);

        error3 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
        error3 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);

        error4 = ar_engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        error4 = ar_engine.AFR_FSDK_GetVersion(ar_version);
    }

    private void load() {
        error0 = ad_engine.AFD_FSDK_StillImageFaceDetection(mImageNV21, mWidth, mHeight, AFD_FSDKEngine.CP_PAF_NV21, ad_results);
        error1 = at_engine.AFT_FSDK_FaceFeatureDetect(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, results);
        handler.sendEmptyMessage(1001);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        AFT_FSDKError err = at_engine.AFT_FSDK_UninitialFaceEngine();
        ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
        ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
        AFR_FSDKError err4 = ar_engine.AFR_FSDK_UninitialEngine();
    }
}
