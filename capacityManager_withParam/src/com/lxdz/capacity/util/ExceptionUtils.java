package com.lxdz.capacity.util;

/**
 * 异常处理类
 * @author Administrator
 *
 */
public class ExceptionUtils extends Exception{
	
	/**
	 * 指定的WIFI 热点不存在
	 */
	public static final int NOT_EXIST_WIFI = 0;
	
	/**
	 * WIFI密码错误
	 */
	public static final int WIFI_PASSWORD_ERROR = 1;
	
	public ExceptionUtils(int ExceptionCode, String exceptionMessage) {
		super(exceptionMessage);
	}

}
