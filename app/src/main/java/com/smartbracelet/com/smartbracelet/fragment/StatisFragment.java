package com.smartbracelet.com.smartbracelet.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.BaseFragment;
import com.smartbracelet.com.smartbracelet.service.LocationService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yang on 16/3/17.
 * 第一个测试版本的GPS显示页面
 * 暂时不用,Textview做的很好看
 */
public class StatisFragment extends BaseFragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LocationService locationService;

    @Bind(R.id.button)
    Button getLocButton;

    @Bind(R.id.latitude)
    TextView postionlatitude;

    @Bind(R.id.longtitude)
    TextView postionlongtitude;

    public StatisHandler mStatisHandler;


    public StatisFragment() {
        // Required empty public constructor
    }

    public StatisFragment(LocationService ls) {
        locationService = ls;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatisHandler = new StatisHandler();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statis, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick(R.id.button)
    void onButtonClick (View view) {
        if (null != locationService) {
            locationService.start();
        }
    }

    public class StatisHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String pl = (String)msg.obj;
                    if (null != postionlatitude) {
                        postionlatitude.setText(pl);
                    }
                    break;
                case 2:
                    String ll = (String)msg.obj;
                    if (null != postionlongtitude) {
                        postionlongtitude.setText(ll);
                    }
                    break;
            }
        }
    }
}
