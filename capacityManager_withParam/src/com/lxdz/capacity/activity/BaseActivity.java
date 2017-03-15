package com.lxdz.capacity.activity;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lxdz.capacity.R;
import com.lxdz.capacity.model.GetParamThread;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;

public class BaseActivity extends FragmentActivity{
	
	public  static MenuItem batteryItem1;
	public  static MenuItem batteryItem2;
	

	
	public static Handler paramSetHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			byte[] temp = new byte[2];
			if(CacheManager.params == null || CacheManager.params.length < 1) return;
			if(batteryItem1 != null) {
				System.arraycopy(CacheManager.params, 26, temp, 0, 2);
				int battery = HexDump.byteToint(temp);
				batteryItem1.setTitle(battery + "%");
				setBatteryIcon(batteryItem1, battery);
			}
			if(batteryItem2 != null) {
				
				System.arraycopy(CacheManager.params, 28, temp, 0, 2);
				int battery = HexDump.byteToint(temp);
				batteryItem2.setTitle(battery + "%");
				setBatteryIcon(batteryItem2, battery);
			}
			
		}
	};
	
	private static void setBatteryIcon(MenuItem batteryMenu, int battery) {
		if(battery > 90) {
			batteryMenu.setIcon(R.drawable.battery100);
		}else if(battery > 80) {
			batteryMenu.setIcon(R.drawable.battery80);
		}else if(battery > 60) {
			batteryMenu.setIcon(R.drawable.battery60);
		}else if(battery > 40) {
			batteryMenu.setIcon(R.drawable.battery40);
		}else if(battery > 10) {
			batteryMenu.setIcon(R.drawable.battery10);
		}else {
			batteryMenu.setIcon(R.drawable.battery0);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		batteryItem1 = (MenuItem) menu.findItem(R.id.batteryLevel1);
		batteryItem2 = (MenuItem) menu.findItem(R.id.batteryLevel2);
		return true;
	}
	

	
	public GetParamThread getParam;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (SocketClient.isConnectedServer && (getParam == null || !getParam.isAlive())) {
			GetParamThread.isReceiveParam = false;
			getParam = new GetParamThread();
			getParam.start();
		}
		return super.onOptionsItemSelected(item);
	}

}
