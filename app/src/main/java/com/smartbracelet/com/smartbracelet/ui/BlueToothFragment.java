package com.smartbracelet.com.smartbracelet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.adapter.DeviceAdapter;
import com.smartbracelet.com.smartbracelet.model.BaseFragment;
import com.smartbracelet.com.smartbracelet.service.BlueToothLoService;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
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

public class BlueToothFragment extends BaseFragment implements ConstDefine{

    @Bind(R.id.search_bt_button)
    Button mSearchButton;

    @Bind(R.id.stop_search_bt_button)
    Button mStopSearchButton;

    @Bind(R.id.connect_bt_button)
    Button mConnectButton;

    @Bind(R.id.unconnect_bt_button)
    Button mUnconnectButton;

    @Bind(R.id.read_bt_button)
    Button mReadButton;

    @Bind(R.id.get_power_button)
    Button mGetPowerButton;

    @Bind(R.id.device_list_id)
    ListView mDeviceList;

    @Bind(R.id.uuid_text_bt)
    EditText mUuidET;

    @Bind(R.id.content_get_bt)
    EditText mConnectGetET;

    @Bind(R.id.connect_result_details_bt)
    TextView mConncetRrtTx;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View mView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Activity mContext;

    public static Handler mBTHandler;

    private List<String> deviceNameList =  new ArrayList<String>();
    private List<String> deviceAddressList =  new ArrayList<String>();
    private SharedPreferencesHelper sharedPreferencesHelper;

    private ArrayAdapter<String> arrayAdapter;

    private final UUID[] MY_UUID = null;

    private final String NAME = "BlueTooth";

    private BluetoothSocket bluetoothSocket;

    public static BluetoothManager mBluetoothManager;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanSettings mSettings;
    private List<ScanFilter> mFilters;
    private List<BluetoothDevice> mDevices;

    private String bleAddress = null;

    // 手机蓝牙地址(第一次获取到的)
    String SP_PHONE_ADDRESS = "init_phone_address";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    // Bluetooth
    //public static BluetoothManager mBluetoothManager;
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
                    int result = msg.arg1;
                    if (0 == result) {
                        mConncetRrtTx.setText("STATE_CONNECTED");
                    } else {
                        mConncetRrtTx.setText("STATE_DISCONNECTED");
                    }
                    break;

                case MSG_CHA_READ:
                    String readStr = msg.obj.toString();
                    if (!TextUtils.isEmpty(readStr)) {
                        mConnectGetET.setText(readStr);
                    }
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ButterKnife.bind(this, mView);
        mBTHandler = new BlueToothHandler();

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBt();
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
        //Scan devices
        scan(view);
    }

    @OnClick(R.id.get_power_button)
    void onGetPowerButtonClick (View view) {
        if (null != bleAddress && !TextUtils.isEmpty(bleAddress)) {
            startBleService(bleAddress, ACTION_READ_CMD);
        }
    }

    @OnClick(R.id.stop_search_bt_button)
    void onStopSearchButtonClick (View view) {
        scanLeDevice(false);
    }

    @OnClick(R.id.read_bt_button)
    void onReadButtonClick (View view) {

        if (null != bleAddress && !TextUtils.isEmpty(bleAddress)) {
            startBleService(bleAddress, ACTION_READ_CMD);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if device supports bluetooth LE
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastHelper.showAlert(mContext, "BLE Not Supported");
            //finish();
        }

        checkBTAvail();
        // Make sure the users bluetooth is turned on
        /*if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/
    }

    private void initBt() {
        /*mBluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBluetoothManager.getAdapter();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        LogUtil.d("" + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device:pairedDevices) {
                deviceNameList.add(device.getName() + ": " + device.getAddress());
            }
        }

        arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, deviceNameList);
        mDeviceList.setAdapter(arrayAdapter);
        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        if (null != bluetoothSocket) {
                            bluetoothSocket.connect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

        });*/

        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        mFilters = new ArrayList<ScanFilter>();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
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

    private void scanLeDevice(final boolean enable) {
        if (mDevices == null) {
            mDevices = new ArrayList<>();
        } else {
            mDevices.clear();
        }

        /*if (enable) {
            mBTHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);

            mBluetoothLeScanner.startScan(mFilters, mSettings, mScanCallback);
        } else {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }*/

        if (null == mBluetoothAdapter && null == mLeScanCallback) {
            return;
        }

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mBTHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            if (null != mUuidET && !mUuidET.getText().toString().equals("")) {
                try {
                    MY_UUID[0] = UUID.fromString(mUuidET.getText().toString());
                } catch (IllegalArgumentException e) {
                    ToastHelper.showAlert(mContext, "无效的UUID");
                } catch (Exception e) {
                    ToastHelper.showAlert(mContext, "Exception" + e);
                }

            }
            //mBluetoothAdapter.startLeScan(mLeScanCallback);
            deviceAddressList.clear();
            mBluetoothAdapter.startLeScan(MY_UUID, mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice btDevice, int rssi,
                                     byte[] scanRecord) {

                    if (!deviceAddressList.contains(btDevice.getAddress())) {
                        bleAddress = btDevice.getAddress();
                        mDevices.add(btDevice);

                        deviceAddressList.add(bleAddress);
                        deviceNameList.add(btDevice.getName() + ": " +bleAddress);

                        arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, deviceNameList);

                        //DeviceAdapter adapter = new DeviceAdapter(mContext, android.R.layout.simple_list_item_1, mDevices);
                        mDeviceList.setAdapter(arrayAdapter);

                        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //connectDevice(mDevices.get(position));
                                startBleService(bleAddress , ACTION_CONNECTED_CMD);
                            }
                        });
                    }
                }
            };

    private void startBleService(String address, String action) {
        Intent startService = new Intent(mContext, BlueToothLoService.class);

        startService.setAction(action);
        startService.putExtra(BLE_ADDRESS, address);
        mContext.startService(startService);
    }
    /*private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            LogUtil.d("callbackType" + String.valueOf(callbackType));
            LogUtil.d("result" + result.toString());
            BluetoothDevice btDevice = result.getDevice();

            //if (btDevice.getName() != null) {
                mDevices.add(btDevice);


                deviceNameList.add(btDevice.getName() + ": " + btDevice.getAddress());

                arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, deviceNameList);

                //DeviceAdapter adapter = new DeviceAdapter(mContext, android.R.layout.simple_list_item_1, mDevices);
                mDeviceList.setAdapter(arrayAdapter);

                mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        connectDevice(mDevices.get(position));
                    }
                });
            //}
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult s : results) {
                LogUtil.d("ScanResult - Results" + s.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            LogUtil.d("Scan Failed - Error Code:" + errorCode);
        }
    };*/

    public void scan(View view) {
        scanLeDevice(true);
    }
}
