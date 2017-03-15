package com.lxdz.capacity.socket;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.CoefficientInfo;
import com.lxdz.capacity.model.GetCapacityTest;
import com.lxdz.capacity.model.LoadResultInfo;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.ParamTestInfo;
import com.lxdz.capacity.model.TransformerInfo;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;

public class TranslateData {
	public static final String PARAM_SETTING = "com.lxdz.capacityManager.ParamSetting";
	public static void translateParam(byte[] data, Context context) {
		if (data == null || data.length == 0)
			return;
		Intent paramIntent = new Intent(PARAM_SETTING);
		context.sendBroadcast(paramIntent);
		if(CacheManager.transformerInfo == null) CacheManager.transformerInfo = new TransformerInfo();
		// TransformerInfo info = new TransformerInfo();
		byte[] temp = new byte[2];
		System.arraycopy(data, 5, temp, 0, 2);
		int value = HexDump.byteToint(temp);
		CacheManager.transformerInfo.setRatedCapacity(value);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.RATED_CAPACITY, value);

		System.arraycopy(data, 7, temp, 0, 2);
		float tempValue = HexDump.byteToint(temp) / 10.0f;
		CacheManager.transformerInfo.setFirstVoltage(tempValue);
		CacheManager.putFloat( CacheManager.PARAM_CONFIG,
				CacheManager.FIRST_VOLTAGE, tempValue);

		System.arraycopy(data, 9, temp, 0, 2);
		tempValue = HexDump.byteToint(temp) / 10.0f;
		CacheManager.transformerInfo.setSecondVoltage(tempValue);
		CacheManager.putFloat( CacheManager.PARAM_CONFIG,
				CacheManager.SECOND_VOLTAGE, tempValue);
		
		float aaa = CacheManager.getFloat(CacheManager.PARAM_CONFIG, CacheManager.SECOND_VOLTAGE, 0);

