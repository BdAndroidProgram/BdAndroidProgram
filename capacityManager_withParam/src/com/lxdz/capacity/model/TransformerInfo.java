package com.lxdz.capacity.model;

import java.util.HashMap;

import android.content.Context;

import com.lxdz.capacity.util.CacheManager;

/**
 * 变压器基本信息表
 * @author Administrator
 *
 */
public class TransformerInfo extends BaseModel{
	
	public static final String TABLENAME="transformer_info";
	
	/**
	 * 属性和字段的对应关系
	 */
	private HashMap<String, BaseModel.ColumnModel> columns;
	
	private int transformerId;
	
	private String transformerCode;
	
	/**
	 * 用户名称，单位
	 */
	private String userName;
	
	private String userAddress;
	
	/**
	 * 测试员	
	 */
	private String testUser;
	
	/**
	 * 容量变压器类型
	 */
	private int capacityTransformerType;
	
	/**
	 * 变压类型
	 */
	private int transformerType;
	
	/**
	 * 当前温度
	 */
	private int currentTemperature;
	
	/**
	 * 额定容量
	 */
	private int ratedCapacity;
	
	/**
	 * 联结组别
	 */
	private int connectionGroup;
	
	/**
	 * 分接档位
	 */
	private int tapGear;
	
	/**
	 * 一次电压
	 */
	private float firstVoltage;
	
	/**
	 * 二次电压
	 */
	private float secondVoltage;
	
	/**
	 * 阻抗电压
	 */
	private float impedanceVoltage;
	
	/**
	 * 校正系数
	 */
	private float correctionCoefficient;
	
	/**
	 * 额定电压（低）
	 */
	private float ratedLowVoltage;
	
	/**
	 * 额定电压(高)
	 */
	private int ratedHighVoltage;
	
	/**
	 * 创建时间
	 */
	private long createTime;
	
	/**
	 * 修改时间
	 */
	private long updateTime;
	
	/**
	 * 对于空载变压器类型
	 */
	private int noloadTransformerType;
	
	/**
	 * 测试方法
	 */
	private int testMethod;

