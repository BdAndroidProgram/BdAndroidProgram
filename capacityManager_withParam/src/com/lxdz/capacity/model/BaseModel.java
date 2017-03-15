package com.lxdz.capacity.model;

import java.util.HashMap;

public abstract class BaseModel {
	

	/**
	 * 获取创建数据库语句
	 * @return
	 */
	public abstract String getCreateSql();
	
	/**
	 * 获取数据库触发器创建语句
	 * @return
	 */
	public abstract String getTrigger();
	
	/**
	 * 获取自动增长的主键，在插入时用
	 * @return
	 */
	public abstract String getAutoIncreamentPK();
	
	/**
	 * 获取字段与属性对应关系,key 为column， value为属性名
	 * @return
	 */
	public abstract HashMap<String, ColumnModel> getColumns();
	
	public class ColumnModel{
		
		public ColumnModel(boolean isTableColumn, String columnName){
			this.columnName = columnName;
			this.isTableColumn = isTableColumn;
		}
		/**
		 * 是否是本表的字段：true是， false关联表中的字段
		 */
		private boolean isTableColumn;
		
		private String columnName;

		public boolean isTableColumn() {
			return isTableColumn;
		}

		public void setTableColumn(boolean isTableColumn) {
			this.isTableColumn = isTableColumn;
		}

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}
	}

}
