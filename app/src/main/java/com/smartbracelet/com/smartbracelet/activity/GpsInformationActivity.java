package com.smartbracelet.com.smartbracelet.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.bean.GpsBean;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GpsInformationActivity extends AppCompatActivity {
    @Bind(R.id.gps_infor_tx)
    TextView gpsInforTx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_information);
        ButterKnife.bind(this);
        setTitle("实时定位信息");
        setupActionBar();
        readInfor();
    }

    private void readInfor() {
        StringBuffer sb = new StringBuffer(256);
        /**
         * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
         * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
         */
        GpsBean gpsBean = GpsBean.getInstance();
        sb.append("\nLocType : ");
        sb.append(gpsBean.getLocType());
        sb.append("\nlatitude : ");
        sb.append(gpsBean.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(gpsBean.getLongitude());
        sb.append("\nradius : ");
        sb.append(gpsBean.getRadius());
        sb.append("\nCountryCode : ");
        sb.append(gpsBean.getCountryCode());
        sb.append("\nCountry : ");
        sb.append(gpsBean.getCountry());
        sb.append("\ncitycode : ");
        sb.append(gpsBean.getCityCode());
        sb.append("\ncity : ");
        sb.append(gpsBean.getCity());
        sb.append("\nDistrict : ");
        sb.append(gpsBean.getDistrict());
        sb.append("\nStreet : ");
        sb.append(gpsBean.getStreet());
        sb.append("\naddr : ");
        sb.append(gpsBean.getAddrStr());
        sb.append("\nDescribe: ");
        sb.append(gpsBean.getLocationDescribe());
        sb.append("\nDirection(not all devices have value): ");
        sb.append(gpsBean.getDirection());

        gpsInforTx.setText(sb);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
}
