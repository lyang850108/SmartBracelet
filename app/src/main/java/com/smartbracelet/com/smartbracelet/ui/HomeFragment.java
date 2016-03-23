package com.smartbracelet.com.smartbracelet.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.BaseFragment;
import com.smartbracelet.com.smartbracelet.service.LocationService;
import com.smartbracelet.com.smartbracelet.util.LogUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends BaseFragment {


    private Context mContext;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LocationService locationService;
    private OnFragmentInteractionListener mFragmentListener;

    @Bind(R.id.button)
    Button getLocButton;

    @Bind(R.id.latitude)
    TextView postionlatitude;

    @Bind(R.id.longtitude)
    TextView postionlongtitude;

    Handler homeHandler = new Handler();

    public Handler mHomeHandler = new Handler(){

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
        };
    };


    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(Context context, LocationService ls) {
        mContext = context;
        locationService = ls;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mFragmentListener != null) {
            mFragmentListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mFragmentListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @OnClick(R.id.button)
    void onButtonClick (View view) {
        if (null != locationService) {
            locationService.start();
        }
    }
}
