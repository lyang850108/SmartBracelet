package com.smartbracelet.com.smartbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.adapter.DeviceListAdapter;
import com.smartbracelet.com.smartbracelet.bean.BlueToothBean;
import com.smartbracelet.com.smartbracelet.bean.GpsBean;
import com.smartbracelet.com.smartbracelet.bluetooth.BleNamesResolver;
import com.smartbracelet.com.smartbracelet.bluetooth.BleWrapper;
import com.smartbracelet.com.smartbracelet.bluetooth.BleWrapperUiCallbacks;
import com.smartbracelet.com.smartbracelet.model.BaseActivity;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.network.NetworkUtil;
import com.smartbracelet.com.smartbracelet.network.PollingUtils;
import com.smartbracelet.com.smartbracelet.network.SocketConnAsync;
import com.smartbracelet.com.smartbracelet.service.HttpPostService;
import com.smartbracelet.com.smartbracelet.service.LocationService;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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

/**
 * Created by Yangli on 16-06-03.
 * 蓝牙手环管理页面
 */
public class DeviceManagerActivity extends BaseActivity implements ConstDefine{
    private Activity pThis;

    @Bind(R.id.fab)
    Button floatingActionButton;

    @Bind(R.id.device_list_mm)
    ListView listView;

    @Bind(R.id.device_im_mm)
    ImageView mDeviceIg;

