package com.lxdz.capacity.dbService;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.lxdz.capacity.db.DBTools;
import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.LoadResultInfo;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.TransformerInfo;
import com.lxdz.capacity.util.CacheManager;

/**
 * 容量测试数据类
 * @author Administrator
 *
 */
public class TransformerDBService {
	private DBTools dbTools;
	private Context context;
	
	public TransformerDBService(Context context) {
		this.context = context;
		this.dbTools = new DBTools(context);
	}
	
	public void insertTransformer(TransformerInfo transformer) {
		Date date = new Date();
		transformer.setUpdateTime(date.getTime());
		if(transformer.getTransformerId() == -1) {
			transformer.setCreateTime(date.getTime());
			TransformerInfo saveInfo = dbTools.insertData(transformer, TransformerInfo.TABLENAME);
			CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, saveInfo.getTransformerId());
			saveInfo.cacheTransformer(context);
		}else {
			this.updateTransformer(transformer);
		}
	}
	
	public void deleteTransformer(int transformerId){
		dbTools.deleteData(TransformerInfo.TABLENAME, " (transformerId=?)", new String[]{transformerId + ""});
		dbTools.deleteData(CapacityResultInfo.TABLENAME, " (transformerId=?)", new String[]{transformerId + ""});
		dbTools.deleteData(LoadResultInfo.TABLENAME, " (transformerId=?)", new String[]{transformerId + ""});
		dbTools.deleteData(NoloadResultInfo.TABLENAME, " (transformerId=?)", new String[]{transformerId + ""});
	}
	
	public void updateTransformer(TransformerInfo transformer){
		try {
			dbTools.updateData(transformer, TransformerInfo.TABLENAME, transformer.getTransformerId());
			//Toast.makeText(context, "数据修改成功", Toast.LENGTH_SHORT).show();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			Toast.makeText(context, "变压器信息保存失败", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	/**
	 * 根据条件查询
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public List<TransformerInfo> searchTransformer(String selection, String[] selectionArgs, String groupBy, String having,
            String orderBy) {
		List<TransformerInfo> result = null;
		try {
			result = dbTools.getObjectByCondition(TransformerInfo.TABLENAME, null, selection, selectionArgs, groupBy, having, orderBy, TransformerInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 更新所有表中同一变压器的测试时间表
	 */
	public void updateTestTime(int transformerId, long updateTime) {
		String sql = "update " + TransformerInfo.TABLENAME + " set updateTime=" + updateTime + " where transformerId=" + transformerId;
		dbTools.excuteSQL(sql);
		
	}
	
	/**
	 * 更新单个参数
	 * @param transformerId
	 * @param obj
	 */
	public void updateSingleParam(String transformerCode, String fieldName, Object value) {
		if(transformerCode == null || transformerCode.length() == 0) return;
		List<TransformerInfo> transformers = this.searchTransformer(" transformerCode=?", new String[]{transformerCode}, null, null, null);
		if(transformers == null || transformers.size() == 0) {
			TransformerInfo info = new TransformerInfo();
			info.setTransformerCode(transformerCode);
			this.insertTransformer(info);
		}
		String sql = "update " + TransformerInfo.TABLENAME + " set " + fieldName + "=" + value + " where transformerCode='" + transformerCode+"'";
		dbTools.excuteSQL(sql);
	}

}
