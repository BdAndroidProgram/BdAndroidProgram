package com.lxdz.capacity.fragment;

import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
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
import com.lxdz.capacity.dbService.LoadDBService;
import com.lxdz.capacity.dbService.TransformerDBService;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.GetLoadTest;
import com.lxdz.capacity.model.GetParamThread;
import com.lxdz.capacity.model.LoadResultInfo;
import com.lxdz.capacity.model.TransformerInfo;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.socket.TranslateData;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;
import com.lxdz.capacity.widget.ConfirmDialog;
import com.lxdz.capacity.widget.UnitView;

/**
 * 负载测试
 * 
 * @author Administrator
 * 
 */
public class LoadTestAllFragment extends BaseFragment {
	// 联结组别
	private Spinner connectionGroup;
	private ArrayAdapter<CharSequence> connectionGroupAdapter;
	private TextView focuschange;

	// 测试方法
	private Spinner testMethod;
	private ArrayAdapter<CharSequence> testMethodAdapter;

	private View containerFather;

	private LoadResultInfo loadInfo;

	private LoadDBService loadDB;

	private EditText ratedCapacity;
	private EditText firstVoltage;
	private EditText currentTemperature;

	private LoadResultPublicFragment loadTestPublic;

	private static int endterCounter = 0;

	private GetParamThread getParam;

	private Button startTest;
	private Button saveBtn;
	private Button printBtn;
	
	private Button getParamBtn;

	private GetLoadTest loadTest;
	private static boolean isCanTest = true;
	private static int subCode = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (containerFather == null) {
			containerFather = inflater.inflate(R.layout.load_test_all,
					container, false);
			if(CacheManager.transformerInfo == null) CacheManager.transformerInfo = new TransformerInfo();
			init();
			initFragment();
			endterCounter = 0;
			loadDB = new LoadDBService(getActivity());
		}
		ViewGroup parent = (ViewGroup) containerFather.getParent();
		if (parent != null) {
			parent.removeView(containerFather);
		}

