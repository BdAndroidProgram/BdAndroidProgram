package com.lxdz.capacity.model;

import java.util.HashMap;

/**
 * 负载测试结果
 * @author Administrator
 *
 */
public class LoadResultInfo extends TestNormalData{
	
	public static final String TABLENAME="load_result";
	
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
	private int loadTestId;
	
	/**
	 * 实测负载损耗
	 */
	private double trueLoadLoss;
	
	/**
	 * 短路阻抗
	 */
	private double shortCircuitImpedance;
	
	/**
	 * 校正负载损耗
	 */
	private double correctLoadLoss;
	
	/**
	 * 测试方法
	 */
	private String testMethod;

	@Override
	public String getCreateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("create table ").append(TABLENAME).append(" ( ")
		.append(" loadTestId INTEGER PRIMARY KEY,").append(" transformerId INTEGER, ")		
		.append("trueLoadLoss double, ").append(" shortCircuitImpedance double, ")
		.append("correctLoadLoss double, ").append("ua double, ")
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
		return "loadTestId";
	}

	@Override
	public HashMap<String, ColumnModel> getColumns() {
		if(columns == null){
			columns = new HashMap<String, BaseModel.ColumnModel>(){{
				put("loadTestId", new BaseModel.ColumnModel(true, "loadTestId"));
				put("transformerId", new BaseModel.ColumnModel(true, "transformerId"));
				
				put("trueLoadLoss", new BaseModel.ColumnModel(true, "trueLoadLoss"));
				put("shortCircuitImpedance", new BaseModel.ColumnModel(true, "shortCircuitImpedance"));
				put("correctLoadLoss", new BaseModel.ColumnModel(true, "correctLoadLoss"));
				
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

	public int getLoadTestId() {
		return loadTestId;
	}

	public void setLoadTestId(int loadTestId) {
		this.loadTestId = loadTestId;
	}
	

	public String getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
	}

	public double getTrueLoadLoss() {
		return trueLoadLoss;
	}

	public void setTrueLoadLoss(double trueLoadLoss) {
		this.trueLoadLoss = trueLoadLoss;
	}

	public double getShortCircuitImpedance() {
		return shortCircuitImpedance;
	}

	public void setShortCircuitImpedance(double shortCircuitImpedance) {
		this.shortCircuitImpedance = shortCircuitImpedance;
	}

	public double getCorrectLoadLoss() {
		return correctLoadLoss;
	}

	public void setCorrectLoadLoss(double correctLoadLoss) {
		this.correctLoadLoss = correctLoadLoss;
	}
	
}
