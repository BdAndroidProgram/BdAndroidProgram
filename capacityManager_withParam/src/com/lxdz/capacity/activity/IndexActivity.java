package com.lxdz.capacity.activity;

import java.util.Date;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.lxdz.capacity.R;
import com.lxdz.capacity.fragment.BaseInfoFragment;
import com.lxdz.capacity.fragment.CapacityTestAllFragment;
import com.lxdz.capacity.fragment.DataManagerAllFragment;
import com.lxdz.capacity.fragment.DebugTestAllFragment;
import com.lxdz.capacity.fragment.LoadTestAllFragment;
import com.lxdz.capacity.fragment.NoLoadTestAllFragment;
import com.lxdz.capacity.fragment.ParamTestingFragment;
import com.lxdz.capacity.model.GetParamThread;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.widget.LoginDialog;
// public class IndexActivity extends BaseActivity
public class IndexActivity extends FragmentActivity{
	
	public static int defaultSelected = -1;
	
	public static ProgressDialog openPageProgress;
	
	public static final String CHANGE_PAGE = "系统提示";
	
	private FragmentTabHost tabHost;
	
	private Class[] fragmentArray = { BaseInfoFragment.class, CapacityTestAllFragment.class,  NoLoadTestAllFragment.class, LoadTestAllFragment.class, DebugTestAllFragment.class, DataManagerAllFragment.class}; // ParamTestingFragment.class,
	private String[] fragmentString = {  "基本信息","容量测试", "空载测试", "负载测试","设备调试",  "数据管理" };//"参数测量", 
	
	static boolean changePage = false;
	
	public static int preClickTab = -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_main);
		System.out.println("indexActivity初始化开始" + new Date().getTime());
		init();
		System.out.println("indexActivity初始化结束" + new Date().getTime());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		GetParamThread.isReceiveParam = true;
	}
	
	private void init(){
		tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		tabHost.setup(this,  getSupportFragmentManager(), R.id.realContent);
		int menuCount = fragmentString.length;
		for(int i = 0; i < menuCount; i++) {
			TabSpec tab = tabHost.newTabSpec(fragmentString[i]).setIndicator(getTabView(i));
			tabHost.addTab(tab, fragmentArray[i], null);
			tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.title_config);
			tabHost.getTabWidget().getChildAt(i).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final TextView text = (TextView)v.findViewById(R.id.tabButton);
					final int index = (Integer)text.getTag();
					if(preClickTab == index) return;
					if( index == 4 && CacheManager.debugPassword.length() == 0) {
						new LoginDialog(IndexActivity.this).show();
					} else {
						changePage = true;
						tabHost.setCurrentTab(index);
						preClickTab = index;
						defaultSelected = index;
					}
				}
			});			
		}
		if(defaultSelected != -1) {
			tabHost.setCurrentTab(defaultSelected);
		}
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
		
		@Override
		public void onTabChanged(String tabId) {
				if(openPageProgress != null) {
					openPageProgress.dismiss();
				}
				openPageProgress = ProgressDialog.show(IndexActivity.this, CHANGE_PAGE, "页面" + tabId + "正在打开，请稍后");
		}
	});
	}
	
	private View getTabView(int index){
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.tab_item, null);
		TextView btn = (TextView)view.findViewById(R.id.tabButton);
		btn.setTag(index);
		btn.setText(fragmentString[index]);
		return view;
	}

	public int getDefaultSelected() {
		return defaultSelected;
	}

	public void setDefaultSelected(int d) {
		defaultSelected = d;
	}
	
	

}
