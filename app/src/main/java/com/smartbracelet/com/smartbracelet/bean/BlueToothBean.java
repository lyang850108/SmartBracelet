package com.smartbracelet.com.smartbracelet.bean;

/**
 * Created by Yang on 16/6/5.
 * 蓝牙设备存储数据载体
 * 采用单例模式
 */
public class BlueToothBean {

    private int batteryLevel;
    private String name;
    private long clickTimes;

    public BlueToothBean() {
    }

    private static BlueToothBean blueToothBean = null;

    public static BlueToothBean getInstance() {
        if (blueToothBean == null) {
            synchronized (BlueToothBean.class) {
                if (blueToothBean == null) {
                    blueToothBean = new BlueToothBean();
                }
            }
        }
        return blueToothBean;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public long getClickTimes() {
        return clickTimes;
    }

    public void setClickTimes(long clickTimes) {
        this.clickTimes = clickTimes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
