package com.lxdz.capacity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.LoadResultInfo;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.TransformerInfo;

/**
 * 数据库帮助类
 * @author liuniu
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	
	public static String DATABASE_NAME = "LXDZ_CAPACITY.db";
	private static final int databaseVersion=1;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, databaseVersion);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(new TransformerInfo().getCreateSql());
		db.execSQL(new CapacityResultInfo().getCreateSql());
		db.execSQL(new LoadResultInfo().getCreateSql());
		db.execSQL(new NoloadResultInfo().getCreateSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	


}
