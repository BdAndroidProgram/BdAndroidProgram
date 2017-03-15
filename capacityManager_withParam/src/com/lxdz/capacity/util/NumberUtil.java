package com.lxdz.capacity.util;

import java.text.DecimalFormat;

public class NumberUtil {
	
	/**
	 * 把小數轉換成指定位數的字符串并返回
	 * @param num
	 * @return
	 */
	public static String getDisplayNumber(double num) {
		DecimalFormat zeroDecimal  = new DecimalFormat("##0");
		DecimalFormat oneDecimal  = new DecimalFormat("##0.0");
		DecimalFormat twoDecimal  = new DecimalFormat("##0.00");
		DecimalFormat threeFormat  = new DecimalFormat("##0.000");
		DecimalFormat fourFormat  = new DecimalFormat("##0.0000");
		
		if (num<10) {
			return fourFormat.format(num);
		} else if(num <100) {
			return threeFormat.format(num);
		} else if(num < 1000) {
			return twoDecimal.format(num);
		} else if(num < 10000) {
			return oneDecimal.format(num);
		} else {
			return zeroDecimal.format(num);
		}
	}

}