		System.arraycopy(data, 11, temp, 0, 2);
		value = HexDump.byteToint(temp);
		CacheManager.transformerInfo.setCapacityTransformerType(value);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.CAPACITY_TRANSFORM_TYPE, value);

		System.arraycopy(data, 13, temp, 0, 2);
		value = HexDump.byteToint(temp);
		CacheManager.transformerInfo.setConnectionGroup(value);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.CONNECTION_GROUP, value);

		System.arraycopy(data, 15, temp, 0, 2);
		tempValue = HexDump.byteToint(temp) / 10.0f;
		CacheManager.transformerInfo.setImpedanceVoltage(tempValue);
		CacheManager.putFloat( CacheManager.PARAM_CONFIG,
				CacheManager.IMPEDANCE_VOLTAGE, tempValue);

		System.arraycopy(data, 17, temp, 0, 2);
		value = HexDump.byteToint(temp);
		CacheManager.transformerInfo.setTapGear(value);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.TAP_GEAR, value);

		System.arraycopy(data, 19, temp, 0, 2);
		tempValue = HexDump.byteToint(temp) / 10.0f;
		CacheManager.transformerInfo.setCorrectionCoefficient(tempValue);
		CacheManager.putFloat( CacheManager.PARAM_CONFIG,
				CacheManager.CORRECTION_COEFFICIENT, tempValue);

		System.arraycopy(data, 21, temp, 0, 2);
		value = HexDump.byteToint(temp);
		CacheManager.transformerInfo.setCurrentTemperature(value);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.CURRENT_TEMPERATUER, value);

		System.arraycopy(data, 23, temp, 0, 2);
		value = HexDump.byteToint(temp);
		CacheManager.transformerInfo.setNoloadTransformerType(value);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.NOLOAD_TRANSFORMER_TYPE, value);

		System.arraycopy(data, 25, temp, 0, 1);
		value = HexDump.byteToint(temp);
		CacheManager.transformerInfo.setTestMethod(value);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.TEST_METHOD, value);
		// Toast.makeText(context, "测试方法" + value, Toast.LENGTH_LONG).show();

		System.arraycopy(data, 26, temp, 0, 2);
		value = HexDump.byteToint(temp);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.BATTERY_LEVEL1, value);

		System.arraycopy(data, 28, temp, 0, 2);
		value = HexDump.byteToint(temp);
		CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.BATTERY_LEVEL2, value);
		
		System.arraycopy(data, 30, temp, 0, 2);
		value = HexDump.byteToint(temp);
		String code=Integer.toString(value);
		if(code.length() < 1 ) {
			code = "0000";
		} else if(code.length() == 1) {
			code = "000" + code;
		}else if(code.length() == 2) {
			code = "00" + code;
		}else  if(code.length() == 3) {
			code = "0" + code;
		} 
		CacheManager.putString( CacheManager.PARAM_CONFIG,
				CacheManager.TRANSFORMER_CODE, code);
		CacheManager.transformerInfo.setTransformerCode(code);
		int transformerID = CacheManager.getInt(
				CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);

		CacheManager.transformerInfo.setTransformerId(transformerID);
		// CacheManager.transformerInfo = info;
	}

	public static CapacityResultInfo translateCapacity(byte[] data) {
		if (data == null || data.length == 0)
			return null;
		DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
		CapacityResultInfo capacityInfo = new CapacityResultInfo();
		System.out.println( data.toString());
		// 1. 校验数据位数据是否正确(先不做处理)
		// 1. 校验数据是否正确，校验位判断，如果不对不做处理
		byte[] temp = new byte[4];
		System.arraycopy(data, 5, temp, 0, 4);
		
		double result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setUa(result);

		System.arraycopy(data, 9, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setIa(result);

		System.arraycopy(data, 13, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setUb(result);
		System.arraycopy(data, 17, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setIb(result);
		System.arraycopy(data, 21, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setUc(result);
		System.arraycopy(data, 25, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setIc(result);
		System.arraycopy(data, 29, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setPa(result);
		System.arraycopy(data, 33, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setPb(result);
		System.arraycopy(data, 37, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		capacityInfo.setPc(result);

		System.arraycopy(data, 41, temp, 0, 4);
		capacityInfo.setTrueCapacity(HexDump.byte2int(temp) / 32768.0);
		System.arraycopy(data, 45, temp, 0, 4);
		capacityInfo.setDetemineCapacity(HexDump.byte2int(temp));

		temp = new byte[2];
		System.arraycopy(data, 49, temp, 0, 2);
		capacityInfo.setImpedanceVoltage(HexDump.byteToint(temp) / 10.0);
		System.arraycopy(data, 51, temp, 0, 2);
		capacityInfo.setCorrectionImpendace(HexDump.byteToint(temp) / 10.0);
		System.arraycopy(data, 53, temp, 0, 2);
		capacityInfo.setNationalLoss(HexDump.byteToint(temp) / 1000.0);
		System.arraycopy(data, 55, temp, 0, 2);
		capacityInfo.setLoadLoss(HexDump.byteToint(temp) / 1000.0);
		System.arraycopy(data, 57, temp, 0, 2);
		capacityInfo.setCorrectedLoss(HexDump.byteToint(temp) / 1000.0);
		// 判断变压器类型和实时温度，存储在当前测试变压器中，是一个基本数据，每个变压器参数设置只保留一份???????????????
		System.arraycopy(data, 59, temp, 0, 2);
		capacityInfo.setReferenceType(HexDump.byteToint(temp));
		/*int capacityTransformTypeTemp = 0;
		int temperatureTemp = 0;
		try {
			capacityTransformTypeTemp = HexDump.byteToint(temp);
			System.arraycopy(data, 61, temp, 0, 2);
			temperatureTemp = HexDump.byteToint(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		int flag = data[63];
		if (flag == 1) {
			GetCapacityTest.isTest = false;
			capacityInfo.setFinishTest(true);

		}
		return capacityInfo;
	}

	public static NoloadResultInfo translateNoload(byte[] data) {
		if (data == null || data.length == 0)
			return null;
		DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
		NoloadResultInfo noloadInfo = new NoloadResultInfo();
		// 1. 校验数据位数据是否正确(先不做处理)
		// 1. 校验数据是否正确，校验位判断，如果不对不做处理
		byte[] temp = new byte[4];
		System.arraycopy(data, 5, temp, 0, 4);
		noloadInfo.setUa(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));

		System.arraycopy(data, 9, temp, 0, 4);
		noloadInfo.setIa(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));

		System.arraycopy(data, 13, temp, 0, 4);
		noloadInfo.setUb(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 17, temp, 0, 4);
		noloadInfo.setIb(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 21, temp, 0, 4);
		noloadInfo.setUc(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 25, temp, 0, 4);
		noloadInfo.setIc(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 29, temp, 0, 4);
		noloadInfo.setPa(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 33, temp, 0, 4);
		noloadInfo.setPb(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 37, temp, 0, 4);
		noloadInfo.setPc(Double.parseDouble(getData(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0))));

		System.arraycopy(data, 41, temp, 0, 4);
		noloadInfo.setNoloadCurrent(Double.parseDouble(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0)));
		System.arraycopy(data, 45, temp, 0, 4);
		noloadInfo.setTrueLoadLoss(Double.parseDouble(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0)));
		System.arraycopy(data, 49, temp, 0, 4);
		noloadInfo.setCorrectionNoloadLoss(Double.parseDouble(decimalFormat
				.format(HexDump.byte2int(temp) / 32768.0)));
		temp = new byte[2];
		System.arraycopy(data, 53, temp, 0, 2);
		noloadInfo.setDetemineTransformerType(HexDump.byteToint(temp));
		return noloadInfo;
	}

	/**
	 * 解析接收到的二进制数据
	 * 
	 * @param data
	 */
	public static LoadResultInfo translateLoad(byte[] data) {
		if (data == null || data.length == 0)
			return null;
		LoadResultInfo loadInfo = new LoadResultInfo();
		// 1. 校验数据位数据是否正确(先不做处理)
		// 1. 校验数据是否正确，校验位判断，如果不对不做处理
		DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
		byte[] temp = new byte[4];
		System.arraycopy(data, 5, temp, 0, 4);
		loadInfo.setUa(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));

		System.arraycopy(data, 9, temp, 0, 4);
		loadInfo.setIa(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));

		System.arraycopy(data, 13, temp, 0, 4);
		loadInfo.setUb(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 17, temp, 0, 4);
		loadInfo.setIb(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 21, temp, 0, 4);
		loadInfo.setUc(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 25, temp, 0, 4);
		loadInfo.setIc(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 29, temp, 0, 4);
		loadInfo.setPa(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 33, temp, 0, 4);
		loadInfo.setPb(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));
		System.arraycopy(data, 37, temp, 0, 4);
		loadInfo.setPc(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0))));

		temp = new byte[2];
		System.arraycopy(data, 41, temp, 0, 2);
		loadInfo.setTrueLoadLoss(HexDump.byteToint(temp));

		System.arraycopy(data, 43, temp, 0, 2);
		loadInfo.setCorrectLoadLoss(HexDump.byteToint(temp));
		System.arraycopy(data, 45, temp, 0, 2);
		loadInfo.setShortCircuitImpedance(HexDump.byteToint(temp) / 10.0);
		return loadInfo;
	}

	public static CoefficientInfo translateCoefficient(byte[] data) {
		CoefficientInfo info = new CoefficientInfo();
		if (data == null || data.length == 0)
			return null;
		DecimalFormat decimalFormat = new DecimalFormat("##0.0000");

		// 1. 校验数据位数据是否正确(先不做处理)
		// 1. 校验数据是否正确，校验位判断，如果不对不做处理
		byte[] temp = new byte[2];

		System.arraycopy(data, 5, temp, 0, 2);
		info.setUa(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byteToint(temp)))));

		System.arraycopy(data, 7, temp, 0, 2);
		info.setUb(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byteToint(temp)))));
		System.arraycopy(data, 9, temp, 0, 2);
		info.setUc(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byteToint(temp)))));
		System.arraycopy(data, 11, temp, 0, 2);
		info.setIa(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byteToint(temp)))));
		System.arraycopy(data, 13, temp, 0, 2);
		info.setIb(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byteToint(temp)))));
		System.arraycopy(data, 15, temp, 0, 2);
		info.setIc(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byteToint(temp)))));
		System.arraycopy(data, 17, temp, 0, 2);
		info.setPa(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byteToint(temp)))));
		System.arraycopy(data, 19, temp, 0, 2);
		info.setPb(Double.parseDouble(getData(decimalFormat.format(HexDump
				.byteToint(temp)))));

		System.arraycopy(data, 21, temp, 0, 2);
		info.setPc(Double.parseDouble(decimalFormat.format(HexDump
				.byteToint(temp))));
		System.arraycopy(data, 23, temp, 0, 2);
		info.setJa(Double.parseDouble(decimalFormat.format(HexDump
				.byteToint(temp))));
		System.arraycopy(data, 25, temp, 0, 2);
		info.setJb(Double.parseDouble(decimalFormat.format(HexDump
				.byteToint(temp))));
		System.arraycopy(data, 27, temp, 0, 2);
		info.setJc(Double.parseDouble(decimalFormat.format(HexDump
				.byteToint(temp))));

		return info;
	}
	
	public static ParamTestInfo translateParamTest(byte[] data){
		if (data == null || data.length == 0)
			return null;
		DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
		ParamTestInfo param = new ParamTestInfo();
		System.out.println( data.toString());
		// 1. 校验数据位数据是否正确(先不做处理)
		// 1. 校验数据是否正确，校验位判断，如果不对不做处理
		byte[] temp = new byte[4];
		System.arraycopy(data, 5, temp, 0, 4);
		
		double result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setUa(result);

		System.arraycopy(data, 9, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setUb(result);

		System.arraycopy(data, 13, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setUc(result);
		System.arraycopy(data, 17, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setUaAverage(result);
		System.arraycopy(data, 21, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setUbAverage(result);
		System.arraycopy(data, 25, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setUcAverage(result);
		System.arraycopy(data, 29, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setIa(result);
		System.arraycopy(data, 33, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setIb(result);
		System.arraycopy(data, 37, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setIc(result);
		System.arraycopy(data, 41, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setPa(result);
		System.arraycopy(data, 45, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setPb(result);
		System.arraycopy(data, 49, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setPc(result);
		
		System.arraycopy(data, 53, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setP(result);
		
		System.arraycopy(data, 57, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setCosa(result);
		System.arraycopy(data, 61, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setCosb(result);
		System.arraycopy(data, 65, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setCosc(result);
		
		System.arraycopy(data, 69, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setFactor(result);
		System.arraycopy(data, 73, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setFrequence(result);
		
		System.arraycopy(data, 77, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setSa(result);
		System.arraycopy(data, 81, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setSb(result);
		System.arraycopy(data, 85, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setSc(result);
		System.arraycopy(data, 89, temp, 0, 4);
		result = Double.parseDouble(getData(decimalFormat.format(HexDump
				.byte2int(temp) / 32768.0)));
		param.setS(result);
		
		return param;
	}

	/**
	 * 限定数据长度为6位
	 * 
	 * @return
	 */
	public static String getData(String data) {
		// String result = "";
		if (data == null || data.length() == 0)
			return "";
		if (data.length() == 6)
			return data;

		if (data.length() > 6) {

			if (data.indexOf(".") != 5) {
				return data.substring(0, 6);
			} else {
				return data.substring(0, 5);
			}
		}
		if (data.length() < 5 && data.indexOf(".") == -1) {
			data = data + ".";
		}
		while (data.length() < 6) {
			data = data + "0";
		}

		return data;
	}

}
