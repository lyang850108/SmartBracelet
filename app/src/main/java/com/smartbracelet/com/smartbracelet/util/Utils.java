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
import java.text.SimpleDateFormat;
import java.util.Date;

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
            jsonObject.put("method", Integer.toString(101));
            subJsonObject.put("DeviceID", "086c8f9d11ff");
            subJsonObject.put("Longitude", "" + longtitude);
            subJsonObject.put("Latitude", "" + latitude);
            subJsonObject.put("Mac", "9E:33:44:12:90:66");
            subJsonObject.put("IMEI", getImei());
            subJsonObject.put("PhoneNumber", getTelNum());
            subJsonObject.put("CreateTime", getTime());
            jsonObject.put("params", subJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject bindJOTel() {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", Integer.toString(106));
            subJsonObject.put("DeviceID", "");
            subJsonObject.put("Mac", "9E:33:44:12:90:66");
            subJsonObject.put("IMEI", getImei());
            subJsonObject.put("PhoneNumber", getTelNum());
            subJsonObject.put("CreateTime", getTime());
            jsonObject.put("params", subJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject bindJOGetId() {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", Integer.toString(104));
            subJsonObject.put("DeviceID", "");
            subJsonObject.put("Mac", "9E:33:44:12:90:66");
            subJsonObject.put("IMEI", getImei());
            subJsonObject.put("PhoneNumber", getTelNum());
            subJsonObject.put("CreateTime", getTime());
            jsonObject.put("params", subJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static JSONObject bindJOWarning() {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", Integer.toString(102));
            subJsonObject.put("DeviceID", "086c8f9d11ff");
            subJsonObject.put("AlarmTypeID", "1");
            subJsonObject.put("CreateTime", getTime());
            jsonObject.put("params", subJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject bindJOMsgPush() {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", Integer.toString(103));
            subJsonObject.put("DeviceID", "086c8f9d11ff");
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

    /*public static long getTime() {
        long time=System.currentTimeMillis();
        return time;
    }*/

    public static String convertUrl (String url) {
        String rtn = null;
        try {
            rtn = URLEncoder.encode(url, "UTF-8");
            LogUtil.d(rtn);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return rtn;
    }

    public static String getTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeFotmat = sdf.format(date);
        return timeFotmat;
    }
}
