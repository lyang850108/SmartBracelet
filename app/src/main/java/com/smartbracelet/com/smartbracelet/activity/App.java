package com.smartbracelet.com.smartbracelet.activity;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Vibrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import com.smartbracelet.com.smartbracelet.BuildConfig;
import com.smartbracelet.com.smartbracelet.network.PollingUtils;
import com.smartbracelet.com.smartbracelet.network.RetrofitService;
import com.smartbracelet.com.smartbracelet.service.HttpPostService;
import com.smartbracelet.com.smartbracelet.service.LocationService;
import com.smartbracelet.com.smartbracelet.util.LiteOrmDBUtil;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.ReschedulableTimerTask;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by Yangli on 16-03-20.
 * 应用启动入口，完成一些初始化工作，启动服务，初始化数据库
 */
public class App extends Application {
    private static Context sContext;
    private static RetrofitService sRetrofitService;
    public static int sScreenHeight;
    public LocationService locationService;
    public Vibrator mVibrator;
    String ACTION_GPS_POST_CMD = "ACTION_GPS_POST_CMD";


    //Alarm Manager test
    public static AlarmManager alarmManager;


    public static Timer timer = new Timer();
    /**
     * Self-define
     */
    public static ReschedulableTimerTask timerTask;


    public static int timesJudgeGps;

    //public static boolean isFirstLuanched = true;

    //Timetask是否运行
    public static boolean isScanningDevice = false;
    @Override
    public void onCreate() {
        super.onCreate();
        //PreferenceUtils.init(this);
        setUpSharedPreferencesHelper(this);

        LiteOrmDBUtil.init(this);
        sContext = getApplicationContext();
        initRetrofitService();
        sScreenHeight = getResources().getDisplayMetrics().heightPixels;
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        //SDKInitializer.initialize(getApplicationContext());

    }

    /**
     * 初始化SharedPreferences
     *
     * @param context 上下文
     */
    private void setUpSharedPreferencesHelper(Context context) {
        SharedPreferencesHelper.getInstance().Builder(context);

    }


    private void initRetrofitService() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.SEVER_URL)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(okHttpClient))
                .setErrorHandler(new MyErrorHandler())
                .build();
        sRetrofitService = restAdapter.create(RetrofitService.class);
    }

    class MyErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            LogUtil.e("handleError:" + cause.getKind());
            Response r = cause.getResponse();
            if (r != null && r.getStatus() == 401) {
                LogUtil.e("handleError,getStatus:" + r.getStatus());
            }
            return cause;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Context getsContext() {
        return sContext;
    }

    public static App getInstance() {
        return (App)sContext;
    }

    public static RetrofitService getRetrofitService() {
        return sRetrofitService;
    }

    @Override
    public void onTerminate() {
        LogUtil.d("onTerminate");

        //Stop the post action

        PollingUtils.stopPollingService(this, HttpPostService.class, ACTION_GPS_POST_CMD);
        //alarmManager.cancel(pendingIntent);
        /*if (null != timerTask) {
            timerTask.cancel();
        }

        if (null != timerTaskWarning) {
            timerTaskWarning.cancel();
        }*/
        super.onTerminate();
    }


}
