package com.smartbracelet.com.smartbracelet.adapter;

import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.util.LogUtil;


import java.util.List;

/**
 * Created by Yangli on 16-03-24.
 * HomeFragment的数据适配器
 * 暂时不用
 */
public class HomeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_NORMAL = 0;
    private Fragment mFragment;
    List<ProgramItem> mData;
    final LayoutInflater mLayoutInflater;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClicked(View view, int postion);
    }
    public interface OnScrollListener {
        void onScrollToEnd();
    }

    public HomeListAdapter(Fragment fragment, LayoutInflater layoutInflater, List<ProgramItem> datas) {
        mFragment = fragment;
        mData = datas;
        mLayoutInflater = layoutInflater;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }


    @Override
    public int getItemViewType(int position) {

            return ITEM_TYPE_NORMAL;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LogUtil.d("onCreateViewHolder:" + viewType);

        return new MyViewHolder(mItemClickListener,
                    mLayoutInflater.inflate(R.layout.home_item_view, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LogUtil.d("onBindViewHolder:" + position);
        if (position > 0) {

            MyViewHolder myHolder = (MyViewHolder)holder;
            ProgramItem program = mData.get(position - 1);
            myHolder.bindTo(program, position);
            // TODO study about the cache strategy about Glide.
            /*Glide.with(mFragment)
                    .load(program.image)
                    .centerCrop()
                    .into(myHolder.mImage);*/
        }
    }

    @Override
    public int getItemCount() {

        return mData.size();
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mDeviceId;
        private TextView mLocation;
        private TextView mTime;
        public ImageView mImage;
        private int mPostion;
        private OnItemClickListener mOnItemClickListener;
        private ProgramItem mBoundItem;

        public MyViewHolder(OnItemClickListener listener, View itemView) {
            super(itemView);
            mOnItemClickListener = listener;
            mDeviceId = (TextView) itemView.findViewById(R.id.device_id);

            itemView.setOnClickListener(this);
        }

        public void bindTo(ProgramItem item, int postion) {
            mBoundItem = item;
            mPostion = postion;
            mDeviceId.setText("1111");
            //mDeviceId.setText(item.id);
            //mImage.setImageDrawable(item.mPicture);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClicked(view, mPostion);
        }


    }


}
