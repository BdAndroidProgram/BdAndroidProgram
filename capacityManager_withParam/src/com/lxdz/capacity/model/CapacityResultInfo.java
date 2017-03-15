package com.lxdz.capacity.model;

import java.util.HashMap;

/**
 * 容量测试结果数据
 * @author Administrator
 *
 */
public class CapacityResultInfo extends TestNormalData{
	
	public static final String TABLENAME="capacity_info";
	
	/**
	 * 属性和字段的对应关系
	 */
	private HashMap<String, BaseModel.ColumnModel> columns;
	
	/**
	 * 测试ID
	 */
	private int capacityTestId;
	
	/**
	 * 变压器ID
	 */
	private int transformerId;
	
	
	/**
	 * 负载损耗
	 */
	private double loadLoss;
	
	/**
	 * 校正损耗
	 */
	private double correctedLoss;
	
	/**
	 * 阻抗电压
	 */
	private double impedanceVoltage;
	
	/**
	 * 判断容量
	 */
	private int detemineCapacity;
	
	/**
	 * 实测容量
	 */
	private double trueCapacity;
	
	/**
	 * 校正阻抗
	 */
	private double correctionImpendace;
	
	/**
	 * 国标损耗
	 */
	private double nationalLoss;
	
	/**
	 * 损耗误差	
	 */
	private double errorLoss;
	
	/**
	 * 参考类型
	 */
	private int referenceType;
	
	/**
	 * 是否已经测试完成，测试完成后，开始测试按钮才可以点击
	 */
	private boolean finishTest;
	
	@Override
	public String getCreateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("create table ").append(TABLENAME).append(" ( ")
		.append(" capacityTestId INTEGER PRIMARY KEY,").append(" transformerId INTEGER,")
		.append("loadLoss double, ").append(" correctedLoss double,")
		.append("impedanceVoltage double, ").append(" detemineCapacity INTEGER,")
		.append("trueCapacity double, ").append(" correctionImpendace double,")
		.append("nationalLoss double, ").append(" errorLoss double,")
		.append("referenceType INTEGER, ").append(" ua double,")
		.append("ub double, ").append(" uc double, ")
		.append("ia double, ").append(" ib double, ")
		.append("ic double, ").append(" pa double, ")
		.append("pb double, ").append(" pc double)");	
		
		return sql.toString();
	}

	@Override
	public String getTrigger() {
		return null;
	}

	@Override
	public String getAutoIncreamentPK() {
		return "capacityTestId";
	}

	@Override
	public HashMap<String, ColumnModel> getColumns() {
		if(columns == null){
			columns = new HashMap<String, BaseModel.ColumnModel>(){{
				put("capacityTestId", new BaseModel.ColumnModel(true, "capacityTestId"));
				put("transformerId", new BaseModel.ColumnModel(true, "transformerId"));
				
				put("loadLoss", new BaseModel.ColumnModel(true, "loadLoss"));
				put("correctedLoss", new BaseModel.ColumnModel(true, "correctedLoss"));
				put("impedanceVoltage", new BaseModel.ColumnModel(true, "impedanceVoltage"));
				
				put("detemineCapacity", new BaseModel.ColumnModel(true, "detemineCapacity"));
				put("trueCapacity", new BaseModel.ColumnModel(true, "trueCapacity"));
				put("correctionImpendace", new BaseModel.ColumnModel(true, "correctionImpendace"));
				
				put("nationalLoss", new BaseModel.ColumnModel(true, "nationalLoss"));
				put("errorLoss", new BaseModel.ColumnModel(true, "errorLoss"));
				put("referenceType",new BaseModel.ColumnModel(true, "referenceType"));
				
				
				put("ua", new BaseModel.ColumnModel(true, "ua"));
				put("ub",new BaseModel.ColumnModel(true, "ub"));
				put("uc", new BaseModel.ColumnModel(true, "uc"));
				put("ia",new BaseModel.ColumnModel(true, "ia"));
				put("ib", new BaseModel.ColumnModel(true, "ib"));
				put("ic",new BaseModel.ColumnModel(true, "ic"));
				put("pa",new BaseModel.ColumnModel(true, "pa"));
				put("pb", new BaseModel.ColumnModel(true, "pb"));
				put("pc",new BaseModel.ColumnModel(true, "pc"));
			}};
		}
		return columns;
	}

	public int getCapacityTestId() {
		return capacityTestId;
	}

	public void setCapacityTestId(int capacityTestId) {
		this.capacityTestId = capacityTestId;
	}

	public int getTransformerId() {
		return transformerId;
	}

	public void setTransformerId(int transformerId) {
		this.transformerId = transformerId;
	}

	public double getLoadLoss() {
		return loadLoss;
	}

	public void setLoadLoss(double loadLoss) {
		this.loadLoss = loadLoss;
	}

	public double getCorrectedLoss() {
		return correctedLoss;
	}

	public void setCorrectedLoss(double correctedLoss) {
		this.correctedLoss = correctedLoss;
	}

	public double getImpedanceVoltage() {
		return impedanceVoltage;
	}

	public void setImpedanceVoltage(double impedanceVoltage) {
		this.impedanceVoltage = impedanceVoltage;
	}


	public double getTrueCapacity() {
		return trueCapacity;
	}

	public void setTrueCapacity(double trueCapacity) {
		this.trueCapacity = trueCapacity;
	}

	

	public double getNationalLoss() {
		return nationalLoss;
	}

	public void setNationalLoss(double nationalLoss) {
		this.nationalLoss = nationalLoss;
	}

	public double getErrorLoss() {
		return errorLoss;
	}

	public void setErrorLoss(double errorLoss) {
		this.errorLoss = errorLoss;
	}

	

	public double getCorrectionImpendace() {
		return correctionImpendace;
	}

	public void setCorrectionImpendace(double correctionImpendace) {
		this.correctionImpendace = correctionImpendace;
	}

	public int getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(int referenceType) {
		this.referenceType = referenceType;
	}

	public int getDetemineCapacity() {
		return detemineCapacity;
	}

	public void setDetemineCapacity(int detemineCapacity) {
		this.detemineCapacity = detemineCapacity;
	}

	public boolean getFinishTest() {
		return finishTest;
	}

	public void setFinishTest(boolean finishTest) {
		this.finishTest = finishTest;
	}


}
