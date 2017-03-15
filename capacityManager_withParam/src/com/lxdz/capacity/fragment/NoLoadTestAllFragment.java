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
import com.lxdz.capacity.dbService.NoloadDBService;
import com.lxdz.capacity.dbService.TransformerDBService;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.GetNoloadTest;
import com.lxdz.capacity.model.GetParamThread;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.TransformerInfo;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.socket.TranslateData;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;
import com.lxdz.capacity.widget.ConfirmDialog;
import com.lxdz.capacity.widget.UnitView;

/**
 * 负载测试
 * 单相测试说明：单相测试中，因为考虑到线的切换，所以需要进行三次测试
 * 第一次的时候，只显示A相，第二次在A相的基础上，增加了B相，然后第三次在前两次的基础上增加了C相和公共数据，在取值的时候，每次值都放
 * 在了A相数据上，所以无论第几次取值都从A相取值，为了公共数据解析不变，所在此类中会有数据转换
 * @author Administrator
 *
 */
public class NoLoadTestAllFragment extends BaseFragment{
	
	private static final String TAG = NoLoadTestAllFragment.class.getSimpleName();
	
	private NoloadDBService noloadDB;
	private TextView focuschange;

	//额定电压（高电压）
	private EditText firstVoltage;
	private EditText secondVoltage;
	
	private Spinner connectionGroup;
	private ArrayAdapter<CharSequence> connectionGroupAdapter;
	
	// 测试方法
	private Spinner testMethod;
	private ArrayAdapter<CharSequence> testMethodAdapter;
	
	private NoloadResultInfo noloadInfo;
	
	private EditText ratedCapacity;
	// private EditText correctionCofficient;
	
	private Button getParamBtn;
	
	private Button startTest;
	private Button saveBtn;
	private Button endTest;
	private Button print;
	private Button testBtn;
	
	private View containerFather;
	GetNoloadTest noloadTest;
	
	private NoloadResultPublicFragment noloadReturn;
	
	private GetParamThread getParam;
	
	private  boolean isCanTest = true;
	
	/**
	 * 单相测试时的数据统计
	 */
	private static int testCount = 0;
	
	private static int endterCounter = 0;
	
	private static int subCode = -1;
	private boolean isParamNull = false;
	
	//容量变压器类型
		private Spinner capacityTransformerType;
		private ArrayAdapter<CharSequence> capacityTypeAdapter;

		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(containerFather == null){
			containerFather = inflater.inflate(R.layout.noload_test_all, container, false);
			if(CacheManager.transformerInfo == null) CacheManager.transformerInfo = new TransformerInfo();
			init();
			initFragment();
			endterCounter = 0;
			noloadDB = new NoloadDBService(getActivity());
		} 
		ViewGroup parent = (ViewGroup) containerFather.getParent();  
        if (parent != null) {  
              parent.removeView(containerFather);  
        }   
		
