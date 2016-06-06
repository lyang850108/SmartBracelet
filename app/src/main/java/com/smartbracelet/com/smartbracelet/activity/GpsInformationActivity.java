package com.smartbracelet.com.smartbracelet.activity;

import android.content.Intent;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.bean.GpsBean;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GpsInformationActivity extends AppCompatPreferenceActivity implements ConstDefine{
    private Preference mTypePrefs;
    private Preference mLongtitudePrefs;
    private Preference mLatitudePrefs;
    private Preference mRadiusPrefs;
    private Preference mCountryCodePrefs;
    private Preference mCountryPrefs;
    private Preference mCityCodeCodePrefs;
    private Preference mCityCodePrefs;
    private Preference mDistrictPrefs;
    private Preference mStreetPrefs;
    private Preference mAddressPrefs;
    private Preference mDescriptionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("实时定位信息");
        setupActionBar();
        loadPrefs();
        readInfor();
    }

    private void readInfor() {
        StringBuffer sb = new StringBuffer(256);
        /**
         * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
         * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
         */
        GpsBean gpsBean = GpsBean.getInstance();
        sb.append("\n类型 : ");
        mTypePrefs.setSummary(Integer.toString(gpsBean.getLocType()));
        sb.append(gpsBean.getLocType());
        sb.append("\n纬度 : ");
        mLatitudePrefs.setSummary(gpsBean.getLatitude() + "");
        sb.append(gpsBean.getLatitude());
        sb.append("\n经度 : ");
        mLongtitudePrefs.setSummary(gpsBean.getLongitude() + "");
        sb.append(gpsBean.getLongitude());
        sb.append("\n半径 : ");
        mRadiusPrefs.setSummary(gpsBean.getRadius() + "");
        sb.append(gpsBean.getRadius());
        sb.append("\n国家码 : ");
        mCountryCodePrefs.setSummary(gpsBean.getCountryCode());
        sb.append(gpsBean.getCountryCode());
        sb.append("\n国家 : ");
        mCountryPrefs.setSummary(gpsBean.getCountry());
        sb.append(gpsBean.getCountry());
        sb.append("\n城市码 : ");
        mCityCodeCodePrefs.setSummary(gpsBean.getCityCode());
        sb.append(gpsBean.getCityCode());
        sb.append("\n城市 : ");
        mCityCodePrefs.setSummary(gpsBean.getCity());
        sb.append(gpsBean.getCity());
        sb.append("\n地区 : ");
        mDistrictPrefs.setSummary(gpsBean.getDistrict());
        sb.append(gpsBean.getDistrict());
        sb.append("\n街道 : ");
        mAddressPrefs.setSummary(gpsBean.getStreet());
        sb.append(gpsBean.getStreet());
        sb.append("\n地址 : ");
        mAddressPrefs.setSummary(gpsBean.getAddrStr());
        sb.append(gpsBean.getAddrStr());
        mDescriptionPrefs.setSummary(gpsBean.getLocationDescribe());
        sb.append("\n概况: ");
        sb.append(gpsBean.getLocationDescribe());
        /*sb.append("\nDirection(not all devices have value): ");
        sb.append(gpsBean.getDirection());*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainMenuActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPrefs() {
        addPreferencesFromResource(R.xml.pref_gps_infor);

        mAddressPrefs = (Preference) findPreference(KEY_INFOR_ADDR);
        mTypePrefs = (Preference) findPreference(KEY_INFOR_TYPE);
        mCityCodeCodePrefs = (Preference) findPreference(KEY_INFOR_CITYCODE);
        mCityCodePrefs = (Preference) findPreference(KEY_INFOR_CITY);
        mCountryCodePrefs = (Preference) findPreference(KEY_INFOR_COUNTRYCODE);
        mCountryPrefs = (Preference) findPreference(KEY_INFOR_COUNTRY);
        mLatitudePrefs = (Preference) findPreference(KEY_INFOR_LATITUDE);
        mLongtitudePrefs = (Preference) findPreference(KEY_INFOR_LONGTITUDE);
        mRadiusPrefs = (Preference) findPreference(KEY_INFOR_RADIUS);
        mDescriptionPrefs = (Preference) findPreference(KEY_INFOR_DES);
        mDistrictPrefs = (Preference) findPreference(KEY_INFOR_DISTRICT);
        mStreetPrefs = (Preference) findPreference(KEY_INFOR_STREET);
    }
}
