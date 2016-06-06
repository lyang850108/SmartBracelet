package com.smartbracelet.com.smartbracelet.bean;

/**
 * Created by Yang on 16/5/28.
 * GPS数据存储数据载体
 * 采用单例模式
 */
public class GpsBean {

    private int locType;
    private double laTitude;
    private double longTitude;
    private float radius;
    private String countyCode;
    private String county;
    private String cityCode;
    private String city;
    private String distrinct;
    private String street;
    private String address;
    private String locationDescrible;
    private float direction;

    public GpsBean() {
    }

    private static GpsBean gpsBean = null;

    public static GpsBean getInstance() {
        if (gpsBean == null) {
            synchronized (GpsBean.class) {
                if (gpsBean == null) {
                    gpsBean = new GpsBean();
                }
            }
        }
        return gpsBean;
    }

    public void setParams(int locType, double laTitude, double longTitude, float radius, String countyCode
            , String county, String cityCode, String city, String distrinct, String street, String address,
                          String locationDescrible, float deriction) {
        this.locType = locType;
        this.laTitude = laTitude;
        this.longTitude = longTitude;
        this.radius = radius;
        this.countyCode = countyCode;
        this.county = county;
        this.cityCode = cityCode;
        this.city = city;
        this.distrinct = distrinct;
        this.street = street;
        this.address = address;
        this.locationDescrible = locationDescrible;
        this.direction = deriction;

    }

    public int getLocType() {
        return locType;
    }

    public double getLatitude() {
        return laTitude;
    }

    public double getLongitude() {
        return longTitude;
    }

    public float getRadius() {
        return radius;
    }

    public String getCountryCode() {
        return countyCode;
    }

    public String getCountry() {
        return county;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return distrinct;
    }

    public String getStreet() {
        return street;
    }

    public String getAddrStr() {
        return address;
    }

    public String getLocationDescribe() {
        return locationDescrible;
    }

    public float getDirection() {
        return direction;
    }
}
