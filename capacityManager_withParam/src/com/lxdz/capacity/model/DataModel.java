package com.lxdz.capacity.model;

import com.lxdz.capacity.util.HexDump;

/**
 * 向服务器发送的数据，处理类
 * @author Administrator
 *
 */
public class DataModel {
	
	private static final String TAG = DataModel.class.getSimpleName();
	
	//主命令码
	public static final String CAPACITY_COMMAND = "02";
	public static final String NOLOAD_COMMAND= "03";
	public static final String LOAD_COMMAND="04";
	public static final String CAPACITY_SAVE_PRINT = "20";
	public static final String NOLOAD_SAVE_PRINT = "21";
	public static final int CAPACITY_COMMAND_INT = 0x02;
	public static final int NOLOAD_COMMAND_INT = 0x03;
	public static final int LOAD_COMMAND_INT = 0x04;
	public static final String GET_PARAM = "01";
	public static final int GET_PARAM_INT = 0x01;
	public static final String GET_PARAMTEST = "05";
	public static final int GET_PARAMTEST_INT = 0x05;
	
	public static final String DEBUG_CAPACITY = "08";
	public static final String DEBUG_NOLOAD = "09";
	public static final String DEBUG_CAPACITY_UA1 = "01";
	public static final String DEBUG_CAPACITY_UB1 = "02";
	public static final String DEBUG_CAPACITY_UC1 = "03";
	public static final String DEBUG_CAPACITY_IA1 = "07";
	public static final String DEBUG_CAPACITY_IB1 = "08";
	public static final String DEBUG_CAPACITY_IC1 = "09";
	public static final String DEBUG_CAPACITY_IA2 = "0A";
	public static final String DEBUG_CAPACITY_IB2 = "0B";
	public static final String DEBUG_CAPACITY_IC2 = "0C";
	public static final String DEBUG_CAPACITY_IA3 = "0D";
	public static final String DEBUG_CAPACITY_IB3 = "0E";
	public static final String DEBUG_CAPACITY_IC3 = "0F";
	
	public static final String DEBUG_CAPACITY_PA1 = "10";
	public static final String DEBUG_CAPACITY_PB1 = "11";
	public static final String DEBUG_CAPACITY_PC1 = "12";
	public static final String DEBUG_CAPACITY_JA1 = "13";
	public static final String DEBUG_CAPACITY_JB1 = "14";
	public static final String DEBUG_CAPACITY_JC1 = "15";
	
	/*public static final String DEBUG_CAPACITY_JA1 = "10";
	public static final String DEBUG_CAPACITY_JB1 = "11";
	public static final String DEBUG_CAPACITY_JC1 = "12";
	public static final String DEBUG_CAPACITY_JA2 = "13";
	public static final String DEBUG_CAPACITY_JB2 = "14";
	public static final String DEBUG_CAPACITY_JC2 = "15";*/
	public static final String DEBUG_CAPACITY_JA3 = "16";
	public static final String DEBUG_CAPACITY_JB3 = "17";
	public static final String DEBUG_CAPACITY_JC3 = "18";
	
	public static final String DEBUG_NOLOAD_UA1 = "19";
	public static final String DEBUG_NOLOAD_UB1 = "1A";
	public static final String DEBUG_NOLOAD_UC1 = "1B";
	public static final String DEBUG_NOLOAD_UA2 = "1C";
	public static final String DEBUG_NOLOAD_UB2 = "1D";
	public static final String DEBUG_NOLOAD_UC2 = "1E";
	public static final String DEBUG_NOLOAD_IA1 = "1F";
	public static final String DEBUG_NOLOAD_IB1 = "20";
	public static final String DEBUG_NOLOAD_IC1 = "21";
	public static final String DEBUG_NOLOAD_IA2 = "22";
	public static final String DEBUG_NOLOAD_IB2 = "23";
	public static final String DEBUG_NOLOAD_IC2 = "24";
	public static final String DEBUG_NOLOAD_IA3 = "25";
	public static final String DEBUG_NOLOAD_IB3 = "26";
	public static final String DEBUG_NOLOAD_IC3 = "27";
	
	public static final String DEBUG_NOLOAD_PA1 = "28";
	public static final String DEBUG_NOLOAD_PB1 = "29";
	public static final String DEBUG_NOLOAD_PC1 = "2A";
	public static final String DEBUG_NOLOAD_JA1 = "2B";
	public static final String DEBUG_NOLOAD_JB1 = "2C";
	public static final String DEBUG_NOLOAD_JC1 = "2D";
	
	/*public static final String DEBUG_NOLOAD_JA1 = "28";
	public static final String DEBUG_NOLOAD_JB1 = "29";
	public static final String DEBUG_NOLOAD_JC1 = "2A";
	public static final String DEBUG_NOLOAD_JA2 = "2B";
	public static final String DEBUG_NOLOAD_JB2 = "2C";
	public static final String DEBUG_NOLOAD_JC2 = "2D";
	public static final String DEBUG_NOLOAD_JA3 = "2E";*/
	public static final String DEBUG_NOLOAD_JB3 = "2F";
	public static final String DEBUG_NOLOAD_JC3 = "30";
	
	public static final String DEBUG_UP_10 = "0100";
	public static final String DEBUG_DOWN_10 = "F100";
	public static final String DEBUG_UP_1 = "0200";
	public static final String DEBUG_DOWN_1 = "F200";
	public static final String DEBUG_UP_01 = "0300";
	public static final String DEBUG_DOWN_01 = "F300";
	public static final String DEBUG_UP_001 = "0400";
	public static final String DEBUG_DOWN_001 = "F400";
	
