package com.lxdz.capacity.fragment;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.lxdz.capacity.R;
import com.lxdz.capacity.activity.IndexActivity;
import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.TestNormalData;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.socket.TranslateData;
import com.lxdz.capacity.util.HexDump;

public class DebugTestAllFragment_copy extends Fragment{
private EditText sendLog;
	
	/**
	 * 调试项目
	 */
	private Spinner debugProject;
	private ArrayAdapter<CharSequence> debugProjectAdapter;
	
	private View containerFather;
	
	private static final String CAPACITY_NAME = "CAPACITY";
	private static final String NOLOAD_NAME = "NOLOAD";
	
	
	private Button up10;
	private Button down10;
	private Button up1;
	private Button down1;
	private Button up01;
	private Button down01;
	private Button up001;
	private Button down001;
	
	
	private Button saveVoltage;
	private Button saveCurrent;
	
	public static boolean loadResult = true;
	public static boolean isDebugPage = false;
	
	public static int loadDataType = 0;
	
	private LinearLayout gearPanel;
	
	/**
	 * 选择的项目
	 */
	private static String projectCode = DataModel.DEBUG_CAPACITY;
	private static String projectName = "CAPACITY";
	private static String classCode;
	private static int gearPosition;
	/**
	 * 选择的档位
	 */
	private String gearCode;
	
	private CapacityResultInfo capacityInfo;
	private NoloadResultInfo noloadInfo;
	
	private RadioGroup debugClass;
	private RadioGroup debugGear;
	
	private TabHost tabHost;
	
