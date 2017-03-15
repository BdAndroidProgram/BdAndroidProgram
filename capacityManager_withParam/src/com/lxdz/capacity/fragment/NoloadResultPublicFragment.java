package com.lxdz.capacity.fragment;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.lxdz.capacity.R;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.TestNormalData;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.widget.UnitView;

/**
 * 负载测试
 * @author Administrator
 *
 */
public class NoloadResultPublicFragment extends Fragment{
	
	private EditText noloadCurrent;
	private EditText trueNoloadLoss;
	private EditText correctionNoloadLoss;
	private Spinner detemineTransformerType;
	private NoloadAndLoadReturnFragment publicData;
	
	private View containerFather;
	ArrayAdapter capacityTypeAdapter;
	
	private LinearLayout result1, result2;
	
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(containerFather == null) {
			containerFather = inflater.inflate(R.layout.noload_test_return_public, container, false);
			initFragment();
			init();
		}
		ViewGroup parent = (ViewGroup) containerFather.getParent();  
        if (parent != null) {  
              parent.removeView(containerFather);  
        }   
        
		
		return containerFather;
	}
	
	public void initFragment() {
		// publicData = (PublicReturnFragment)fragmentManager.findFragmentById(R.id.publicResult_noloadPublic);
		FragmentManager fm = getChildFragmentManager();
		publicData = new NoloadAndLoadReturnFragment();
		fm.beginTransaction().add(R.id.publicResult_noloadPublic, publicData).commit();
	}
	
	private void init() {
		UnitView unit = (UnitView)containerFather.findViewById(R.id.noloadCurrent_noloadPublic);
		noloadCurrent = unit.getDataText();
		noloadCurrent.setText("0.000");
		
		unit = (UnitView)containerFather.findViewById(R.id.correctionNoloadLoss_noloadPublic);
		correctionNoloadLoss = unit.getDataText();
		correctionNoloadLoss.setText("0.000");
		
		unit = (UnitView)containerFather.findViewById(R.id.trueNoloadLoss_noloadPublic);
		trueNoloadLoss = unit.getDataText();
		trueNoloadLoss.setText("0.000");
		
		detemineTransformerType = (Spinner)containerFather.findViewById(R.id.deteminTransformerType_noloadPublic);
		capacityTypeAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.capacityType_noload, R.layout.adapter_item);
		detemineTransformerType.setAdapter(capacityTypeAdapter);
		detemineTransformerType.setEnabled(false);
		detemineTransformerType.setSelection(0, true);
		
		result1 = (LinearLayout)containerFather.findViewById(R.id.result1_noloadAll);
		result2 = (LinearLayout)containerFather.findViewById(R.id.result2_noloadAll);
	}

	/**
	 * 在单相测试的时候，每次返回的数据都是读取的ua，ia，pa这三个数据，
	 * @param noloadInfo
	 */
	public void setValue(NoloadResultInfo noloadInfo, int testCount){
		if(noloadInfo == null) noloadInfo = new NoloadResultInfo();
		// publicData.setValue(noloadInfo);
		// int testMethodSeleted = CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TEST_METHOD, 0) - 1;
		int testMethodSeleted = 0;
		if (CacheManager.transformerInfo != null) testMethodSeleted = CacheManager.transformerInfo.getTestMethod() - 1;
		// 单相测试
		if(testMethodSeleted == 0) {
			switch(testCount){
			// 第一次测试, 因为是在每一次测试完成之后testcount + 1， 所以这里的值从1开始
			case 1:
				publicData.setValue(0, noloadInfo);
				break;
			case 2:
				publicData.setValue(1, noloadInfo);
				break;
			case 3:
				publicData.setValue(2, noloadInfo);
				setPublicValue(noloadInfo);
				break;
				
			}
		}else if(testMethodSeleted == 1) {
			// 三相测试
			publicData.setValue(0, noloadInfo);
			publicData.setValue(2, noloadInfo);
			setPublicValue(noloadInfo);
		}else if(testMethodSeleted == 2) {
			publicData.setValue(noloadInfo);
			setPublicValue(noloadInfo);
		}
		
	}
	/**
	 * 在单相测试的时候，每次返回的数据都是读取的ua，ia，pa这三个数据，
	 * @param noloadInfo
	 */
	public void setValue1(NoloadResultInfo noloadInfo){
		if(noloadInfo == null) noloadInfo = new NoloadResultInfo();
		// publicData.setValue(noloadInfo);
		// int testMethodSeleted = CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TEST_METHOD, 0) - 1;
		int testMethodSeleted = 0;
		if (CacheManager.transformerInfo != null) testMethodSeleted = CacheManager.transformerInfo.getTestMethod() - 1;


			// 三相测试
		publicData.setValue(0, noloadInfo);
		publicData.setValue(1, noloadInfo);
		publicData.setValue(2, noloadInfo);
		setPublicValue(noloadInfo);

		
	}
	
	private void setPublicValue(NoloadResultInfo noloadInfo) {
		DecimalFormat threeFormat  = new DecimalFormat("##0.000");
		noloadCurrent.setText(threeFormat.format(noloadInfo.getNoloadCurrent()));
		correctionNoloadLoss.setText(threeFormat.format(noloadInfo.getCorrectionNoloadLoss() ));
		trueNoloadLoss.setText(threeFormat.format(noloadInfo.getTrueLoadLoss()));
		int tempType = noloadInfo.getDetemineTransformerType()-1 < 0 ? 13 : noloadInfo.getDetemineTransformerType()-1;
		detemineTransformerType.setSelection( tempType );
	}
	
	public void initData(){
		if(publicData != null) publicData.initData();
		noloadCurrent.setText("0.000");
		correctionNoloadLoss.setText("0.000");
		trueNoloadLoss.setText("0.000");
		detemineTransformerType.setSelection(0);
	}
	
	public NoloadResultInfo getValue() {
		TestNormalData baseInfo = publicData.getValue();
		NoloadResultInfo noloadInfo = new NoloadResultInfo();
		if(baseInfo != null) {
			noloadInfo.setUa(baseInfo.getUa());
			noloadInfo.setUb(baseInfo.getUb());
			noloadInfo.setUc(baseInfo.getUc());
			noloadInfo.setIa(baseInfo.getIa());
			noloadInfo.setIb(baseInfo.getIb());
			noloadInfo.setIc(baseInfo.getIc());
			noloadInfo.setPa(baseInfo.getPa());
			noloadInfo.setPb(baseInfo.getPb());
			noloadInfo.setPc(baseInfo.getPc());
		}
		noloadInfo.setTransformerId(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1));
		
		noloadInfo.setNoloadCurrent(Double.parseDouble(noloadCurrent.getText().toString()));
		noloadInfo.setTrueLoadLoss(Double.parseDouble(trueNoloadLoss.getText().toString()));
		noloadInfo.setCorrectionNoloadLoss(Double.parseDouble(correctionNoloadLoss.getText().toString()));
		noloadInfo.setDetemineTransformerType(detemineTransformerType.getSelectedItemPosition());
		return noloadInfo;
	}
	
	public void setResultVisible(int type) {
		switch(type) {
		case 0:
			// 单相测试
			result1.setVisibility(View.GONE);
			result2.setVisibility(View.GONE);
			publicData.isShowB(View.GONE);
			publicData.isShowC(View.GONE);
			publicData.updateLabelA("ab");
			break;
		case 1:
			// 三相测试
			result1.setVisibility(View.VISIBLE);
			result2.setVisibility(View.VISIBLE);
			publicData.isShowB(View.GONE);
			publicData.isShowC(View.VISIBLE);
			break;
		case 2:
			// 四相测试
			result1.setVisibility(View.VISIBLE);
			result2.setVisibility(View.VISIBLE);
			publicData.isShowB(View.VISIBLE);
			publicData.isShowC(View.VISIBLE);
			break;
		}
		
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
			switch(index) {
			case 0:
				result1.setVisibility(View.GONE);
				result2.setVisibility(View.GONE);
				publicData.isShowB(View.GONE);
				publicData.isShowC(View.GONE);
				publicData.updateLabelA("ab");
				break;
			case 1:
				result1.setVisibility(View.GONE);
				result2.setVisibility(View.GONE);
				publicData.isShowB(View.VISIBLE);
				publicData.isShowC(View.GONE);
				publicData.updateLabelA("ab");
				publicData.updateLabelB("bc");
				break;
			case 2:
				result1.setVisibility(View.VISIBLE);
				result2.setVisibility(View.VISIBLE);
				publicData.isShowB(View.VISIBLE);
				publicData.isShowC(View.VISIBLE);
				publicData.updateLabelA("ab");
				publicData.updateLabelB("bc");
				publicData.updateLabelC("ca");
				break;
			}
			// 单相测试
			
			break;
		case 1:
			// 三相测试
			result1.setVisibility(View.VISIBLE);
			result2.setVisibility(View.VISIBLE);
			publicData.isShowB(View.GONE);
			publicData.isShowC(View.VISIBLE);
			publicData.updateLabelA("ab");
			publicData.updateLabelC("cb");
			break;
		case 2:
			// 四相测试
			result1.setVisibility(View.VISIBLE);
			result2.setVisibility(View.VISIBLE);
			publicData.isShowB(View.VISIBLE);
			publicData.isShowC(View.VISIBLE);
			publicData.updateLabelA("a");
			publicData.updateLabelB("b");
			publicData.updateLabelC("c");
			break;
		}
	}
	
	
	
}
