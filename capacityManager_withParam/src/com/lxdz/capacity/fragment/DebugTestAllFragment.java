package com.lxdz.capacity.fragment;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.lxdz.capacity.R;
import com.lxdz.capacity.activity.IndexActivity;
import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.CoefficientInfo;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.GetCapacityTest;
import com.lxdz.capacity.model.GetNoloadTest;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.TestNormalData;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.socket.TranslateData;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;
import com.lxdz.capacity.widget.ConfirmDialog;
import com.lxdz.capacity.widget.UnitView;

public class DebugTestAllFragment extends BaseFragment{
	
	private static final String TAG = DebugTestAllFragment.class.getSimpleName();
	
	/**
	 * 调试项目
	 */
	private View containerFather;
	
	private static final String CAPACITY_NAME = "CAPACITY";
	private static final String NOLOAD_NAME = "NOLOAD";
	
	private Button saveVoltage;
	private Button saveCurrent;
	private Button debugCoefficient;
	
	public static boolean isDebugPage = false;
	public static int loadDataType = 0;
	
	/**
	 * 选择的项目
	 */
	private static String projectCode = DataModel.DEBUG_NOLOAD;
	private static String projectName = NOLOAD_NAME;
	private static String classCode;
	/**
	 * 选择的档位
	 */
	private String gearCode;
	
	private CapacityResultInfo capacityInfo;
	private NoloadResultInfo noloadInfo;
	private CoefficientInfo coefficientInfo;
	
	private RadioGroup debugClass1;
	private RadioGroup debugProjectRadio;
	private boolean isCheck = false;
	
	private static int endterCounter = 0;
	
	public PublicReturnFragment publicData;
	public PublicReturnFragment coefficientData;
	
	private EditText inputValue;
	private Button capacityTest;
	
	private GetCapacityTest testThread;
	GetNoloadTest noloadTest;
	
	private RadioButton noloadBtn;
	private RadioButton capacityBtn;
	private static int selectedProject;
	
	private EditText ja;
	private EditText jb;
	private EditText jc;
	
	Drawable leftDraw;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getIntExtra(SocketClient.COMMAND_TYPE, -1);
			byte[] receiveData = intent.getByteArrayExtra(SocketClient.NEW_DATA);
			if(type == DataModel.CAPACITY_COMMAND_INT) {
				capacityInfo = TranslateData.translateCapacity(receiveData);
			}else if(type == DataModel.NOLOAD_COMMAND_INT) {
				noloadInfo = TranslateData.translateNoload(receiveData);
			}
			switch(type) {
			case DataModel.CAPACITY_COMMAND_INT:
				capacityInfo = TranslateData.translateCapacity(receiveData);
				setValue(type);
				break;
			case DataModel.NOLOAD_COMMAND_INT:
				noloadInfo = TranslateData.translateNoload(receiveData);
				setValue(type);
				break;
			case DataModel.DEBUG_CAPACITY_COEFFICIENT_INT:
			case DataModel.DEBUG_NOLOAD_COEFFICIENT_INT:
				coefficientInfo = TranslateData.translateCoefficient(receiveData);
				setCoefficientValue();
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(containerFather == null){
			containerFather = inflater.inflate(R.layout.debug_test_all, container, false);
			endterCounter = 0;
			init();
			initFragment();
		} 
		ViewGroup parent = (ViewGroup) containerFather.getParent();  
        if (parent != null) {  
              parent.removeView(containerFather);  
        }   
		
		return containerFather;
	}
	
