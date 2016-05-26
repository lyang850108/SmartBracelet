package com.smartbracelet.com.smartbracelet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.smartbracelet.com.smartbracelet.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends AppCompatActivity {
    private Activity pThis;

    @Bind(R.id.func1)
    ImageButton imageButton1;

    @Bind(R.id.func2)
    ImageButton imageButton2;

    @Bind(R.id.func3)
    ImageButton imageButton3;

    @Bind(R.id.func4)
    ImageButton imageButton4;

    @Bind(R.id.func5)
    ImageButton imageButton5;

    @Bind(R.id.func6)
    ImageButton imageButton6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        pThis = this;
        ButterKnife.bind(pThis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(pThis);
    }

    @OnClick(R.id.func1)
    public void onFunc1Click (View view) {

    }

    @OnClick(R.id.func2)
    public void onFunc2Click (View view) {
        startActivity(new Intent(this, ProgramItemActivity.class));
    }

    @OnClick(R.id.func3)
    public void onFunc3Click (View view) {

    }

    @OnClick(R.id.func4)
    public void onFunc4Click (View view) {

    }

    @OnClick(R.id.func5)
    public void onFunc5Click (View view) {

    }

    @OnClick(R.id.func6)
    public void onFunc6Click (View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
