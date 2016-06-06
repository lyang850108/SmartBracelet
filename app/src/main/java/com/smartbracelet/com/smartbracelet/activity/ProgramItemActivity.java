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
import com.smartbracelet.com.smartbracelet.model.BaseActivity;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yangli on 16-04-30.
 * 服务器推送消息页面，展示103协议下发的短消息
 */
public class ProgramItemActivity extends BaseActivity {
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