	public PublicReturnFragment publicData;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getIntExtra(SocketClient.COMMAND_TYPE, 0) != DataModel.CAPACITY_COMMAND_INT) return;
			int type = intent.getIntExtra(SocketClient.COMMAND_TYPE, -1);
			byte[] receiveData = intent.getByteArrayExtra(SocketClient.NEW_DATA);
			if(type == DataModel.CAPACITY_COMMAND_INT) {
				capacityInfo = TranslateData.translateCapacity(receiveData);
			}else if(type == DataModel.NOLOAD_COMMAND_INT) {
				noloadInfo = TranslateData.translateNoload(receiveData);
			}
			setValue(type);
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(containerFather == null) containerFather = inflater.inflate(R.layout.debug_test_all_copy, container, false);
		ViewGroup parent = (ViewGroup) containerFather.getParent();  
        if (parent != null) {  
              parent.removeView(containerFather);  
        }   
		init();
		
		return containerFather;
	}
	
	public void initFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		/*testData = new CapacityResultPublicFragment();
		fragmentManager.beginTransaction().add(R.id.capacityTest_capacityMain, testData).commit();*/
		publicData = (PublicReturnFragment)fragmentManager.findFragmentById(R.id.publicResult_debugAll);
	}
	
	private void init() {
		gearPanel = (LinearLayout)containerFather.findViewById(R.id.gearPanel_debugAllCopy);
		// debugProject = (Spinner) containerFather.findViewById(R.id.debugProject_debugAll);
		debugProjectAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.debugProject, R.layout.adapter_item);
		debugProject.setAdapter(debugProjectAdapter);
		debugProject.setSelection(1, false);
		debugProject.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				loadResult = false;
				if(position == 1){
					projectName = "CAPACITY";
					projectCode = DataModel.DEBUG_CAPACITY;
					loadDataType = DataModel.CAPACITY_COMMAND_INT;
					loadResult = true;
					new GetTestData(DataModel.CAPACITY_COMMAND_INT).start();
					
				}else if(position == 2){
					projectName = "NOLOAD";
					projectCode = DataModel.DEBUG_NOLOAD;
					loadDataType = DataModel.NOLOAD_COMMAND_INT;
					loadResult = true;	
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		debugClass = (RadioGroup)containerFather.findViewById(R.id.debugClass_debugAllCopy);
		debugClass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radio = (RadioButton)containerFather.findViewById(checkedId);
				
				classCode = radio.getText().toString().toUpperCase();
				if(classCode.indexOf("U") != -1) {
					gearPanel.setVisibility(View.GONE);
					if(DataModel.DEBUG_CAPACITY.equals(projectCode)) {
						if("UA".equals(classCode)) {
							gearCode = DataModel.DEBUG_CAPACITY_UA1;
						}else if("UB".equals(classCode)) {
							gearCode = DataModel.DEBUG_CAPACITY_UB1;
						}else if("UC".equals(classCode)) {
							gearCode = DataModel.DEBUG_CAPACITY_UC1;
						}
					}else if(DataModel.DEBUG_NOLOAD.equals(projectCode)) {
						
						if("UA".equals(classCode)) {
							gearCode = DataModel.DEBUG_NOLOAD_UA1;
						}else if("UB".equals(classCode)) {
							gearCode = DataModel.DEBUG_NOLOAD_UB1;
						}else if("UC".equals(classCode)) {
							gearCode = DataModel.DEBUG_NOLOAD_UC1;
						}
					}
					}else {
						gearPanel.setVisibility(View.VISIBLE);
					}
				
			}
		});
		debugGear = (RadioGroup)containerFather.findViewById(R.id.debugGear_debugAllCopy);
		debugGear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radio = (RadioButton)containerFather.findViewById(checkedId);
				String temp = radio.getText().toString();
				if("100%".equals(temp)) {
					gearPosition = 1;
				}else if ("10%".equals(temp)){
					gearPosition = 2;
				}else {
					gearPosition = 3;
				}
			}
		});
		
		up10 = (Button)containerFather.findViewById(R.id.up10);
		up10.setOnClickListener(new ButtonListener());
		down10 = (Button)containerFather.findViewById(R.id.down10);
		down10.setOnClickListener(new ButtonListener());
		up1 = (Button)containerFather.findViewById(R.id.up1);
		up1.setOnClickListener(new ButtonListener());
		
		down1 = (Button)containerFather.findViewById(R.id.down1);
		down1.setOnClickListener(new ButtonListener());
		up01 = (Button)containerFather.findViewById(R.id.up01);
		up01.setOnClickListener(new ButtonListener());
		down01 = (Button)containerFather.findViewById(R.id.down01);
		down01.setOnClickListener(new ButtonListener());
		up001 = (Button)containerFather.findViewById(R.id.up001);
		up001.setOnClickListener(new ButtonListener());
		down001 = (Button)containerFather.findViewById(R.id.down001);
		down001.setOnClickListener(new ButtonListener());
		
		saveVoltage = (Button) containerFather.findViewById(R.id.saveVoltage_debugAll);
		saveVoltage.setOnClickListener(new SaveButton());
		
		saveCurrent = (Button) containerFather.findViewById(R.id.saveCurrent_debugAll);
		saveCurrent.setOnClickListener(new SaveButton());
	}
	
	
	IntentFilter filter = new IntentFilter(SocketClient.ON_NEW_DATA);
	
	@Override
	public void onResume() {
		super.onResume();
		loadResult = true;
		isDebugPage = true;
		setValue(DataModel.CAPACITY_COMMAND_INT);
		if(IndexActivity.openPageProgress != null) IndexActivity.openPageProgress.dismiss();
		this.getActivity().registerReceiver(receiver, filter);
		new GetTestData(DataModel.CAPACITY_COMMAND_INT).start();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		loadResult = false;
		isDebugPage = false;
	}
	
	
	private void setValue(int type) {
		DecimalFormat decimalFormat  = new DecimalFormat("##0.0000");
		TestNormalData testData = null;
		if(type == DataModel.CAPACITY_COMMAND_INT) {
			testData = capacityInfo;
		}else if(type == DataModel.NOLOAD_COMMAND_INT) {
			testData = noloadInfo;
		}else if(type == 0) {
			testData = new TestNormalData();
		}
		if(testData == null) return;
		publicData.setValue(testData);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 不知道在这里注销行不行，以后有了设备再考量下，看会不会影响广播的接收？？？？？？？？？？？？？？？？？？？
		this.getActivity().unregisterReceiver(receiver);
	}
	
	class SaveButton implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			DataModel model = new DataModel();
			switch(v.getId()) {
			case R.id.saveCurrent_debugAll:
				if(CAPACITY_NAME.equals(projectName)) {
					model.setRequestCode(DataModel.DEBUG_SAVE_CAPACITY, DataModel.DEBUG_SAVE_CAPACITY_CURRENT, DataModel.NO_DATA);
				}else if(NOLOAD_NAME.equals(projectName)) {
					model.setRequestCode(DataModel.DEBUG_SAVE_NOLOAD, DataModel.DEBUG_SAVE_NOLOAD_CURRENT, DataModel.NO_DATA);
				}
				break;
			case R.id.saveVoltage_debugAll:
				if(CAPACITY_NAME.equals(projectName)) {
					model.setRequestCode(DataModel.DEBUG_SAVE_CAPACITY, DataModel.DEBUG_SAVE_CAPACITY_VOLTAGE, DataModel.NO_DATA);
				}else if(NOLOAD_NAME.equals(projectName)) {
					model.setRequestCode(DataModel.DEBUG_SAVE_NOLOAD, DataModel.DEBUG_SAVE_NOLOAD_VOLTAGE, DataModel.NO_DATA);
				}
				break;
			}
			SocketClient.isTesting = true;
			SocketClient.write(model);
			
		}
		
	}
	
	class ButtonListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			DataModel model = new DataModel();
			if(projectCode == null ){
				Toast.makeText(getActivity(), "请选择项目", Toast.LENGTH_SHORT).show();
				return;
			}
			getGearCode();
			if(gearCode == null) {
				Toast.makeText(getActivity(), "请选择操作项", Toast.LENGTH_SHORT).show();
				return;
			}
			
			switch(v.getId()){
			
			
			case R.id.up10:
				model.setRequestCode(projectCode, gearCode, DataModel.DEBUG_UP_10);
				break;
			case R.id.down10:
				model.setRequestCode(projectCode, gearCode, DataModel.DEBUG_DOWN_10);
				break;
			case R.id.up1:
				model.setRequestCode(projectCode, gearCode, DataModel.DEBUG_UP_1);
				break;
			case R.id.down1:
				model.setRequestCode(projectCode, gearCode, DataModel.DEBUG_DOWN_1);
				break;
				
			case R.id.up01:
				model.setRequestCode(projectCode, gearCode, DataModel.DEBUG_UP_01);
				break;
			case R.id.down01:
				model.setRequestCode(projectCode, gearCode, DataModel.DEBUG_DOWN_01);
				break;
			case R.id.up001:
				model.setRequestCode(projectCode, gearCode, DataModel.DEBUG_UP_001);
				break;
			case R.id.down001:
				model.setRequestCode(projectCode, gearCode, DataModel.DEBUG_DOWN_001);
				break;
			}
			SocketClient.isTesting = true;
			if (SocketClient.write(model)) {
				String text = sendLog.getText().toString() + "\r\n";
				sendLog.setText(text + HexDump.BytesToHexString(model.getRequestCode()));
			};
			
		}
		
	}
	
	/**
	 * 生成调试码
	 * @return
	 */
	private void getGearCode() {
		
		if(projectName == null) return;
		
		String fieldName = "DEBUG_" + projectName + "_" + classCode.toUpperCase() + gearPosition;
		Field field;
		DataModel model = new DataModel();
		try {
			field = DataModel.class.getDeclaredField(fieldName);
			gearCode = (String)field.get(model);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static class GetTestData extends Thread{
		
		DataModel param;
		
		public GetTestData(int type) {
			param = new DataModel();
			// param.setRequestCode(DataModel.DEBUG_CAPACITY, DataModel.DEBUG_CAPACITY_UA1, DataModel.DEBUG_UP_10);
			if(type == DataModel.CAPACITY_COMMAND_INT) {
				param.setRequestCode(DataModel.CAPACITY_COMMAND, DataModel.NO_COMMAND, DataModel.NO_DATA);
			} else if(type == DataModel.NOLOAD_COMMAND_INT) {
				param.setRequestCode(DataModel.NOLOAD_COMMAND, DataModel.NO_COMMAND, DataModel.NO_DATA);
			}
			
		}
		
		public void run() {
			while(loadResult) {
				SocketClient.isTesting = true;
				SocketClient.write(param) ;
				try {
					Thread.sleep(SocketClient.longCommandSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
		}
	}
	}

}

