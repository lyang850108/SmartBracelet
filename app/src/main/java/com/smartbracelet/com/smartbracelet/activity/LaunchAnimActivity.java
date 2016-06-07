package com.smartbracelet.com.smartbracelet.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.BaseActivity;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;
import com.smartbracelet.com.smartbracelet.util.Utils;
import com.smartbracelet.com.smartbracelet.view.AlertDialogCreator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yangli on 16-05-31.
 * 应用第一次启动页面
 * 后续可增加使用详情简介的Activity
 */
public class LaunchAnimActivity extends BaseActivity

{
    @Bind(R.id.ic_launch_title)
    ImageView imageView;

    @Bind(R.id.tx_launch_title)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_anim);
        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //Logo的旋转动画
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.luancher_img_repeat);
        imageView.setAnimation(operatingAnim);
        operatingAnim.start();

        TranslateAnimation alphaAnimation2 = new TranslateAnimation(0, 0, 0,
                -70);
        alphaAnimation2.setDuration(800);
        alphaAnimation2.setRepeatCount(3);
        alphaAnimation2.setRepeatMode(Animation.REVERSE);
        textView.setAnimation(alphaAnimation2);
        alphaAnimation2.start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance();

                if (null != sharedPreferencesHelper) {
                    if (TextUtils.isEmpty(Utils.getTelNum(sharedPreferencesHelper))) {
                        Intent intent = new Intent(LaunchAnimActivity.this, InputTelNumActivity.class);
                        startActivity(intent);
                        LaunchAnimActivity.this.finish();
                    } else {
                        Intent intent = new Intent(LaunchAnimActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                        LaunchAnimActivity.this.finish();
                    }

                }


            }
        }, 6000);

    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

}
