package com.lxdz.capacity.dbService;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.lxdz.capacity.db.DBTools;
import com.lxdz.capacity.model.LoadResultInfo;
import com.lxdz.capacity.model.TransformerInfo;

/**
 * 容量测试数据类
 * @author Administrator
 *
 */
public class LoadDBService {
	private DBTools dbTools;
	private Context context;
	
	public LoadDBService(Context context) {
		this.context = context;
		this.dbTools = new DBTools(context);
	}
	
	public void insertLoad(LoadResultInfo load) {
		if(load == null )return ;
		this.deleteLoad(" transformerId=?", new String[]{load.getTransformerId() + ""});
		
		if(dbTools.insertData(load, LoadResultInfo.TABLENAME) != null) {
			Toast.makeText(context, "负载测试数据保存成功", Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(context, "负载测试数据保存失败", Toast.LENGTH_SHORT).show();
		}
		String sql = "update " + TransformerInfo.TABLENAME + " set updateTime=" + new Date().getTime() + " where transformerId=" + load.getTransformerId();
		dbTools.excuteSQL(sql);
	}
	
	public void deleteLoad(String sqlWhere, String[] sqlClause){
		dbTools.deleteData(LoadResultInfo.TABLENAME, sqlWhere, sqlClause);
	}
	
	/**
	 * 根据主键进行修改
	 * @param load
	 */
	public void updateLoad(LoadResultInfo load){
		try {
			dbTools.updateData(load, LoadResultInfo.TABLENAME, load.getLoadTestId());
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
	public List<LoadResultInfo> searchLoad(String selection, String[] selectionArgs, String groupBy, String having,
            String orderBy) {
		List<LoadResultInfo> result = null;
		try {
			result = dbTools.getObjectByCondition(LoadResultInfo.TABLENAME, null, selection, selectionArgs, groupBy, having, orderBy, LoadResultInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
