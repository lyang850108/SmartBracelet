package com.smartbracelet.com.smartbracelet.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.activity.App;
import com.smartbracelet.com.smartbracelet.activity.ProgramItemActivity;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yangli on 16-04-05.
 */
public class Utils implements ConstDefine{
    public static final boolean DEBUG = true;
    private static final int NOTIFICATION_ID = 123;
    private static String mPhoneAddress = "";
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


    public static JSONObject bindJOGps(double latitude, double longtitude, String address) {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", Integer.toString(101));
            subJsonObject.put("DeviceID", address);
            subJsonObject.put("Longitude", "" + longtitude);
            subJsonObject.put("Latitude", "" + latitude);
            subJsonObject.put("Mac", "9E:33:44:12:90:66");
            subJsonObject.put("IMEI", getImei());
            subJsonObject.put("PhoneNumber", getTelNumber());
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
            subJsonObject.put("PhoneNumber", getTelNumber());
            subJsonObject.put("CreateTime", getTime());
            jsonObject.put("params", subJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject bindJOTelTest(String macAddress) {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", Integer.toString(106));
            subJsonObject.put("DeviceID", macAddress);
            subJsonObject.put("Mac", "9E:33:44:12:90:66");
            subJsonObject.put("IMEI", getImei());
            LogUtil.d("bindJOTelTest telNumer: " + getTelNumber());
            subJsonObject.put("PhoneNumber", getTelNumber());
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
            subJsonObject.put("PhoneNumber", getTelNumber());
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

    public static JSONObject bindJOWarningTest(String macAddress, int warningType) {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", Integer.toString(102));
            subJsonObject.put("DeviceID", macAddress);
            subJsonObject.put("AlarmTypeID", warningType);
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

    public static JSONObject bindJOMsgPushTest(String address) {
        JSONObject subJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", Integer.toString(103));
            subJsonObject.put("DeviceID", address);
            jsonObject.put("params", subJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String getTelNum(SharedPreferencesHelper sharedPreferencesHelper) {
        TelephonyManager telephonyManager = (TelephonyManager) App.getsContext().getSystemService(Context.TELEPHONY_SERVICE);
        String num = telephonyManager.getLine1Number();
        if (null == num || TextUtils.isEmpty(num)) {

            num = sharedPreferencesHelper.getString(SP_PHONE_NUMBER);
            if (null == num || TextUtils.isEmpty(num)) {
                return "";
            } else {
                mPhoneAddress = num;
            }
        } else {
            try {
                num = checkPhoneNum(num);

                mPhoneAddress = num;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        LogUtil.d("getTelNum" + num);
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

    public  static int parseJsonResult (String json) {
        if (null == json) {
            return -1;
        }
        int result = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject params = jsonObject.getJSONObject("params");
            result = params.getInt("Result");
            LogUtil.d("parseJsonResult = " + result);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public  static int parseMsgTypeResult (String json) {
        int result = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject params = jsonObject.getJSONObject("params");
            result = params.getInt("MsgTypeID");
            LogUtil.d("parseMsgTypeResult = " + result);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*public  static int parseMsgTypeStatus (String json) {
        int result = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray marks = jsonObject.getJSONArray("params");
            for(int i=0; i< marks.length(); i++){
                JSONObject mark = (JSONObject)marks.get(i);
                String showMessage = mark.getString("MsgTypeID");

                String replyId = mark.getString("Msg");
                String time = mark.getString("CreateTime");
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }*/

    public static String getTelNumber() {
        return mPhoneAddress;
    }
    public static void setTelNumber(String address) {
        mPhoneAddress =  address;
    }

    public static byte[] parseHexStringToBytes(final String hex) {
        String tmp = hex.substring(2).replaceAll("[^[0-9][a-f]]", "");
        byte[] bytes = new byte[tmp.length() / 2]; // every two letters in the string are one byte finally

        String part = "";

        for(int i = 0; i < bytes.length; ++i) {
            part = "0x" + tmp.substring(i*2, i*2+2);
            bytes[i] = Long.decode(part).byteValue();
        }

        return bytes;
    }

    public static void notifyMessageComing(Context context, String title, String body) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        //通知消息与Intent关联
        Intent intent = new Intent(context, ProgramItemActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getsContext(), 1, intent, Notification.FLAG_AUTO_CANCEL);
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(body)
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                .setTicker(body) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_MAX) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_about_contact);//设置通知小ICON


        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }


    protected static String checkPhoneNum(String phoneNum) throws Exception {

        Pattern p1 = Pattern.compile("^((\\+{0,1}86){0,1})1[0-9]{10}");
        Matcher m1 = p1.matcher(phoneNum);
        if (m1.matches()) {
            Pattern p2 = Pattern.compile("^((\\+{0,1}86){0,1})");
            Matcher m2 = p2.matcher(phoneNum);
            StringBuffer sb = new StringBuffer();
            while (m2.find()) {
                m2.appendReplacement(sb, "");
            }
            m2.appendTail(sb);
            return sb.toString();

        } else {
            throw new Exception("The format of phoneNum "+phoneNum+"  is not correct!Please correct it");
        }

    }


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGpsOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent intent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent); // 设置完成后返回到原来的界面
    }

    public static boolean isMobileNO(String mobiles){

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        System.out.println(m.matches()+"---");

        return m.matches();

    }


}
