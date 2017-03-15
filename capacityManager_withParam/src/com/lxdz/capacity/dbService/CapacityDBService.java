package com.lxdz.capacity.dbService;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.lxdz.capacity.db.DBTools;
import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.TransformerInfo;

/**
 * 容量测试数据类
 * @author Administrator
 *
 */
public class CapacityDBService {
	private DBTools dbTools;
	private Context context;
	
	public CapacityDBService(Context context) {
		this.context = context;
		this.dbTools = new DBTools(context);
	}
	
	public void insertCapacity(CapacityResultInfo capacity) {
		if(capacity == null )return ;
		this.deleteCapacity(" transformerId=?", new String[]{capacity.getTransformerId() + ""});
		dbTools.insertData(capacity, CapacityResultInfo.TABLENAME);
		String sql = "update " + TransformerInfo.TABLENAME + " set updateTime=" + new Date().getTime() + " where transformerId=" + capacity.getTransformerId();
		dbTools.excuteSQL(sql);
	}
	
	public void deleteCapacity(String sqlWhere, String[] sqlClause){
		dbTools.deleteData(CapacityResultInfo.TABLENAME, sqlWhere, sqlClause);
	}
	
	public void updateCapacity(CapacityResultInfo capacity){
		try {
			dbTools.updateData(capacity, CapacityResultInfo.TABLENAME, capacity.getCapacityTestId());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据条件查询
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public List<CapacityResultInfo> searchCapacity(String selection, String[] selectionArgs, String groupBy, String having,
            String orderBy) {
		List<CapacityResultInfo> result = null;
		try {
			result = dbTools.getObjectByCondition(CapacityResultInfo.TABLENAME, null, selection, selectionArgs, groupBy, having, orderBy, CapacityResultInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
