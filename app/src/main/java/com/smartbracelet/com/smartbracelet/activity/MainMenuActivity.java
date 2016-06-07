package com.smartbracelet.com.smartbracelet.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.bean.GpsBean;
import com.smartbracelet.com.smartbracelet.service.LocationService;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;
import com.smartbracelet.com.smartbracelet.util.Utils;
import com.smartbracelet.com.smartbracelet.view.AlertDialogCreator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yangli on 16-05-31.
 * 应用的主页面
 * 包含了5个功能单元
 * 拍照 位置 手环 消息 打卡
 */
public class MainMenuActivity extends AppCompatActivity implements ConstDefine {
    private Activity pThis;

    private LocationService locationService;
    private AlertDialog mAlertDialog;


    @Bind(R.id.func1)
    ImageView imageButton1;

    @Bind(R.id.func2)
    ImageView imageButton2;

    @Bind(R.id.func3)
    ImageView imageButton3;

    @Bind(R.id.func4)
    ImageView imageButton4;

    @Bind(R.id.func5)
    ImageView imageButton5;

    //public static double latitude = 0;
    //public static double longtitude = 0;

    private SharedPreferencesHelper sharedPreferencesHelper;

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sbLongtitude = new StringBuffer(256);
                StringBuffer sbLatitude = new StringBuffer(256);
                //sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                //sb.append(location.getTime());
                double latitude = location.getLatitude();
                double longtitude = location.getLongitude();
                LogUtil.d("onReceiveLocation latitude: " + latitude + " longtitude: " + longtitude);

                //Store the preference
                if (null != sharedPreferencesHelper) {
                    sharedPreferencesHelper.putDouble(LATITUDE_PREF, latitude);
                    sharedPreferencesHelper.putDouble(LONGTITUDE_PREF, longtitude);
                }

                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */

                int locType = location.getLocType();


                float radius = location.getRadius();
                String countyCode = location.getCountryCode();
                String county = location.getCountry();
                String cityCode = location.getCityCode();
                String city = location.getCity();
                String distrinct = location.getDistrict();
                String street = location.getStreet();
                String address = location.getAddrStr();
                String locationDescrible = location.getLocationDescribe();
                Float direction = location.getDirection();

                GpsBean.getInstance().setParams(locType, latitude, longtitude, radius, countyCode,
                        county, cityCode, city, distrinct, street, address, locationDescrible, direction);
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_main_menu);
        pThis = this;
        ButterKnife.bind(pThis);


        sharedPreferencesHelper = SharedPreferencesHelper.getInstance();

        initGPS(pThis);

    }

    @Override
    protected void onStart() {
        super.onStart();

        locationService = ((App) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != locationService) {
            locationService.start();
        }
    }
    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(pThis);
    }

    @OnClick(R.id.func1)
    public void onFunc1Click(View view) {

        startActivity(new Intent(this, PersonalInforActivity.class));
    }

    @OnClick(R.id.func2)
    public void onFunc2Click(View view) {
        startActivity(new Intent(this, GpsInformationActivity.class));
    }

    @OnClick(R.id.func3)
    public void onFunc3Click(View view) {
        startActivity(new Intent(this, DeviceManagerActivity.class));
    }

    @OnClick(R.id.func4)
    public void onFunc4Click(View view) {
        startActivity(new Intent(this, ProgramItemActivity.class));
    }

    @OnClick(R.id.func5)
    public void onFunc5Click(View view) {
        startActivity(new Intent(this, CheckInActivity.class));
    }


    /**
     * 初始化GPS
     * @param context
     */
    private void initGPS(final Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "请打开GPS",
                    Toast.LENGTH_SHORT).show();

            if (null != pThis) {
                if (null != mAlertDialog) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
                AlertDialogCreator.getInstance().setmButtonOnClickListener(mDialogListener);
                mAlertDialog = AlertDialogCreator
                        .getInstance()
                        .createAlertDialogType(
                                pThis,
                                getString(R.string.tip_title),
                                getString(R.string.open_gps), DIALOG_TYPE_GPS);
                mAlertDialog.show();
            }

        } else {
            // 弹出Toast
//			Toast.makeText(TrainDetailsActivity.this, "GPS is ready",
//					Toast.LENGTH_LONG).show();
//			// 弹出对话框
//			new AlertDialog.Builder(this).setMessage("GPS is ready")
//					.setPositiveButton("OK", null).show();
        }
    }

    /**
     * 对话框监听器
     */
    private AlertDialogCreator.ButtonOnClickListener mDialogListener = new AlertDialogCreator.ButtonOnClickListener() {
        @Override
        public void buttonTrue() {

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

    private SystemBarTintManager tintManager;
    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            //int color = pThis.getColor(R.color.color_theme);
            //tintManager.setStatusBarTintColor(color);
            tintManager.setStatusBarTintDrawable(getDrawable(R.mipmap.bg_main_menu));
            tintManager.setStatusBarTintEnabled(true);
        }
    }
}
