package com.lxdz.capacity.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.lxdz.capacity.R;
import com.lxdz.capacity.activity.IndexActivity;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.TransformerInfo;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.widget.UnitView;

/**
 * 参数设置
 * @author Administrator
 *
 */
public class ParamSettingFragment extends Fragment{
	
	//变压器ID
	private EditText transformerId;
		private EditText transformerCode;
	
	//容量变压器类型
	private Spinner capacityTransformerType;
	
	private ArrayAdapter<CharSequence> capacityTypeAdapter;
	
	private EditText userName;
	
	private EditText userAddress;
	
	private EditText testUser;
	
	//当前温度
		private EditText currentTemperature;
	
	// 额定容量
	private EditText ratedCapacity;
	
	//变压类型
	private Spinner transformerType;
	private ArrayAdapter<CharSequence> transformerTypeAdapter;
	
	// 联结组别
	private Spinner connectionGroup;
	private ArrayAdapter<CharSequence> connectionGroupAdapter;
	
	// 分接档位
	private Spinner tapGear;
	private ArrayAdapter<CharSequence> tapGearAdapter;
	
	// 一次电压
	private EditText firstVoltage;
	
	// 二次电压
	private EditText secondVoltage;
	
	//阻抗电压
	private EditText impendaceVoltage;
	
	// 校正系数
	private EditText correctCoefficent;
	
	/*// 自定义1
	private EditText custom1;
	
	// 自定义2
	private EditText custom2;*/
	
	/*private EditText wifiName;
	
	private EditText wifiPassword;*/
	
	private View containerFather;
	
