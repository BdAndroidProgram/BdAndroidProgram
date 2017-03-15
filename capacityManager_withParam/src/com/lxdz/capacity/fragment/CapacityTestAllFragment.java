package com.lxdz.capacity.fragment;

import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdz.capacity.R;
import com.lxdz.capacity.activity.IndexActivity;
import com.lxdz.capacity.dbService.CapacityDBService;
import com.lxdz.capacity.dbService.TransformerDBService;
import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.GetCapacityTest;
import com.lxdz.capacity.model.GetParamThread;
import com.lxdz.capacity.model.TransformerInfo;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.socket.TranslateData;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;
import com.lxdz.capacity.widget.ConfirmDialog;
import com.lxdz.capacity.widget.UnitView;

/**
 * 容量测试
 * 
 * @author Administrator
 * 
 */
public class CapacityTestAllFragment extends BaseFragment {

	private static final String TAG = CapacityTestAllFragment.class
			.getSimpleName();

	private View containerFather;

	private EditText firstVoltage;

	private EditText secondVoltage;

	private static int endterCounter = 0;

	private CapacityDBService capacityDB;
	private TextView focuschange;

	/**
	 * 阻抗电压
	 */
	private EditText impedanceVoltage;

	private EditText currentTemperature;
	
	private EditText transformerId;

	/**
	 * 分接档位
	 */
	private Spinner tapGear;
	private ArrayAdapter<CharSequence> tapGearAdapter;

	/**
	 * 联结组别
	 */
	private Spinner connectionGroup;
	private ArrayAdapter<CharSequence> connectionGroupAdapter;

	// 容量变压器类型
	private Spinner capacityTransformerType;
	private ArrayAdapter<CharSequence> capacityTypeAdapter;

	private Button print;

	private Button saveBtn;
	
	private Button getParamBtn;

	private Button beginTest;

	private CapacityResultInfo capacityInfo;

	CapacityResultPublicFragment testData;
	FragmentManager fm;

	private GetParamThread getParam;

	private boolean isLeavePage = false;

	public static boolean isSetParam = true;
	
	private static int subCode = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		if (containerFather == null) {
			containerFather = inflater.inflate(R.layout.capacity_test_all,
					container, false);
			initFragment();
			capacityDB = new CapacityDBService(getActivity());
			if(CacheManager.transformerInfo == null) CacheManager.transformerInfo = new TransformerInfo();
			init();
			endterCounter = 0;
		}
		
		ViewGroup parent = (ViewGroup) containerFather.getParent();
		if (parent != null) {
			parent.removeView(containerFather);
		}

