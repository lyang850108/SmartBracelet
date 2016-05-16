package com.smartbracelet.com.smartbracelet.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.adapter.DeviceListAdapter;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.service.LocationService;
import com.smartbracelet.com.smartbracelet.util.BleNamesResolver;
import com.smartbracelet.com.smartbracelet.util.BleWrapper;
import com.smartbracelet.com.smartbracelet.util.BleWrapperUiCallbacks;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LiteOrmDBUtil;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.ReschedulableTimerTask;
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
    FloatingActionButton mFloatingActionBtn;

    private Activity mContext;

    public static Handler mBTHandler;

    //0x00：没有配对 0x01：配对成功
    public String mBindResult = "0x00000000";

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

    private BluetoothGattCharacteristic characBatteryLevel;

    private BluetoothGattCharacteristic charac01;

    private BluetoothGattCharacteristic charac02;


    private class BlueToothTestHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHA_READ:
                    int type = msg.arg1;
                    if (TYPE_GET_CLCIK_TIMES == type) {
                        if (null != charac01) {
                            mBleWrapper.requestCharacteristicValue(charac01);
                        }
                    }
                    break;

                case MSG_CHA_WRITE:
                    int type2 = msg.arg1;
                    if (TYPE_SET_BIND_STATE == type2) {
                        if (null != charac02) {
                            String newValue = mBindResult;
                            byte[] dataToWrite = Utils.parseHexStringToBytes(newValue);
                            LogUtil.d("MSG_CHA_WRITE : " + dataToWrite.toString());
                            mBleWrapper.writeDataToCharacteristic(charac02, dataToWrite);
                        }
                    }
                    break;


                case MSG_SERCH_DONE:
                    mPostDataTask = new PostDataTask("120.25.89.222/main.cgi", Utils.bindJOTelTest(bleAddress).toString(), TYPE_GET_NUM_PARM);
                    mPostDataTask.execute(0);
                    loadingDialog.show();

                    break;


                case MSG_CHA_SEND_LOCATION:
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
                mBindResult = "0x00010002";
                handleDeviceConnected(gatt, device);

            }

            @Override
            public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {
                LogUtil.d("uiDeviceDisconnected");
                mBindResult = "0x00000003";
                handleDeviceDisconnected(gatt, device);
            }

            @Override
            public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device, List<BluetoothGattService> services) {
                for (BluetoothGattService service : mBleWrapper.getCachedServices()) {
                    String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
                    String name = BleNamesResolver.resolveServiceName(uuid);
                    String type = (service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY) ? "Primary" : "Secondary";
                    if (service.getUuid().equals(Service.BATTERY_SERVICE) || service.getUuid().equals(Service.UNKNOWN_SERVICE2)) {
                        mBleWrapper.getCharacteristicsForService(service);
                    }

                }
            }


            @Override
            public void uiCharacteristicForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, List<BluetoothGattCharacteristic> chars) {
                for (BluetoothGattCharacteristic ch : chars) {
                    String uuid = ch.getUuid().toString().toLowerCase(Locale.getDefault());
                    String name = BleNamesResolver.resolveCharacteristicName(uuid);
                    if (ch.getUuid().equals(Characteristic.BATTERY_LEVEL)) {
                        characBatteryLevel = ch;
                        gatt.readCharacteristic(ch);
                    } else if (ch.getUuid().equals(Characteristic.CHAR01_LEVEL2)) {
                        charac01 = ch;
                        //gatt.readCharacteristic(ch);
                    } else if (ch.getUuid().equals(Characteristic.CHAR02_LEVEL2)) {
                        charac02 = ch;
                        //gatt.writeCharacteristic(ch);
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


                sendMsg(MSG_CHA_READ, TYPE_GET_CLCIK_TIMES);
            }

            @Override
            public void uiClickValueRead(final String value) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.append("\n 点击次数" + value);
                    }
                });

                sendMsg(MSG_CHA_WRITE, TYPE_SET_BIND_STATE);
            }

            public void uiSuccessfulWrite(final BluetoothGatt gatt,
                                          final BluetoothDevice device,
                                          final BluetoothGattService service,
                                          final BluetoothGattCharacteristic ch,
                                          final String description) {
                LogUtil.d("uiSuccessfulWrite : ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Writing to " + description + " was finished successfully!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void uiFailedWrite(final BluetoothGatt gatt,
                                      final BluetoothDevice device,
                                      final BluetoothGattService service,
                                      final BluetoothGattCharacteristic ch,
                                      final String description) {
                LogUtil.d("uiFailedWrite : ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Writing to " + description + " FAILED!", Toast.LENGTH_LONG).show();
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
        if (null != sharedPreferencesHelper) {
            sharedPreferencesHelper.putInt(SP_POST_INTERNAL, 60000);
        }

        if (null != sharedPreferencesHelper && TextUtils.isEmpty(Utils.getLocalNum())) {
            checkPhomeNumerAvailble(sharedPreferencesHelper);
        }

        initGPS(mContext);
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
                mConnectedAddTx.setText(device.getName() + "\n" + device.getAddress() + "\n" + " Connected");

                if (STATE_DEVICE_UNBIND == sharedPreferencesHelper.getInt(SP_BIND_STATE)) {
                    sendMsg(MSG_SERCH_DONE, 0);
                } else if (STATE_DEVICE_BIND == sharedPreferencesHelper.getInt(SP_BIND_STATE)) {
                    mTextView.append("\n 该手环已被绑定过 ");
                    sendGPGTimely();
                }
            }
        });
    }

    private void sendMsg(int msg, int type) {
        Message message = new Message();
        message.what = msg;
        if (0 < type) {
            message.arg1 = type;
        }
        mBTHandler.sendMessage(message);
    }

    private void handleDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectedAddTx.setText(device.getName() + "\n" + device.getAddress() + "\n" + " Disconnected");
            }
        });

        //Disconnected start scaning again
        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkBTAvail();

        //Add demo begin
        // on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
        /*if (mBleWrapper.isBtEnabled() == false) {
            // BT is not turned on - ask user to make it enabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            // see onActivityResult to check what is the status of our request
        }*/

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

    @Override
    protected void onStop() {
        super.onStop();
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

    private static List<ProgramItem> list = new ArrayList<ProgramItem>();

    private void handlePushMsg() {
        try {
            JSONObject jsonObject = new JSONObject(postDetailRTR);
            JSONArray marks = jsonObject.getJSONArray("params");
            for (int i = 0; i < marks.length(); i++) {
                JSONObject mark = (JSONObject) marks.get(i);
                String showMessage = mark.getString("MsgTypeID");
                String msg = mark.getString("Msg");
                String time = mark.getString("CreateTime");
                if (showMessage.equals("1")) {
                    showMessage = "短消息 ";
                    ProgramItem programItem = new ProgramItem();
                    programItem.title = showMessage;
                    programItem.body = msg;
                    programItem.timeStamp = time;

                    //Don't add the same item
                    if (!list.contains(programItem)) {
                        list.add(programItem);
                        Utils.notifyMessageComing(mContext, showMessage, msg);
                    }

                } else if (showMessage.equals("2")) {
                    showMessage = "设置GPS上报间隔时间 ";
                } else if (showMessage.equals("3")) {
                    showMessage = "设置手环扫描间隔 ";
                }

                if (null != list && list.size() > 0) {
                    LogUtil.d("LiteOrmDBUtil.insertAll :" + list.size());
                    LiteOrmDBUtil.insertAll(list);
                }

                //Notify
                //Utils.notiy(mContext, showMessage, msg);
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

            App.timerTask = new ReschedulableTimerTask() {
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
            LogUtil.d("buttonTrue buttonTrue");
            sendGPGTimely();
        }

        @Override
        public void buttonTrue(int ring_dis) {
            if (DIALOG_TYPE_GPS == ring_dis) {
                // 转到手机设置界面，用户设置GPS
                Intent intent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent); // 设置完成后返回到原来的界面
            }

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
    private NotificationManager mNotificationManager;
    private Notification notification;

    private void checkBTAvail() {
        if (null == mBtAdapter) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (!mBtAdapter.enable()) {
            /*try {
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
            }*/
        }
    }


    private void initGPS(final Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "请打开GPS",
                    Toast.LENGTH_SHORT).show();

            if (null != mContext) {
                if (null != mAlertDialog) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
                AlertDialogCreator.getInstance().setmButtonOnClickListener(mDialogListener);
                mAlertDialog = AlertDialogCreator
                        .getInstance()
                        .createAlertDialogType(
                                mContext,
                                getString(R.string.tip_title),
                                getString(R.string.open_gps), DIALOG_TYPE_GPS);
                mAlertDialog.show();
            }

        } else {
            // 弹出Toast
//			Toast.makeText(TrainDetailsActivity.this, "GPS is ready",
//					Toast.LENGTH_LONG).show();
//			// 弹出对话框
//			new AlertDialog.Builder(this).setMessage("GPS is ready")
//					.setPositiveButton("OK", null).show();
        }
    }

}
