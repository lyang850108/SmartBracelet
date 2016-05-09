package com.smartbracelet.com.smartbracelet.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbracelet.com.smartbracelet.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProgramItemActivity extends AppCompatActivity {
    private View mView;

    @Bind(R.id.recycler_view_blog)
    RecyclerView mRecyclerView;

    ImageView mTitleImage;


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
    private BlogAdapter mBlogAdapter;
    private void initView() {

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBlogAdapter = new BlogAdapter(this.getLayoutInflater());
        mRecyclerView.setAdapter(mBlogAdapter);

        //if do nothing recyllerview wil refresh everywhere
        //mRecyclerView.addOnScrollListener(mRecylerViewScrollListener);
    }

    private class BlogAdapter extends RecyclerView.Adapter<MyViewHolder> {
        ArrayList<BlogItem> mData;
        final LayoutInflater mLayoutInflater;
        public BlogAdapter(LayoutInflater layoutInflater) {
            mData = new ArrayList<BlogItem>();
            mLayoutInflater = layoutInflater;
            for (int i = 0; i < 20; i++) {
                Drawable d;

                d = getResources().getDrawable(R.mipmap.blog_im1);
                mData.add(new BlogItem("服务器推送消息Title " + i, "Number" + i + "\n" +"正文内容\n" +
                        "Whitsunday lsland,Whitsunday lslands ", d));
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            return new MyViewHolder(
                    mLayoutInflater.inflate(R.layout.program_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTv;
        private TextView mDescripTv;
        BlogItem mBoundItem;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTitleTv = (TextView) itemView.findViewById(R.id.tx_title_blog);
            mDescripTv = (TextView) itemView.findViewById(R.id.tx_descrip_blog);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ToDo
                }
            });
        }

        public void bindTo(BlogItem item) {
            mBoundItem = item;
            mTitleTv.setText(item.mTitle);
            mDescripTv.setText(item.mDescrip);
        }

    }

    private static class BlogItem {
        String mTitle;
        String mDescrip;
        Drawable mPicture;
        private static int idCounter = 0;

        public BlogItem(String title, String descrip, Drawable d) {
            mTitle = title;
            mDescrip = descrip;
            mPicture = d;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, TestFlowActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
