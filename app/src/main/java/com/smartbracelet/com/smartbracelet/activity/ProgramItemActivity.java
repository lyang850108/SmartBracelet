package com.smartbracelet.com.smartbracelet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.adapter.ProgramItemAdapter;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProgramItemActivity extends AppCompatActivity {
    private View mView;

    @Bind(R.id.recycler_view_blog)
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_item);
        setTitle("服务器推送消息");

        ButterKnife.bind(this);
        initView();
        setupActionBar();
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private LinearLayoutManager mLinearLayoutManager;
    private ProgramItemAdapter mProgramItemAdapter;

    private void initView() {

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProgramItemAdapter = new ProgramItemAdapter(this.getLayoutInflater());
        mProgramItemAdapter.setOnItemClickListener(new ProgramItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int postion) {
                LogUtil.d("onItemClicked" + postion);
                onShareButtonClick(postion);

            }
        });
        mRecyclerView.setAdapter(mProgramItemAdapter);

        //if do nothing recyllerview wil refresh everywhere
        //mRecyclerView.addOnScrollListener(mRecylerViewScrollListener);
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

    public void onShareButtonClick(int pos) {

        // 生成一个Intent对象
        Intent intent = new Intent();
        if (pos > -1 && null != mProgramItemAdapter) {
            ProgramItemAdapter.BlogItem tempItem = mProgramItemAdapter.mData.get(pos);
            String tempTitle = tempItem.mTitle;
            String tempBody = tempItem.mDescrip;
            intent.putExtra("title", tempTitle);   // 传递数据
            intent.putExtra("body", tempBody);   // 传递数据
            intent.setClass(this, ProgramDetailActivity.class); // 指定跳向哪一个Activity(第二个参数)
            //Activity_02.this.startActivity(intent);
            startActivity(intent);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