	public static final String DEBUG_SAVE_CAPACITY = "13";
	public static final String DEBUG_SAVE_CAPACITY_VOLTAGE = "01";
	public static final String DEBUG_SAVE_CAPACITY_CURRENT = "02";
	public static final String DEBUG_SAVE_NOLOAD = "14";
	public static final String DEBUG_SAVE_NOLOAD_VOLTAGE = "01";
	public static final String DEBUG_SAVE_NOLOAD_CURRENT = "02";
	
	public static final String DEBUG_CAPACITY_COEFFICIENT = "31";
	public static final int DEBUG_CAPACITY_COEFFICIENT_INT = 0x31;
	public static final String DEBUG_NOLOAD_COEFFICIENT = "32";
	public static final int DEBUG_NOLOAD_COEFFICIENT_INT = 0x32;
 
	/**
	 * 调取参数
	 */
	public static final String LOAD_PARAM = "01";
	
	// 容量参数设置主命令
	public static final String CAPACITY_PARAM_SET = "06";
	//空载参数设置主命令
	public static final String NOLOADE_PARAM_SET = "07";
	
	
	//子命令
	//额定容量
	public static final String RATED_CAPACITY = "0A";
	
	public static final int RATED_CAPACITY_INT = 0x0a;
	
	// 设置变压器编号
	public static final String TRANSFORMER_CODE = "13";
	public static final int TRANSFORMER_CODE_INT = 0x13;
	
	//额定高压
	public static final String RATED_HIGH_VOLTAGE = "0B";
	public static final int RATED_HIGH_VOLTAGE_INT = 0x0b;
	
	//额定低压
	public static final String RATED_LOW_VOLTAGE = "0C";
	public static final int RATED_LOW_VOLTAGE_INT = 0x0c;
	
	//链接组别
	public static final String CONNECTION_GROUP = "0D";
	public static final int CONNECTION_GROUP_INT = 0x0d;
	
	//分接档位
	public static final String TAP_GEAR = "0E";
	public static final int TAP_GEAR_INT = 0x0e;
	
	//阻抗电压
	public static final String IMPENDANCE_VOLTAGE = "0F";
	public static final int IMPENDANCE_VOLTAGE_INT = 0x0f;
	
	//设置温度
	public static final String TEMPERATURE = "10";
	public static final int TEMPERATURE_INT = 0x10;
	
	//容量测试变压器类型
	public static final String CAPACITY_TRANSFORMER_TYPE = "11";
	public static final int CAPACITY_TRANSFORMER_TYPE_INT = 0x11;
	//容量测试变压器类型
	
	public static final String CAPACITY_TRANSFORMER_TYPE_NOLOAD = "14";
	public static final int CAPACITY_TRANSFORMER_TYPE_INT_NOLOAD = 0x14;
	
	/**
	 * 空数据
	 */
	public static final String NO_COMMAND = "00";
	
	public static final String NO_DATA = "0000";
	
	/**
	 * 字节头
	 */
	public static final String START = "EB"; 
	
	public static final byte START_INT = (byte)0xeb;
	
	public static final String TEST_METHOD = "12";
	public static final int TEST_METHOD_INT = 0x12;
	
	/**
	 * 单相测试停止测试
	 */
	public static final String SINGLE_PHASE_TEST_PAUSE = "17";
	public static final String SINGLE_PHASE_TEST_STOP = "18";
	public static final String CAPACITY_PRINT = "15";
	public static final String NOLOAD_PRINT = "16";
	
	/**
	 * 主命令， 一个字节
	 */
	private String mainCommand;
	
	/**
	 * 子命令， 一个字节
	 */
	private String subCommand;
	
	/**
	 * 校验码，采用机校验和形式， 一个字节
	 */
	private String checkData;
	
	/**
	 * 需要发送的数据, 两个字节
	 */
	private String sendData;
	
	/**
	 * 拼接之后的生成的命令码
	 */
	private byte[] requestCode;
	
	
	public byte[] getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(String mainCommand, String subCommand, String data) {
		this.mainCommand = mainCommand;
		this.subCommand = subCommand;
		this.sendData = data;
		StringBuffer code = new StringBuffer();
		code.append(START).append(mainCommand).append(subCommand);
		if(data == null || data.length() == 0) {
			code.append(NO_DATA);
		} else {
			if(data.length() % 2 != 0) data = "0" + data;
			code.append(data);
		}
		String checkNum = HexDump.getCheckNumberReverse(HexDump.hexStringToByteArray(code.toString()), code.toString().length() / 2);
		code.append(checkNum);
		this.requestCode = HexDump.hexStringToByteArray(code.toString());
	}
	public void setRequestCode(String mainCommand, String data) {
		this.mainCommand = mainCommand;
		this.sendData = data;
		StringBuffer code = new StringBuffer();
		code.append(START).append(mainCommand);
		if(data == null || data.length() == 0) {
			code.append(NO_DATA);
		} else {
			if(data.length() % 2 != 0) data = "0" + data;
			code.append(data);
		}
		String checkNum = HexDump.getCheckNumberReverse(HexDump.hexStringToByteArray(code.toString()), code.toString().length() / 2);
		code.append(checkNum);
		this.requestCode = HexDump.hexStringToByteArray(code.toString());
	}
	

	public String getMainCommand() {
		return mainCommand;
	}

	public void setMainCommand(String mainCommand) {
		this.mainCommand = mainCommand;
	}

	public String getSubCommand() {
		return subCommand;
	}

	public void setSubCommand(String subCommand) {
		this.subCommand = subCommand;
	}

	public String getCheckData() {
		return checkData;
	}

	public void setCheckData(String checkData) {
		this.checkData = checkData;
	}

	public String getSendData() {
		return sendData;
	}

	public void setSendData(String sendData) {
		this.sendData = sendData;
	}



	

	

}