	public void initFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		publicData = (PublicReturnFragment)fragmentManager.findFragmentById(R.id.publicResult_debugAll);
		coefficientData = (PublicReturnFragment)fragmentManager.findFragmentById(R.id.debugCofficient_debugAll);
		coefficientData.clearUnit();
	}
	
	private void init() {
		UnitView unit = (UnitView)containerFather.findViewById(R.id.ja_debugAll);
		ja = unit.getDataText();
		ja.setText("0.0000");
		unit = (UnitView)containerFather.findViewById(R.id.jb_debugAll);
		jb = unit.getDataText();
		jb.setText("0.0000");
		unit = (UnitView)containerFather.findViewById(R.id.jc_debugAll);
		jc = unit.getDataText();
		jc.setText("0.0000");
		
		
		debugProjectRadio = (RadioGroup)containerFather.findViewById(R.id.debugProjectRadio_debugAll);
		debugProjectRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				if(checkedId == R.id.noloadBtn_debugAll) {
					System.out.println("我变了空载测试+++++++++++++++++++++++++++++++++++++++++++++++++++++++" + checkedId);
					if(CacheManager.currentTest == -1 || CacheManager.currentTest != DataModel.NOLOAD_COMMAND_INT){
						initData();
						GetNoloadTest.loadResult = true;
						if(!SocketClient.isConnectedServer) {
							ConfirmDialog confirm = new ConfirmDialog(getActivity());
							confirm.show();
							noloadBtn.setChecked(false);
							capacityBtn.setChecked(true);
							return;
						}
						selectedProject = noloadBtn.getId();
						capacityBtn.setCompoundDrawables(null, null, null, null);
						noloadBtn.setCompoundDrawables(leftDraw, null, null, null);
							projectName = "NOLOAD";
							projectCode = DataModel.DEBUG_NOLOAD;
							loadDataType = DataModel.NOLOAD_COMMAND_INT;
							GetCapacityTest.isTest = false;
							
							if(noloadTest == null || !noloadTest.isAlive()) {
								GetNoloadTest.loadResult = true;
								noloadTest = new GetNoloadTest();
								noloadTest.start();
							}
							capacityTest.setVisibility(View.GONE);
							capacityTest.setEnabled(false);
							CacheManager.currentTest = DataModel.NOLOAD_COMMAND_INT;
						
					}
				}else if(checkedId == R.id.capacityBtn_debugAll) {
					System.out.println("我变了容量测试----------------------------------------------------------" + checkedId);
					if(CacheManager.currentTest == -1 || CacheManager.currentTest != DataModel.CAPACITY_COMMAND_INT){
						initData();
						GetCapacityTest.isTest = true;
						if(!SocketClient.isConnectedServer) {
							ConfirmDialog confirm = new ConfirmDialog(getActivity());
							confirm.show();
							capacityBtn.setChecked(false);
							noloadBtn.setChecked(true);
							return;
						}
						selectedProject = capacityBtn.getId();
						capacityBtn.setCompoundDrawables(leftDraw, null, null, null);
						noloadBtn.setCompoundDrawables(null, null, null, null);
						projectName = "CAPACITY";
						projectCode = DataModel.DEBUG_CAPACITY;
						loadDataType = DataModel.CAPACITY_COMMAND_INT;
						GetNoloadTest.loadResult = false;
						if(testThread == null || !testThread.isAlive()) {
							GetCapacityTest.isTest = true;
							GetCapacityTest.isSendStart = false;
							testThread = new GetCapacityTest();
							testThread.start();
						}
						capacityTest.setVisibility(View.VISIBLE);
						capacityTest.setEnabled(false);
						CacheManager.currentTest = DataModel.CAPACITY_COMMAND_INT;
						
					}
				}
			}
		});
		noloadBtn = (RadioButton)containerFather.findViewById(R.id.noloadBtn_debugAll);
		/*noloadBtn.setOnClickListener(new OnClickListener() {
			
			private void noloadTest() {
				if(!SocketClient.isConnectedServer) {
					ConfirmDialog confirm = new ConfirmDialog(getActivity());
					confirm.show();
					noloadBtn.setChecked(false);
					capacityBtn.setChecked(true);
					return;
				}
				selectedProject = noloadBtn.getId();
				capacityBtn.setCompoundDrawables(null, null, null, null);
				noloadBtn.setCompoundDrawables(leftDraw, null, null, null);
					projectName = "NOLOAD";
					projectCode = DataModel.DEBUG_NOLOAD;
					loadDataType = DataModel.NOLOAD_COMMAND_INT;
					GetCapacityTest.isTest = false;
					
					if(noloadTest == null || !noloadTest.isAlive()) {
						GetNoloadTest.loadResult = true;
						noloadTest = new GetNoloadTest();
						noloadTest.start();
					}
					capacityTest.setVisibility(View.GONE);
					capacityTest.setEnabled(false);
					CacheManager.currentTest = DataModel.NOLOAD_COMMAND_INT;
			}
			
			@Override
			public void onClick(View v) {
				if(CacheManager.currentTest == -1 || CacheManager.currentTest != DataModel.NOLOAD_COMMAND_INT){
					initData();
					GetNoloadTest.loadResult = true;
					noloadTest();
					
				}
			}
		});*/
		capacityBtn = (RadioButton)containerFather.findViewById(R.id.capacityBtn_debugAll);
		/*capacityBtn.setOnClickListener(new OnClickListener() {
			
			private void capacityTest() {
				if(!SocketClient.isConnectedServer) {
					ConfirmDialog confirm = new ConfirmDialog(getActivity());
					confirm.show();
					capacityBtn.setChecked(false);
					noloadBtn.setChecked(true);
					return;
				}
				selectedProject = capacityBtn.getId();
				capacityBtn.setCompoundDrawables(leftDraw, null, null, null);
				noloadBtn.setCompoundDrawables(null, null, null, null);
				projectName = "CAPACITY";
				projectCode = DataModel.DEBUG_CAPACITY;
				loadDataType = DataModel.CAPACITY_COMMAND_INT;
				GetNoloadTest.loadResult = false;
				if(GetCapacityTest.isTest) {
					Toast.makeText(getActivity(), "容量还正在测试中", Toast.LENGTH_SHORT).show();
				}
				if(testThread == null || !testThread.isAlive()) {
					Toast.makeText(getActivity(), "开始测试", Toast.LENGTH_SHORT).show();
					GetCapacityTest.isTest = true;
					GetCapacityTest.isSendStart = false;
					testThread = new GetCapacityTest();
					testThread.start();
				}else {
					Toast.makeText(getActivity(), "未能重新进行容量测试", Toast.LENGTH_SHORT).show();
				}
				capacityTest.setVisibility(View.VISIBLE);
				capacityTest.setEnabled(false);
				CacheManager.currentTest = DataModel.CAPACITY_COMMAND_INT;
			}
			
			@Override
			public void onClick(View v) {
				if(CacheManager.currentTest == -1 || CacheManager.currentTest != DataModel.CAPACITY_COMMAND_INT){
					initData();
					GetCapacityTest.isTest = true;
					capacityTest();
					
				}
			}
		});*/
		
		
		debugClass1 = (RadioGroup)containerFather.findViewById(R.id.debugClass1_debugAll);
		debugClass1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(group != null && checkedId > -1 && isCheck){
					isCheck = false;
					// debugClass2.clearCheck();
					
			    }
				if(checkedId > -1) {
					isCheck = true;
					RadioButton radio = (RadioButton)containerFather.findViewById(checkedId);
					gearCode = (String)radio.getTag();
					classCode = radio.getText().toString().toUpperCase();
				}
				
				
				
			}
		});
		saveVoltage = (Button) containerFather.findViewById(R.id.sendData_debugAll);
		saveVoltage.setOnClickListener(new SaveButton());
		saveVoltage = (Button) containerFather.findViewById(R.id.saveVoltage_debugAll);
		saveVoltage.setOnClickListener(new SaveButton());
		inputValue = (EditText)containerFather.findViewById(R.id.inputValue_debugAll);
		saveCurrent = (Button) containerFather.findViewById(R.id.saveCurrent_debugAll);
		saveCurrent.setOnClickListener(new SaveButton());
		
		debugCoefficient = (Button)containerFather.findViewById(R.id.getCofficientBtn_debugAll);
		debugCoefficient.setOnClickListener(new SaveButton());
		
		capacityTest = (Button)containerFather.findViewById(R.id.capacityTest_debugAll);
		capacityTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(testThread == null || !testThread.isAlive()) {
					GetCapacityTest.isTest = true;
					GetCapacityTest.isSendStart = false;
					testThread = new GetCapacityTest();
					testThread.start();
				}
				capacityTest.setEnabled(false);
			}
		});
	}
	
	
	IntentFilter filter = new IntentFilter(SocketClient.ON_NEW_DATA);
	
	@Override
	public void onResume() {
		super.onResume();
		Resources res = getResources();
		leftDraw = res.getDrawable(R.drawable.seleted);
		leftDraw.setBounds(0, 0, leftDraw.getMinimumWidth(), leftDraw.getMinimumHeight());
		setValue(DataModel.CAPACITY_COMMAND_INT);
		loadDataType = DataModel.NOLOAD_COMMAND_INT;
		
		this.getActivity().registerReceiver(receiver, filter);
		Log.i(TAG, "onresume");
		if(debugProjectRadio != null && capacityBtn != null && noloadBtn != null) {
			debugProjectRadio.check(R.id.noloadBtn_debugAll);
			capacityBtn.setCompoundDrawables(null, null, null, null);
			noloadBtn.setCompoundDrawables(leftDraw, null, null, null);
			selectedProject = noloadBtn.getId();
		}
		
		if(capacityTest != null) {
			capacityTest.setVisibility(View.GONE);
		}
		
		GetCapacityTest.isTest = false;
		
		if(!SocketClient.isConnectedServer) {
			ConfirmDialog confirm = new ConfirmDialog(getActivity());
			confirm.show();
			
		}else {
			GetNoloadTest.loadResult = true;
			noloadTest = new GetNoloadTest();
			noloadTest.start();
		}
		
		if(++endterCounter > 1) {
			publicData.initData();
			coefficientData.initData();
			ja.setText("0.0000");
			jb.setText("0.0000");
			jc.setText("0.0000");
		}
		SocketClient.currentPage = this;
		if(IndexActivity.openPageProgress != null) IndexActivity.openPageProgress.dismiss();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		GetNoloadTest.loadResult = false;
		GetCapacityTest.isTest = false;
		GetCapacityTest.isSendStart = false;
		SocketClient.isTesting = false;
		this.getActivity().unregisterReceiver(receiver);
		SocketClient.currentPage = null;
	}
	
	/**
	 * 设置调试系数的值
	 */
	private void setCoefficientValue() {
		if(coefficientInfo == null) return;
		DecimalFormat decimalFormat  = new DecimalFormat("##0.0000");
		ja.setText(TranslateData.getData(decimalFormat.format(coefficientInfo.getJa())));
		jb.setText(TranslateData.getData(decimalFormat.format(coefficientInfo.getJb())));
		jc.setText(TranslateData.getData(decimalFormat.format(coefficientInfo.getJc())));
		TestNormalData testData = coefficientInfo;
		coefficientData.setValue(testData);
		
	}
	
	private void setValue(int type) {
		TestNormalData testData = null;
		if(type == DataModel.CAPACITY_COMMAND_INT) {
			testData = capacityInfo;
			if(capacityInfo != null && capacityInfo.getFinishTest()) {
				GetCapacityTest.isTest = false;
				capacityTest.setEnabled(true);
				SocketClient.isTesting = false;
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}else if(type == DataModel.NOLOAD_COMMAND_INT) {
			testData = noloadInfo;
		}else if(type == 0) {
			testData = new TestNormalData();
		}
		if(testData == null) return;
		publicData.setValue(testData);
		
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
				//SocketClient.isTesting = true;
				if(SocketClient.write(model)) {
					Toast.makeText(getActivity(), "电流保存成功", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(getActivity(), "电流保存失败", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.saveVoltage_debugAll:
				if(CAPACITY_NAME.equals(projectName)) {
					model.setRequestCode(DataModel.DEBUG_SAVE_CAPACITY, DataModel.DEBUG_SAVE_CAPACITY_VOLTAGE, DataModel.NO_DATA);
				}else if(NOLOAD_NAME.equals(projectName)) {
					model.setRequestCode(DataModel.DEBUG_SAVE_NOLOAD, DataModel.DEBUG_SAVE_NOLOAD_VOLTAGE, DataModel.NO_DATA);
				}
				//SocketClient.isTesting = true;
				if(SocketClient.write(model)) {
					Toast.makeText(getActivity(), "电压保存成功", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(getActivity(), "电压保存失败", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.sendData_debugAll:
				String inputData = inputValue.getText().toString();
				if(inputData == null || inputData.length() == 0) {
					Toast.makeText(getActivity(), "请输入调试数据", Toast.LENGTH_SHORT).show();
					break;
				}
				if(projectCode == null ){
					Toast.makeText(getActivity(), "请选择项目", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(classCode == null) {
					Toast.makeText(getActivity(), "请选择操作项", Toast.LENGTH_SHORT).show();
					return;
				}
				getGearCode();
				if(gearCode == null) {
					Toast.makeText(getActivity(), "请选择操作项", Toast.LENGTH_SHORT).show();
					return;
				}
				
				double data = Double.parseDouble(inputValue.getText().toString());
				
				// 只有JA,JB,JC三相数据不可以比2大
				if(classCode.toUpperCase().indexOf("J") == -1 && data > 2) { 
						Toast.makeText(getActivity(), "输入的数据不能大于2， 请重新输入", Toast.LENGTH_SHORT).show();
						break;
				}else if(classCode.toUpperCase().indexOf("J") != -1 && (data < 0 || data > 127)){
					Toast.makeText(getActivity(), "角度只能为0～127之间的整数", Toast.LENGTH_SHORT).show();
					break;
				}else if(classCode.toUpperCase().indexOf("J") == -1 && data < 2){
					data = data  * 32768.0;
				}
				model.setRequestCode(projectCode, gearCode, HexDump.getLowStartData((int)data));
				//SocketClient.isTesting = true;
				if(SocketClient.write(model)) {
					Toast.makeText(getActivity(), "调试系数发送成功", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(getActivity(), "调试系数发送失败", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.getCofficientBtn_debugAll:
				if(CAPACITY_NAME.equals(projectName)) {
					model.setRequestCode(DataModel.DEBUG_CAPACITY_COEFFICIENT, DataModel.NO_COMMAND, DataModel.NO_DATA);
				}else if(NOLOAD_NAME.equals(projectName)) {
					model.setRequestCode(DataModel.DEBUG_NOLOAD_COEFFICIENT, DataModel.NO_COMMAND, DataModel.NO_DATA);
				}
				//SocketClient.isTesting = true;
				if(SocketClient.write(model)) {
					Toast.makeText(getActivity(), "调试系数调取成功", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	}
	
	/**
	 * 生成调试码
	 * @return
	 */
	private void getGearCode() {
		
		if(projectName == null) return;
		
		String fieldName = "DEBUG_" + projectName + "_" + classCode.toUpperCase() + 1;
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

	@Override
	public void initData() {
		if(publicData != null)publicData.initData();
		if(coefficientData != null) coefficientData.initData();
		ja.setText("0.0000");
		jb.setText("0.0000");
		jc.setText("0.0000");
		GetNoloadTest.loadResult = false;
		GetCapacityTest.isTest = false;
		GetCapacityTest.isSendStart = false;
		// SocketClient.isTesting = false;
	}
}

