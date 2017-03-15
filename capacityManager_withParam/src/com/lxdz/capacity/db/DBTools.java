package com.lxdz.capacity.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lxdz.capacity.model.BaseModel;

/**
 * 数据库工具类，有一个要求，表中的字段和类中的属性名字要相同
 * @author Administrator
 *
 */
public class DBTools {
	private  DBHelper dbHelper;
	private String TAG = DBTools.class.getSimpleName();
	
	public DBTools(Context context){
		DatabaseContext dbContext = new DatabaseContext(context);
		dbHelper = new DBHelper(dbContext);
	}
	
	/**
	 * 
	 * @param tableName 表名
	 * @param columns 需要查询的字段
	 * @param sqlWhere 需要查询的条件
	 * @return
	 */
	public <T>T getObject(String tableName, String[] columns, String sqlWhere, Class cls) throws Exception{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, columns, sqlWhere, null, null, null, null);
		T t =  convertCursorToObject(cursor, cls, 0);
		
		db.close();
		return t;
	}
	
	/**
	 * 查询单个对象
	 * @param sql 查询语句
	 * @param values 查询语句中？处的值
	 * @param cls 返回的对象类型
	 * @return
	 * @throws Exception
	 */
	public <T>T getObject(String sql, String[] values, Class cls)  throws Exception{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, values);
		T t =  convertCursorToObject(cursor, cls, 0);
		db.close();
		return t;
	}
	
	private <T extends BaseModel>T convertCursorToObject(Cursor cursor, Class cls, int position) throws Exception{
		cursor.moveToPosition(position);
		T instance = (T)cls.newInstance();
		// Field[] fields = cls.getFields();
		Field[] fields = cls.getDeclaredFields();
		if(cls.getSuperclass() != BaseModel.class || cls.getSuperclass() != Object.class) {
			Class parent = cls.getSuperclass();
			Field[] parentFields = parent.getDeclaredFields();
			if(parentFields.length > 0) {
				Field[] temp = new Field[fields.length + parentFields.length];
				System.arraycopy(fields, 0,  temp, 0,fields.length);
				System.arraycopy(parentFields, 0, temp, fields.length, parentFields.length);
				fields = temp;
			}
		}
		Field.setAccessible(fields, true);
		for(Field field : fields){
			
			// int index = cursor.getColumnIndex(field.getName().toUpperCase()) == -1 ? cursor.getColumnIndex(field.getName().toLowerCase()) : -1;
			int index = cursor.getColumnIndex(field.getName());
			if(index == -1) continue;
			String value = cursor.getString(index);
			if(value == null) continue;
			String fieldType = field.getType().getSimpleName().toLowerCase();
			if("integer".equals(fieldType) || "int".equals(fieldType)) {
				field.setInt(instance, Integer.parseInt(value));
			} else if("float".equals(fieldType)){
				field.setFloat(instance, Float.parseFloat(value));
			} else if("double".equals(fieldType)){
				field.setDouble(instance, Double.parseDouble(value));
			} else if("long".equals(fieldType)){
				field.setLong(instance, Long.parseLong(value));
			}else {
				field.set(instance, value);
			}
			
			
		}
		
		
		return instance;
	}
	
	/**
	 * 根据条件查询
	 * @param tableName
	 * @param sqlWhere
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T>List<T> getObjectByCondition(String tableName,  String sqlWhere, Class cls) throws Exception{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		StringBuffer sql = new StringBuffer();
		
		sql.append("select * from ").append(tableName);	
		if(sqlWhere != null ){
			sql.append(sqlWhere);
		}
		Log.d(TAG, "查询语句" + sql.toString());
		
        Cursor rec = db.rawQuery(sql.toString(), null);
        int columnCount = rec.getColumnCount();
        int rawCount = rec.getCount();
        List<T> result = new ArrayList<T>();
        for(int i = 0; i < rawCount; i++){
        	rec.moveToPosition(i);
        	result.add((T)convertCursorToObject(rec, cls, i));
        }
        rec.close();
        db.close();
		return result;
	}
	
	/**
	 * 根据条件查询
	 * @param tableName
	 * @param sqlWhere
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T>List<T> getObjectByCondition(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
            String orderBy, Class cls)  throws Exception{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
        Cursor rec = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        
        
        int columnCount = rec.getColumnCount();
        int rawCount = rec.getCount();
        List<T> result = new ArrayList<T>();
        for(int i = 0; i < rawCount; i++){
        	rec.moveToPosition(i);
        	result.add((T)convertCursorToObject(rec, cls, i));
        }
        rec.close();
        db.close();
		return result;
	}
	
	/**
	 * 根据条件查询
	 * @param tableName
	 * @param sqlWhere
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T>List<T> getObjectByCondition(String sql, Class cls) throws Exception{
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Log.d(TAG, "查询语句" + sql.toString());
        Cursor rec = db.rawQuery(sql.toString(), null);
        
        int rawCount = rec.getCount();
        List<T> result = new ArrayList<T>();
        for(int i = 0; i < rawCount; i++){
        	rec.moveToPosition(i);
        	result.add((T)convertCursorToObject(rec, cls, i));
        }
        rec.close();
        db.close();
		return result;
	}
	
	
	/**
	 * 分页查询
	 * @param start 起始
	 * @param end 结束
	 * @return
	 */
	public Cursor getObjectPage(String tableName, String sqlWhere, int page, int pageSize){
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ").append(tableName).append(sqlWhere).append(" limit ").append(" ").append(pageSize).append(" offset ").append(page * pageSize);
		
        Cursor rec = db.rawQuery(sql.toString(), null);
        
		return rec;
	}
	
	/**
	 * 根据主键修改数据
	 * @throws NoSuchFieldException 
	 */
	public <T extends BaseModel>T updateData(BaseModel obj, String tableName, Object primaryValue) throws NoSuchFieldException{
		T newAdd = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues value = this.getContentValues(obj);
		try {
			db.update(tableName, value, obj.getAutoIncreamentPK() + "=?", new String[]{primaryValue + ""});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		if(obj.getAutoIncreamentPK() != null) {
			String sql = "select * from " + tableName +" where " + obj.getAutoIncreamentPK() + "=" + primaryValue;
			Cursor cursor = db.rawQuery(sql, null);
			
			try {
				newAdd = convertCursorToObject(cursor, obj.getClass(), 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
		return newAdd;
	}
	
	public <T extends BaseModel>T insertData(BaseModel obj, String tableName){
		T newAdd = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues value = this.getContentValues(obj);
		
		if(value == null) return null;
		long insertId = db.insert(tableName, null, value);
		if(insertId == -1) return null;
		if(obj.getAutoIncreamentPK() != null) {
			String sql = "select * from " + tableName +" order by " + obj.getAutoIncreamentPK() + " desc limit 0,1";
			Cursor cursor = db.rawQuery(sql, null);
			
			try {
				newAdd = convertCursorToObject(cursor, obj.getClass(), 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
		return newAdd;
	}
	
	/**
	 * 判断一个字段是否存在
	 * @return
	 */
	private boolean isExistField(Class cls, String fieldName) {
		boolean result = true;
		try {
			cls.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			return false;
		}
		return result;
	}
	
	private ContentValues getContentValues(BaseModel obj) {
		ContentValues value = new ContentValues();
		HashMap<String, BaseModel.ColumnModel> columns = obj.getColumns();
		if(columns == null) return null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Iterator<String> columnIter = columns.keySet().iterator();
		while(columnIter.hasNext()){
			String fieldName = columnIter.next();
			BaseModel.ColumnModel columnName = columns.get(fieldName);
			if(columnName.isTableColumn() && columnName.getColumnName().toUpperCase().equals(obj.getAutoIncreamentPK().toUpperCase())) continue;
			 Field f = null;
			try {
				if(!columnName.isTableColumn()) continue;
				if(!this.isExistField(obj.getClass(), fieldName)) {
					if(this.isExistField(obj.getClass().getSuperclass(), fieldName)){
						f = obj.getClass().getSuperclass().getDeclaredField(fieldName);
					}else {
						continue;
					}
				}else {
					f = obj.getClass().getDeclaredField(fieldName);
				}
				
				boolean accessFlag = f.isAccessible();
	            f.setAccessible(true);
	            Object fieldValue = f.get(obj);
	            f.setAccessible(accessFlag);
	            if (fieldValue == null) continue;
	            if(fieldValue instanceof Integer){
					value.put(columnName.getColumnName(), (Integer)fieldValue);
				} else if(fieldValue instanceof Float){
					value.put(columnName.getColumnName(), (Float)fieldValue);
				} else {
					value.put(columnName.getColumnName(), fieldValue.toString() );
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	/**
	 * 批量插入
	 * @param values 插入的值
	 * @param tableName
	 * @param cls 插入的类型
	 * @throws Exception
	 */
	public void  bulkInsert(List values, String tableName, Class cls) throws Exception{
		
		if(values == null || values.size() == 0) return ;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		
		BaseModel obj = (BaseModel)values.get(0);
		Map<String, BaseModel.ColumnModel> columnFields = obj.getColumns();
		
		String columns = "";
		Iterator<String> columnIter = columnFields.keySet().iterator();
		while(columnIter.hasNext()){
			BaseModel.ColumnModel columnName = columnFields.get(columnIter.next());
			if(!columnName.isTableColumn() || columnName.getColumnName().toUpperCase().equals(obj.getAutoIncreamentPK().toUpperCase())) continue;
			
			columns += columnName.getColumnName() + ",";
			
		}
		String sql = "INSERT INTO " + tableName + " (" + columns.substring(0, columns.length() - 1) + ") VALUES (" ;
		StringBuffer sqlTemp;
		
		for(Object value : values){
			sqlTemp = new StringBuffer(sql);
			columnIter = columnFields.keySet().iterator();
			while(columnIter.hasNext()){
				try {
					String fieldName = columnIter.next();
					BaseModel.ColumnModel columnName = columnFields.get(fieldName);
					if(!columnName.isTableColumn() || columnName.getColumnName().toUpperCase().equals(obj.getAutoIncreamentPK().toUpperCase())) continue;
					
		            Field f = value.getClass().getDeclaredField(fieldName);
		            boolean accessFlag = f.isAccessible();
		            f.setAccessible(true);
		            
		            Object fieldValue = f.get(value);
		            f.setAccessible(accessFlag);
		            if (fieldValue == null) continue;
		            if(fieldValue instanceof String){
		            	sqlTemp.append("'").append(fieldValue).append("',");
		            } else {
		            	sqlTemp.append(fieldValue).append(",");
		            }
					
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				
			}
			db.execSQL(sqlTemp.toString().substring(0, sqlTemp.length() - 1) + " )");
			
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	
	/**
	 * 	更新数据
	 * @param dataMAP的key为需要更新的字段名，value为需要更新的值
	 * @param table
	 * @param whereSql
	 * @param whereArgs
	 */
	public void updateData(ContentValues value, String table, String whereSql, String[] whereArgs){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.update(table, value, whereSql, whereArgs);
		
	}
	
	public void deleteData(String tableName, String whereClause, String whereArgs[]) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(tableName, whereClause, whereArgs);
		
	}
	
	
	public void deleteData(String tableName, String sqlWhere){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "delete from " + tableName;
		if(sqlWhere != null) {
			sql += sqlWhere;
		}
		db.execSQL(sql);
		db.close();
	}
	
	public int getCount(String tableName){
		String sql = "select count(*) from " + tableName;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToNext()){
			return cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return 0;
	}
	
	public void excuteSQL(String sql) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		db.execSQL(sql);
		db.close();
	}
	


}
