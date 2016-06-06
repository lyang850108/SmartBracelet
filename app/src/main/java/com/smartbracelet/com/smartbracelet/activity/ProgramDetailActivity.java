package com.smartbracelet.com.smartbracelet.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.util.LiteOrmDBUtil;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Yangli on 16-04-31.
 * 服务器推送消息详情页面
 */
public class ProgramDetailActivity extends AppCompatActivity {

    @Bind(R.id.detail_title_id)
    TextView mDetailTitleTx;

    @Bind(R.id.detail_body_id)
    TextView mDetailBodyTx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail);
        ButterKnife.bind(this);
        setTitle("消息详情");
        setupActionBar();

        Intent intent = getIntent();
        String title = intent.getStringExtra("title"); // 接收Intent的数据
        String body = intent.getStringExtra("body"); // 接收Intent的数据
        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(body)) {

            mDetailTitleTx.setText(title);
            mDetailBodyTx.setText(body);

        }
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
            startActivity(new Intent(this, ProgramItemActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
