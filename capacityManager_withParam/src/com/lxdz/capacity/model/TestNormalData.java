package com.lxdz.capacity.model;

import java.util.HashMap;

/**
 * 数据测试时基础数据
 * @author Administrator
 *
 */
public class TestNormalData extends BaseModel{

	/**
	 * 电压
	 */
	private double ua;
	
	private double ub;
	
	private double uc;
	
	/**
	 * 电流
	 */
	private double ia;
	
	private double ib;
	
	private double ic;
	
	/**
	 * 功率
	 */
	private double pa;
	
	private double pb;
	
	private double pc;

	@Override
	public String getCreateSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTrigger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAutoIncreamentPK() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ColumnModel> getColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getUa() {
		return ua;
	}

	public void setUa(double ua) {
		this.ua = ua;
	}

	public double getUb() {
		return ub;
	}

	public void setUb(double ub) {
		this.ub = ub;
	}

	public double getUc() {
		return uc;
	}

	public void setUc(double uc) {
		this.uc = uc;
	}

	public double getIa() {
		return ia;
	}

	public void setIa(double ia) {
		this.ia = ia;
	}

	public double getIb() {
		return ib;
	}

	public void setIb(double ib) {
		this.ib = ib;
	}

	public double getIc() {
		return ic;
	}

	public void setIc(double ic) {
		this.ic = ic;
	}

	public double getPa() {
		return pa;
	}

	public void setPa(double pa) {
		this.pa = pa;
	}

	public double getPb() {
		return pb;
	}

	public void setPb(double pb) {
		this.pb = pb;
	}

	public double getPc() {
		return pc;
	}

	public void setPc(double pc) {
		this.pc = pc;
	}

}
