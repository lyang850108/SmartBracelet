package com.smartbracelet.com.smartbracelet.util;

import java.util.UUID;

import static java.util.UUID.fromString;

/**
 * Created by leo.yang on 2016/4/15.
 */
public interface ConstDefine {

    String ACTION_CONNECTED_CMD = "ACTION_SERVICE_CONNECTED_CMD";

    String ACTION_READ_CMD = "ACTION_SERVICE_READ_CMD";

    String BLE_ADDRESS = "mAddress";

    String BLE_RSSI = "mRssi";

    final int MSG_SEARCH_OUT = 0;
    final int MSG_SERCH_DONE = 1;
    final int MSG_CHA_READ = 2;

    UUID UUID_READ_SERVICE = fromString("000055ff-0000-1000-8000-00805f9b34fb");

    UUID UUID_READ_SERVICE_CHARACTER = fromString("000033f2-0000-1000-8000-00805f9b34fb");
}