	//调取参数
	// private Button loadParam;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		containerFather = inflater.inflate(R.layout.param_setting, container, false);
		init();
		return containerFather;
	}
	
	/**
	 * 初始化
	 */
	public void init() {
		
		//变压器编号
		transformerCode = (EditText)containerFather.findViewById(R.id.transformerCode_param);
		transformerId = (EditText)containerFather.findViewById(R.id.transformerId_param);
		
		// 容量变压器类型
		capacityTypeAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.capacityTransformerTypeArray, R.layout.adapter_item);
		capacityTransformerType = (Spinner) containerFather.findViewById(R.id.capacityTransformerType_param);
		capacityTransformerType.setAdapter(capacityTypeAdapter);
		capacityTransformerType.setEnabled(false);
		userName = (EditText)containerFather.findViewById(R.id.username_param);		
		userAddress = (EditText)containerFather.findViewById(R.id.userAddress_param);
		testUser = (EditText)containerFather.findViewById(R.id.testUser_param);
		// 当前温度
		UnitView temp = (UnitView)containerFather.findViewById(R.id.currentTemperatue_param);
		currentTemperature = temp.getDataText();
		currentTemperature.setEnabled(false);
		
		//额定容量
		temp = (UnitView)containerFather.findViewById(R.id.ratedCapacity_param);
		ratedCapacity = temp.getDataText();
		ratedCapacity.setEnabled(false);
		
		// 空载变压器类型
		transformerType = (Spinner)containerFather.findViewById(R.id.transformerType_param);
		transformerTypeAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.noloadTransformerTypeArray, R.layout.adapter_item);
		transformerType.setAdapter(transformerTypeAdapter);
		transformerType.setEnabled(false);
		
		// 联结组别
		connectionGroup = (Spinner)containerFather.findViewById(R.id.connectionGroup_param);
		connectionGroupAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.connectionGroupArray, R.layout.adapter_item);
		connectionGroup.setAdapter(connectionGroupAdapter);
		connectionGroup.setEnabled(false);
		// 分接档位
		tapGear = (Spinner)containerFather.findViewById(R.id.tapGear_param);
		tapGearAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.tapGearArray, R.layout.adapter_item);
		tapGear.setAdapter(tapGearAdapter);
		tapGear.setEnabled(false);
		// 一次电压
		temp = (UnitView)containerFather.findViewById(R.id.firstVoltage_param);
		firstVoltage = temp.getDataText();
		firstVoltage.setEnabled(false);
		// 二次电压
		temp = (UnitView)containerFather.findViewById(R.id.secondVoltage_param);
		secondVoltage = temp.getDataText();
		secondVoltage.setEnabled(false);
		// 阻抗电压
		temp = (UnitView)containerFather.findViewById(R.id.impendanceVoltage_param);
		impendaceVoltage = temp.getDataText();
		impendaceVoltage.setEnabled(false);
		// 校正系数
		correctCoefficent = (EditText)containerFather.findViewById(R.id.correctCoefficient_param);
		correctCoefficent.setEnabled(false);
		/*custom1 = (EditText)containerFather.findViewById(R.id.custom1_param);
		custom2 = (EditText)containerFather.findViewById(R.id.custom2_param);*/
		
		/*wifiName = (EditText)containerFather.findViewById(R.id.wifiName_param);
		wifiPassword = (EditText)containerFather.findViewById(R.id.password_param);*/
		
		//调取参数
		/*loadParam = (Button)containerFather.findViewById(R.id.loadParam_param);
		loadParam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataModel data = new DataModel();
				data.setRequestCode(DataModel.LOAD_PARAM, DataModel.NO_COMMAND, DataModel.NO_DATA );
				SocketClient.getSocketClient().write(data);
			}
		});*/
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getIntExtra(SocketClient.COMMAND_TYPE, 0) != DataModel.CAPACITY_COMMAND_INT) return;
			byte[] receiveData = intent.getByteArrayExtra(SocketClient.NEW_DATA);
			Toast.makeText(getActivity(), "参数设置界面接收到数据", Toast.LENGTH_LONG).show();
		}
	};
	IntentFilter filter = new IntentFilter(SocketClient.ON_NEW_DATA);
	
	@Override
	public void onResume() {
		super.onResume();
		if(IndexActivity.openPageProgress != null) IndexActivity.openPageProgress.dismiss();
		// this.getActivity().registerReceiver(receiver, filter);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 不知道在这里注销行不行，以后有了设备再考量下，看会不会影响广播的接收？？？？？？？？？？？？？？？？？？？
		// this.getActivity().unregisterReceiver(receiver);
	}
	
	public void setValue(TransformerInfo info) {
		transformerCode.setText(info.getTransformerCode());
		transformerId.setText(info.getTransformerId() + "");
		capacityTransformerType.setSelection(info.getCapacityTransformerType() - 1);
		testUser.setText(info.getTestUser() == null ? "" : info.getTestUser());
		currentTemperature.setText(info.getCurrentTemperature() +"");
		ratedCapacity.setText(info.getRatedCapacity() + "");
		transformerType.setSelection(info.getNoloadTransformerType() - 1);
		connectionGroup.setSelection(info.getConnectionGroup() - 1);
		tapGear.setSelection(info.getTapGear() - 1);
		correctCoefficent.setText(info.getCorrectionCoefficient() + "");
		firstVoltage.setText(info.getFirstVoltage() + "");
		secondVoltage.setText(info.getSecondVoltage() + "");
		impendaceVoltage.setText(info.getImpedanceVoltage() + "");
		userName.setText(info.getUserName() == null ? "" : info.getUserName());
		userAddress.setText(info.getUserAddress() == null ? "" : info.getUserAddress());
	}
	
	/**
	 * 输入框失去焦点事件
	 * @author Administrator
	 *
	 */
	class TextFocusChange implements View.OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// 如果为控件获取焦点，则退出
			if(v.hasFocus()) return;
			DataModel data = new DataModel();
			switch(v.getId()) {
			case R.id.currentTemperatue_param:
				// data.setRequestCode("", DataModel.CAPACITY_TRANSFORMER_TYPE, );
			}
		}
		
	}

}
