package com.smartbracelet.com.smartbracelet.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.activity.MainActivity;
import com.smartbracelet.com.smartbracelet.activity.PeripheralActivity;
import com.smartbracelet.com.smartbracelet.adapter.DeviceListAdapter;
import com.smartbracelet.com.smartbracelet.model.BaseFragment;
import com.smartbracelet.com.smartbracelet.util.BleWrapper;
import com.smartbracelet.com.smartbracelet.util.BleWrapperUiCallbacks;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;
import com.smartbracelet.com.smartbracelet.util.ToastHelper;
import com.smartbracelet.com.smartbracelet.view.AlertDialogCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlueToothFragment extends BaseFragment implements ConstDefine{

    @Bind(R.id.search_bt_button)
    Button mSearchButton;

    @Bind(R.id.stop_search_bt_button)
    Button mStopSearchButton;


    @Bind(R.id.device_list_id)
    ListView mDeviceList;

    @Bind(R.id.uuid_text_bt)
    EditText mUuidET;

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

    //Add Demo begin
    private static final long SCANNING_TIMEOUT = 5 * 1000; /* 5 seconds */
    private static final int ENABLE_BT_REQUEST_ID = 1;

    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private DeviceListAdapter mDevicesListAdapter = null;
    private BleWrapper mBleWrapper = null;
    //Add end

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

                    break;

                case MSG_CHA_READ:

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
        //Add demo begin
        // create BleWrapper with empty callback object except uiDeficeFound function (we need only that here)
        mBleWrapper = new BleWrapper(mContext, new BleWrapperUiCallbacks.Null() {
            @Override
            public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
                handleFoundDevice(device, rssi, record);
            }
        });

        // check if we have BT and BLE on board
        if(mBleWrapper.checkBleHardwareAvailable() == false) {
            bleMissing();
        }
        //Add end

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // search Time out
   /* private void addScanningTimeout() {
        if (null != mBTHandler) {
            Message msg = new Message();
            msg.what = MSG_SEARCH_OUT;
            mBTHandler.sendMessageDelayed(msg, 1000);
        }
    }*/


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
        //initBt();
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


    @OnClick(R.id.search_bt_button)
    void onSearchButtonClick (View view) {
        //Scan devices
        //scan(view);
        //Add demo begin
        mScanning = true;
        mBleWrapper.startScanning();
        //Add demo end
    }

    @OnClick(R.id.stop_search_bt_button)
    void onStopSearchButtonClick (View view) {
        //scanLeDevice(false);
        //Add demo begin
        mScanning = false;
        mBleWrapper.stopScanning();
        //Add demo end
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if device supports bluetooth LE
        /*if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastHelper.showAlert(mContext, "BLE Not Supported");
            //finish();
        }*/
        //checkBTAvail();

        //Add demo begin
        // on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
        if(mBleWrapper.isBtEnabled() == false) {
            // BT is not turned on - ask user to make it enabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            // see onActivityResult to check what is the status of our request
        }

        // initialize BleWrapper object
        mBleWrapper.initialize();

        mDevicesListAdapter = new DeviceListAdapter(mContext);
        mDeviceList.setAdapter(mDevicesListAdapter);
        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = mDevicesListAdapter.getDevice(position);
                if (device == null) return;

                final Intent intent = new Intent(mContext, PeripheralActivity.class);
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_RSSI, mDevicesListAdapter.getRssi(position));

                if (mScanning) {
                    mScanning = false;
                    mBleWrapper.stopScanning();
                }

                startActivity(intent);
            }
        });


        // Automatically start scanning for devices
        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();
        //Add demo end


    }

    private void initBt() {

        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mFilters = new ArrayList<ScanFilter>();

    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }*/

        //Add demo begin
        mScanning = false;
        mBleWrapper.stopScanning();
        mDevicesListAdapter.clearList();
        //Add demo end
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
        public void buttonTrue(String value) {

        }

        @Override
        public void buttonCancel() {
            if (!isEnabled) {
                ToastHelper.showAlert(mContext, getString(R.string.boolth_eable_tip));
                //finish();
            }
        }
    };

    //Add demo begin
    /* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device,
                                   final int rssi,
                                   final byte[] scanRecord)
    {
        // adding to the UI have to happen in UI thread
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(device.getName())) {
                    mDevicesListAdapter.addDevice(device, rssi, scanRecord);
                    mDevicesListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void btDisabled() {
        ToastHelper.showAlert(mContext, "Sorry, BT has to be turned ON for us to work!");
        mContext.finish();
    }

    private void bleMissing() {
        ToastHelper.showAlert(mContext, "BLE Hardware is required but not available!");
        mContext.finish();
    }

    /* make sure that potential scanning will take no longer
	 * than <SCANNING_TIMEOUT> seconds from now on */
    private void addScanningTimeout() {
        Runnable timeout = new Runnable() {
            @Override
            public void run() {
                if(mBleWrapper == null) return;
                mScanning = false;
                mBleWrapper.stopScanning();
            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
    }
    //Add demo end

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
