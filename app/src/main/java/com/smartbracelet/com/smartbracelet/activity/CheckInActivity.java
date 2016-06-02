package com.smartbracelet.com.smartbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.smartbracelet.com.smartbracelet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CheckInActivity extends AppCompatActivity {
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

        setupActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(pThis);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainMenuActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
