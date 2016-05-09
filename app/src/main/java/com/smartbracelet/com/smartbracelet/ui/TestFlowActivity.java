package com.smartbracelet.com.smartbracelet.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.adapter.DeviceListAdapter;
import com.smartbracelet.com.smartbracelet.service.LocationService;
import com.smartbracelet.com.smartbracelet.util.BleNamesResolver;
import com.smartbracelet.com.smartbracelet.util.BleWrapper;
import com.smartbracelet.com.smartbracelet.util.BleWrapperUiCallbacks;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.Gps;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.PositionUtil;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;
import com.smartbracelet.com.smartbracelet.util.ToastHelper;
import com.smartbracelet.com.smartbracelet.util.Utils;
import com.smartbracelet.com.smartbracelet.view.AlertDialogCreator;
import com.smartbracelet.com.smartbracelet.view.LoadingDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestFlowActivity extends AppCompatActivity implements ConstDefine {

    /*@Bind(R.id.test_device_list)
    ListView mDeviceList;*/

    @Bind(R.id.test_text_view)
    TextView mTextView;

    @Bind(R.id.test_ca_tx)
    TextView mConnectedAddTx;

    @Bind(R.id.test_bt_tx)
    TextView mBatteryLevelTx;

    @Bind(R.id.test_bt_im)
    ImageView mBatteryLevelIm;

    @Bind(R.id.test_fab)
    android.support.design.widget.FloatingActionButton mFloatingActionBtn;

    private Activity mContext;

    public static Handler mBTHandler;

    private List<String> deviceNameList = new ArrayList<String>();
    private List<String> deviceAddressList = new ArrayList<String>();
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

    private String mBatterymValue = null;

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

    private LocationService locationService;

    private String permissionInfo;
    private final int SDK_PERMISSION_REQUEST = 127;

    private LoadingDialog loadingDialog;
    private String postDetailRTR;

    private static int CURRENT_TYPE_POST = 9;

    private static int retryCnt = 0;

    private class BlueToothTestHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_OUT:
                    break;

                case MSG_SERCH_DONE:
                    LogUtil.d("MSG_SERCH_DONE : " + bleAddress);

                    mPostDataTask = new PostDataTask("120.25.89.222/main.cgi", Utils.bindJOTelTest(bleAddress).toString(), TYPE_GET_NUM_PARM);
                    mPostDataTask.execute(0);
                    loadingDialog.show();

                    break;


                case MSG_CHA_SEND_LOCATION:
                    LogUtil.d("MSG_CHA_SEND_LOCATION");
                    mPostDataTask = new PostDataTask("120.25.89.222/main.cgi", Utils.bindJOGps(latitude, longtitude, bleAddress).toString(), TYPE_UPLOAD_LOCATION);
                    mPostDataTask.execute(0);
                    break;

                case MSG_PUSH_MSG:
                    LogUtil.d("MSG_PUSH_MSG");
                    mPostDataTask = new PostDataTask("120.25.89.222/main.cgi", Utils.bindJOMsgPushTest(bleAddress).toString(), TYPE_PUSH_MSG);
                    mPostDataTask.execute(0);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_flow);


        mContext = this;

        ButterKnife.bind(this);
        mBTHandler = new BlueToothTestHandler();

        mBleWrapper = new BleWrapper(mContext, new BleWrapperUiCallbacks.Null() {
            @Override
            public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
                handleFoundDevice(device, rssi, record);
            }

            @Override
            public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {

                LogUtil.d("uiDeviceConnected");
                handleDeviceConnected(gatt, device);

            }

            @Override
            public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {
                LogUtil.d("uiDeviceDisconnected");
                handleDeviceDisconnected(gatt, device);
            }

            @Override
            public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device, List<BluetoothGattService> services) {
                for (BluetoothGattService service : mBleWrapper.getCachedServices()) {
                    String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
                    String name = BleNamesResolver.resolveServiceName(uuid);
                    String type = (service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY) ? "Primary" : "Secondary";

                    if (service.getUuid().equals(Service.BATTERY_SERVICE) || service.getUuid().equals(Service.UNKNOWN_SERVICE)) {
                        mBleWrapper.getCharacteristicsForService(service);
                    }

                }
            }


            @Override
            public void uiCharacteristicForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, List<BluetoothGattCharacteristic> chars) {
                for(BluetoothGattCharacteristic ch : chars) {
                    String uuid = ch.getUuid().toString().toLowerCase(Locale.getDefault());
                    String name = BleNamesResolver.resolveCharacteristicName(uuid);
                    LogUtil.d("uiCharacteristicForService name " + name);
                    if (ch.getUuid().equals(Characteristic.BATTERY_LEVEL)) {
                        gatt.readCharacteristic(ch);
                    }
                    if (ch.getUuid().equals(Characteristic.CHAR01_LEVEL)) {
                        gatt.readCharacteristic(ch);
                    }
                    if (ch.getUuid().equals(Characteristic.CHAR02_LEVEL)) {
                        gatt.writeCharacteristic(ch);
                    }
                }
            }

            @Override
            public void uiGotNotification(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void uiBatteryValueRead(String value) {
                mBatterymValue = value;
                LogUtil.d("uiBatteryValueRead : " + value);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBatteryLevelTx.setText(mBatterymValue);
                    }
                });

            }

            @Override
            public void uiClickValueRead(final String value) {
                LogUtil.d("uiClickValueRead : " + value);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.append("点击次数" + value);
                    }
                });
            }
        });

        // check if we have BT and BLE on board
        if (mBleWrapper.checkBleHardwareAvailable() == false) {
            bleMissing();
        }
        //Add end

        loadingDialog = new LoadingDialog(mContext);

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
        LogUtil.d("sharedPreferencesHelper" + sharedPreferencesHelper);
        if (null != sharedPreferencesHelper) {
            sharedPreferencesHelper.putInt(SP_POST_INTERNAL, 60000);
        }

        getPersimmions();

        if (null != sharedPreferencesHelper) {
            checkPhomeNumerAvailble(sharedPreferencesHelper);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.menu_program:
                startActivity(new Intent(this, ProgramItemActivity.class));
                break;
        }
        return true;
    }

    private void checkPhomeNumerAvailble(SharedPreferencesHelper sharedPreferencesHelper) {
        if (TextUtils.isEmpty(Utils.getTelNum(sharedPreferencesHelper))) {
            //ToDo
            final EditText input = new EditText(this);
            if (null != mContext) {
                if (null != mAlertDialog) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
                AlertDialogCreator.getInstance().setmButtonOnClickListener(mDialogListener);
                mAlertDialog = AlertDialogCreator
                        .getInstance()
                        .createAlertDialogEdit(
                                mContext,
                                getString(R.string.tip_title),
                                getString(R.string.test_store_num_tip_content), input);
                mAlertDialog.show();
            }
        }
    }

    private void handleDeviceConnected(final BluetoothGatt gatt, final BluetoothDevice device) {
        // 只保留最原始的蓝牙地址

        if (TextUtils.isEmpty(sharedPreferencesHelper.getString(SP_PHONE_ADDRESS))) {
            sharedPreferencesHelper.putString(SP_PHONE_ADDRESS, device.getAddress());
            App.isFirstLaunched = false;
        }

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectedAddTx.setText(device.getName() + "\n" + device.getAddress()+ "\n" + " Connected");

                if (STATE_DEVICE_UNBIND == sharedPreferencesHelper.getInt(SP_BIND_STATE)) {
                    Message message = new Message();
                    message.what = MSG_SERCH_DONE;
                    mBTHandler.sendMessage(message);
                } else if (STATE_DEVICE_BIND == sharedPreferencesHelper.getInt(SP_BIND_STATE)) {
                    mTextView.append("\n 该手环已被绑定过 ");
                    sendGPGTimely();
                }
            }
        });
    }

    private void handleDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectedAddTx.setText(device.getName() + "\n" + device.getAddress()+ "\n" + " Disconnected");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        //Add demo begin
        // on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
        if (mBleWrapper.isBtEnabled() == false) {
            // BT is not turned on - ask user to make it enabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            // see onActivityResult to check what is the status of our request
        }

        // initialize BleWrapper object
        mBleWrapper.initialize();

        mDevicesListAdapter = new DeviceListAdapter(mContext);
        /*mDeviceList.setAdapter(mDevicesListAdapter);
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
        });*/

        //Add demo end
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Add demo begin
        mScanning = false;
        mBleWrapper.stopScanning();
        mDevicesListAdapter.clearList();
        //Add demo end
    }

    //Add demo begin
    /* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device,
                                   final int rssi,
                                   final byte[] scanRecord) {
        // adding to the UI have to happen in UI thread
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*if (!TextUtils.isEmpty(device.getName())) {
                    mDevicesListAdapter.addDevice(device, rssi, scanRecord);
                    mDevicesListAdapter.notifyDataSetChanged();
                }*/
                //For test
                String bindAddress = sharedPreferencesHelper.getString(SP_PHONE_ADDRESS);

                if (bindAddress.equals(device.getAddress()) || App.isFirstLaunched) {
                    if (!TextUtils.isEmpty(device.getName()) && device.getName().startsWith("utt")) {
                        LogUtil.d("existAddress" + bindAddress);
                        String mName = device.getName();
                        String mAddress = device.getAddress();
                        int mRssi = rssi;

                        if (mScanning) {
                            mScanning = false;
                            mBleWrapper.stopScanning();
                        }

                        // start automatically connecting to the device
                        mTextView.setText("connecting ...");
                        bleAddress = mAddress;
                        mBleWrapper.connect(mAddress);
                    }
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
                if (mBleWrapper == null) return;
                mScanning = false;
                mBleWrapper.stopScanning();
            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
    }
    //Add demo end


    @Override
    protected void onStart() {
        super.onStart();
        locationService = ((App) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.test_fab)
    public void onFabClick(View v) {
        // Automatically start scanning for devices
        //Add demo begin
        if (mBleWrapper.isConnected()) {
            mBleWrapper.stopScanning();
            mDevicesListAdapter.clearList();
            //Add demo end

            if (null != App.timerTask) {
                LogUtil.d("stop_post_package");
                App.timerTask.cancel();
            }
        }

        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();

        if (null != locationService) {
            locationService.start();
        }
        mTextView.setText("Utt smart bracelet begin ...");
        //loadingDialog.show();
    }

    @OnClick(R.id.test_bt_im)
    public void onBattButtonClick() {
        LogUtil.d("onBattButtonClick");
        mPostDataTask = new PostDataTask("120.25.89.222/main.cgi", Utils.bindJOWarningTest(bleAddress, 1).toString(), TYPE_WARNING_NOTIFY);
        mPostDataTask.execute(0);
    }

    public static double latitude = 0;
    public static double longtitude = 0;

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sbLongtitude = new StringBuffer(256);
                StringBuffer sbLatitude = new StringBuffer(256);
                //sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                //sb.append(location.getTime());
                latitude = location.getLatitude();
                longtitude = location.getLongitude();
            }
        }

    };

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
    }

    //hTTP

    private String postRTR;

    private void httpPostParams(String url, String params, int mPostType) {
        String httpUrl = url;
        CURRENT_TYPE_POST = mPostType;
        //创建httpRequest对象
        if (null == url) {
            return;
        }


        try {
            LogUtil.e("doInBackground, Post httpUrl:" + httpUrl);
            HttpPost httpRequest = new HttpPost(httpUrl);

            //设置字符集
            //LogUtil.e("post, yangli:" + subitJson);


            StringEntity se = new StringEntity(params, "utf-8");
            //请求httpRequest
            httpRequest.setEntity(se);


            //取得默认的HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            //取得HttpResponse
            LogUtil.e("doInBackground, httpclient.execute");
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            //HttpStatus.SC_OK表示连接成功
            LogUtil.e("post, yangli:" + httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                //取得返回的字符串
                //String strResult = EntityUtils.toString(httpResponse.getEntity());
                postRTR = "请求成功!";
                postDetailRTR = EntityUtils.toString(httpResponse.getEntity());
                //postDetailRTR = httpResponse.getEntity().toString();
            } else {
                postRTR = "请求错误! 错误码" + httpResponse.getStatusLine().getStatusCode();
                LogUtil.e("post, yangli:" + "请求错误");
            }
        } catch (ClientProtocolException e) {
            postRTR = "ClientProtocolException!";
            LogUtil.e("post, yangli:" + "ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            postRTR = "IOException!";
            LogUtil.e("post, yangli:" + "IOException");
            e.printStackTrace();
        } catch (Exception e) {
            postRTR = "Exception!";
            LogUtil.e("post, yangli:" + "Exception");
            e.printStackTrace();
        }

    }

    private PostDataTask mPostDataTask;
    private AlertDialog mAlertDialog;

    private class PostDataTask extends AsyncTask<Integer, Void, Void> {

        private String mPostWord;
        private int mPostType;
        private String mParamsPost;

        HttpResponse httpResponse;

        PostDataTask(String url, String params, int type) {
            mPostWord = url;
            mPostType = type;
            mParamsPost = params;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int index = 0;
            String url = "";

            //String httpUrl = "http://api.gigaset.com/cn/mobile/v1/demovideo/querydemo";

            url = "http://" + mPostWord;


            httpPostParams(url, mParamsPost, mPostType);
            return null;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /*if (null != mEditParamsText) {
                mEditParamsText.setText(subitJson);
            }*/
            if (TYPE_GET_NUM_PARM == CURRENT_TYPE_POST) {
                mTextView.append("\n http 后台结果  " + postRTR);
                mTextView.append("\n http 后台返回数据    " + postDetailRTR);
                //mPostDetailsRtrTx.setText(postDetailRTR);
                loadingDialog.dismiss();
                if (postRTR.contains("请求成功") && 0 == Utils.parseJsonResult(postDetailRTR)) {
                    mTextView.append("\n 服务器处理成功 ");
                    //该设备已经绑定过
                    sharedPreferencesHelper.putInt(SP_BIND_STATE, STATE_DEVICE_BIND);

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
                                        getString(R.string.test_bind_tip_content));
                        mAlertDialog.show();
                    }

                } else if (postRTR.contains("请求成功") && 1 == Utils.parseJsonResult(postDetailRTR)) {
                    mTextView.append("\n 该手环已被绑定过 ");
                    sendGPGTimely();
                } else {
                    //Limited retry counts are 3
                    if (2 == retryCnt) {
                        retryCnt = 0;
                        return;
                    }
                    retryCnt++;
                    //Again
                    Message message = new Message();
                    message.what = MSG_SERCH_DONE;
                    mBTHandler.sendMessageDelayed(message, 60000);
                }
            } else if (TYPE_UPLOAD_LOCATION == CURRENT_TYPE_POST) {
                handleGpsMsg();

            } else if (TYPE_PUSH_MSG == CURRENT_TYPE_POST) {
                handlePushMsg();
            } else if (TYPE_WARNING_NOTIFY == CURRENT_TYPE_POST) {
                handleWarningMsg();
            }

        }
    }

    private void handleWarningMsg() {
        if (-1 == Utils.parseJsonResult(postDetailRTR)) {
            mTextView.append("\n 服务器处理失败，要求app重新上报");
        } else if (0 == Utils.parseJsonResult(postDetailRTR)) {
            mTextView.append("\n 服务器处理成功");
        } else {
            mTextView.append("\n 告警消息推送返回错误");
        }
    }

    private void handleGpsMsg() {
        if (-1 == Utils.parseJsonResult(postDetailRTR)) {
            mTextView.append("\n 坐标数据服务器处理失败");
        } else if (0 == Utils.parseJsonResult(postDetailRTR)) {
            mTextView.append("\n 坐标数据服务器处理成功，没有越界");
        } else if (1 == Utils.parseJsonResult(postDetailRTR)) {
            mTextView.append("\n 坐标数据服务器处理成功，坐标越界");
        } else if (2 == Utils.parseJsonResult(postDetailRTR)) {
            //mTextView.append("\n 坐标数据服务器处理成功，设备未进行人员信息绑定");
            if (null != App.timerTask) {
                LogUtil.d("stop_post_package");
                App.timerTask.cancel();
            }
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
                                getString(R.string.test_gps_bind_tip_content));
                mAlertDialog.show();
            }
        } else {
            mTextView.append("\n 坐标数据服务器没有返回");
        }
    }

    private void handlePushMsg() {
        try {
            JSONObject jsonObject = new JSONObject(postDetailRTR);
            JSONArray marks = jsonObject.getJSONArray("params");
            for(int i=0; i< marks.length(); i++){
                JSONObject mark = (JSONObject)marks.get(i);
                String showMessage = mark.getString("MsgTypeID");
                if (showMessage.equals("1")) {
                    showMessage = "短消息 ";
                } else if (showMessage.equals("2")) {
                    showMessage = "设置GPS上报间隔时间 ";
                } else if (showMessage.equals("3")) {
                    showMessage = "设置手环扫描间隔 ";
                }

                String msg = mark.getString("Msg");
                String time = mark.getString("CreateTime");
                mTextView.append("\n " + showMessage + "Msg: " + msg + "Create time : " + time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendGPGTimely() {

        if (null != App.timerTask) {
            LogUtil.d("stop_post_package");
            App.timerTask.cancel();
        }

        if (0 != longtitude && 0 != latitude && null != sharedPreferencesHelper) {

            long times = sharedPreferencesHelper.getInt(SP_POST_INTERNAL);
            LogUtil.d("times == " + times);
            if (0 == times) {
                times = 20000;
            }
            //Must cancel first

            App.timerTask = new TimerTask() {
                @Override
                public void run() {
                    //SEND MESSAGE TIMER
                    Message msg = new Message();
                    msg.what = MSG_PUSH_MSG;
                    if (null != mBTHandler) {
                        mBTHandler.sendMessage(msg);
                        App.timesJudgeGps++;
                    }

                    if (3 == App.timesJudgeGps) {
                        App.timesJudgeGps = 0;
                        Message msg2 = new Message();
                        msg2.what = MSG_CHA_SEND_LOCATION;
                        if (null != mBTHandler) {
                            mBTHandler.sendMessage(msg2);
                        }
                    }
                }
            };
            App.timer.schedule(App.timerTask, 0, times);
        }
    }

    private AlertDialogCreator.ButtonOnClickListener mDialogListener = new AlertDialogCreator.ButtonOnClickListener() {
        @Override
        public void buttonTrue() {
            sendGPGTimely();
        }

        @Override
        public void buttonTrue(int ring_dis) {

        }

        @Override
        public void buttonTrue(String value) {
            if (null != value && (!TextUtils.isEmpty(value)) && null != sharedPreferencesHelper) {
                Utils.setTelNumber(value);
                sharedPreferencesHelper.putString(SP_PHONE_NUMBER, value);
            }
        }

        @Override
        public void buttonTrue(String valuekey, String name) {

        }

        @Override
        public void buttonCancel() {

            //ToastHelper.showAlert(mContext, getString(R.string.boolth_eable_tip));
            //finish();
        }
    };
}