	@Override
	public String getCreateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("create table ").append(TABLENAME).append(" ( ")
		.append(" transformerId INTEGER PRIMARY KEY,").append(" userName varchar(50),").append(" transformerCode varchar(50),")
		.append(" userAddress varchar(100),").append(" testUser  varchar(50), ")
		.append(" capacityTransformerType INTEGER,").append(" transformerType INTEGER,")
		.append(" currentTemperature double,").append(" ratedCapacity INTEGER,")
		.append(" connectionGroup INTEGER,").append(" tapGear  INTEGER,")
		.append(" firstVoltage double,").append(" secondVoltage double, ")
		.append("impedanceVoltage double, ").append(" correctionCoefficient double,")
		.append(" ratedLowVoltage double,").append(" ratedHighVoltage INTEGER,")
		.append(" noloadTransformerType INTEGER,").append(" testMethod INTEGER,")
		.append(" createTime INTEGER,").append(" updateTime INTEGER)");
		return sql.toString();
	}

	@Override
	public String getTrigger() {
		return null;
	}

	@Override
	public String getAutoIncreamentPK() {
		return "transformerId";
	}

	@Override
	public HashMap<String, ColumnModel> getColumns() {
		if(columns == null){
			columns = new HashMap<String, BaseModel.ColumnModel>(){{
				put("transformerId", new BaseModel.ColumnModel(true, "transformerId"));
				put("transformerCode", new BaseModel.ColumnModel(true, "transformerCode"));
				put("userName", new BaseModel.ColumnModel(true, "userName"));
				put("userAddress", new BaseModel.ColumnModel(true, "userAddress"));
				put("testUser", new BaseModel.ColumnModel(true, "testUser"));
				put("capacityTransformerType", new BaseModel.ColumnModel(true, "capacityTransformerType"));
				put("transformerType", new BaseModel.ColumnModel(true, "transformerType"));
				put("currentTemperature", new BaseModel.ColumnModel(true, "currentTemperature"));
				put("ratedCapacity", new BaseModel.ColumnModel(true, "ratedCapacity"));
				put("connectionGroup", new BaseModel.ColumnModel(true, "connectionGroup"));
				put("tapGear", new BaseModel.ColumnModel(true, "tapGear"));
				put("firstVoltage", new BaseModel.ColumnModel(true, "firstVoltage"));
				put("secondVoltage",new BaseModel.ColumnModel(true, "secondVoltage"));
				put("impedanceVoltage", new BaseModel.ColumnModel(true, "impedanceVoltage"));
				put("correctionCoefficient",new BaseModel.ColumnModel(true, "correctionCoefficient"));
				
				put("ratedLowVoltage", new BaseModel.ColumnModel(true, "ratedLowVoltage"));
				put("ratedHighVoltage",new BaseModel.ColumnModel(true, "ratedHighVoltage"));
				
				put("noloadTransformerType",new BaseModel.ColumnModel(true, "noloadTransformerType"));
				put("testMethod",new BaseModel.ColumnModel(true, "testMethod"));
				
				put("createTime", new BaseModel.ColumnModel(true, "createTime"));
				put("updateTime",new BaseModel.ColumnModel(true, "updateTime"));
			}};
		}
		return columns;
	}
	
	/**
	 * 缓存变压器配置参数
	 * @param info
	 */
	public void cacheTransformer(Context context) {
		String code=CacheManager.TRANSFORMER_CODE;
		if(code!=null)
		{
			if(code.length() < 1 ) {
			code = "0000";
			} else if(code.length() == 1) {
			code = "000" + code;
			}else if(code.length() == 2) {
			code = "00" + code;
			}else  if(code.length() == 3) {
			code = "0" + code;
			}
		}
		CacheManager.putString( CacheManager.PARAM_CONFIG, code, this.getTransformerCode());
		CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, this.getTransformerId());
		CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TEST_USER, this.getTestUser());
		CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_NAME, this.getUserName());
		CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_ADDRESS, this.getUserAddress());
		
		CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.RATED_CAPACITY, this.getRatedCapacity());
		CacheManager.putFloat( CacheManager.PARAM_CONFIG, CacheManager.FIRST_VOLTAGE, this.getFirstVoltage());
		CacheManager.putFloat( CacheManager.PARAM_CONFIG, CacheManager.SECOND_VOLTAGE, this.getSecondVoltage());
		CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.CAPACITY_TRANSFORM_TYPE, this.getCapacityTransformerType() + 1);
		CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.CONNECTION_GROUP, this.getConnectionGroup() + 1);
		CacheManager.putFloat( CacheManager.PARAM_CONFIG, CacheManager.IMPEDANCE_VOLTAGE, this.getImpedanceVoltage());
		CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TAP_GEAR, this.getTapGear() + 1);
		CacheManager.putFloat( CacheManager.PARAM_CONFIG, CacheManager.CORRECTION_COEFFICIENT, this.getCorrectionCoefficient());
		CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.CURRENT_TEMPERATUER, this.getCurrentTemperature());
		CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.NOLOAD_TRANSFORMER_TYPE, this.getNoloadTransformerType() + 1);
		CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TEST_METHOD, this.getTestMethod() + 1);
	}
	
	/**
	 * 得到缓存的数据
	 * @return
	 */
	public TransformerInfo getCacheTransformer(Context context) {
		TransformerInfo info = new TransformerInfo();
		info.setTransformerId(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1));
		String code=CacheManager.TRANSFORMER_CODE;
		if(code!=null)
		{
			if(code.length() < 1 ) {
			code = "0000";
			} else if(code.length() == 1) {
			code = "000" + code;
			}else if(code.length() == 2) {
			code = "00" + code;
			}else  if(code.length() == 3) {
			code = "0" + code;
			}
		}
		info.setTransformerCode(CacheManager.getString( CacheManager.PARAM_CONFIG, code, ""));
		info.setTestUser(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.TEST_USER, ""));
		info.setUserName(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.USER_NAME, ""));
		info.setUserAddress(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.USER_ADDRESS, ""));
		info.setRatedCapacity(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.RATED_CAPACITY, 0));
		info.setFirstVoltage(CacheManager.getFloat( CacheManager.PARAM_CONFIG, CacheManager.FIRST_VOLTAGE, 0));
		info.setSecondVoltage(CacheManager.getFloat( CacheManager.PARAM_CONFIG, CacheManager.SECOND_VOLTAGE, 0));
		info.setCapacityTransformerType(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.CAPACITY_TRANSFORM_TYPE, 0));
		info.setConnectionGroup(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.CONNECTION_GROUP, 0));
		info.setImpedanceVoltage(CacheManager.getFloat( CacheManager.PARAM_CONFIG, CacheManager.IMPEDANCE_VOLTAGE, 0));
		info.setTapGear(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TAP_GEAR, 0));
		info.setCorrectionCoefficient(CacheManager.getFloat( CacheManager.PARAM_CONFIG, CacheManager.CORRECTION_COEFFICIENT, 0));
		info.setCurrentTemperature(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.CURRENT_TEMPERATUER, 0));
		info.setNoloadTransformerType(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.NOLOAD_TRANSFORMER_TYPE, 0));
		info.setTestMethod(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TEST_METHOD, 0));
		
		return info;
	}

	public int getTransformerId() {
		return transformerId;
	}

	public void setTransformerId(int transformerId) {
		this.transformerId = transformerId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getTestUser() {
		return testUser;
	}

	public void setTestUser(String testUser) {
		this.testUser = testUser;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public void setColumns(HashMap<String, BaseModel.ColumnModel> columns) {
		this.columns = columns;
	}

	public String getTransformerCode() {
		String code=transformerCode;
		if(code!=null)
		{
			if(code.length() < 1 ) {
			code = "0000";
			} else if(code.length() == 1) {
			code = "000" + code;
			}else if(code.length() == 2) {
			code = "00" + code;
			}else  if(code.length() == 3) {
			code = "0" + code;
			}
		}
		return code;
	}

	public void setTransformerCode(String transformerCode) {
		this.transformerCode = transformerCode;
	}

	public int getCapacityTransformerType() {
		return capacityTransformerType;
	}

	public void setCapacityTransformerType(int capacityTransformerType) {
		this.capacityTransformerType = capacityTransformerType;
	}

	public int getTransformerType() {
		return transformerType;
	}

	public void setTransformerType(int transformerType) {
		this.transformerType = transformerType;
	}

	public int getConnectionGroup() {
		return connectionGroup;
	}

	public void setConnectionGroup(int connectionGroup) {
		this.connectionGroup = connectionGroup;
	}

	public int getTapGear() {
		return tapGear;
	}

	public void setTapGear(int tapGear) {
		this.tapGear = tapGear;
	}



	public int getNoloadTransformerType() {
		return noloadTransformerType;
	}

	public void setNoloadTransformerType(int noloadTransformerType) {
		this.noloadTransformerType = noloadTransformerType;
	}

	public int getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(int testMethod) {
		this.testMethod = testMethod;
	}


	public float getFirstVoltage() {
		return firstVoltage;
	}

	public void setFirstVoltage(float firstVoltage) {
		this.firstVoltage = firstVoltage;
	}

	public float getSecondVoltage() {
		return secondVoltage;
	}

	public void setSecondVoltage(float secondVoltage) {
		this.secondVoltage = secondVoltage;
	}

	public float getImpedanceVoltage() {
		return impedanceVoltage;
	}

	public void setImpedanceVoltage(float impedanceVoltage) {
		this.impedanceVoltage = impedanceVoltage;
	}

	public float getCorrectionCoefficient() {
		return correctionCoefficient;
	}

	public void setCorrectionCoefficient(float correctionCoefficient) {
		this.correctionCoefficient = correctionCoefficient;
	}

	public float getRatedLowVoltage() {
		return ratedLowVoltage;
	}

	public void setRatedLowVoltage(float ratedLowVoltage) {
		this.ratedLowVoltage = ratedLowVoltage;
	}

	public int getCurrentTemperature() {
		return currentTemperature;
	}

	public void setCurrentTemperature(int currentTemperature) {
		this.currentTemperature = currentTemperature;
	}

	public int getRatedCapacity() {
		return ratedCapacity;
	}

	public void setRatedCapacity(int ratedCapacity) {
		this.ratedCapacity = ratedCapacity;
	}

}
