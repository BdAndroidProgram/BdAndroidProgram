package com.lxdz.capacity.fragment;


import java.text.DecimalFormat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lxdz.capacity.R;
import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.TestNormalData;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.widget.UnitView;

public class CapacityResultPublicFragment extends Fragment{
	
	private static final String TAG = CapacityResultPublicFragment.class.getSimpleName();
	
	private View containerFather;
	
	private PublicReturnFragment publicData;
	
	private LinearLayout result1;
	private LinearLayout result2;
	private LinearLayout result3;
	
	private EditText nationalLoss;
	private EditText loadLoss;
	private EditText detemineCapacity;
	private EditText trueCapacity;
	private EditText correctionImpendance;
	private EditText correctLoss;
	private Spinner returnTransformerType;
	private EditText impedanceVoltage;
	
	private TextView returnTransformerTypeLabel;
	
	ArrayAdapter capacityTypeAdapter;
	FragmentManager fm ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(containerFather == null) {
			containerFather = inflater.inflate(R.layout.capacity_test_return_public, container, false);
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
		
		// publicData = (PublicReturnFragment)fm.findFragmentById(R.id.publicResult_capacityAll);
		publicData = new PublicReturnFragment();
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		//FragmentManager ft = getChildFragmentManager();
		ft.add(R.id.publicResult_capacityAll, publicData, "publicFragment").commit();
	}
	
	
	/**
	 * 初始化
	 */
	public void init() {
		UnitView unit = (UnitView)(UnitView)containerFather.findViewById(R.id.nationalLoss_capacityAll);
		nationalLoss = unit.getDataText();
		nationalLoss.setText("0.000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.loadLoss_capacityAll);
		loadLoss = unit.getDataText();
		loadLoss.setText("0.000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.correctionLoss_capacityAll);
		correctLoss = unit.getDataText();
		correctLoss.setText("0.000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.deteminCapacity_capacityAll);
		detemineCapacity = unit.getDataText();
		detemineCapacity.setText("0");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.trueCapacity_capacityAll);
		trueCapacity = unit.getDataText();
		trueCapacity.setText("0.0");
		
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.correctionImpedance_capacityAll);
		correctionImpendance = unit.getDataText();
		correctionImpendance.setText("0.0000");
		capacityTypeAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.capacityType_capacity, R.layout.adapter_item);
		returnTransformerType = (Spinner)containerFather.findViewById(R.id.deteminTransformerType_capacityAll);
		returnTransformerType.setAdapter(capacityTypeAdapter);
		returnTransformerType.setEnabled(false);
		returnTransformerType.setSelection(0, true);
		
		unit = (UnitView)containerFather.findViewById(R.id.impedanceVoltageReturn_capacityAll);
		impedanceVoltage = unit.getDataText();
		impedanceVoltage.setText("0.0000");
		returnTransformerTypeLabel = (TextView)containerFather.findViewById(R.id.returnTransformerLabel_capacityAll);
		
		result1 = (LinearLayout)containerFather.findViewById(R.id.capacityResult1);
		result2 = (LinearLayout)containerFather.findViewById(R.id.capacityResult2);
		result3 = (LinearLayout)containerFather.findViewById(R.id.capacityResult3);
	}
	
	/**
	 * 2015.7.24阻抗電壓，校正電壓要求改成兩位小數
	 * @param capacityInfo
	 */
	public void setValue(CapacityResultInfo capacityInfo){
		DecimalFormat decimalFormat  = new DecimalFormat("##0.00");
		DecimalFormat oneDecimal  = new DecimalFormat("##0.0");
		DecimalFormat threeFormat  = new DecimalFormat("##0.000");
		if(capacityInfo == null) capacityInfo = new CapacityResultInfo();
		
		publicData.setValue(capacityInfo);
		if(capacityInfo.getFinishTest()) {
			this.hideResult(View.VISIBLE);
			impedanceVoltage.setText(decimalFormat.format(capacityInfo.getImpedanceVoltage()));
			
			nationalLoss.setText(threeFormat.format(capacityInfo.getNationalLoss()));
			loadLoss.setText(threeFormat.format(capacityInfo.getLoadLoss()));
			correctLoss.setText(threeFormat.format(capacityInfo.getCorrectedLoss()));
			detemineCapacity.setText(capacityInfo.getDetemineCapacity() + "");
			trueCapacity.setText(oneDecimal.format(capacityInfo.getTrueCapacity()));
			correctionImpendance.setText(decimalFormat.format(capacityInfo.getCorrectionImpendace()));
			returnTransformerType.setSelection(capacityInfo.getReferenceType());
		}else {
			this.hideResult(View.GONE);
		}
	}
	
	public void hideResult(int isShow) {
		result1.setVisibility(isShow);
		result2.setVisibility(isShow);
		result3.setVisibility(isShow);
	}
	
	public void initData(){
		if(publicData != null) publicData.initData();
		nationalLoss.setText("0.000");
		loadLoss.setText("0.000");
		correctLoss.setText("0.000");
		detemineCapacity.setText("0");
		trueCapacity.setText("0.0");
		correctionImpendance.setText("0.0000");
	}
	
	public CapacityResultInfo getValue() {
		TestNormalData baseInfo = publicData.getValue();
		CapacityResultInfo capacityInfo = new CapacityResultInfo();
		if(baseInfo != null) {
			capacityInfo.setUa(baseInfo.getUa());
			capacityInfo.setUb(baseInfo.getUb());
			capacityInfo.setUc(baseInfo.getUc());
			capacityInfo.setIa(baseInfo.getIa());
			capacityInfo.setIb(baseInfo.getIb());
			capacityInfo.setIc(baseInfo.getIc());
			capacityInfo.setPa(baseInfo.getPa());
			capacityInfo.setPb(baseInfo.getPb());
			capacityInfo.setPc(baseInfo.getPc());
		}
		capacityInfo.setTransformerId(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1));
		
		capacityInfo.setNationalLoss(Double.parseDouble(nationalLoss.getText().toString()));
		capacityInfo.setDetemineCapacity(Integer.parseInt(detemineCapacity.getText().toString()));
		capacityInfo.setImpedanceVoltage(Double.parseDouble(impedanceVoltage.getText().toString()));
		capacityInfo.setLoadLoss(Double.parseDouble(loadLoss.getText().toString()));
		capacityInfo.setTrueCapacity(Double.parseDouble(trueCapacity.getText().toString()));
		capacityInfo.setCorrectionImpendace(Double.parseDouble(correctionImpendance.getText().toString()));
		capacityInfo.setCorrectedLoss(Double.parseDouble(correctLoss.getText().toString()));
		capacityInfo.setReferenceType(returnTransformerType.getSelectedItemPosition());
		return capacityInfo;
	}
	

}
