package com.example.littlecar;

import android.app.Application; 
import android.bluetooth.BluetoothDevice;

public class MyApplication extends Application{
	private String mDeviceName;
	private BluetoothDevice device = null;
	public void setDeviceName(String name){
		mDeviceName = name;
	}
	public String getDeviceName(){
		return mDeviceName;
	}
	public void setDevice(BluetoothDevice d){
		device = d;
	}
	public BluetoothDevice getDevice(){
		return device;
	}
	
}
