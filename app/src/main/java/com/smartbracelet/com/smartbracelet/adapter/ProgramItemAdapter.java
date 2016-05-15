package com.smartbracelet.com.smartbracelet.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.util.LiteOrmDBUtil;
import com.smartbracelet.com.smartbracelet.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 16/5/14.
 */
public class ProgramItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final LayoutInflater mLayoutInflater;

    public interface OnItemClickListener {
        void onItemClicked(View view, int postion);
    }

    public List<BlogItem> mData;


    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public ProgramItemAdapter(LayoutInflater layoutInflater) {
        mData = new ArrayList<BlogItem>();
        mLayoutInflater = layoutInflater;
        List<ProgramItem> list = new ArrayList<ProgramItem>();
        list = LiteOrmDBUtil.getQueryAll(ProgramItem.class);
        LogUtil.d("list" + list.size());
        for (int i = 0; i < list.size(); i++) {

            LogUtil.d("BlogAdapter" + list.get(i).title);
            ProgramItem programItem = list.get(i);
            mData.add(new BlogItem(programItem.title, programItem.body, programItem.timeStamp));
        }

        //For test
        /*for (int i = 0; i < 5; i++) {

            mData.add(new BlogItem("Title" + i, "Bldy" + i, "Time" + i));
        }*/
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return new MyViewHolder(
                mLayoutInflater.inflate(R.layout.program_item_view, parent, false), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myHolder = (MyViewHolder) holder;
        myHolder.bindTo(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTv;
        private TextView mDescripTv;
        private TextView mRecvTimeTv;
        BlogItem mBoundItem;
        private int mPostion;
        private OnItemClickListener mOnItemClickListener;

        public MyViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            mTitleTv = (TextView) itemView.findViewById(R.id.tx_title_blog);
            mDescripTv = (TextView) itemView.findViewById(R.id.tx_descrip_blog);
            mRecvTimeTv = (TextView) itemView.findViewById(R.id.tx_recv_time);
            mOnItemClickListener = listener;

            itemView.setOnClickListener(this);
        }

        public void bindTo(BlogItem item, int postion) {
            mBoundItem = item;
            mPostion = postion;
            mTitleTv.setText(item.mTitle);
            mDescripTv.setText(item.mDescrip);
            mRecvTimeTv.setText(item.mTime);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClicked(view, mPostion);
        }

    }

    public static class BlogItem {
        public String mTitle;
        public String mDescrip;
        public String mTime;
        private static int idCounter = 0;

        public BlogItem(String title, String descrip, String time) {
            mTitle = title;
            mDescrip = descrip;
            mTime = time;
        }
    }


}



