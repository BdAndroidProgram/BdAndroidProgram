package com.lxdz.capacity.model;

import java.util.HashMap;

/**
 * 负载测试结果实体类
 * @author Administrator
 *
 */
public class NoloadResultInfo extends TestNormalData{
	
	public static final String TABLENAME="noload_info";
	
	/**
	 * 属性和字段的对应关系
	 */
	private HashMap<String, BaseModel.ColumnModel> columns;
	
	
	/**
	 * 变压器ID
	 */
	private int transformerId;
	
	/**
	 * 空载测试Id
	 */
	private int noloadTestId;
	
	/**
	 * 空载电流
	 */
	private double noloadCurrent;
	
	/**
	 * 实测空载损耗
	 */
	private double trueLoadLoss;
	
	/**
	 * 校正空载损耗
	 */
	private double correctionNoloadLoss;
	
	/**
	 * 判断变压器类型
	 */
	private int detemineTransformerType;
	
	/**
	 * 测试方法
	 */
	private String testMethod;

	@Override
	public String getCreateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("create table ").append(TABLENAME).append(" ( ")
		.append(" noloadTestId INTEGER PRIMARY KEY,").append(" transformerId INTEGER, ")		
		.append("noloadCurrent double, ").append(" trueLoadLoss double, ")
		.append("correctionNoloadLoss double, ").append(" detemineTransformerType INTEGER, ")
		.append("testMethod varchar(20), ").append(" ua double, ")
		.append("ub double, ").append(" uc double, ")
		.append("ia double, ").append(" ib double, ")
		.append("ic double, ").append(" pa double, ")
		.append("pb double, ").append(" pc double )");	
		return sql.toString();
	}

	@Override
	public String getTrigger() {
		return null;
	}

	@Override
	public String getAutoIncreamentPK() {
		// TODO Auto-generated method stub
		return "noloadTestId";
	}

	@Override
	public HashMap<String, ColumnModel> getColumns() {
		if(columns == null){
			columns = new HashMap<String, BaseModel.ColumnModel>(){{
				put("noloadTestId", new BaseModel.ColumnModel(true, "noloadTestId"));
				put("transformerId", new BaseModel.ColumnModel(true, "transformerId"));
				
				put("noloadCurrent", new BaseModel.ColumnModel(true, "noloadCurrent"));
				put("trueLoadLoss", new BaseModel.ColumnModel(true, "trueLoadLoss"));
				put("correctionNoloadLoss",new BaseModel.ColumnModel(true, "correctionNoloadLoss"));
				put("detemineTransformerType",new BaseModel.ColumnModel(true, "detemineTransformerType"));
				
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

	public int getTransformerId() {
		return transformerId;
	}

	public void setTransformerId(int transformerId) {
		this.transformerId = transformerId;
	}

	public int getNoloadTestId() {
		return noloadTestId;
	}

	public void setNoloadTestId(int noloadTestId) {
		this.noloadTestId = noloadTestId;
	}


	public String getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
	}

	public double getNoloadCurrent() {
		return noloadCurrent;
	}

	public void setNoloadCurrent(double noloadCurrent) {
		this.noloadCurrent = noloadCurrent;
	}

	public double getTrueLoadLoss() {
		return trueLoadLoss;
	}

	public void setTrueLoadLoss(double trueLoadLoss) {
		this.trueLoadLoss = trueLoadLoss;
	}

	public double getCorrectionNoloadLoss() {
		return correctionNoloadLoss;
	}

	public void setCorrectionNoloadLoss(double correctionNoloadLoss) {
		this.correctionNoloadLoss = correctionNoloadLoss;
	}

	public int getDetemineTransformerType() {
		return detemineTransformerType;
	}

	public void setDetemineTransformerType(int detemineTransformerType) {
		this.detemineTransformerType = detemineTransformerType;
	}

	

	

}
