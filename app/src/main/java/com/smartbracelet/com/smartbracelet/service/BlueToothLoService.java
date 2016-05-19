package com.smartbracelet.com.smartbracelet.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.smartbracelet.com.smartbracelet.activity.App;
import com.smartbracelet.com.smartbracelet.fragment.BlueToothFragment;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LogUtil;

import java.util.List;

public class BlueToothLoService extends Service implements ConstDefine{

    private Context mContext;

    public static BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBtAdapter;
    private BluetoothGatt mGatt;
    private BluetoothGattCharacteristic mBGCnotify;
    private BluetoothGattCharacteristic mBGCwrite;
    private BluetoothGattCharacteristic mBGCread;

    public BlueToothLoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = App.getsContext();
        if (null != mContext) {
            mBluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBtAdapter = mBluetoothManager.getAdapter();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("onStartCommand :" + intent.getAction());

        if (null != intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                String address = intent.getStringExtra(BLE_ADDRESS_PREF);
                if (ACTION_CONNECTED_CMD.equals(action)) {
                    LogUtil.d("onStartCommand address:" + address);
                    connectDevice(address);
                } else if (ACTION_READ_CMD.equals(action)) {
                    LogUtil.d("ACTION_READ_CMD:" + address);
                    readDataFromDevice(address);
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public boolean connectDevice(final String address) {
        LogUtil.d("connectDevice" + address);
        /*if (mGatt == null) {
            mGatt = device.connectGatt(mContext, false, gattCallback);
            //scanLeDevice(false);// will stop after first device detection
        }*/


        boolean returnTag = false;

        if (null != mBtAdapter && null != mBluetoothManager) {

            final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);

            if (null != device) {
                int connectionState = mBluetoothManager.getConnectionState(
                        device, BluetoothProfile.GATT);
                if (connectionState == BluetoothProfile.STATE_DISCONNECTED) {
                    mGatt = device.connectGatt(this, false,
                            gattCallback);
                    if (null != mGatt) {
                        returnTag = true;
                    } else {
                        returnTag = false;
                    }

                } else {
                    // 本身是链接状态
                    returnTag = true;
                }
            } else {
                returnTag = false;
            }
        } else {
            returnTag = false;
        }
        return returnTag;

    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtil.d("onConnectionStateChange" + "Status: " + status);
            Message msg = new Message();
            msg.what = MSG_SERCH_DONE;
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:

                    msg.arg1 = 0;
                    if (null != BlueToothFragment.mBTHandler) {
                        BlueToothFragment.mBTHandler.sendMessage(msg);
                    }
                    LogUtil.d("gattCallback" + "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    msg.arg1 = 1;
                    if (null != BlueToothFragment.mBTHandler) {
                        BlueToothFragment.mBTHandler.sendMessage(msg);
                    }
                    LogUtil.e("gattCallback" + "STATE_DISCONNECTED");
                    break;
                default:
                    LogUtil.e("gattCallback" + "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            /*List<BluetoothGattService> services = gatt.getServices();

            gatt.readCharacteristic(services.get(1).getCharacteristics().get
                    (0));*/

            dicoveredSetSerCha(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            String readStr = new String(characteristic.getValue());
            Message msg = new Message();
            msg.what = MSG_CHA_READ;
            msg.obj = readStr;
            if (null != BlueToothFragment.mBTHandler) {
                BlueToothFragment.mBTHandler.sendMessage(msg);
            }
            LogUtil.d("onCharacteristicRead" + readStr);

            gatt.disconnect();
        }
    };

    private void dicoveredSetSerCha(final BluetoothGatt gatt) {
        if (null != gatt) {
            List<BluetoothGattService> sListServices = gatt.getServices();

            LogUtil.d(sListServices.toString());
            boolean isnotify = false;
            boolean isread = false;
            boolean iswrite = false;

            for (BluetoothGattService bluetoothGattService : sListServices) {
                if (null != bluetoothGattService) {
                    if (UUID_READ_SERVICE.equals(bluetoothGattService.getUuid()) && isread) {
                        isread = true;

                        //INIT CHARACTER
                        BluetoothGattCharacteristic bluetoothGattCharacteristicRead = bluetoothGattService.getCharacteristic(UUID_READ_SERVICE_CHARACTER);
                        if (null != bluetoothGattCharacteristicRead) {
                            mBGCread = bluetoothGattCharacteristicRead;
                        }
                    } else if (UUID_WRITE_SERVICE.equals(bluetoothGattService.getUuid()) && iswrite) {
                        iswrite = true;

                        //INIT CHARACTER
                        BluetoothGattCharacteristic bluetoothGattCharacteristicWrite = bluetoothGattService.getCharacteristic(UUID_WRITE_SERVICE_CHARACTER);
                        if (null != bluetoothGattCharacteristicWrite) {
                            mBGCwrite = bluetoothGattCharacteristicWrite;
                        }
                    }
                }
            }

        }

    }

    private void readDataFromDevice(String address) {
        if (!TextUtils.isEmpty(address)) {
            requestCharacteristicValue(mBGCread, mGatt);
        }
    }

    public void requestCharacteristicValue(BluetoothGattCharacteristic ch,
                                           BluetoothGatt mBluetoothGattTag) {
        if (null != ch && null != mBluetoothGattTag) {
            LogUtil.d("requestCharacteristicValue......Begin");
            mBluetoothGattTag.readCharacteristic(ch);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGatt == null) {
            return;
        }
        mGatt.close();
    }
}