    @Bind(R.id.device_tx_mm)
    TextView mInforTx;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.device_battery_ll)
    LinearLayout linearLayout;

    @Bind(R.id.device_battery_tx)
    TextView mBatteryTx;

    private DeviceListAdapter mDeviceListAdapter = null;

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
    //String SP_PHONE_ADDRESS = "init_phone_address";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    // Bluetooth
    //public static BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBtAdapter;

    // 搜索重试次数
    private int retySearchCount = 0;
    // 连接重试三次
    private int retyContactCount = 3;

    //Add Demo begin
    private static final long SCANNING_TIMEOUT = 5 * 1000; /* 5 seconds */
    private static final int ENABLE_BT_REQUEST_ID = 1;

    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private BleWrapper mBleWrapper = null;
    //Add end

    private String permissionInfo;
    private final int SDK_PERMISSION_REQUEST = 127;

    private LoadingDialog loadingDialog;
    private String postDetailRTR;

    private static int CURRENT_TYPE_POST = 9;

    private static int retryCnt = 0;

    private BluetoothGattCharacteristic characBatteryLevel;

    private BluetoothGattCharacteristic charac01;

    private BluetoothGattCharacteristic charac02;

    private BlueToothBean mBlueToothBean;

    private class BTHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_OUT:
                    int timesUpload = msg.arg1;
                    int backRtr = msg.arg2;
                    /*if (0 == backRtr) {
                        if (null != mTextView) {
                            mTextView.append("\n 后台服务器上报次数" + timesUpload);
                            mTextView.append("\n 后台服务器上报时间" + Utils.getTime());
                        }
                    } else {
                        if (null != mTextView) {
                            mTextView.append("\n 后台服务器上报失败 ***" + Utils.getTime());
                        }
                    }*/


                    break;
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
                    mPostDataTask = new PostDataTask(UTT_SETVER_URL, Utils.bindJOTelTest(bleAddress).toString(), TYPE_GET_NUM_PARM);
                    mPostDataTask.execute(0);
                    try {
                        loadingDialog.show();
                    } catch (Exception e) {
                        LogUtil.d(e.toString());
                    }


                    break;


                case MSG_CHA_SEND_LOCATION:
                    mPostDataTask = new PostDataTask(UTT_SETVER_URL, Utils.bindJOGps(GpsBean.getInstance().getLatitude() , GpsBean.getInstance().getLongitude(), bleAddress).toString(), TYPE_UPLOAD_LOCATION);
                    mPostDataTask.execute(0);
                    break;

                /*case MSG_PUSH_MSG:
                    LogUtil.d("MSG_PUSH_MSG");
                    mPostDataTask = new PostDataTask("120.25.89.222/main.cgi", Utils.bindJOMsgPushTest(bleAddress).toString(), TYPE_PUSH_MSG);
                    mPostDataTask.execute(0);
                    break;*/

                case MSG_STATE_WARNING:
                    if (null != mBleWrapper && !mBleWrapper.isConnected()) {
                        LogUtil.d("*********MSG_STATE_WARNING");
                        int warningType = msg.arg1;
                        mPostDataTask = new PostDataTask(UTT_SETVER_URL, Utils.bindJOWarningTest(bleAddress, warningType).toString(), TYPE_WARNING_NOTIFY);
                        mPostDataTask.execute(0);
                    }

                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manager);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        setTitle("设备管理");
        pThis= this;
        ButterKnife.bind(this);
        mBTHandler = new BTHandler();

        mBlueToothBean = BlueToothBean.getInstance();

        initBle(pThis);

        // check if we have BT and BLE on board
        if (mBleWrapper.checkBleHardwareAvailable() == false) {
            bleMissing();
        }
        //Add end

        loadingDialog = new LoadingDialog(pThis);

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
        if (null != sharedPreferencesHelper) {
            sharedPreferencesHelper.putInt(SP_POST_INTERNAL, 60000);
        }

        mDeviceListAdapter = new DeviceListAdapter(pThis);
        listView.setAdapter(mDeviceListAdapter);
        listView.setOnItemClickListener(onItemClickListener);

        //Add the animation in the startup by yangli 2013.11.11 happy singles day
        /*TranslateAnimation alphaAnimation = new TranslateAnimation(0, 0, 0,
                -70);
        alphaAnimation.setDuration(500);
        alphaAnimation.setRepeatCount(3);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        floatingActionButton.setAnimation(alphaAnimation);
        alphaAnimation.start();*/
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

        //进度条展示
        setProgressBarIndeterminate(true);
        if (View.GONE == mInforTx.getVisibility()) {
            mInforTx.setVisibility(View.VISIBLE);
        }
        mInforTx.setText("请激活手环 搜索设备中.......");



        if (null != mBleWrapper && mBleWrapper.isConnected()) {
            return;
        }

        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanning = false;
        mBleWrapper.stopScanning();
        mDeviceListAdapter.clearList();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(pThis);
    }

    @OnClick(R.id.fab)
    public void onFabClick (View view) {
        /*Snackbar.make(view, "搜索设备中.......", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
        LogUtil.d("onFabClick tag = " + view.getTag());
        if (0 == view.getTag()) {
            sendMsg(MSG_SERCH_DONE, 0);
        }

    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            LogUtil.d("onItemClick" + position);
            final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
            if (null == device) {
                return;
            }
            String bindAddress = sharedPreferencesHelper.getString(BLE_ADDRESS_PREF);
            LogUtil.d("handleFoundDevice" + device.getAddress() + " name: " + device.getName());
            if (bindAddress.equals(device.getAddress()) || (App.isFirstLuanched && TextUtils.isEmpty(bindAddress))) {
                if (!TextUtils.isEmpty(device.getName()) && device.getName().startsWith("utt")) {
                    //mTextView.append("\n数据库存储的地址" + bindAddress);
                    LogUtil.d("existAddress" + bindAddress);
                    String mName = device.getName();
                    String mAddress = device.getAddress();

                    if (mScanning) {
                        mScanning = false;
                        mBleWrapper.stopScanning();
                    }

                    // start automatically connecting to the device
                    //mTextView.setText("connecting ...");
                    bleAddress = mAddress;
                    Boolean connectRtr = mBleWrapper.connect(mAddress);
                    if (connectRtr) {
                        //mTextView.setText("connecting init successful");
                    } else {
                        //mTextView.setText("connecting init failed");
                    }
                }
            }

        }
    };

    public static PendingIntent pendingIntent;
    private void initBle(final Activity pThis) {
        mBleWrapper = new BleWrapper(pThis, new BleWrapperUiCallbacks.Null() {
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
                pThis.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mBatteryLevelTx.setText(mBatterymValue);
                        linearLayout.setVisibility(View.VISIBLE);
                        mBatteryTx.setText(mBatterymValue.substring(0, mBatterymValue.indexOf("bat")));
                    }
                });


                sendMsg(MSG_CHA_READ, TYPE_GET_CLCIK_TIMES);
            }

            @Override
            public void uiClickValueRead(final String value) {
                pThis.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mTextView.append("\n 点击次数" + value);
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


    }

    private void btDisabled() {
        ToastHelper.showAlert(pThis, "Sorry, BT has to be turned ON for us to work!");
        pThis.finish();
    }

    private void bleMissing() {
        ToastHelper.showAlert(pThis, "BLE Hardware is required but not available!");
        pThis.finish();
    }

    private void checkBTAvail() {
        if (null == mBtAdapter) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (!mBtAdapter.enable()) {
            /*try {
                if (null != pThis) {
                    if (null != mAlertDialog) {
                        mAlertDialog.dismiss();
                        mAlertDialog = null;
                    }
                    AlertDialogCreator.getInstance().setmButtonOnClickListener(mDialogListener);
                    mAlertDialog = AlertDialogCreator
                            .getInstance()
                            .createAlertDialog(
                                    pThis,
                                    getString(R.string.tip_title),
                                    getString(R.string.boolth_tip_content));
                    mAlertDialog.show();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }*/
        }
    }

    public PostDataTask mPostDataTask;
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
                handleUploadTelMsg();
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
            //mTextView.append("\n 服务器处理失败，要求app重新上报");
        } else if (0 == Utils.parseJsonResult(postDetailRTR)) {
            //mTextView.append("\n 服务器处理成功");
        } else {
            //mTextView.append("\n 告警消息推送返回错误");
        }
    }

    private void handleUploadTelMsg() {
        //mTextView.append("\n http 后台结果  " + postRTR);
        //mTextView.append("\n http 后台返回数据    " + postDetailRTR);
        //mPostDetailsRtrTx.setText(postDetailRTR);
        try {
            loadingDialog.dismiss();
        } catch (IllegalArgumentException e) {
            LogUtil.d(e.toString());
        }

        if (postRTR.contains("请求成功") && 0 == Utils.parseJsonResult(postDetailRTR)) {
            //mTextView.append("\n 服务器处理成功 ");

            if (null != pThis) {
                if (null != mAlertDialog) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
                AlertDialogCreator.getInstance().setmButtonOnClickListener(mDialogListener);
                mAlertDialog = AlertDialogCreator
                        .getInstance()
                        .createAlertDialog(
                                pThis,
                                getString(R.string.tip_title),
                                getString(R.string.test_bind_tip_content));
                mAlertDialog.show();
            }

        } else if (postRTR.contains("请求成功") && 1 == Utils.parseJsonResult(postDetailRTR)) {
            //mTextView.append("\n 该手环已被绑定过 请换个手环");
            Toast.makeText(getApplicationContext(), "该手环已被绑定过 请换个手环", Toast.LENGTH_LONG).show();

        } else {
            //if (STATE_DEVICE_UNBIND == sharedPreferencesHelper.getInt(SP_BIND_STATE)) {
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
            //}

        }
    }

    private AlertDialogCreator.ButtonOnClickListener mDialogListener = new AlertDialogCreator.ButtonOnClickListener() {
        @Override
        public void buttonTrue() {
            sendGPS();
        }

        @Override
        public void buttonTrue(int ring_dis) {
            if (DIALOG_TYPE_GPS == ring_dis) {
                // 转到手机设置界面，用户设置GPS
                Intent intent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                pThis.startActivity(intent); // 设置完成后返回到原来的界面
            }

        }

        @Override
        public void buttonTrue(String value) {

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

    private void sendGPS() {

        Message msg2 = new Message();
        msg2.what = MSG_CHA_SEND_LOCATION;
        if (null != mBTHandler) {
            mBTHandler.sendMessage(msg2);
        }
    }

    private void sendMsg(int msg, int type) {
        Message message = new Message();
        message.what = msg;
        if (0 < type) {
            message.arg1 = type;
        }
        mBTHandler.sendMessage(message);
    }

    private String postRTR;

    private void httpPostParams(String url, String params, int mPostType) {
        String httpUrl = url;
        CURRENT_TYPE_POST = mPostType;
        //创建httpRequest对象
        if (null == url) {
            return;
        }


        try {
            HttpPost httpRequest = new HttpPost(httpUrl);

            //设置字符集
            //LogUtil.e("post, yangli:" + subitJson);


            StringEntity se = new StringEntity(params, "utf-8");
            //请求httpRequest
            httpRequest.setEntity(se);


            //取得默认的HttpClient
            //HttpClient httpclient = new DefaultHttpClient();

            //Change to Https due of lazy reson i didn't use the SocketConnAsync at all
            // 参数
            HttpParams httpParameters = new BasicHttpParams();
            // 设置连接超时
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            // 设置socket超时
            HttpConnectionParams.setSoTimeout(httpParameters, 3000);
            //使用Https
            HttpClient httpclient = SocketConnAsync.initHttpClient(httpParameters);

            //取得HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            //HttpStatus.SC_OK表示连接成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                //取得返回的字符串
                //String strResult = EntityUtils.toString(httpResponse.getEntity());
                postRTR = "请求成功!";
                postDetailRTR = EntityUtils.toString(httpResponse.getEntity());
                //postDetailRTR = httpResponse.getEntity().toString();
            } else {
                postRTR = "请求错误! 错误码" + httpResponse.getStatusLine().getStatusCode();
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

    private String mConnectedAddress;

    private void

    handleDeviceConnected(final BluetoothGatt gatt, final BluetoothDevice device) {

        pThis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*mConnectedAddress = device.getAddress();
                if (null != mConnectedAddTx) {
                    mConnectedAddTx.setText(device.getName() + "\n" + device.getAddress() + "\n" + " Connected");
                }*/
                //连接成功后设备列表消失
                if (null != listView) {
                    listView.setVisibility(View.GONE);
                }

                if (null != mDeviceIg) {
                    //设备连接图标显示
                    mDeviceIg.setVisibility(View.VISIBLE);
                }

                if (null != mInforTx) {
                    mInforTx.setVisibility(View.VISIBLE);
                    mInforTx.setText("连接成功");
                }


                int bindState = sharedPreferencesHelper.getInt(SP_BIND_STATE);
                LogUtil.d("handleDeviceConnected SP_BIND_STATE" + bindState);
                if (STATE_DEVICE_UNBIND == bindState) {
                    //这里没有绑定过，需检查各个性能指标再发送激活命令
                    if (isActivationSB()) {
                        if (View.GONE == floatingActionButton.getVisibility()) {
                            floatingActionButton.setVisibility(View.VISIBLE);
                        }
                        floatingActionButton.setText(R.string.activation);
                        floatingActionButton.setTag(0);
                    }

                } else if (STATE_DEVICE_BIND == bindState) {
                    //mTextView.append("\n 该手环已被绑定过 ");
                    //STOP FIRST

                    if (false == PollingUtils.isPollServiceRunning(pThis)) {
                        //mTextView.append("\n 后台服务重新开启 蚂蚁 蚂蚁 ");
                        startPollService();
                    }

                }
            }
        });

        /*if (App.isScanningDevice) {
            stopScanningTask();
        }*/
    }

    private void handleDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device) {
        pThis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mInforTx && null != linearLayout) {
                    mInforTx.setText("连接断开");

                    //电量提醒消失
                    linearLayout.setVisibility(View.GONE);
                }



                //mConnectedAddTx.setText(device.getName() + "\n" + device.getAddress() + "\n" + " Disconnected");
            }
        });

        //这里会有个问题 如果长时间待机会杀死Timetask,如果扫描放在onreume里 会导致逻辑冲突
        /*if (!App.isScanningDevice) {
            startScanningTask();
        }*/

        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();


        //Notify the server in 120s
        sendWarningDelayed(WARNING_TYPE_DEVCE_DISCONNECTED);
    }

    private void startScanningTask() {
        App.isScanningDevice = true;
        int times = 8000;
        App.timerTask = new ReschedulableTimerTask() {
            @Override
            public void run() {
                LogUtil.d("startScanningTask");
                //Disconnected start scaning again
                mScanning = true;
                // remember to add timeout for scanning to not run it forever and drain the battery
                addScanningTimeout();
                mBleWrapper.startScanning();
            }
        };
        App.timer.schedule(App.timerTask, 0, times);
    }

    private void stopScanningTask() {
        App.isScanningDevice = false;
        if (null != App.timerTask) {
            LogUtil.d("stopScanningTask");
            App.timerTask.cancel();
        }
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

    private void stopPollService() {

    }

    private void startPollService() {

        /*if (null != App.timerTask) {
            App.timerTask.cancel();
        }*/


        if (0 != GpsBean.getInstance().getLongitude() && 0 != GpsBean.getInstance().getLongitude()  && null != sharedPreferencesHelper) {

            long times = sharedPreferencesHelper.getInt(SP_POST_INTERNAL);


            if (0 == times) {
                times = 20000;
            }

            //mTextView.append("\n 开始执行后台坐标上报业务");
            PollingUtils.startPollingService(this, times, HttpPostService.class, ACTION_GPS_POST_CMD);

        } else {
            //mTextView.append("\n 无法获取经纬度，不能执行后台上报业务");
        }
    }

    private void sendWarningDelayed(final int type) {

        long times = 120000;
        //Must cancel first

        //SEND MESSAGE TIMER
        Message msg = new Message();
        msg.what = MSG_STATE_WARNING;
        msg.arg1 = type;
        if (null != mBTHandler) {
            mBTHandler.sendMessageDelayed(msg, times);
        }

    }

    private void handleGpsMsg() {
        if (-1 == Utils.parseJsonResult(postDetailRTR)) {
            //mTextView.append("\n 坐标数据服务器处理失败");
        } else if (0 == Utils.parseJsonResult(postDetailRTR)) {
            //mTextView.append("\n 坐标数据服务器处理成功，没有越界");
            // 只保留最原始的蓝牙地址
            if (TextUtils.isEmpty(sharedPreferencesHelper.getString(BLE_ADDRESS_PREF))) {
                if (App.isFirstLuanched) {
                    App.isFirstLuanched = false;
                }
                LogUtil.d("handleGpsMsg getAddress" + mConnectedAddress);
                if (!TextUtils.isEmpty(mConnectedAddress)) {
                    sharedPreferencesHelper.putString(BLE_ADDRESS_PREF, mConnectedAddress);
                }

                //该设备已经绑定过
                sharedPreferencesHelper.putInt(SP_BIND_STATE, STATE_DEVICE_BIND);
            }
            startPollService();
        } else if (1 == Utils.parseJsonResult(postDetailRTR)) {
            //mTextView.append("\n 坐标数据服务器处理成功，坐标越界");
            // 只保留最原始的蓝牙地址
            if (TextUtils.isEmpty(sharedPreferencesHelper.getString(BLE_ADDRESS_PREF))) {
                if (App.isFirstLuanched) {
                    App.isFirstLuanched = false;
                }
                LogUtil.d("handleGpsMsg getAddress" + mConnectedAddress);
                if (!TextUtils.isEmpty(mConnectedAddress)) {
                    sharedPreferencesHelper.putString(BLE_ADDRESS_PREF, mConnectedAddress);
                }
                //该设备已经绑定过
                sharedPreferencesHelper.putInt(SP_BIND_STATE, STATE_DEVICE_BIND);
            }
            startPollService();
        } else if (2 == Utils.parseJsonResult(postDetailRTR)) {
            //mTextView.append("\n 坐标数据服务器处理成功，设备未进行人员信息绑定");

            if (null != pThis) {
                if (null != mAlertDialog) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
                AlertDialogCreator.getInstance().setmButtonOnClickListener(mDialogListener);
                mAlertDialog = AlertDialogCreator
                        .getInstance()
                        .createAlertDialog(
                                pThis,
                                getString(R.string.tip_title),
                                getString(R.string.test_gps_bind_tip_content));
                mAlertDialog.show();
            }
        } else {
            //mTextView.append("\n 坐标数据服务器没有返回");
        }
    }

    private List<ProgramItem> list = new ArrayList<ProgramItem>();

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
                    }

                } else if (showMessage.equals("2")) {
                    showMessage = "设置GPS上报间隔时间 ";
                } else if (showMessage.equals("3")) {
                    showMessage = "设置手环扫描间隔 ";
                }

                if (null != list && list.size() > 0) {
                    removePrograms(list);
                    LiteOrmDBUtil.insertAll(list);
                }

                //Notify
                if (true == sharedPreferencesHelper.getBoolean(NOTIFICATION_PREF)) {
                    Utils.notifyMessageComing(pThis, showMessage, msg);
                }

                //mTextView.append("\n " + showMessage + "Msg: " + msg + "Create time : " + time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * 通过循环删除
     *
     * @param list
     */
    public void removePrograms(List<ProgramItem> list) {

        try {
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = list.size() - 1; j > i; j--) {
                    if (list.get(j).timeStamp.equals(list.get(i).timeStamp)) {
                        list.remove(j);
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            LogUtil.d("" + e);
        }

    }

    /* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device,
                                   final int rssi,
                                   final byte[] scanRecord) {
        // adding to the UI have to happen in UI thread
        pThis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String bindAddress = sharedPreferencesHelper.getString(BLE_ADDRESS_PREF);
                LogUtil.d("handleFoundDevice" + device.getAddress() + " name: " + device.getName());
                if (App.isFirstLuanched && TextUtils.isEmpty(bindAddress)) {
                    if (!TextUtils.isEmpty(device.getName())&& device.getName().startsWith("utt")) {
                        //如果第一次搜索到设备，底部的文字View应该隐藏
                        if (null != mInforTx) {
                            mInforTx.setVisibility(View.GONE);
                        }
                        mDeviceListAdapter.addDevice(device, rssi, scanRecord);
                        mDeviceListAdapter.notifyDataSetChanged();
                        inVisibleProgress();

                    }
                } else {
                    if (bindAddress.equals(device.getAddress())) {
                        if (!TextUtils.isEmpty(device.getName()) && device.getName().startsWith("utt")) {
                            inVisibleProgress();
                            //mTextView.append("\n数据库存储的地址" + bindAddress);
                            LogUtil.d("existAddress" + bindAddress);
                            String mName = device.getName();
                            String mAddress = device.getAddress();
                            int mRssi = rssi;

                            if (mScanning) {
                                mScanning = false;
                                mBleWrapper.stopScanning();
                            }

                            // start automatically connecting to the device
                            //mTextView.setText("connecting ...");
                            bleAddress = mAddress;
                            Boolean connectRtr = mBleWrapper.connect(mAddress);
                            if (connectRtr) {
                                //mTextView.setText("connecting init successful");
                            } else {
                                //mTextView.setText("connecting init failed");
                            }
                        }
                    }
                }

                //For test
                /*String bindAddress = sharedPreferencesHelper.getString(BLE_ADDRESS_PREF);
                LogUtil.d("handleFoundDevice" + device.getAddress() + " name: " + device.getName());
                if (bindAddress.equals(device.getAddress()) || (App.isFirstLuanched && TextUtils.isEmpty(bindAddress))) {
                    if (!TextUtils.isEmpty(device.getName()) && device.getName().startsWith("utt")) {
                        //mTextView.append("\n数据库存储的地址" + bindAddress);
                        LogUtil.d("existAddress" + bindAddress);
                        String mName = device.getName();
                        String mAddress = device.getAddress();
                        int mRssi = rssi;

                        if (mScanning) {
                            mScanning = false;
                            mBleWrapper.stopScanning();
                        }

                        // start automatically connecting to the device
                        //mTextView.setText("connecting ...");
                        bleAddress = mAddress;
                        Boolean connectRtr = mBleWrapper.connect(mAddress);
                        if (connectRtr) {
                            //mTextView.setText("connecting init successful");
                        } else {
                            //mTextView.setText("connecting init failed");
                        }
                    }
                }*/


            }
        });
    }

    private void inVisibleProgress() {
        if (null != progressBar) {
            setProgressBarIndeterminate(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean isActivationSB() {
        if (null == mBlueToothBean) {
            return false;
        }

        if (!mBleWrapper.isConnected()) {
            Toast.makeText(getApplicationContext(), "手环未连接", Toast.LENGTH_LONG).show();
            return false;
        }
        LogUtil.d("getBatteryLevel:" + mBlueToothBean.getBatteryLevel());
        if (30 > mBlueToothBean.getBatteryLevel()) {
            Toast.makeText(getApplicationContext(), "电量太低", Toast.LENGTH_LONG).show();
            return false;
        }

        if (false == NetworkUtil.isNetworkAvailable(pThis)) {
            Toast.makeText(getApplicationContext(), "网络数据不可用", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
