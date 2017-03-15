package com.lxdz.capacity.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdz.capacity.R;
import com.lxdz.capacity.model.GetParamThread;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.widget.LoginDialog;

public class MainActivity extends BaseActivity {
	private final String TAG = MainActivity.class.getSimpleName();
	private GridView menuGridView;

	private static HashMap<Integer, MenuItem> menus;
	
	private static final int HELP_PAGE = 6;
	public static boolean isMainPage = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isMainPage = true;
		setContentView(R.layout.activity_main);
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int mwidth=display.getWidth();
		@SuppressWarnings("deprecation")
		int mheight=display.getHeight();
		System.out.println("宽："+mwidth+"高："+mheight);
		initSocketClient();
		init();
		initHelpFile();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		GetParamThread.isReceiveParam = true;
		isMainPage = false;
		SocketClient.isTesting = false;
	}
	
	// IntentFilter paramFilter = new IntentFilter(TranslateData.PARAM_SETTING);
	@Override
	protected void onResume() {
		super.onResume();
		isMainPage = true;
		if (SocketClient.isConnectedServer && (SocketClient.sendPackage == null || !SocketClient.sendPackage.isAlive())) {
			SocketClient.sendPackage = new SocketClient().new SendHeartPackage();
			SocketClient.sendPackage.start();
		}
	}
	
	/**
	 * 初始化socket客户端
	 */
	public void initSocketClient(){
		Intent intent = new Intent(MainActivity.this, SocketClient.class);
		startService(intent);
	}

	
	protected void init() {
		
		menus = new HashMap<Integer, MenuItem>() {
			{
				put(0,
						new MenuItem("基本信息", getResources().getDrawable(
								R.drawable.base_info)));
				/*put(1,
						new MenuItem("参数测量", getResources().getDrawable(
												R.drawable.param_test_ico)));*/
				put(1,
						new MenuItem("容量测试", getResources().getDrawable(
								R.drawable.data_acquired)));
				
				put(2,
						new MenuItem("空载测试", getResources().getDrawable(
								R.drawable.noload)));
				put(3,
						new MenuItem("负载测试", getResources().getDrawable(
								R.drawable.load)));
				
				put(4,
				new MenuItem("设备调试", getResources().getDrawable(
						R.drawable.data_analysis)));
				
				put(5,
						new MenuItem("数据管理", getResources().getDrawable(
								R.drawable.system_manager)));
				
				
				
				put(HELP_PAGE,
						new MenuItem("帮助", getResources().getDrawable(
								R.drawable.system_help)));
			}
		};
		menuGridView = (GridView) findViewById(R.id.menuGrid);
		GridAdapter gridAdapter = new GridAdapter(this);
		menuGridView.setAdapter(gridAdapter);
		menuGridView.setOnItemClickListener(menuItemClick);
	}
	
	/**
	 * 拷贝帮助文件到指定目录下
	 */
	private void initHelpFile() {
		InputStream inputStream = null;
		try {
			inputStream = getResources().openRawResource(R.raw.help);
			byte[] reader = new byte[inputStream.available()];
			while (inputStream.read(reader) != -1) {
			}
			writefile(reader, "/sdcard/bdlx/help.pdf");
		} catch (IOException e) {
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	// ==========文件输出=====================
		public void writefile(byte[] str, String path) {
			File file;
			FileOutputStream out;
			try {
				// 创建文件
				file = new File(path);
				file.createNewFile();
				// 打开文件file的OutputStream
				out = new FileOutputStream(file);

				// 将字符串转换成byte数组写入文件
				out.write(str);
				// 关闭文件file的OutputStream
				out.close();
			} catch (IOException e) {
				
			}
		}

	private AdapterView.OnItemClickListener menuItemClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, final int position,
				long id) {
			
			if(IndexActivity.openPageProgress != null) IndexActivity.openPageProgress.dismiss();
			
			switch(position) {
			case HELP_PAGE: // 帮助菜单
				File file = new File("/sdcard/bdlx/help.pdf");

				if (file.exists()) {
					Uri path = Uri.fromFile(file);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(path, "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(MainActivity.this,
								"没有可以打开pdf帮助文档的应用程序，请下载pdf阅读器！！",
								Toast.LENGTH_SHORT).show();
					}finally {
						if(IndexActivity.openPageProgress != null) IndexActivity.openPageProgress.dismiss();
					}
				}
				break;
			default: {
				if(position ==4 && CacheManager.debugPassword.length() == 0) {
					new LoginDialog(MainActivity.this).show();
					
				}else {
					Intent intent = new Intent(MainActivity.this, IndexActivity.class);
					IndexActivity.defaultSelected = position;
					IndexActivity.preClickTab = position;
					startActivity(intent);
					IndexActivity.openPageProgress = ProgressDialog.show(MainActivity.this, IndexActivity.CHANGE_PAGE, menus.get(position).menuTitle);
				}
			}
			}
			
		}
	};

	public class MenuItem {
		public String menuTitle;
		public Drawable drawable;

		public MenuItem() {
		}

		public MenuItem(String menuTitle, Drawable drawable) {
			this.menuTitle = menuTitle;
			this.drawable = drawable;
		}
	}

	public class ViewHolder {
		public TextView button;
	}

	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public final int getCount() {
			return menus.size();
		}

		public final Object getItem(int paramInt) {
			return null;
		}

		public final long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View convertView,
				ViewGroup paramViewGroup) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.menu_item, null);
				holder.button = (TextView) convertView
						.findViewById(R.id.menuItem);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MenuItem menu = menus.get(paramInt);
			holder.button.setText(menu.menuTitle);
			holder.button.setTextSize(22);
			Drawable draw = menu.drawable;
			draw.setBounds(0, 0, draw.getIntrinsicWidth(),
					draw.getIntrinsicHeight());
			holder.button.setCompoundDrawables(null, draw, null, null);

			return convertView;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		GetParamThread.isReceiveParam = true;
		Intent intent = new Intent(MainActivity.this, SocketClient.class);
		stopService(intent);
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("确认退出吗？");
		builder.setTitle("提示");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
				CacheManager.currentTest = -1;
				CacheManager.debugPassword = "";
				onDestroy();
				System.exit(0);
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});
		builder.create().show();
	}
	
	

}
