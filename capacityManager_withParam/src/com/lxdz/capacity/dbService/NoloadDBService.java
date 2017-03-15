package com.lxdz.capacity.dbService;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.lxdz.capacity.db.DBTools;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.TransformerInfo;

/**
 * 容量测试数据类
 * @author Administrator
 *
 */
public class NoloadDBService {
	private DBTools dbTools;
	private Context context;
	
	public NoloadDBService(Context context) {
		this.context = context;
		this.dbTools = new DBTools(context);
	}
	
	public void insertNoload(NoloadResultInfo noload) {
		if(noload == null) return;
		
		this.deleteNoload(" transformerId=? ", new String[]{noload.getTransformerId() + ""});
		
		dbTools.insertData(noload, NoloadResultInfo.TABLENAME);
		String sql = "update " + TransformerInfo.TABLENAME + " set updateTime=" + new Date().getTime() + " where transformerId=" + noload.getTransformerId();
		dbTools.excuteSQL(sql);
	}
	
	public void deleteNoload(String sqlWhere, String[] sqlClause){
		dbTools.deleteData(NoloadResultInfo.TABLENAME, sqlWhere, sqlClause);
	}
	
	public void updateNoload(NoloadResultInfo noload){
		try {
			dbTools.updateData(noload, NoloadResultInfo.TABLENAME, noload.getNoloadTestId());
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
	public List<NoloadResultInfo> searchNoload(String selection, String[] selectionArgs, String groupBy, String having,
            String orderBy) {
		List<NoloadResultInfo> result = null;
		try {
			result = dbTools.getObjectByCondition(NoloadResultInfo.TABLENAME, null, selection, selectionArgs, groupBy, having, orderBy, NoloadResultInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
