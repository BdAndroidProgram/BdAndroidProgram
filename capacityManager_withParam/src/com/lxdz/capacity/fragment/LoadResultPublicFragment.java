package com.lxdz.capacity.fragment;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lxdz.capacity.R;
import com.lxdz.capacity.model.LoadResultInfo;
import com.lxdz.capacity.model.TestNormalData;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.widget.UnitView;

/**
 * 负载测试
 * @author Administrator
 *
 */
public class LoadResultPublicFragment extends Fragment{
	
	private NoloadAndLoadReturnFragment publicData;
	
		private View containerFather;
		
	
		private EditText trueLoadLoss;
		private EditText correctionLoadLoss;
		private EditText shortCircuitImpedance;
		

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(containerFather == null) {
			containerFather = inflater.inflate(R.layout.load_test_return_public, container, false);
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
		FragmentManager fm = getChildFragmentManager();
		publicData = new NoloadAndLoadReturnFragment();
		fm.beginTransaction().add(R.id.publicResult_loadPublic, publicData).commit();
	}
	
	
	private void init() {
		UnitView unit = (UnitView)(UnitView)containerFather.findViewById(R.id.trueLoadLoss_loadPublic);
		trueLoadLoss = unit.getDataText();
		trueLoadLoss.setText("0.000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.shortCircuitImpedance_loadPublic);
		shortCircuitImpedance = unit.getDataText();
		shortCircuitImpedance.setText("0.000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.correctionLoadLoss_loadPublic);
		correctionLoadLoss = unit.getDataText();
		correctionLoadLoss.setText("0.000");
		
	}
	
	public LoadResultInfo getValue() {
		TestNormalData baseInfo = publicData.getValue();
		LoadResultInfo loadInfo = new LoadResultInfo();
		if(baseInfo != null) {
			loadInfo.setUa(baseInfo.getUa());
			loadInfo.setUb(baseInfo.getUb());
			loadInfo.setUc(baseInfo.getUc());
			loadInfo.setIa(baseInfo.getIa());
			loadInfo.setIb(baseInfo.getIb());
			loadInfo.setIc(baseInfo.getIc());
			loadInfo.setPa(baseInfo.getPa());
			loadInfo.setPb(baseInfo.getPb());
			loadInfo.setPc(baseInfo.getPc());
		}
		loadInfo.setTransformerId(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1));
		
		loadInfo.setTrueLoadLoss(Double.parseDouble(trueLoadLoss.getText().toString()));
		loadInfo.setShortCircuitImpedance(Double.parseDouble(shortCircuitImpedance.getText().toString()));
		loadInfo.setShortCircuitImpedance(Double.parseDouble(correctionLoadLoss.getText().toString()));
		return loadInfo;
	}
	

	
	public void setValue(LoadResultInfo loadInfo){
		if(loadInfo == null) loadInfo = new LoadResultInfo();
		publicData.setValue(loadInfo);
		DecimalFormat decimalFormat  = new DecimalFormat("##0.0000");
		trueLoadLoss.setText(decimalFormat.format(loadInfo.getTrueLoadLoss()));
		shortCircuitImpedance.setText(decimalFormat.format(loadInfo.getShortCircuitImpedance()));
		correctionLoadLoss.setText(decimalFormat.format(loadInfo.getCorrectLoadLoss() ));
	}
	
	public void initData(){
		if(publicData != null) publicData.initData();
		trueLoadLoss.setText("0.000");
		correctionLoadLoss.setText("0.000");
		shortCircuitImpedance.setText("0.000");
	}
	
	/**
	 * 
	 * @param type 测试类型，0：单相，1：三相， 2：四相
	 * @param index 测试次数，主要用在单相测试
	 */
	public void setResultLabel(int type, int index) {
		if(publicData == null) return;
		switch(type) {
		
		case 0:
			// 三相测试
			
			publicData.isShowB(View.GONE);
			publicData.updateLabelA("ab");
			publicData.updateLabelC("cb");
			break;
		case 1:
			// 四相测试
			
			publicData.isShowB(View.VISIBLE);
			publicData.isShowC(View.VISIBLE);
			publicData.updateLabelA("a");
			publicData.updateLabelB("b");
			publicData.updateLabelC("c");
			break;
		}
	}
}
