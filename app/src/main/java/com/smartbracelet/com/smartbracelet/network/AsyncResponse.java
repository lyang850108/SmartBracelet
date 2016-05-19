package com.smartbracelet.com.smartbracelet.network;

public interface AsyncResponse {
    void onDataReceivedSuccess(String type, String data);
    void onDataReceivedFailed();

    /**
     * Created by leo.yang on 2016/5/9.
     */
    class Gps {
        private double wgLat;
        private double wgLon;
        public Gps(double wgLat, double wgLon) {
            setWgLat(wgLat);
            setWgLon(wgLon);
        }
        public double getWgLat() {
            return wgLat;
        }
        public void setWgLat(double wgLat) {
            this.wgLat = wgLat;
        }
        public double getWgLon() {
            return wgLon;
        }
        public void setWgLon(double wgLon) {
            this.wgLon = wgLon;
        }
        @Override
        public String toString() {
            return wgLat + "," + wgLon;
        }
    }
}
