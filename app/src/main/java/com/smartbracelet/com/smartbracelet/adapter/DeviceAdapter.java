package com.smartbracelet.com.smartbracelet.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 16/4/16.
 */
public class DeviceAdapter {
    ArrayAdapter<String> mAdapter;

    public DeviceAdapter(Context context, int layout, List<BluetoothDevice> list) {
        mAdapter = new ArrayAdapter<String>(context, layout, getNameList(list));
    }

    private List<String> getNameList(List<BluetoothDevice> list) {
        List<String> names = new ArrayList<>();

        for (BluetoothDevice d : list) {
            names.add(d.getName());
        }

        return names;
    }

    public ArrayAdapter<String> getArrayAdapter() {
        return mAdapter;
    }
}
