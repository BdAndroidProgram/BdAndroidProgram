package com.lxdz.capacity.adapter;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lxdz.capacity.socket.WifiAdmin;

/**
 * 扫描到的WIFI列表
 * @author Administrator
 *
 */
public class WifiAdapter extends BaseAdapter{
	
	private WifiAdmin wifiAdmin;
	
	private List<ScanResult> wifiList;
	
	private LayoutInflater inflater;
	
	public WifiAdapter(Context context) {
		wifiAdmin = new WifiAdmin(context);
		wifiList = wifiAdmin.getWifiList();
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return wifiList.size();
	}

	@Override
	public Object getItem(int position) {
		return wifiList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return wifiList.get(position).level;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		/*if(convertView == null) {
			convertView = inflater.inflate(R.layout.wifi_item, null);
			holder = new ViewHolder();
			holder.wifiName = (TextView)convertView.findViewById(R.id.wifiName);
			holder.wifiState = (TextView)convertView.findViewById(R.id.wifiState);
			holder.wifiStrength = (ImageButton)convertView.findViewById(R.id.wifiStrength);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}*/
		holder.wifiName.setText(wifiList.get(position).SSID);
		holder.wifiState.setText(wifiList.get(position).capabilities);
		
		return convertView;
	}
	
	class ViewHolder{
		TextView wifiName;
		TextView wifiState;
		ImageButton wifiStrength;
	}

}
