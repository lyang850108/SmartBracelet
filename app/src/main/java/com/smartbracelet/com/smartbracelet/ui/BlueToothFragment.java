package com.smartbracelet.com.smartbracelet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.BaseFragment;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;
import com.smartbracelet.com.smartbracelet.util.ToastHelper;
import com.smartbracelet.com.smartbracelet.view.AlertDialogCreator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlueToothFragment extends BaseFragment {

    @Bind(R.id.search_bt_button)
    Button mSearchButton;

    @Bind(R.id.stop_search_bt_button)
    Button mStopSearchButton;

    @Bind(R.id.connect_bt_button)
    Button mConnectButton;

    @Bind(R.id.unconnect_bt_button)
    Button mUnconnectButton;

    @Bind(R.id.device_list_id)
    ListView mListDevices;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View mView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private final int MSG_SEARCH_OUT = 0;
    private final int MSG_SERCH_DONE = 1;

    private OnFragmentInteractionListener mListener;

    private Activity mContext;

    public BlueToothHandler mBTHandler;

    private List<String> deviceNameList =  new ArrayList<String>();

    private SharedPreferencesHelper sharedPreferencesHelper;

    private ArrayAdapter<String> arrayAdapter;

    private final UUID MY_UUID = UUID.randomUUID();

    private final String NAME = "BlueTooth";

    private BluetoothSocket bluetoothSocket;

    // 手机蓝牙地址(第一次获取到的)
    String SP_PHONE_ADDRESS = "init_phone_address";

    // Bluetooth
    public static BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBtAdapter;

    private BluetoothDevice bluetoothDevice;

    // 搜索重试次数
    private int retySearchCount = 0;
    // 连接重试三次
    private int retyContactCount = 3;

    public BlueToothFragment() {
    }

    public BlueToothFragment(MainActivity mainActivity) {
        // Required empty public constructor
        mContext = mainActivity;
    }

    private class BlueToothHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_OUT:

                    break;
                case MSG_SERCH_DONE:

                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance();

    }

    @Override
    public void onStart() {
        startScanningBT();
        super.onStart();
    }

    // search Time out
    private void addScanningTimeout() {
        if (null != mBTHandler) {
            Message msg = new Message();
            msg.what = MSG_SEARCH_OUT;
            mBTHandler.sendMessageDelayed(msg, 1000);
        }
    }

    private void startScanningBT() {
        /*addScanningTimeout();

        ToastHelper.showAlert(mContext, getString(R.string.new_guid_searching));

        if (null != mBtAdapter && null != mLeScanCallback) {
            deviceNameList.clear();
            mBtAdapter.startLeScan(mLeScanCallback);
        }*/
    }

    // stops current scanning
    public void stopScanningBT() {
        if (null != mBtAdapter && null != mLeScanCallback) {
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // ble search callback
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {

            //if (BluetoothUtil.checkAutoContactDeviceFilter(device)) {

            LogUtil.d( "new device name : " + device.getAddress());

                // 防止同一个address多次链接
                if (!deviceNameList.contains(device.getAddress())) {

                    int connectionState = mBluetoothManager.getConnectionState(
                            device, BluetoothProfile.GATT);

                    if (connectionState == BluetoothProfile.STATE_DISCONNECTED) {
                        // 添加
                        if (rssi > -55) {
                            if (null != mBTHandler)
                                mBTHandler.sendEmptyMessage(1007);

                            // 足够近情况下才做自动连接
                            mBTHandler.sendMessageDelayed(
                                    mBTHandler.obtainMessage(1006,
                                            device.getAddress()), 2000);
                            deviceNameList.add(device.getAddress());
                            retySearchCount = 0;
                        }
                    }
                }
            }
        //}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBt();
        checkBTAvail();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    @OnClick(R.id.connect_bt_button)
    void onConnectButtonClick (View view) {

    }

    @OnClick(R.id.unconnect_bt_button)
    void onUnconnectButtonClick (View view) {

    }

    @OnClick(R.id.search_bt_button)
    void onSearchButtonClick (View view) {

    }

    @OnClick(R.id.stop_search_bt_button)
    void onStopSearchButtonClick (View view) {

    }

    private void initBt() {
        mBluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBluetoothManager.getAdapter();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device:pairedDevices) {
                deviceNameList.add(device.getName() + ": " + device.getAddress());
            }
        }

        arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, deviceNameList);
        mListDevices.setAdapter(arrayAdapter);
        mListDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = arrayAdapter.getItem(position);
                String address = s.substring(s.indexOf(":") + 1).trim();

                if (mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }

                if (null == bluetoothDevice) {
                    bluetoothDevice = mBtAdapter.getRemoteDevice(address);
                }
                if (null == bluetoothSocket) {
                    try {
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                        if(null != bluetoothSocket) {
                            bluetoothSocket.connect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

        });

    }

    private AlertDialog mAlertDialog;
    private void checkBTAvail() {
        if (null == mBtAdapter) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (!mBtAdapter.enable()) {
            try {
                if (null != mContext) {
                    if (null != mAlertDialog) {
                        mAlertDialog.dismiss();
                        mAlertDialog = null;
                    }
                    AlertDialogCreator.getInstance().setmButtonOnClickListener(mDialogListener);
                    mAlertDialog = AlertDialogCreator
                            .getInstance()
                            .createAlertDialog(
                                    mContext,
                                    getString(R.string.tip_title),
                                    getString(R.string.boolth_tip_content));
                    mAlertDialog.show();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private boolean isEnabled = false;
    private AlertDialogCreator.ButtonOnClickListener mDialogListener = new AlertDialogCreator.ButtonOnClickListener() {
        @Override
        public void buttonTrue() {
            // check if bluetooth is supported
            if (!mContext.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(mContext, R.string.ble_not_supported,
                        Toast.LENGTH_SHORT).show();
                //finish();
            }
            if (!mBtAdapter.isEnabled()) {
                mBtAdapter.enable();
                // 只保留最原始的蓝牙地址

                if (TextUtils.isEmpty(sharedPreferencesHelper.getString(SP_PHONE_ADDRESS))) {
                    sharedPreferencesHelper.putString(SP_PHONE_ADDRESS, mBtAdapter.getAddress());
                }

                ToastHelper.showAlert(mContext, getString(R.string.boolth_eable_tip));
                isEnabled = true;
            }
        }

        @Override
        public void buttonTrue(int ring_dis) {

        }

        @Override
        public void buttonTrue(String valuekey, String name) {

        }

        @Override
        public void buttonCancel() {
            if (!isEnabled) {
                ToastHelper.showAlert(mContext, getString(R.string.boolth_eable_tip));
                //finish();
            }
        }
    };
}
