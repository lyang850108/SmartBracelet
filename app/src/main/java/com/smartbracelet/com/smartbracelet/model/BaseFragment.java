package com.smartbracelet.com.smartbracelet.model;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.smartbracelet.com.smartbracelet.R;


/**
 * Created by Yangli on 16-03-22.
 */
public class BaseFragment extends Fragment {

    protected Activity mActivity;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean handleBackKey() {
        return false;
    }


    public void onFabClicked() {

    }

    public void onRefresh() {
        // implement by child who wants handle refresh.
    }
    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }
    public void setSwipeEnable(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }
}