		return containerFather;
	}
	
	public void initFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		noloadReturn = (NoloadResultPublicFragment)fragmentManager.findFragmentById(R.id.noloadTest_noloadAll);
	}
	
	// wifi是否连接过滤器
	IntentFilter wifiConnected = new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION);
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
				setParam(true);
				
			}
			
		}
	};
	
	private void init() {
		focuschange =(TextView)containerFather.findViewById(R.id.focuschange);
		
		// 联结组别
		connectionGroup = (Spinner)containerFather.findViewById(R.id.connectionGroup_noloadAll);
		connectionGroupAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.connectionGroupArray, R.layout.adapter_item);
		connectionGroup.setAdapter(connectionGroupAdapter);
		
		connectionGroup.setSelection(CacheManager.transformerInfo.getConnectionGroup() - 1, true);
		connectionGroup.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				srcValue = CacheManager.transformerInfo.getTestMethod();
				
				setStartTestBtn();
				if(!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					connectionGroup.setSelection((int)srcValue);
					return;
				}
				DataModel data = new DataModel();
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET, DataModel.CONNECTION_GROUP, Integer.toString(position + 1, 16) + DataModel.NO_COMMAND);
				subCode = DataModel.CONNECTION_GROUP_INT;
				sendParam(data);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		// 测试方法
		testMethod = (Spinner)containerFather.findViewById(R.id.testMethod_noloadAll);
		testMethodAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.testMethodArray, R.layout.adapter_item);
		testMethod.setAdapter(testMethodAdapter);
		testMethod.setSelection(1, true);
		testMethod.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				srcValue = CacheManager.transformerInfo.getTestMethod();
				
				if(position == 3) return;
				noloadReturn.setResultLabel(position,0);
				testCount = 0;
				setStartTestBtn();
				if(position == 1 || position == 2) {
					endTest.setVisibility(View.GONE);
				}else {
					endTest.setVisibility(View.VISIBLE);
					endTest.setEnabled(false);
				}
				TextView text = (TextView)view;
				startTest.setText(text.getText().toString());
				
				
				if(!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					testMethod.setSelection((int)srcValue);
					return;
				}
				DataModel data = new DataModel();
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET, DataModel.TEST_METHOD, Integer.toString(position + 1, 16) + DataModel.NO_COMMAND);
				subCode = DataModel.TEST_METHOD_INT;
				
				sendParam(data);
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		// 容量变压器类型
		capacityTypeAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.noloadTransformerTypeArray, R.layout.adapter_item);
		capacityTransformerType = (Spinner) containerFather.findViewById(R.id.transformerType_noloadAll);
		capacityTransformerType.setAdapter(capacityTypeAdapter);
		capacityTransformerType.setSelection(CacheManager.transformerInfo.getNoloadTransformerType() - 1, true);
		capacityTransformerType.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				srcValue = CacheManager.transformerInfo.getNoloadTransformerType();
				setStartTestBtn();
				if(!SocketClient.isConnectedServer) {
					isUpdateCorrect = false;
					capacityTransformerType.setSelection((int)srcValue);
					return;
				}
				DataModel data = new DataModel();
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET, DataModel.CAPACITY_TRANSFORMER_TYPE_NOLOAD, Integer.toString(position + 1, 16) + DataModel.NO_COMMAND);
				
				subCode = DataModel.CAPACITY_TRANSFORMER_TYPE_INT_NOLOAD;
				sendParam(data);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		UnitView unit = (UnitView)containerFather.findViewById(R.id.ratedCapacity_noloadAll);
		ratedCapacity = unit.getDataText();
		ratedCapacity.setText("0");
		ratedCapacity.setOnFocusChangeListener(new FocusChange());
		unit = (UnitView)containerFather.findViewById(R.id.firstVoltage_noloadAll);
		firstVoltage = unit.getDataText();
		firstVoltage.setText("0.0");
		firstVoltage.setOnFocusChangeListener(new FocusChange());
		unit = (UnitView)containerFather.findViewById(R.id.secondVoltage_noloadAll);
		secondVoltage = unit.getDataText();
		secondVoltage.setOnFocusChangeListener(new FocusChange());
		secondVoltage.setText("0.0");
		
		startTest = (Button) containerFather.findViewById(R.id.startTest_noloadAll);
		startTest.setOnClickListener(new ButtonListener());
		saveBtn = (Button) containerFather.findViewById(R.id.save_noloadAll);
		saveBtn.setOnClickListener(new ButtonListener());
		
		testBtn = (Button) containerFather.findViewById(R.id.test_noloadAll);
		testBtn.setOnClickListener(new ButtonListener());
		
		getParamBtn = (Button) containerFather.findViewById(R.id.getParam_noloadAll);
		getParamBtn.setOnClickListener(new ButtonListener());
		endTest = (Button)containerFather.findViewById(R.id.endTest_noloadAll);
		endTest.setOnClickListener(new ButtonListener());
		print = (Button)containerFather.findViewById(R.id.print_noloadAll);
		print.setOnClickListener(new ButtonListener());
	}
	
	private void sendParam(DataModel data){
		//SocketClient.isTesting = true;
		if(SocketClient.write(data)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(++endterCounter > 1) noloadReturn.initData();
			if(getParam == null || !getParam.isAlive() ) {
				
				CacheManager.params = null;
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
	
	@Override
	public void onPause() {
		super.onPause();
		isCanTest = true;
		GetNoloadTest.loadResult = false;
		GetParamThread.isReceiveParam = true;
		SocketClient.isTesting = false;
		DataModel model = new DataModel();
		model.setRequestCode(DataModel.NOLOAD_COMMAND, DataModel.SINGLE_PHASE_TEST_STOP, DataModel.NO_DATA);
		
		SocketClient.write(model);
		this.getActivity().unregisterReceiver(wifiConnect);
		this.getActivity().unregisterReceiver(receiver);
		SocketClient.currentPage = null;
	}
	
	class FocusChange implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			setStartTestBtn();
			DataModel data = new DataModel();
			if(hasFocus) return;
			UnitView unit = (UnitView)v.getParent().getParent();
			float value = 0;
			String sendData = null;
			String strValue = "";
			
			switch(unit.getId()){
			case R.id.ratedCapacity_noloadAll:
				srcValue = CacheManager.transformerInfo.getRatedCapacity();
				if(!SocketClient.isConnectedServer ) {
					isUpdateCorrect = false;
					ratedCapacity.setText(srcValue + "");
					break;	
				}			
				strValue = ratedCapacity.getText().toString();
				int intValue = 0;
				if(strValue != null && strValue.trim().length() != 0) {
					intValue = Integer.parseInt(strValue) ;
				}else {
					ratedCapacity.setText("0");
				}
				sendData = HexDump.getLowStartData(intValue);
				if(sendData == null) break;
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET, DataModel.RATED_CAPACITY, sendData);
				subCode = DataModel.RATED_CAPACITY_INT;
				break;				
			case R.id.firstVoltage_noloadAll:
				srcValue = CacheManager.transformerInfo.getFirstVoltage();
				if(!SocketClient.isConnectedServer ) {
					isUpdateCorrect = false;
					firstVoltage.setText(srcValue + "");
					break;
				}
				strValue = firstVoltage.getText().toString();
				if(strValue != null && strValue.trim().length() != 0) {
					value = Float.parseFloat(strValue) ;
				}else {
					firstVoltage.setText("0.0");
				}
				
				sendData = HexDump.getLowStartData((int)(value * 10));
				if(sendData == null) break;
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET, DataModel.RATED_HIGH_VOLTAGE, sendData);
				subCode = DataModel.RATED_HIGH_VOLTAGE_INT;
				break;
			case R.id.secondVoltage_noloadAll:
				srcValue = CacheManager.transformerInfo.getSecondVoltage();
				if(!SocketClient.isConnectedServer ) {
					isUpdateCorrect = false;
					secondVoltage.setText(srcValue + "");
					break;
				}
				strValue = secondVoltage.getText().toString();
				if(strValue != null && strValue.trim().length() != 0) {
					value = Float.parseFloat(strValue) ;
				}else {
					secondVoltage.setText("0.0");
				}
				
				sendData = HexDump.getLowStartData((int)(value * 10));
				if(sendData == null) break;
				data.setRequestCode(DataModel.NOLOADE_PARAM_SET, DataModel.RATED_LOW_VOLTAGE, sendData);
				subCode = DataModel.RATED_LOW_VOLTAGE_INT;
				break;
			
			
			}
			if(data.getRequestCode() == null || data.getRequestCode().length == 0) return;
			sendParam(data);
			
			if(++endterCounter > 1) noloadReturn.initData();
		}
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getIntExtra(SocketClient.COMMAND_TYPE, 0) != DataModel.NOLOAD_COMMAND_INT) return;
			byte[] receiveData = intent.getByteArrayExtra(SocketClient.NEW_DATA);
			noloadInfo = TranslateData.translateNoload(receiveData);
			int testMethodSelected = CacheManager.transformerInfo.getTestMethod() - 1;
			if(testMethodSelected != 0) {
				noloadReturn.setValue(noloadInfo, 0);
			}else {
				// 见类的注释
				switch(testCount) {
				case 2:
					noloadInfo.setUb(noloadInfo.getUa());
					noloadInfo.setIb(noloadInfo.getIa());
					noloadInfo.setPb(noloadInfo.getPa());
					break;
				case 3:
					noloadInfo.setUc(noloadInfo.getUa());
					noloadInfo.setIc(noloadInfo.getIa());
					noloadInfo.setPc(noloadInfo.getPa());
				}
				noloadReturn.setValue(noloadInfo, testCount);
			}
			
		}
	};
	IntentFilter filter = new IntentFilter(SocketClient.ON_NEW_DATA);
	
	@Override
	public void onResume() {
		super.onResume();
		isCanTest = true;
		testCount = 0;
		startTest.setEnabled(isCanTest);
		subCode = -1;
		
		if(SocketClient.isConnectedServer) {
			SocketClient.isTesting = true;
			GetParamThread.isReceiveParam = true;
			subCode = -1;
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
			setParam(true);
		
			this.getActivity().registerReceiver(wifiConnect, wifiConnected);
		this.getActivity().registerReceiver(receiver, filter);
		if(++endterCounter > 1) noloadReturn.initData();
		SocketClient.currentPage = this;
		if(IndexActivity.openPageProgress != null) {
			IndexActivity.openPageProgress.dismiss();
		}
	}
	
	int currentTestMethod = -1;
	
	class ButtonListener implements View.OnClickListener{
		private void startTest() {
			if(!SocketClient.isConnectedServer) {
				ConfirmDialog confirm = new ConfirmDialog(getActivity());
				confirm.show();
				return;
			}else {
				
					SocketClient.isTesting = true;
					GetParamThread.isReceiveParam = true;
					subCode = -1;
					getParam = new GetParamThread();
					getParam.start();
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					TranslateData.translateParam(CacheManager.params, getActivity());
					SocketClient.isTesting = false;
					setParam(true);
			}
			if(currentTestMethod == 0) {
				noloadReturn.setResultLabel(currentTestMethod, testCount);
				isCanTest = false;
				startTest.setEnabled(isCanTest);
				endTest.setEnabled(!isCanTest);
				if(testCount < 2){
					endTest.setText("暂停测试");
				}else {
					endTest.setText("停止测试");
				}
				testCount++;
			}
			if(noloadTest == null || !noloadTest.isAlive()) {
				GetNoloadTest.loadResult = true;
				noloadTest = new GetNoloadTest();
				noloadTest.start();
				isCanTest = false;
				startTest.setEnabled(isCanTest);
			}
		}
		
		@Override
		public void onClick(View v) {
			focuschange.setFocusable(true);
			focuschange.setFocusableInTouchMode(true);
			focuschange.requestFocus();
			currentTestMethod = CacheManager.transformerInfo.getTestMethod() - 1;
			switch(v.getId()){
			case R.id.startTest_noloadAll:
				if(CacheManager.currentTest == -1 || CacheManager.currentTest != DataModel.NOLOAD_COMMAND_INT) {
				/*	String message = "请确定外部三相电源是否断开？";
					AlertDialog.Builder builder = new Builder(getActivity());
					
					builder.setMessage(message);
					builder.setTitle("提示");

					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							startTest();
							CacheManager.currentTest = DataModel.NOLOAD_COMMAND_INT;
						}
					});

					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.cancel();
						}
					});
					AlertDialog dialog = builder.create();
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();*/
					
					startTest();
					CacheManager.currentTest = DataModel.NOLOAD_COMMAND_INT;
				}else {
					// 单相测试的，开始测试
					startTest();
					CacheManager.currentTest = DataModel.NOLOAD_COMMAND_INT;
				}
				
				
				break;
			case R.id.save_noloadAll:
				if(noloadInfo != null) {
					
					noloadInfo.setTransformerId(CacheManager.transformerInfo.getTransformerId());
					noloadDB.insertNoload(noloadInfo);
					// Toast.makeText(getActivity(), "空载测试数据保存成功", Toast.LENGTH_SHORT).show();
					
				}else {
					NoloadResultInfo noload = new NoloadResultInfo();
					noload.setTransformerId(CacheManager.transformerInfo.getTransformerId());
					noload = noloadReturn.getValue();
					noloadDB.insertNoload(noloadInfo);
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
				/********20160716***********************************
				TransformerDBService db = new TransformerDBService(getActivity());
				TransformerInfo info = new TransformerInfo();
				db.insertTransformer(CacheManager.transformerInfo);
				*******************************************************/
				Toast.makeText(getActivity(), "空载测试数据保存成功", Toast.LENGTH_SHORT).show();
				break;
			case R.id.endTest_noloadAll:
				if(currentTestMethod == 0) {
					GetNoloadTest.loadResult = false;
					SocketClient.isTesting = false;
					startTest.setEnabled(true);
					endTest.setEnabled(false);
					if(testCount > 2) testCount = 0;
					DataModel model = new DataModel();
					model.setRequestCode(DataModel.NOLOAD_COMMAND, DataModel.SINGLE_PHASE_TEST_PAUSE, DataModel.NO_DATA);
					//SocketClient.isTesting = true;
					SocketClient.write(model);
				}
				break;
			case R.id.print_noloadAll:
				String printTime = HexDump.getPrintTime(new Date());
				DataModel model = new DataModel();
				model.setRequestCode(DataModel.NOLOAD_PRINT, printTime.substring(0, 2), printTime.substring(2));
				//SocketClient.isTesting = true;
				SocketClient.write(model);
				break;
			case R.id.getParam_noloadAll:
				if(SocketClient.isConnectedServer) {
					SocketClient.isTesting = true;
					GetParamThread.isReceiveParam = true;
					subCode = -1;
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
					setParam(true);
					Toast.makeText(getActivity(), "参数调取成功", Toast.LENGTH_LONG).show();
				}
					break;
			case R.id.test_noloadAll:
				GetNoloadTest.loadResult = false;
				isCanTest = true;
				startTest.setEnabled(true);
			}
		}
		
	}
	
	
	/**
	 * 当开始测试之后，如果重新进行了参数设置，则需要停止
	 */
	private void setStartTestBtn() {
		// 如果现在正在测试，则停止测试
		if(!isCanTest || (noloadTest != null && noloadTest.isAlive())) {
			isCanTest = true;
			startTest.setEnabled(isCanTest);
			if(noloadTest != null ){
				GetNoloadTest.loadResult = false;
				SocketClient.isTesting = false;
				noloadTest = null;
			}
			
		}
		
		/*isCanTest = true;
		startTest.setEnabled(isCanTest);
		GetNoloadTest.loadResult = false;*/
	}
	
	private void setParam(boolean isFromResume) {
		int intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.RATED_CAPACITY, 0);
		ratedCapacity.setText(CacheManager.transformerInfo.getRatedCapacity() + "");
		
		float temp = CacheManager.getFloat( CacheManager.PARAM_CONFIG, CacheManager.FIRST_VOLTAGE, 0);
		firstVoltage.setText(CacheManager.transformerInfo.getFirstVoltage() + "");
		temp = CacheManager.getFloat( CacheManager.PARAM_CONFIG, CacheManager.SECOND_VOLTAGE, 0);
		secondVoltage.setText(CacheManager.transformerInfo.getSecondVoltage() + "");
		
		// intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.CONNECTION_GROUP, 0);
		intTemp = CacheManager.transformerInfo.getConnectionGroup();
		if(intTemp <= connectionGroupAdapter.getCount() && connectionGroup.getSelectedItemPosition() != intTemp - 1) {
			connectionGroup.setSelection(intTemp - 1, true);
		}
		
		// intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.NOLOAD_TRANSFORMER_TYPE, 0);
		intTemp = CacheManager.transformerInfo.getNoloadTransformerType();
		if(intTemp <= capacityTypeAdapter.getCount() && capacityTransformerType.getSelectedItemPosition() != intTemp - 1) {
			capacityTransformerType.setSelection(intTemp - 1, true);
		}
		
		intTemp = CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TEST_METHOD, 0) - 1;
		intTemp = CacheManager.transformerInfo.getTestMethod() - 1;
		if(isFromResume && intTemp == 0) {
			testMethod.setSelection(3, true);
		}
		if( intTemp != testMethod.getSelectedItemPosition() && intTemp <= testMethodAdapter.getCount() ) {
			
			testMethod.setSelection(intTemp );
		}
		
	}
	
	public static final int FIRST_VOLTAGE = 0x0b;
	public static final int SECOND_VOLTAGE = 0x0c;
	public static final int CONNECTION_GROUP = 0x0d;
	public static final int RATED_CAPACITY = 0x0a;
	public static final int TEST_METHOD = 0x12;
	public static final int CAPACITY_TRANSFORMER_TYPE_NOLOAD = 0x14;
	public static boolean isUpdateCorrect = false;
	/**
	 * 更新的原始值
	 */
	public static float srcValue = 0;
	
	
	private void checkParam(){
		TranslateData.translateParam(CacheManager.params, getActivity());
		TransformerDBService transformerDB = new TransformerDBService(getActivity());
			Message msg = Message.obtain();
			String fieldName = "";
			switch(subCode){
			case FIRST_VOLTAGE:
				fieldName = "一次电压";
				if(CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getFirstVoltage() != Float.parseFloat(firstVoltage.getText().toString()))){
					isUpdateCorrect = false;
					firstVoltage.setText(srcValue + "");
				} else {
					isUpdateCorrect = true;
					
					transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "firstVoltage", CacheManager.transformerInfo.getFirstVoltage());
				}
				break;
			case SECOND_VOLTAGE:
				fieldName = "二次电压";
				if(CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getSecondVoltage() != Float.parseFloat(secondVoltage.getText().toString()))){
					isUpdateCorrect = false;
					secondVoltage.setText(srcValue + "");
				} else {
					isUpdateCorrect = true;
					transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "secondVoltage",  CacheManager.transformerInfo.getSecondVoltage());
				}
				break;
			case RATED_CAPACITY: 
				fieldName = "额定容量";
				if(CacheManager.params != null && (isParamNull ||  CacheManager.transformerInfo.getRatedCapacity() != Integer.parseInt(ratedCapacity.getText().toString()))){
					isUpdateCorrect = false;
					ratedCapacity.setText(((int)srcValue) + "");
				} else {
					isUpdateCorrect = true;
					transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "ratedCapacity",  CacheManager.transformerInfo.getRatedCapacity());
				}
				break;
			case CONNECTION_GROUP:
				fieldName = "联结组别";
				if(CacheManager.params != null && (isParamNull ||  CacheManager.transformerInfo.getConnectionGroup() - 1 != connectionGroup.getSelectedItemPosition())){
					isUpdateCorrect = false;
					connectionGroup.setSelection((int)srcValue - 1, true);
				} else {
					isUpdateCorrect = true;
					transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "connectionGroup", CacheManager.transformerInfo.getConnectionGroup());
				}
				break;
			case TEST_METHOD:
				fieldName = "测试方法";
				if(CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getTestMethod() - 1 != testMethod.getSelectedItemPosition())){
					isUpdateCorrect = false;
					testMethod.setSelection((int)srcValue - 1, true);
				} else {
					isUpdateCorrect = true;
					transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "testMethod", CacheManager.transformerInfo.getTestMethod());
				}
				break;
			case CAPACITY_TRANSFORMER_TYPE_NOLOAD:
				fieldName = "变压器类型";
				if(CacheManager.params != null && (isParamNull || CacheManager.transformerInfo.getNoloadTransformerType() - 1 != capacityTransformerType.getSelectedItemPosition())){
					isUpdateCorrect = false;
					capacityTransformerType.setSelection((int)srcValue - 1,  true);
				} else {
					isUpdateCorrect = true;
					transformerDB.updateSingleParam(CacheManager.transformerInfo.getTransformerCode(), "transformerType", CacheManager.transformerInfo.getNoloadTransformerType());
				}
				break;
			}
			if(!isUpdateCorrect) {
				Toast.makeText(getActivity(), fieldName + "数据修改失败", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getActivity(), fieldName + "数据修改成功", Toast.LENGTH_SHORT).show();
			}
	}


	@Override
	public void initData() {
		startTest.setEnabled(true);
		if(noloadReturn != null) noloadReturn.initData();
		GetNoloadTest.loadResult = false;
		GetParamThread.isReceiveParam = true;
		SocketClient.isTesting = false;
	}
	
}
