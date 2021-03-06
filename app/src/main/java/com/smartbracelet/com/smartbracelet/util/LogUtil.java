package com.smartbracelet.com.smartbracelet.util;

import android.util.Log;

/**
 * Created by Yangli on 15-10-30.
 * 日志工具类
 */
public class LogUtil {
    private static final String TEST_TAG = "zjltest";
    public static void d(String tag, String msg) {
        if (Utils.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (Utils.DEBUG) {
            Log.d(TEST_TAG, msg);
        }
    }

    public static void e(String msg) {
        if (Utils.DEBUG) {
            Log.e(TEST_TAG, msg);
        }
    }
}
