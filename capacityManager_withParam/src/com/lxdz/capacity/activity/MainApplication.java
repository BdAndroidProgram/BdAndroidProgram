package com.lxdz.capacity.activity;


import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

	/**
	 * ȫ�ֵ�������.
	 */
	private static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mContext = getApplicationContext();
		
	}	
	
	/**��ȡContext.
	 * @return
	 */
	public static Context getContext(){
		return mContext;
	}
	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	
}

