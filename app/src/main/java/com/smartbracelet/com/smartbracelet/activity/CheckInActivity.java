package com.smartbracelet.com.smartbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yangli on 16-06-03.
 * 打卡页面
 */
public class CheckInActivity extends BaseActivity {
    private Activity pThis;

    @Bind(R.id.check_announce_id)
    ImageButton mAnnounceBt;

    @Bind(R.id.check_talk_id)
    ImageButton mTalkBt;

    @Bind(R.id.check_study_id)
    ImageButton mStudyBt;

    @Bind(R.id.check_labor_id)
    ImageButton mLaborBt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        pThis = this;
        ButterKnife.bind(pThis);
        setTitle(getString(R.string.main_menu_clockin));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(pThis);
    }

}
