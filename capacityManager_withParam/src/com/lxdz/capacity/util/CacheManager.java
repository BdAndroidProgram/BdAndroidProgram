package com.lxdz.capacity.util;

import com.lxdz.capacity.activity.MainApplication;
import com.lxdz.capacity.model.TransformerInfo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用来保存基本配置信息
 * @author Administrator
 *
 */
public class CacheManager {
	
	public static final String PARAM_CONFIG = "paramConfigCache";
	
	private static CacheManager cacheManager;
	
	public static int currentTest = -1;
	
	public static TransformerInfo transformerInfo = null;
	
	public static String debugPassword = "";
	/**
	 * 当前温度
	 */
	public static final String CURRENT_TEMPERATUER = "currentTemperature";
	public static final String CAPACITY_TRANSFORM_TYPE ="capacityTransformType";
	// public static final String RATED_HIGH_VOLTAGE = "ratedHighVoltage";
	// public static final String RATED_LOW_VOLTAGE = "ratedLowVoltage";
	public static final String IMPEDANCE_VOLTAGE = "impendanceVoltage";
	public static final String TAP_GEAR = "tapGear";
	public static final String CONNECTION_GROUP = "connectionGroup";
	public static final String FIRST_VOLTAGE = "firstVoltage";
	public static final String SECOND_VOLTAGE = "secondVoltage";
	public static final String RATED_CAPACITY = "ratedCapacity";
	public static final String CORRECTION_COEFFICIENT = "correctionCoefficient";
	public static final String NOLOAD_TRANSFORMER_TYPE = "noloadTransformerType";
	public static final String TEST_METHOD = "testMethod";
	public static final String BATTERY_LEVEL1 = "batteryLevel1";
	public static final String BATTERY_LEVEL2 = "batteryLevel2";
	
	public static final String TRANSFORMER_CODE = "transformerCode";
	public static final String TRANSFORMER_ID = "transformerId";
	public static final String TEST_USER = "testUser";
	public static final String USER_NAME = "userName";
	public static final String USER_ADDRESS = "userAddress";
	
	
	public static final String WIFI_NAME = "wifiName";
	public static final String WIFI_PASSWORD = "wifiPassword";
	
	private static Context context = null;
	private CacheManager(){
		
	}
	
	public static CacheManager getCacheManager() {
		if(cacheManager == null) {
			cacheManager = new CacheManager();
		}
		return cacheManager;
	}
	
/*	private byte[] capacityData;
	private byte[] noloadData;
	private byte[] loadData;*/
	public static  byte[] params;
	
	
	/**
	 * 读取字符类型的数据
	 * @param context
	 * @param shareName
	 * @param fieldName
	 * @return
	 */
	public static String getString(String shareName, String fieldName, String defaultValue){
		context = MainApplication.getContext();
		SharedPreferences share = context.getSharedPreferences(shareName, 0);
		return share.getString(fieldName, defaultValue);
	}
	
	/**
	 * 读取Int类型的数据
	 * @param context
	 * @param shareName
	 * @param fieldName
	 * @return
	 */
	public static int getInt( String shareName, String fieldName, int defaultValue) {
		context = MainApplication.getContext();
		SharedPreferences share = context.getSharedPreferences(shareName, 0);
		return share.getInt(fieldName, defaultValue);
	}
	
	/**
	 * 读取Float类型的数据
	 * @param context
	 * @param shareName
	 * @param fieldName
	 * @return
	 */
	public static float getFloat( String shareName, String fieldName, int defaultValue) {
		context = MainApplication.getContext();
		SharedPreferences share = context.getSharedPreferences(shareName, 0);
		return share.getFloat(fieldName, defaultValue);
	}
	
	
	
	
	/**
	 * 存放string类型的数据
	 * @param context
	 * @param shareName
	 * @param fieldName
	 * @param value
	 */
	public static void putString( String shareName, String fieldName, String value) {
		context = MainApplication.getContext();
		SharedPreferences share = context.getSharedPreferences(shareName, 0);
		share.edit().putString(fieldName, value).commit();
	}
	
	/**
	 * 存放int类型的数据
	 * @param context
	 * @param shareName
	 * @param fieldName
	 * @param value
	 */
	public static void putInt( String shareName, String fieldName, int value) {
		context = MainApplication.getContext();
		SharedPreferences share = context.getSharedPreferences(shareName, 0);
		share.edit().putInt(fieldName, value).commit();
	}
	
	/**
	 * 存放float类型数据
	 * @param context
	 * @param shareName
	 * @param fieldName
	 * @param value
	 */
	public static void putFloat( String shareName, String fieldName, float value) {
		context = MainApplication.getContext();
		SharedPreferences share = context.getSharedPreferences(shareName, 0);
		share.edit().putFloat(fieldName, value).commit();
	}

/*	public byte[] getCapacityData() {
		return capacityData;
	}

	public void setCapacityData(byte[] capacityData) {
		this.capacityData = capacityData;
	}

	public byte[] getNoloadData() {
		return noloadData;
	}

	public void setNoloadData(byte[] noloadData) {
		this.noloadData = noloadData;
	}

	public byte[] getLoadData() {
		return loadData;
	}

	public void setLoadData(byte[] loadData) {
		this.loadData = loadData;
	}*/

}
