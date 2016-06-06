package com.smartbracelet.com.smartbracelet.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.activity.App;
import com.smartbracelet.com.smartbracelet.activity.TestFlowActivity;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.network.AsyncResponse;
import com.smartbracelet.com.smartbracelet.network.SocketConnAsync;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LiteOrmDBUtil;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;
import com.smartbracelet.com.smartbracelet.util.Utils;
import com.smartbracelet.com.smartbracelet.view.AlertDialogCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo.yang on 2016/5/9.
 * Http推送服务类
 * 用于发送后台服务（消息推送103 以及坐标上报101）
 */
public class HttpPostService extends Service implements ConstDefine{

    int recoidTimes = 0;

    public SocketConnAsync socketConn = null;

    private SharedPreferencesHelper sharedPreferencesHelper;

    public HttpPostService(Context context) {
    }

    public HttpPostService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();


        sharedPreferencesHelper = SharedPreferencesHelper.getInstance();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("onStartCommand :" + intent.getAction());

        if (null != intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                String bleAddress = sharedPreferencesHelper.getString(BLE_ADDRESS_PREF);
                LogUtil.d("onStartCommand" + bleAddress);
                if (ACTION_GPS_POST_CMD.equals(action)) {
                    doAsyncPostToServer(bleAddress, TYPE_PUSH_MSG_PARM);
                    App.timesJudgeGps++;

                    if (3 == App.timesJudgeGps) {
                        App.timesJudgeGps = 0;
                        doAsyncPostToServer(bleAddress, TYPE_UPLOAD_LOCATION_PARM);
                    }

                } else if (ACTION_WARNING_POST_CMD.equals(action)) {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void doAsyncPostToServer(String bleAddress, String type) {
        try {
            socketConn = new SocketConnAsync(UTT_SETVER_URL);
            String[] params = new String[2];
            params[0] = type;
            if (type.equals(TYPE_UPLOAD_LOCATION_PARM)) {
                double longtitude = sharedPreferencesHelper.getDouble(LONGTITUDE_PREF);
                double latitude = sharedPreferencesHelper.getDouble(LATITUDE_PREF);
                LogUtil.d("HttpPostService doAsyncUpGps longtitude" + longtitude);
                params[1] = Utils.bindJOGps(latitude, longtitude, bleAddress).toString();
            } else if (type.equals(TYPE_PUSH_MSG_PARM)) {
                params[0] = TYPE_PUSH_MSG_PARM;
                params[1] = Utils.bindJOMsgPushTest(bleAddress).toString();
            } else if (type.equals(TYPE_POST_TELNUM_PARM)) {
                params[0] = TYPE_POST_TELNUM_PARM;
                params[1] = Utils.bindJOTelTest(bleAddress).toString();
            }

            socketConn.execute(params);
            socketConn.setOnAsyncResponse(new AsyncResponse() {
                //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量

                @Override
                public void onDataReceivedSuccess(String type, String data) {
                    recoidTimes ++;
                    Message message = new Message();
                    message.what = MSG_SEARCH_OUT;
                    message.arg1 = recoidTimes;
                    message.arg2 = 0;
                    TestFlowActivity.mBTHandler.sendMessage(message);
                    if (type.equals(TYPE_UPLOAD_LOCATION_PARM)) {

                    } else if (type.equals(TYPE_PUSH_MSG_PARM)) {
                        handlePushMsg(data);
                    } else if (type.equals(TYPE_POST_TELNUM_PARM)) {

                    }
                }

                @Override
                public void onDataReceivedFailed() {
                    Message message = new Message();
                    message.what = MSG_SEARCH_OUT;
                    message.arg1 = recoidTimes;
                    message.arg2 = 1;
                    TestFlowActivity.mBTHandler.sendMessage(message);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ProgramItem> list = new ArrayList<ProgramItem>();

    private void handlePushMsg(String postDetailRTR) {
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
                //Utils.notiy(mContext, showMessage, msg);
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
}
