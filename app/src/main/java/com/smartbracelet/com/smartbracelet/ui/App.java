package com.smartbracelet.com.smartbracelet.ui;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import com.smartbracelet.com.smartbracelet.BuildConfig;
import com.smartbracelet.com.smartbracelet.network.RetrofitService;
import com.smartbracelet.com.smartbracelet.service.LocationService;
import com.smartbracelet.com.smartbracelet.util.LiteOrmDBUtil;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.PreferenceUtils;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by Yangli on 16-03-20.
 */
public class App extends Application {
    private static Context sContext;
    private static RetrofitService sRetrofitService;
    public static int sScreenHeight;
    public LocationService locationService;
    public Vibrator mVibrator;
    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceUtils.init(this);
        LiteOrmDBUtil.init(this);
//        LiteOrmDBUtil.test();
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
        super.onTerminate();

    }
}
