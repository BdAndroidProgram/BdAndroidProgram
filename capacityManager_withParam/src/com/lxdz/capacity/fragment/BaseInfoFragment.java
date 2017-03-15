package com.lxdz.capacity.fragment;

import java.util.List;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdz.capacity.R;
import com.lxdz.capacity.activity.IndexActivity;
import com.lxdz.capacity.dbService.TransformerDBService;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.GetParamThread;
import com.lxdz.capacity.model.TransformerInfo;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.socket.TranslateData;
import com.lxdz.capacity.socket.WifiAdmin;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;

/**
 * 参数设置
 * @author Administrator
 *
 */
public class BaseInfoFragment extends Fragment{
	
	private TransformerDBService transformerDB;
	
	//变压器ID
	private EditText transformerCode;
	
	
	private EditText userName;
	
	private EditText userAddress;
	
	private EditText testUser;
	
	
	private EditText wifiName;
	
	private EditText wifiPassword;
	
	private EditText primaryId;
	private TextView password_text;
	
	private View containerFather;
	
	//调取参数
	private Button saveBtn;
	
	private String prePassword;
	
	WifiAdmin wifi = null;
	private GetParamThread getParam;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		containerFather = inflater.inflate(R.layout.base_info, container, false);
		transformerDB = new TransformerDBService(getActivity());
		if(CacheManager.transformerInfo == null) CacheManager.transformerInfo = new TransformerInfo();
		init();
		return containerFather;
	}
	
	/**
	 * 初始化
	 */
	public void init() {
		
		password_text = (TextView)containerFather.findViewById(R.id.password_text);
		
		//变压器编号
		transformerCode = (EditText)containerFather.findViewById(R.id.transformerCode_base);
		transformerCode.setOnFocusChangeListener(new TextFocusChange());
		/*
		transformerCode.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) return;
				
				String code = transformerCode.getText().toString().trim();
				if(code.equals(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, ""))) {
					return;
				}
				if(code.length() < 1 ) {
					code = "0000";
				} else if(code.length() == 1) {
					code = "000" + code;
				}else if(code.length() == 2) {
					code = "00" + code;
				}else  if(code.length() == 3) {
					code = "0" + code;
				} 
				String sendData = HexDump.getLowStartData(Integer.parseInt(code));
				DataModel model = new DataModel() ;
				if (sendData == null) return;
				model.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.TRANSFORMER_CODE, sendData);
				SocketClient.write(model);
					
				
				
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, code);
				CacheManager.transformerInfo.setTransformerCode(code);
				List<TransformerInfo> transformers = transformerDB.searchTransformer(" transformerCode=?", new String[]{code}, null, null, null);
				if(transformers == null || transformers.size() == 0) {
					primaryId.setText("");
					CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);
					CacheManager.transformerInfo.setTransformerId(-1);
					return;
				}else {
					TransformerInfo info = transformers.get(0);
					CacheManager.transformerInfo.setTransformerId(info.getTransformerId());
					info.cacheTransformer(getActivity().getApplicationContext());
				}
				//setValue();
				transformerCode.setText(code);
				
				
			}
		});
		*/
		
		primaryId = (EditText)containerFather.findViewById(R.id.transformerId_base);
		
		userName = (EditText)containerFather.findViewById(R.id.username_base);	
		userName.setOnFocusChangeListener(new TextFocusChange());
		/*
		userName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String userNameTemp = userName.getText().toString();
				
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_NAME, userNameTemp);
				
			}
		});
		*/
		
		userAddress = (EditText)containerFather.findViewById(R.id.userAddress_base);		
		userAddress.setOnFocusChangeListener(new TextFocusChange());
		/*
		userAddress.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String userAddressTemp = userAddress.getText().toString();
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_ADDRESS, userAddressTemp);
				
				
			}
		});
		*/
		testUser = (EditText)containerFather.findViewById(R.id.testUser_base);
		testUser.setOnFocusChangeListener(new TextFocusChange());
		/*
		testUser.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String testUserTemp = testUser.getText().toString();
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TEST_USER, testUserTemp);
			}
		});
		*/
		wifiName = (EditText)containerFather.findViewById(R.id.wifiName_base);
		wifiName.setOnFocusChangeListener(new TextFocusChange());
		
		wifiPassword = (EditText)containerFather.findViewById(R.id.password_base);
		wifiPassword.setOnFocusChangeListener(new TextFocusChange());
		/*
		wifiPassword.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (wifi == null) wifi = new WifiAdmin(getActivity());
				String password = wifiPassword.getText().toString();
				if(password.length() < 8) return;
				if(prePassword != null && prePassword.equals(password)) {
					return;
				}else if(prePassword != null && !prePassword.equals(password)){
					prePassword = password;
					WifiAdmin.wifiPassword = password;
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_PASSWORD, password);
					wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
				}else if(prePassword == null) {
					prePassword = password;
					WifiAdmin.wifiPassword = password;
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_PASSWORD, password);
				}
				
			}
		});
		*/
		//保存
		saveBtn = (Button)containerFather.findViewById(R.id.saveData_base);
		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				password_text.setFocusable(true);
				password_text.setFocusableInTouchMode(true);
				password_text.requestFocus();
				//userName.findFocus();
				//focuschange.setText("11");
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (wifi == null) wifi = new WifiAdmin(getActivity());
				String ssid = wifiName.getText().toString();
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_NAME, ssid);
				if(!wifi.getmWifiInfo().getSSID().replace("\"", "").equals(ssid)) {
					WifiAdmin.wifiSSID = ssid;
					wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
				}
				
				TransformerInfo transformer = CacheManager.transformerInfo;
				if (transformer == null) transformer =  new TransformerInfo();
				String code = transformerCode.getText().toString().trim();
				//if(code == null || code.length() == 0) {
				// if(transformer.getTransformerCode() == null || transformer.getTransformerCode().length() == 0) {
					// 变压器编号
					// String code = transformerCode.getText().toString();
					if(code == null || code.length() == 0) {
						Toast.makeText(getActivity(), "变压器编号不能为空", Toast.LENGTH_SHORT).show();
						return;
					}
					if(code.length() < 1 ) {
						code = "0000";
					} else if(code.length() == 1) {
						code = "000" + code;
					}else if(code.length() == 2) {
						code = "00" + code;
					}else  if(code.length() == 3) {
						code = "0" + code;
					} 
					transformer.setTransformerCode(code);
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, code);
					//CacheManager.transformerInfo.setTransformerCode(code);
					System.out.println("变压器编号"+code);
					List<TransformerInfo> transformers = transformerDB.searchTransformer(" transformerCode=?", new String[]{code}, null, null, null);
					if(transformers == null || transformers.size() == 0) {
						primaryId.setText("");
						CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);
						CacheManager.transformerInfo.setTransformerId(-1);
						transformer.setTransformerId(-1);
					} else {
						TransformerInfo info = transformers.get(0);
						info.cacheTransformer(getActivity());
						CacheManager.transformerInfo.setTransformerId(info.getTransformerId());
						transformer.setTransformerId(info.getTransformerId());
					}
					/*else {
						TransformerInfo info = transformers.get(0);
						info.cacheTransformer(getActivity());
					}*/
					//transformer.setTransformerCode(code);
				//}
				// transformer = transformer.getCacheTransformer(getActivity());
				if( testUser.getText() != null || !testUser.getText().toString().equals("")){
					String testUserTemp = testUser.getText().toString();
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TEST_USER, testUserTemp);
					transformer.setTestUser(testUserTemp);
				}
				if( userName.getText() != null || !userName.getText().toString().equals("") ) {
					String userNameTemp = userName.getText().toString();
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_NAME, userNameTemp);
					transformer.setUserName(userNameTemp);
				}
				if( userAddress.getText() != null|| !userAddress.getText().toString().equals("") ) {
					String userAddressTemp = userAddress.getText().toString();
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_ADDRESS, userAddressTemp);
					transformer.setUserAddress(userAddressTemp);
				}
				if(Integer.parseInt(transformer.getTransformerCode()) > 10000) {
					Toast.makeText(getActivity(), "变压器编号不能大于10000", Toast.LENGTH_SHORT).show();
					return;
				}
				transformerDB.insertTransformer(transformer);
				//CacheManager.transformerInfo = transformer;
				Toast.makeText(getActivity(), "参数数据数据保存成功", Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	IntentFilter filter = new IntentFilter(SocketClient.ON_NEW_DATA);
	
	@Override
	public void onResume() {
		super.onResume();
		if(IndexActivity.openPageProgress != null) IndexActivity.openPageProgress.dismiss();
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

		setValue();
	}
	
	private void setValue() {
		primaryId.setText(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1) + "");
		String code = CacheManager.transformerInfo.getTransformerCode();
		if(code == null || code.length() == 0) {
			transformerCode.setText(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, ""));
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
			transformerCode.setText(code);
		}
		
		
		List<TransformerInfo> transformers = transformerDB.searchTransformer(" transformerCode=?", new String[]{CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, "")}, null, null, null);
		if(transformers == null || transformers.size() == 0) {
			primaryId.setText("");
			CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);
			
		}else {
			TransformerInfo info = transformers.get(0);
			
			info.cacheTransformer(getActivity().getApplicationContext());
		}
		
		
		
		testUser.setText(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.TEST_USER, ""));
		userName.setText(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.USER_NAME, ""));
		userAddress.setText(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.USER_ADDRESS, ""));
		wifiName.setText(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_NAME, ""));
		wifiPassword.setText(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_PASSWORD, ""));
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
			//if(v.hasFocus()) return;
			if (v.hasFocus() == false)
			{
			switch(v.getId()) {
			case R.id.transformerCode_base:
				/*
				String code = transformerCode.getText().toString();
				if(code.equals(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, ""))) {
					break;
				}
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, code);
				List<TransformerInfo> transformers = transformerDB.searchTransformer(" transformerCode=?", new String[]{code}, null, null, null);
				if(transformers == null || transformers.size() == 0) {
					primaryId.setText("");
					CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);
					break;
				}
				TransformerInfo info = transformers.get(0);
				info.cacheTransformer(getActivity());
				setValue();
				*/
				String code = transformerCode.getText().toString().trim();
				if(code.equals(CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, ""))) {
					return;
				}
				if(code.length() < 1 ) {
					code = "0000";
				} else if(code.length() == 1) {
					code = "000" + code;
				}else if(code.length() == 2) {
					code = "00" + code;
				}else  if(code.length() == 3) {
					code = "0" + code;
				} 
				transformerCode.setText(code);
				String sendData = HexDump.getLowStartData(Integer.parseInt(code));
				DataModel model = new DataModel() ;
				if (sendData == null) return;
				model.setRequestCode(DataModel.CAPACITY_PARAM_SET,
						DataModel.TRANSFORMER_CODE, sendData);
				SocketClient.write(model);
					
				
				
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, code);
				CacheManager.transformerInfo.setTransformerCode(code);
				System.out.println("变压器编号2"+code);
				List<TransformerInfo> transformers = transformerDB.searchTransformer(" transformerCode=?", new String[]{code}, null, null, null);
				if(transformers == null || transformers.size() == 0) {
					primaryId.setText("");
					CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);
					CacheManager.transformerInfo.setTransformerId(-1);
					return;
				}else {
					TransformerInfo info = transformers.get(0);
					CacheManager.transformerInfo.setTransformerId(info.getTransformerId());
					info.cacheTransformer(getActivity().getApplicationContext());
				}
				//setValue();
				
				
				break;
			case R.id.testUser_base:
				String testUserTemp = testUser.getText().toString();
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TEST_USER, testUserTemp);
				break;
			
			case R.id.userAddress_base:
				String userAddressTemp = userAddress.getText().toString();
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_ADDRESS, userAddressTemp);
				break;
			case R.id.wifiName_base:
				if (wifi == null) wifi = new WifiAdmin(getActivity());
				String ssid = wifiName.getText().toString();
				
				if(wifi.getmWifiInfo().getSSID().replace("\"", "").equals(ssid) && CacheManager.getString( CacheManager.PARAM_CONFIG,  CacheManager.WIFI_NAME, "").equals(ssid) ) break;
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_NAME, ssid);
				WifiAdmin.wifiSSID = ssid;
				
				wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
				break;
			case R.id.password_base:
				/*
				String password = wifiPassword.getText().toString();
				if(prePassword != null && prePassword.equals(password)) {
					break;
				}
				if (wifi == null) wifi = new WifiAdmin(getActivity());
				prePassword = password;
				WifiAdmin.wifiPassword = password;
				CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_PASSWORD, password);
				wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
				*/
				if (wifi == null) wifi = new WifiAdmin(getActivity());
				String password = wifiPassword.getText().toString();
				//if(password.length() < 8) return;
				if(prePassword != null && prePassword.equals(password)) {
					return;
				}else if(prePassword != null && !prePassword.equals(password)){
					prePassword = password;
					WifiAdmin.wifiPassword = password;
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_PASSWORD, password);
					wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
				}else if(prePassword == null) {
					prePassword = password;
					WifiAdmin.wifiPassword = password;
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_PASSWORD, password);
				}
				break;
			}
		}
		}
		
	}

}