		return containerFather;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra(SocketClient.COMMAND_TYPE, 0) != DataModel.CAPACITY_COMMAND_INT)
				return;
			byte[] receiveData = intent
					.getByteArrayExtra(SocketClient.NEW_DATA);
			translateReceiveData(receiveData);
			testData.setValue(capacityInfo);
		}
	};

	public BroadcastReceiver wifiConnect = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(!SocketClient.isWifiCorrect) return;
			if (getParam == null || !getParam.isAlive()) {
				CacheManager.params = null;
				GetParamThread.isReceiveParam = false;
				getParam = new GetParamThread();
				getParam.start();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				TranslateData.translateParam(CacheManager.params, getActivity());
				setParam();
			}
		}
	};

	/**
	 * 解析接收到的二进制数据
	 * 
	 * @param data
	 */
	private void translateReceiveData(byte[] data) {
		capacityInfo = TranslateData.translateCapacity(data);

		if (capacityInfo.getFinishTest()) {
			isParamEnable(capacityInfo.getFinishTest());
			beginTest.setEnabled(true);
			saveBtn.setEnabled(true);
			getParamBtn.setEnabled(true);
			print.setEnabled(true);
			SocketClient.isTesting = false;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (data == null || data.length == 0)
			return;

		byte[] temp = new byte[2];
		int temperatureTemp = 0;
		try {
			
			System.arraycopy(data, 61, temp, 0, 2);
			temperatureTemp = HexDump.byteToint(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*CacheManager.putInt( CacheManager.PARAM_CONFIG,
				CacheManager.CURRENT_TEMPERATUER, temperatureTemp);*/
		CacheManager.transformerInfo.setCurrentTemperature(temperatureTemp);
	}

	IntentFilter filter = new IntentFilter(SocketClient.ON_NEW_DATA);
	// wifi是否连接过滤器
		IntentFilter wifiConnected = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "onActivityCreated");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SocketClient.isConnectedServer) {
			SocketClient.isTesting = true;
			GetParamThread.isReceiveParam = false;
			getParam = new GetParamThread();
			getParam.start();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TranslateData.translateParam(CacheManager.params, getActivity());
			SocketClient.isTesting = false;
		}
		if(CacheManager.transformerInfo == null) CacheManager.transformerInfo = new TransformerInfo();
			setParam();
		
		GetCapacityTest.isTest = false;
		isLeavePage = false;
		beginTest.setEnabled(true);
		saveBtn.setEnabled(true);
		getParamBtn.setEnabled(true);
		print.setEnabled(true);
		isParamEnable(true);
		
		this.getActivity().registerReceiver(wifiConnect, wifiConnected);
		this.getActivity().registerReceiver(receiver, filter);

		if (++endterCounter > 1) {
			testData.initData();
			testData.hideResult(View.GONE);
		}
			
		SocketClient.currentPage = this;
		
		if (IndexActivity.openPageProgress != null)
			IndexActivity.openPageProgress.dismiss();
	}

	@Override
	public void onPause() {
		super.onPause();
		GetParamThread.isReceiveParam = true;
		isLeavePage = true;
		SocketClient.isTesting = false;
		GetCapacityTest.isTest = false;
		this.getActivity().unregisterReceiver(wifiConnect);
		this.getActivity().unregisterReceiver(receiver);
		SocketClient.currentPage = null;
		Log.i(TAG, "capacity on pause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "capacity onDestroy");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i(TAG, "capacity on stop");
	}

	public void initFragment() {
		testData = (CapacityResultPublicFragment) getFragmentManager()
				.findFragmentById(R.id.capacityTest_capacityMain);
	}

	private boolean isParamNull = false;
	
	private void sendParam(DataModel data) {
		if (SocketClient.write(data)) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			GetParamThread.isReceiveParam = false;

			if (getParam == null || !getParam.isAlive()) {
				GetParamThread.isReceiveParam = false;
				getParam = new GetParamThread();
				getParam.start();
				CacheManager.params = null;
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(CacheManager.params == null || CacheManager.params.length < 1) {
					isUpdateCorrect = false;
					isParamNull = true;
					checkParam();
					
				}else {
					isParamNull = false;
					TranslateData.translateParam(CacheManager.params, getActivity());
					checkParam();
				}
				
			}

		}
	}

	public void init() {
        focuschange =(TextView)containerFather.findViewById(R.id.focuschange);
		transformerId = (EditText)containerFather.findViewById(R.id.transformerCode_capacityMain);
		transformerId.setText("0");
		transformerId.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) return;
				/*String temp = CacheManager.getString(
						CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE,
						"");*/
				String temp = CacheManager.transformerInfo.getTransformerCode();
				System.out.println("变压器编号初始"+temp);
				srcValue = temp == null || temp.length() == 0 ? 0 : Integer.parseInt(temp);
				
				DataModel model = new DataModel() ;
				String dataStr = transformerId.getText().length() < 1 ? "0" : transformerId.getText().toString();
				int data = Integer.parseInt(dataStr);
				if(data > 10000) {
					Toast.makeText(getActivity(), "变压器编号不能大于10000", Toast.LENGTH_SHORT).show();
					return;
				}
				String sendData = HexDump.getLowStartData(data);
				if (sendData == null) return;
				model.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.TRANSFORMER_CODE, sendData);
				CacheManager.transformerInfo.setTransformerCode(dataStr);
				CacheManager.putString( CacheManager.PARAM_CONFIG,
						CacheManager.TRANSFORMER_CODE, dataStr );
				//SocketClient.isTesting = true;
				if(SocketClient.write(model)) {
					//SocketClient.isTesting = false;
					TransformerDBService transformerDB = new TransformerDBService(getActivity());
					List<TransformerInfo> transformers = transformerDB.searchTransformer(" transformerCode=?", new String[]{dataStr}, null, null, null);
					if(transformers == null || transformers.size() == 0) {
						/*CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);*/
						CacheManager.transformerInfo.setTransformerId(-1);
						TransformerInfo transformer = new TransformerInfo();
						transformer = transformer.getCacheTransformer(getActivity());
						transformer.setTransformerId(-1);
						transformerDB.insertTransformer(transformer);
					}
				}
				
			}
		});
		
		// 联结组别
		connectionGroup = (Spinner) containerFather
				.findViewById(R.id.connectionGroup_capacityMain);
		connectionGroupAdapter = ArrayAdapter.createFromResource(
				this.getActivity(), R.array.connectionGroupArray,
				R.layout.adapter_item);
		connectionGroup.setAdapter(connectionGroupAdapter);
		connectionGroup.setSelection(
				CacheManager.transformerInfo.getConnectionGroup(),
				 true);
		connectionGroup.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				/*srcValue = CacheManager.getInt(
						CacheManager.PARAM_CONFIG,
						CacheManager.CONNECTION_GROUP, -1);*/
				srcValue = CacheManager.transformerInfo.getConnectionGroup();
				if(!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					connectionGroup.setSelection((int) srcValue);
					return;
				}
				DataModel data = new DataModel();
				data.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.CONNECTION_GROUP,
						Integer.toString(position + 1, 16)
								+ DataModel.NO_COMMAND);
				Log.i(TAG, "连接组别" + HexDump.toHexString(data.getRequestCode()));
				subCode = DataModel.CONNECTION_GROUP_INT;
				sendParam(data);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		// 分接档位
		tapGear = (Spinner) containerFather
				.findViewById(R.id.tapGear_capacityMain);
		tapGearAdapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.tapGearArray, R.layout.adapter_item);
		tapGear.setAdapter(tapGearAdapter);
		tapGear.setSelection(
				CacheManager.transformerInfo.getTapGear() - 1, true);
		tapGear.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				/*srcValue etInt(
						CacheManager.PARAM_CONFIG, CacheManager.TAP_GEAR, -1);*/
				srcValue = CacheManager.transformerInfo.getTapGear();
				if(!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					tapGear.setSelection((int) srcValue);
					Log.i(TAG, "WIFI连接断开");
					return;
				}
				DataModel data = new DataModel();
				data.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.TAP_GEAR, Integer.toString(position + 1, 16)
								+ DataModel.NO_COMMAND);
				Log.i(TAG, "分接档位" + HexDump.toHexString(data.getRequestCode()));
				subCode = DataModel.TAP_GEAR_INT;
				sendParam(data);
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// 容量变压器类型
		capacityTypeAdapter = ArrayAdapter.createFromResource(
				this.getActivity(), R.array.capacityTransformerTypeArray,
				R.layout.adapter_item);
		capacityTransformerType = (Spinner) containerFather
				.findViewById(R.id.capacityTransformerType_capacityMain);
		capacityTransformerType.setAdapter(capacityTypeAdapter);
		capacityTransformerType.setSelection(CacheManager.transformerInfo.getCapacityTransformerType() - 1, true);
				
		capacityTransformerType
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						
						/*srcValue = Int(
								CacheManager.PARAM_CONFIG,
								CacheManager.CAPACITY_TRANSFORM_TYPE, -1);*/
						srcValue = CacheManager.transformerInfo.getCapacityTransformerType();
						if(!SocketClient.isConnectedServer) {
							isUpdateCorrect = false;
							capacityTransformerType.setSelection((int) srcValue);
							return;
						}
						DataModel data = new DataModel();
						data.setRequestCode(DataModel.CAPACITY_PARAM_SET,
								DataModel.CAPACITY_TRANSFORMER_TYPE,
								Integer.toString(position + 1, 16)
										+ DataModel.NO_COMMAND);
						Log.i(TAG,
								"变压器类型"
										+ HexDump.toHexString(data
												.getRequestCode()));
						subCode = DataModel.CAPACITY_TRANSFORMER_TYPE_INT;
						sendParam(data);
						
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		UnitView unit = (UnitView) containerFather
				.findViewById(R.id.firstVoltage_capacityMain);
		firstVoltage = unit.getDataText();
		firstVoltage.setOnFocusChangeListener(new FocusChange());
		unit = (UnitView) (UnitView) containerFather
				.findViewById(R.id.secondVoltage_capacityMain);
		secondVoltage = unit.getDataText();
		secondVoltage.setOnFocusChangeListener(new FocusChange());
		unit = (UnitView) (UnitView) containerFather
				.findViewById(R.id.impendanceVoltage_capacityMain);
		impedanceVoltage = unit.getDataText();
		impedanceVoltage.setOnFocusChangeListener(new FocusChange());

		unit = (UnitView) (UnitView) containerFather
				.findViewById(R.id.currentTemperature_capacityMain);
		currentTemperature = unit.getDataText();
		currentTemperature.setOnFocusChangeListener(new FocusChange());

		saveBtn = (Button) containerFather
				.findViewById(R.id.saveDate_capacityMain);
		saveBtn.setOnClickListener(new ButtonClickListener());

		getParamBtn = (Button) containerFather
				.findViewById(R.id.getParam_capacityMain);
		getParamBtn.setOnClickListener(new ButtonClickListener());

		beginTest = (Button) containerFather
				.findViewById(R.id.beginTest_capacityMain);
		beginTest.setOnClickListener(new ButtonClickListener());

		print = (Button) containerFather.findViewById(R.id.print_capacityMain);
		print.setOnClickListener(new ButtonClickListener());

	}

	private void setParam() {
		/*String strtemp = CacheManager.getString(
				CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE,
				"");*/
		
		String code=CacheManager.transformerInfo.getTransformerCode();
		System.out.println("变压器编号1"+code);
		if(code == null || code.length() == 0) {
			code=CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, "");
		}else {
			if(code.length() < 1 ) {
				code = "0000";
			} else if(code.length() == 1) {
			code = "000" + code;
			}else if(code.length() == 2) {
			code = "00" + code;
			}else  if(code.length() == 3) {
			code = "0" + code;
			} 
		}
		transformerId.setText(code);
		/*float temp = CacheManager.getFloat(
				CacheManager.PARAM_CONFIG, CacheManager.FIRST_VOLTAGE, 0);*/
		firstVoltage.setText(CacheManager.transformerInfo.getFirstVoltage() + "");

		/*temp = CacheManager.getFloat( CacheManager.PARAM_CONFIG,
				CacheManager.SECOND_VOLTAGE, 0);*/
		secondVoltage.setText(CacheManager.transformerInfo.getSecondVoltage() + "");

		/*temp = CacheManager.getFloat( CacheManager.PARAM_CONFIG,
				CacheManager.IMPEDANCE_VOLTAGE, 0);*/
		impedanceVoltage.setText(CacheManager.transformerInfo.getImpedanceVoltage() + "");

		/*int intTemp = CacheManager.getInt(
				CacheManager.PARAM_CONFIG,
				CacheManager.CAPACITY_TRANSFORM_TYPE, 0);*/
		int intTemp = CacheManager.transformerInfo.getCapacityTransformerType();
		if (intTemp <= capacityTypeAdapter.getCount()
				&& capacityTransformerType.getSelectedItemPosition() != intTemp - 1) {
			capacityTransformerType.setSelection(intTemp - 1, true);
		}

		/*intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG,
				CacheManager.TAP_GEAR, 0);*/
		intTemp = CacheManager.transformerInfo.getTapGear();
		if (intTemp <= tapGearAdapter.getCount()
				&& tapGear.getSelectedItemPosition() != intTemp - 1) {
			tapGear.setSelection(intTemp - 1, true);
		}

		/*intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG,
				CacheManager.CONNECTION_GROUP, 0);*/
		intTemp = CacheManager.transformerInfo.getConnectionGroup();
		if (intTemp <= connectionGroupAdapter.getCount()
				&& connectionGroup.getSelectedItemPosition() != intTemp  - 1) {
			connectionGroup.setSelection(intTemp - 1, true);
		}

		/*intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG,
				CacheManager.CURRENT_TEMPERATUER, 0);*/
		intTemp = CacheManager.transformerInfo.getCurrentTemperature();
		currentTemperature.setText(intTemp + "");
		//transformerId.setText(CacheManager.transformerInfo.getTransformerCode());

	}

	class FocusChange implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			DataModel data = new DataModel();
			if (hasFocus || isLeavePage)
				return;
			Log.i(TAG, Integer.toHexString(v.getId()));
			UnitView unit = (UnitView) v.getParent().getParent();
			float value = 0;
			String sendData = null;
			String strValue = "";
			
			switch (unit.getId()) {
			case R.id.firstVoltage_capacityMain:
				/*srcValue = CacheManager.getFloat(
						CacheManager.PARAM_CONFIG, CacheManager.FIRST_VOLTAGE,
						0);*/
				srcValue = CacheManager.transformerInfo.getFirstVoltage();
				if(!SocketClient.isConnectedServer ) {
					isUpdateCorrect = false;
					firstVoltage.setText(srcValue + "");
					break;	
				}		
				strValue = firstVoltage.getText().toString();
				if (strValue != null && strValue.trim().length() != 0) {
					value = Float.parseFloat(strValue);
				} else {
					firstVoltage.setText("0.0");
				}

				sendData = HexDump.getLowStartData((int) (value * 10));
				if (sendData == null)
					break;
				data.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.RATED_HIGH_VOLTAGE, sendData);
				
				subCode = DataModel.RATED_HIGH_VOLTAGE_INT;
				break;
			case R.id.secondVoltage_capacityMain:
				/*srcValue = CacheManager.getFloat(
						CacheManager.PARAM_CONFIG, CacheManager.SECOND_VOLTAGE,
						0);*/
				srcValue = CacheManager.transformerInfo.getSecondVoltage();
				if(!SocketClient.isConnectedServer ) {
					isUpdateCorrect = false;
					secondVoltage.setText(srcValue + "");
					break;	
				}		
				strValue = secondVoltage.getText().toString();
				if (strValue != null && strValue.trim().length() != 0) {
					value = Float.parseFloat(strValue);
				} else {
					secondVoltage.setText("0.0");
				}

				sendData = HexDump.getLowStartData((int) (value * 10));
				if (sendData == null)
					break;
				data.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.RATED_LOW_VOLTAGE, sendData);
				
				subCode = DataModel.RATED_LOW_VOLTAGE_INT;
				break;
			case R.id.impendanceVoltage_capacityMain:
				/*srcValue = CacheManager.getFloat(
						CacheManager.PARAM_CONFIG,
						CacheManager.IMPEDANCE_VOLTAGE, 0);*/
				srcValue = CacheManager.transformerInfo.getImpedanceVoltage();
				if(!SocketClient.isConnectedServer ) {
					isUpdateCorrect = false;
					
					impedanceVoltage.setText(srcValue + "");
					break;	
				}		
				strValue = impedanceVoltage.getText().toString();
				if (strValue != null && strValue.trim().length() != 0) {
					value = Float.parseFloat(strValue);
				} else {
					impedanceVoltage.setText("0.0");
				}
				strValue = impedanceVoltage.getText().toString();
				if (strValue != null && strValue.trim().length() != 0) {
					value = Float.parseFloat(strValue);
				}

				sendData = HexDump.getLowStartData((int) (value * 10));
				if (sendData == null)
					break;
				data.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.IMPENDANCE_VOLTAGE, sendData);
				
				subCode = DataModel.IMPENDANCE_VOLTAGE_INT;
				break;
			case R.id.currentTemperature_capacityMain:
				int intvalue = 0;
				/*srcValue = CacheManager.getInt(
						CacheManager.PARAM_CONFIG,
						CacheManager.CURRENT_TEMPERATUER, 0);*/
				srcValue = CacheManager.transformerInfo.getCurrentTemperature();
				if(!SocketClient.isConnectedServer ) {
					isUpdateCorrect = false;
					currentTemperature.setText(srcValue + "");
					break;	
				}		
				strValue = currentTemperature.getText().toString();
				if (strValue != null && strValue.trim().length() != 0) {
					intvalue = Integer.parseInt(strValue);
				} else {
					currentTemperature.setText("0");
				}
				
				sendData = HexDump.getLowStartData(intvalue);
				if (sendData == null)
					break;
				data.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.TEMPERATURE, sendData);
				
				subCode = DataModel.TEMPERATURE_INT;
				break;
			}
			
			if (data.getRequestCode() == null || data.getRequestCode().length == 0) return;
				
			//SocketClient.isTesting = true;
			if (SocketClient.write(data)) {
				Log.i(TAG, "参数设置" + HexDump.toHexString(data.getRequestCode()));
				//SocketClient.isTesting = false;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (getParam == null || (!getParam.isAlive() && !isLeavePage)) {
					CacheManager.params = null;
					GetParamThread.isReceiveParam = false;
					getParam = new GetParamThread();
					getParam.start();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(CacheManager.params == null || CacheManager.params.length < 1) {
						isUpdateCorrect = false;
						isParamNull = true;
						checkParam();
					}else {
						isParamNull = false;
						TranslateData.translateParam(CacheManager.params, getActivity());
						checkParam();
					}
				}
			}
		}

	}

	class ButtonClickListener implements OnClickListener {
		
		private void startTest() {
			if(!SocketClient.isConnectedServer) {
				ConfirmDialog confirm = new ConfirmDialog(getActivity());
				confirm.show();
				return;
			}
			saveBtn.setEnabled(false);
			getParamBtn.setEnabled(false);
			print.setEnabled(false);
			if (testThread == null || !testThread.isAlive()) {
				GetCapacityTest.isTest = true;
				GetCapacityTest.isSendStart = false;
				testThread = new GetCapacityTest();
				testThread.start();
			}
			if (GetCapacityTest.isTest) {
				beginTest.setEnabled(false);
				isParamEnable(false);
			} else {
				isParamEnable(true);
				beginTest.setEnabled(true);
			}
			Toast.makeText(getActivity(), "正在进行容量测试，请耐心等待", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onClick(View v) {
			focuschange.setFocusable(true);
			focuschange.setFocusableInTouchMode(true);
			focuschange.requestFocus();
			switch (v.getId()) {
			
			case R.id.saveDate_capacityMain:
				TransformerInfo transformer = new TransformerInfo();
				transformer = transformer.getCacheTransformer(getActivity());
				String dataStr = transformerId.getText().length() < 1 ? "0" : transformerId.getText().toString();
				int data = Integer.parseInt(dataStr);
				if(data > 10000) {
					Toast.makeText(getActivity(), "变压器编号不能大于10000", Toast.LENGTH_SHORT).show();
					return;
				}
				TransformerDBService transformerDB = new TransformerDBService(getActivity());
				List<TransformerInfo> transformers = transformerDB.searchTransformer(" transformerCode=?", new String[]{dataStr}, null, null, null);
				if(transformers == null || transformers.size() == 0) {
					CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);
					CacheManager.transformerInfo.setTransformerId(-1);
					transformer.setTransformerId(-1);
					
					//transformerDB.insertTransformer(transformer);
				}
			    else 
			    {
			    	TransformerInfo info = transformers.get(0);
			    	info.cacheTransformer(getActivity());
			    	CacheManager.transformerInfo.setTransformerId(info.getTransformerId());
			    	transformer.setTransformerId(info.getTransformerId());
			    }
				if (capacityInfo == null) { // 默认情况测试数据不允许修改，所以可以直接保存capacity对象，在模拟时手动输入，需要手动取值
					capacityInfo = new CapacityResultInfo();
				}
				capacityInfo = testData.getValue();
				capacityDB.insertCapacity(capacityInfo);
				//TransformerDBService db = new TransformerDBService(getActivity());
				//TransformerInfo info = new TransformerInfo();
				
				transformerDB.insertTransformer(CacheManager.transformerInfo);
				Toast.makeText(getActivity(), "容量信息保存成功", Toast.LENGTH_SHORT).show();

				break;
			case R.id.beginTest_capacityMain:
				
				startTest();
				CacheManager.currentTest = DataModel.CAPACITY_COMMAND_INT;
				break;
			case R.id.print_capacityMain:
				String printTime = HexDump.getPrintTime(new Date());
				DataModel param = new DataModel();
				param.setRequestCode(DataModel.CAPACITY_PRINT,
						printTime.substring(0, 2), printTime.substring(2));
				//SocketClient.isTesting = true;
				if(SocketClient.write(param)) {
					//SocketClient.isTesting = false;
				}
				
				break;
			case R.id.getParam_capacityMain: 
				if (SocketClient.isConnectedServer) {
					SocketClient.isTesting = true;
					GetParamThread.isReceiveParam = false;
					getParam = new GetParamThread();
					getParam.start();
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					TranslateData.translateParam(CacheManager.params, getActivity());
					SocketClient.isTesting = false;
					if(CacheManager.transformerInfo == null) CacheManager.transformerInfo = new TransformerInfo();
					setParam();
					Toast.makeText(getActivity(), "参数调取成功", Toast.LENGTH_LONG).show();
				}
				
					
			}
		}
	}

	private GetCapacityTest testThread;

	/**
	 * 禁用所有的参数设置控件
	 */
	private void isParamEnable(boolean isEnable) {
		firstVoltage.setEnabled(isEnable);
		secondVoltage.setEnabled(isEnable);
		capacityTransformerType.setEnabled(isEnable);
		impedanceVoltage.setEnabled(isEnable);
		currentTemperature.setEnabled(isEnable);
		tapGear.setEnabled(isEnable);
		connectionGroup.setEnabled(isEnable);
	}

	

	@Override
	public void initData() {
		beginTest.setEnabled(true);
		saveBtn.setEnabled(true);
		getParamBtn.setEnabled(true);
		print.setEnabled(true);
		if (testData != null)
			testData.initData();
		
		GetParamThread.isReceiveParam = true;
		GetCapacityTest.isTest = false;
		SocketClient.isTesting = false;
	}

	public static boolean isUpdateCorrect = false;
	/**
	 * 更新的原始值
	 */
	public static float srcValue = 0;

	public void checkParam(){
		String fieldName = "";
		TransformerDBService transformerDB = new TransformerDBService(getActivity());
		// TranslateData.translateParam(CacheManager.params, getActivity());
		switch (subCode) {
		case DataModel.RATED_HIGH_VOLTAGE_INT:
			fieldName = "一次电压";
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getFirstVoltage() != Float.parseFloat(firstVoltage.getText()
					.toString()))) {
				isUpdateCorrect = false;
				firstVoltage.setText(srcValue + "");
			} else {
				isUpdateCorrect = true;
				
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "firstVoltage", CacheManager.transformerInfo.getFirstVoltage());
			}
			
			break;
		case DataModel.RATED_LOW_VOLTAGE_INT:
			fieldName = "二次电压";
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getSecondVoltage() != Float.parseFloat(secondVoltage.getText()
					.toString()))) {
				isUpdateCorrect = false;
				secondVoltage.setText(srcValue + "");

			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "secondVoltage", CacheManager.transformerInfo.getSecondVoltage());
			}
			break;
		case DataModel.IMPENDANCE_VOLTAGE_INT:
			fieldName = "阻抗电压";
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getImpedanceVoltage() != Float.parseFloat(impedanceVoltage.getText()
					.toString()))) {
				isUpdateCorrect = false;
				impedanceVoltage.setText(srcValue + "");
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "impedanceVoltage", CacheManager.transformerInfo.getImpedanceVoltage());
			}
			
			break;
		case DataModel.TEMPERATURE_INT:
			fieldName = "当前温度";
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getCurrentTemperature() != Integer.parseInt(currentTemperature.getText()
					.toString()))) {
				isUpdateCorrect = false;
				currentTemperature.setText(((int) srcValue) + "");

			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "currentTemperature", CacheManager.transformerInfo.getCurrentTemperature());
			}
			
			break;
		case DataModel.CONNECTION_GROUP_INT:
			fieldName = "联结组别";
			
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getConnectionGroup() -1 != connectionGroup
					.getSelectedItemPosition())) {
				isUpdateCorrect = false;
				connectionGroup.setSelection((int) srcValue -1);
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "connectionGroup", CacheManager.transformerInfo.getConnectionGroup());
			}
			//paramSet(DataModel.CONNECTION_GROUP_INT);
			break;
		case DataModel.TAP_GEAR_INT:
			fieldName = "分接档位";
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getTapGear() - 1 != tapGear
					.getSelectedItemPosition())) {
				isUpdateCorrect = false;
				Log.i(TAG, "难道已经执行到这里了？");
				tapGear.setSelection((int) srcValue -1);
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "tapGear", CacheManager.transformerInfo.getTapGear());
			}
			
			break;
		case DataModel.CAPACITY_TRANSFORMER_TYPE_INT:
			fieldName = "变压器类型";
			if (CacheManager.params != null && ( isParamNull || CacheManager.transformerInfo.getCapacityTransformerType() - 1 != capacityTransformerType
					.getSelectedItemPosition())) {
				/*if(isParamNull)	Toast.makeText( fieldName + "参数为空",
						Toast.LENGTH_SHORT).show();
				
				int aa = CacheManager.getInt(
						CacheManager.PARAM_CONFIG,
						CacheManager.CAPACITY_TRANSFORM_TYPE, 0) - 1;
				Toast.makeText(getActivity(), aa + "测试",
						Toast.LENGTH_SHORT).show();
				
				int bb = capacityTransformerType
						.getSelectedItemPosition();
				Toast.makeText(getActivity(), bb + "测试",
						Toast.LENGTH_SHORT).show();*/
				
				
				isUpdateCorrect = false;
				capacityTransformerType.setSelection((int) srcValue -1);
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "capacityTransformerType", CacheManager.transformerInfo.getCapacityTransformerType());
			}
			
			break;
		case DataModel.TRANSFORMER_CODE_INT:
			fieldName = "变压器编号";
			String temp = CacheManager.transformerInfo.getTransformerCode();
			int tempInt = temp.length() < 1 ? 0 : Integer.parseInt(temp);
			if (CacheManager.params != null && (isParamNull || tempInt != Integer.parseInt(transformerId.getText().toString()))) {
				isUpdateCorrect = false;
				transformerId.setText((int)srcValue -1);
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "transformerCode", CacheManager.transformerInfo.getTransformerCode());
			}
				
			break;
			}
		if (!isUpdateCorrect) {
			Toast.makeText(getActivity(), fieldName + "数据修改失败",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), fieldName + "数据修改成功",
					Toast.LENGTH_SHORT).show();
		}
	}
	


}