		return containerFather;
	}

	public void initFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		loadTestPublic = (LoadResultPublicFragment) fragmentManager
				.findFragmentById(R.id.loadTest_loadAll);
	}

	// wifi是否连接过滤器
	IntentFilter wifiConnected = new IntentFilter(
			ConnectivityManager.CONNECTIVITY_ACTION);
	public BroadcastReceiver wifiConnect = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!SocketClient.isWifiCorrect)
				return;
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
				TranslateData
						.translateParam(CacheManager.params, getActivity());
				setParam(true);
			}
		}
	};

	private void init() {
		
		focuschange =(TextView)containerFather.findViewById(R.id.focuschange);

		// 联结组别
		connectionGroup = (Spinner) containerFather
				.findViewById(R.id.connectionGroup_loadAll);
		connectionGroupAdapter = ArrayAdapter.createFromResource(
				this.getActivity(), R.array.connectionGroupArray,
				R.layout.adapter_item);
		connectionGroup.setAdapter(connectionGroupAdapter);
		connectionGroup.setSelection(
				CacheManager.transformerInfo.getConnectionGroup() - 1, true);
		connectionGroup.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				DataModel data = new DataModel();
				srcValue = CacheManager.transformerInfo.getConnectionGroup();
				if (!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					connectionGroup.setSelection((int)srcValue);
					return;
				}
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET,
						DataModel.CONNECTION_GROUP,
						Integer.toString(position + 1, 16)
								+ DataModel.NO_COMMAND);
				subCode = DataModel.CONNECTION_GROUP_INT;
				sendParam(data);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// 测试方法
		testMethod = (Spinner) containerFather
				.findViewById(R.id.testMethod_loadAll);
		testMethodAdapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.testMethodArrayLoad, R.layout.adapter_item);
		testMethod.setAdapter(testMethodAdapter);
		testMethod.setSelection(0, true); // 默认为三相测试
		testMethod.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 2)
					return;
				loadTestPublic.setResultLabel(position, 0);
				srcValue = CacheManager
						.getInt( CacheManager.PARAM_CONFIG,
								CacheManager.TEST_METHOD, -1);

				if (position == 0 || position == 1) {
					TextView text = (TextView) view;
					startTest.setText(text.getText().toString());
				}
				if (!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					testMethod.setSelection((int)srcValue);
					return;
				}
				DataModel data = new DataModel();
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET,
						DataModel.TEST_METHOD,
						Integer.toString(position + 2, 16)
								+ DataModel.NO_COMMAND);
				subCode = DataModel.TEST_METHOD_INT;

				sendParam(data);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		UnitView unit = (UnitView) containerFather
				.findViewById(R.id.ratedCapacity_loadAll);
		ratedCapacity = unit.getDataText();
		ratedCapacity.setOnFocusChangeListener(new FocusChange());
		unit = (UnitView) (UnitView) containerFather
				.findViewById(R.id.firstVoltage_loadAll);
		firstVoltage = unit.getDataText();
		firstVoltage.setOnFocusChangeListener(new FocusChange());

		unit = (UnitView) (UnitView) containerFather
				.findViewById(R.id.currentTemperature_loadAll);
		currentTemperature = unit.getDataText();
		currentTemperature.setOnFocusChangeListener(new FocusChange());

		startTest = (Button) containerFather
				.findViewById(R.id.beginTest_loadAll);
		startTest.setOnClickListener(new ButtonListener());

		saveBtn = (Button) containerFather.findViewById(R.id.save_loadAll);
		saveBtn.setOnClickListener(new ButtonListener());
		
		getParamBtn = (Button) containerFather.findViewById(R.id.getParam_loadAll);
		getParamBtn.setOnClickListener(new ButtonListener());
		
		printBtn = (Button)containerFather.findViewById(R.id.print_loadAll);
		printBtn.setOnClickListener(new ButtonListener());

	}

	class ButtonListener implements View.OnClickListener {		
		@Override
		public void onClick(View v) {
			focuschange.setFocusable(true);
			focuschange.setFocusableInTouchMode(true);
			focuschange.requestFocus();
			switch (v.getId()) {
			case R.id.beginTest_loadAll:
				if (!SocketClient.isConnectedServer) {
					ConfirmDialog confirm = new ConfirmDialog(getActivity());
					confirm.show();
					break;
				}

				if (loadTest == null || !loadTest.isAlive()) {
					loadTest.loadResult = true;
					loadTest = new GetLoadTest();
					loadTest.start();
				}
				isCanTest = false;
				startTest.setEnabled(isCanTest);
				break;
			case R.id.save_loadAll:
				if (loadInfo != null) {
					loadInfo.setTransformerId(CacheManager.transformerInfo.getTransformerId());
					loadDB.insertLoad(loadInfo);
					
					
				} else {
					LoadResultInfo load = new LoadResultInfo();
					load.setTransformerId(CacheManager.transformerInfo.getTransformerId());
					load = loadTestPublic.getValue();
					loadDB.insertLoad(load);
				}
				String code=CacheManager.transformerInfo.getTransformerCode();
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
				TransformerInfo transformer = new TransformerInfo();
				transformer = transformer.getCacheTransformer(getActivity());
				TransformerDBService transformerDB = new TransformerDBService(getActivity());
				List<TransformerInfo> transformers = transformerDB.searchTransformer(" transformerCode=?", new String[]{code}, null, null, null);
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
				transformerDB.insertTransformer(CacheManager.transformerInfo);
				/*******20160718注释******************************************
				TransformerDBService db = new TransformerDBService(getActivity());
				TransformerInfo info = new TransformerInfo();
				db.insertTransformer(CacheManager.transformerInfo);
				****************************************************/
				Toast.makeText(getActivity(), "负载测试数据保存成功", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.print_loadAll:
				String printTime = HexDump.getPrintTime(new Date());
				DataModel model = new DataModel();
				model.setRequestCode(DataModel.NOLOAD_PRINT, printTime.substring(0, 2), printTime.substring(2));
				//SocketClient.isTesting = true;
				SocketClient.write(model);
				break;
			case R.id.getParam_loadAll:
				if (SocketClient.isConnectedServer) {
					SocketClient.isTesting = true;
					GetParamThread.isReceiveParam = false;
					getParam = new GetParamThread();
					subCode = -1;
					getParam.start();
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					TranslateData.translateParam(CacheManager.params, getActivity());
					SocketClient.isTesting = false;
					if(CacheManager.transformerInfo == null) CacheManager.transformerInfo = new TransformerInfo();
					setParam(true);
					Toast.makeText(getActivity(), "参数调取成功", Toast.LENGTH_LONG).show();
				}
			}
		}

	}

	class FocusChange implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus)
				return;
			DataModel data = new DataModel();
			float value = 0;
			UnitView unit = (UnitView) v.getParent().getParent();

			String strValue = "";
			String sendData = null;
			switch (unit.getId()) {
			case R.id.firstVoltage_loadAll:
				srcValue = CacheManager.transformerInfo.getFirstVoltage();
				if (!SocketClient.isConnectedServer) {
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
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET,
						DataModel.RATED_HIGH_VOLTAGE, sendData);
				subCode = DataModel.RATED_HIGH_VOLTAGE_INT;
				break;
			case R.id.ratedCapacity_loadAll:
				int intValue = 0;
				srcValue = CacheManager.transformerInfo.getRatedCapacity();
				if (!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					ratedCapacity.setText(srcValue + "");
					break;
				}
				strValue = ratedCapacity.getText().toString();
				if (strValue != null && strValue.trim().length() != 0) {
					intValue = Integer.parseInt(strValue);
				} else {
					ratedCapacity.setText("0");
				}
				sendData = HexDump.getLowStartData(intValue);
				if (sendData == null)
					break;
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET,
						DataModel.RATED_CAPACITY, sendData);
				subCode = DataModel.RATED_CAPACITY_INT;
				break;

			case R.id.currentTemperature_loadAll:
				int tempValue = 0;
				srcValue = CacheManager.transformerInfo.getCurrentTemperature();
				if (!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					currentTemperature.setText(srcValue + "");
					break;
				}
				strValue = currentTemperature.getText().toString();
				if (strValue != null && strValue.trim().length() != 0) {
					tempValue = Integer.parseInt(strValue);
				} else {
					currentTemperature.setText("0");
				}
				sendData = HexDump.getLowStartData(tempValue);
				if (sendData == null)
					break;
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET,
						DataModel.TEMPERATURE, sendData);
				subCode = DataModel.TEMPERATURE_INT;
				break;
			}
			if (data.getRequestCode() == null
					|| data.getRequestCode().length == 0)
				return;
			sendParam(data);

		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra(SocketClient.COMMAND_TYPE, 0) != DataModel.LOAD_COMMAND_INT)
				return;
			byte[] receiveData = intent
					.getByteArrayExtra(SocketClient.NEW_DATA);
			
			loadInfo = TranslateData.translateLoad(receiveData);
			loadTestPublic.setValue(loadInfo);
		}
	};

	IntentFilter filter = new IntentFilter(SocketClient.ON_NEW_DATA);

	@Override
	public void onResume() {
		super.onResume();
		this.getActivity().registerReceiver(wifiConnect, wifiConnected);

		this.getActivity().registerReceiver(receiver, filter);
		if (SocketClient.isConnectedServer) {
			SocketClient.isTesting = true;
			GetParamThread.isReceiveParam = false;
			getParam = new GetParamThread();
			subCode = -1;
			getParam.start();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TranslateData.translateParam(CacheManager.params, getActivity());
			SocketClient.isTesting = false;
		}
		setParam(true);
		if (startTest != null) {
			isCanTest = true;
			startTest.setEnabled(isCanTest);
		}
		SocketClient.currentPage = this;

		if (++endterCounter > 1)
			loadTestPublic.initData();
		if (IndexActivity.openPageProgress != null)
			IndexActivity.openPageProgress.dismiss();
	}

	private void setParam(boolean isFromResume) {
		float temp = CacheManager.transformerInfo.getFirstVoltage();
		firstVoltage.setText(temp + "");

		int intTemp = CacheManager.getInt(
				CacheManager.PARAM_CONFIG, CacheManager.CURRENT_TEMPERATUER, 0);
		currentTemperature.setText(CacheManager.transformerInfo.getCurrentTemperature() + "");
		intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG,
				CacheManager.RATED_CAPACITY, 0);
		ratedCapacity.setText(CacheManager.transformerInfo.getRatedCapacity() + "");


		intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG,
				CacheManager.CONNECTION_GROUP, 0);
		intTemp = CacheManager.transformerInfo.getConnectionGroup();
		if (intTemp < connectionGroupAdapter.getCount()
				&& connectionGroup.getSelectedItemPosition() != intTemp - 1) {
			connectionGroup.setSelection(intTemp - 1, true);
		}
		intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG,
				CacheManager.TEST_METHOD, 0) - 2;
		intTemp = CacheManager.transformerInfo.getTestMethod() - 2;
		if (intTemp != testMethod.getSelectedItemPosition()
				&& intTemp <= testMethodAdapter.getCount()) {

			testMethod.setSelection(intTemp);
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		GetParamThread.isReceiveParam = true;
		SocketClient.isTesting = false;
		GetLoadTest.loadResult = false;
		this.getActivity().unregisterReceiver(wifiConnect);

		this.getActivity().unregisterReceiver(receiver);
		SocketClient.currentPage = null;
		
	}
	private boolean isParamNull = false;
	private void sendParam(DataModel data) {
		isCanTest = true;
		startTest.setEnabled(isCanTest);
		loadTest.loadResult = false;
		SocketClient.isTesting = false;
		//SocketClient.isTesting = true;
		if (SocketClient.write(data)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (++endterCounter > 1)
				loadTestPublic.initData();
			if (getParam == null || !getParam.isAlive()) {
				GetParamThread.isReceiveParam = false;
				getParam = new GetParamThread();
				CacheManager.params = null;
				getParam.start();
				CacheManager.params = null;
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (CacheManager.params == null || CacheManager.params.length < 1) {
					isUpdateCorrect = false;
					isParamNull = true;
					checkParam();
				} else {
					TranslateData.translateParam(CacheManager.params,
							getActivity());
					isParamNull = false;
					checkParam();
				}
			}
		}
	}

	@Override
	public void initData() {
		startTest.setEnabled(true);
		if (loadTestPublic != null)
			loadTestPublic.initData();
		GetParamThread.isReceiveParam = true;
		SocketClient.isTesting = false;
		GetLoadTest.loadResult = false;
	}

	public static final int FIRST_VOLTAGE = 0x0b;
	public static final int TEMPRATURE = 0x10;
	public static final int CONNECTION_GROUP = 0x0d;
	public static final int RATED_CAPACITY = 0x0a;
	public static final int TEST_METHOD = 0x12;

	public static boolean isUpdateCorrect = false;
	/**
	 * 更新的原始值
	 */
	public static float srcValue = 0;

	private void checkParam() {
		TranslateData.translateParam(CacheManager.params, getActivity());
		TransformerDBService transformerDB = new TransformerDBService(getActivity());
		Message msg = Message.obtain();
		String fieldName = "";
		switch (subCode) {
		case FIRST_VOLTAGE:
			fieldName = "一次电压";
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getFirstVoltage() != Float
					.parseFloat(firstVoltage.getText().toString()))) {
				isUpdateCorrect = false;
				firstVoltage.setText(srcValue + "");
			} else {
				isUpdateCorrect = true;
				
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "firstVoltage", CacheManager.transformerInfo.getFirstVoltage());
			}
			break;
		case TEMPRATURE:
			fieldName = "当前温度";
				
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getCurrentTemperature() != Integer
					.parseInt(currentTemperature.getText().toString()))) {
				isUpdateCorrect = false;
				currentTemperature.setText(((int) srcValue) + "");
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "currentTemperature", CacheManager.transformerInfo.getCurrentTemperature());
			}
			break;
		case RATED_CAPACITY:
			fieldName = "额定容量";
				
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getRatedCapacity() != Integer
					.parseInt(ratedCapacity.getText().toString()))) {
				isUpdateCorrect = false;
				ratedCapacity.setText(((int) srcValue) + "");
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "ratedCapacity", CacheManager.transformerInfo.getRatedCapacity());
			}
			break;
		case CONNECTION_GROUP:
			fieldName = "联结组别";
				
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getConnectionGroup() - 1 != connectionGroup
					.getSelectedItemPosition())) {
				isUpdateCorrect = false;
				connectionGroup.setSelection((int) srcValue - 1, true);
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "connectionGroup", CacheManager.transformerInfo.getConnectionGroup());
			}
			break;
		case TEST_METHOD:
			fieldName = "测试方法";
				
			if (CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getTestMethod() - 2 != testMethod
					.getSelectedItemPosition())) {
				isUpdateCorrect = false;
				testMethod.setSelection((int) srcValue - 2, true);
			} else {
				isUpdateCorrect = true;
				transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "testMethod", CacheManager.transformerInfo.getTestMethod());
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
