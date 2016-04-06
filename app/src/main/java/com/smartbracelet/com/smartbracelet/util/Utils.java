package com.smartbracelet.com.smartbracelet.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.smartbracelet.com.smartbracelet.ui.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Yangli on 16-04-05.
 */
public class Utils {
    public static final boolean DEBUG = true;
    public static final long SWIPE_BEHAVIOR_ANIMATION_TIME = 200;
    public static void showShortToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showInputMethod(View view) {
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideInputMethod(View view) {
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static JSONObject bindJOGps(double latitude, double longtitude) {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            subJsonObject.put("deviceid", "686c0888-34a9-43b1-86da-9bb7feb90122");
            subJsonObject.put("x", latitude);
            subJsonObject.put("Y", longtitude);
            subJsonObject.put("imei", getImei());
            subJsonObject.put("phonenum", getTelNum());
            subJsonObject.put("time", getTime());
            jsonObject.put("method", 101);
            jsonObject.put("params", subJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject bindJOTel(double latitude, double longtitude) {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            subJsonObject.put("deviceid", "686c0888-34a9-43b1-86da-9bb7feb90122");
            subJsonObject.put("x", latitude);
            subJsonObject.put("y", longtitude);
            subJsonObject.put("imei", getImei());
            subJsonObject.put("phonenum", "18576625591");
            subJsonObject.put("time", "20160321142336");
            jsonObject.put("method", 101);
            jsonObject.put("params", subJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String getTelNum() {
        TelephonyManager telephonyManager = (TelephonyManager) App.getsContext().getSystemService(Context.TELEPHONY_SERVICE);
        String num = telephonyManager.getLine1Number();
        if (null == num) {
            num = "13823209476";
        }
        LogUtil.d(num);
        return num;
    }

    public static String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) App.getsContext().getSystemService(Context.TELEPHONY_SERVICE);
        //GSM IMEI; CDMA MEID
        String imei = telephonyManager.getDeviceId();
        if (null == imei) {
            imei = "351672070095881";
        }
        LogUtil.d(imei);
        return imei;
    }

    public static long getTime() {
        long time=System.currentTimeMillis();
        return time;
    }

    public static String convertUrl (String url) {
        String rtn = null;
        try {
            rtn = URLEncoder.encode(url, "GBK");
            LogUtil.d(rtn);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return rtn;
    }
}
