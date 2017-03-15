package com.lxdz.capacity.fragment;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxdz.capacity.R;
import com.lxdz.capacity.model.TestNormalData;
import com.lxdz.capacity.socket.TranslateData;
import com.lxdz.capacity.util.NumberUtil;
import com.lxdz.capacity.widget.UnitView;

public class PublicReturnFragment extends Fragment{
	
private View containerFather;

	private static final String TAG = PublicReturnFragment.class.getSimpleName();
	
	private EditText ua;
	private EditText ub;
	private EditText uc;
	private EditText ia;
	private EditText ib;
	private EditText ic;
	private EditText pa;
	private EditText pb;
	private EditText pc;
	private UnitView uaUnit;
	private UnitView ubUnit;
	private UnitView ucUnit;
	private UnitView iaUnit;
	private UnitView ibUnit;
	private UnitView icUnit;
	private UnitView paUnit;
	private UnitView pbUnit;
	private UnitView pcUnit;
	
	private TextView uaLabel;
	private TextView ubLabel;
	private TextView ucLabel;
	private TextView iaLabel;
	private TextView ibLabel;
	private TextView icLabel;
	private TextView paLabel;
	private TextView pbLabel;
	private TextView pcLabel;
	
	private LinearLayout ubLinear, ibLinear, pbLinear, ucLinear, icLinear, pcLinear;
	
	// private TestNormalData publicData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(containerFather == null) containerFather = inflater.inflate(R.layout.public_return_data, container, false);
		ViewGroup parent = (ViewGroup) containerFather.getParent();  
        if (parent != null) {  
              parent.removeView(containerFather);  
        }   
        Log.i(TAG, "oncreate");
		init();
		
		return containerFather;
	}
	

	
	/**
	 * 初始化
	 */
	public void init() {
		
		uaUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.ua_public);
		ua = uaUnit.getDataText();
		ua.setText("0.0000");
		ubUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.ub_public);
		ub = ubUnit.getDataText();
		ub.setText("0.0000");
		ucUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.uc_public);
		uc = ucUnit.getDataText();
		uc.setText("0.0000");
		iaUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.ia_public);
		ia = iaUnit.getDataText();
		ia.setText("0.0000");
		ibUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.ib_public);
		ib = ibUnit.getDataText();
		ib.setText("0.0000");
		icUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.ic_public);
		ic = icUnit.getDataText();
		ic.setText("0.0000");
		paUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.pa_public);
		pa = paUnit.getDataText();
		pa.setText("0.0000");
		pbUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.pb_public);
		pb = pbUnit.getDataText();
		pb.setText("0.0000");
		pcUnit = (UnitView)(UnitView)containerFather.findViewById(R.id.pc_public);
		pc = pcUnit.getDataText();
		pc.setText("0.0000");
		
		ubLinear = (LinearLayout)containerFather.findViewById(R.id.ubLinear);
		ibLinear = (LinearLayout)containerFather.findViewById(R.id.ibLinear);
		pbLinear = (LinearLayout)containerFather.findViewById(R.id.pbLinear);
		ucLinear = (LinearLayout)containerFather.findViewById(R.id.ucLinear);
		icLinear = (LinearLayout)containerFather.findViewById(R.id.icLinear);
		pcLinear = (LinearLayout)containerFather.findViewById(R.id.pcLinear);
		
		uaLabel = (TextView)containerFather.findViewById(R.id.uaLabel);
		ubLabel = (TextView)containerFather.findViewById(R.id.ubLabel);
		ucLabel = (TextView)containerFather.findViewById(R.id.ucLabel);
		iaLabel = (TextView)containerFather.findViewById(R.id.iaLabel);
		ibLabel = (TextView)containerFather.findViewById(R.id.ibLabel);
		icLabel = (TextView)containerFather.findViewById(R.id.icLabel);
		paLabel = (TextView)containerFather.findViewById(R.id.paLabel);
		pbLabel = (TextView)containerFather.findViewById(R.id.pbLabel);
		pcLabel = (TextView)containerFather.findViewById(R.id.pcLabel);
	}
	
	public void setValue(TestNormalData publicData){
		if(publicData == null) publicData = new TestNormalData();
		DecimalFormat decimalFormat  = new DecimalFormat("##0.0000");
		ua.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getUa())));
		ub.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getUb())));
		uc.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getUc())));
		
		ia.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getIa())) );
		ib.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getIb())));
		ic.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getIc())));
		pa.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getPa())));
		pb.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getPb())));
		pc.setText(TranslateData.getData(NumberUtil.getDisplayNumber(publicData.getPc())));
	}
	
	/**
	 * 
	 * @param type 单相测试和三相测试，按次数设置值
	 * @param publicData
	 */
	public void setValue(int type, TestNormalData publicData) {
		if(publicData == null) publicData = new TestNormalData();
		// DecimalFormat decimalFormat  = new DecimalFormat("##0.0000");
		switch(type) {
		case 0:
			// 设置A相
			ua.setText(NumberUtil.getDisplayNumber(publicData.getUa()));
			ia.setText(NumberUtil.getDisplayNumber(publicData.getIa()));
			pa.setText(NumberUtil.getDisplayNumber(publicData.getPa()));
			break;
		case 1:
			ub.setText(NumberUtil.getDisplayNumber(publicData.getUb()));
			ib.setText(NumberUtil.getDisplayNumber(publicData.getIb()));
			pb.setText(NumberUtil.getDisplayNumber(publicData.getPb()));
			break;
		case 2:
			uc.setText(NumberUtil.getDisplayNumber(publicData.getUc()));
			ic.setText(NumberUtil.getDisplayNumber(publicData.getIc()));
			pc.setText(NumberUtil.getDisplayNumber(publicData.getPc()));
		}
	}
	
	public TestNormalData getValue() {
		TestNormalData capacityInfo = new TestNormalData();
		capacityInfo.setUa(Double.parseDouble(ua.getText().toString()));
		capacityInfo.setUb(Double.parseDouble(ub.getText().toString()));
		capacityInfo.setUc(Double.parseDouble(uc.getText().toString()));
		capacityInfo.setIa(Double.parseDouble(ia.getText().toString()));
		capacityInfo.setIb(Double.parseDouble(ib.getText().toString()));
		capacityInfo.setIc(Double.parseDouble(ic.getText().toString()));
		capacityInfo.setPa(Double.parseDouble(pa.getText().toString()));
		capacityInfo.setPb(Double.parseDouble(pb.getText().toString()));				
		capacityInfo.setPc(Double.parseDouble(pc.getText().toString()));
		return capacityInfo;
	}
	
	public void initData() {
		ua.setText("0.0000");
		ub.setText("0.0000");
		uc.setText("0.0000");
		
		ia.setText("0.0000" );
		ib.setText("0.0000");
		ic.setText("0.0000");
		pa.setText("0.0000");
		pb.setText("0.0000");
		pc.setText("0.0000");
	}
	
	/**
	 * 是否显示B相数据
	 */
	public void isShowB(int isShow) {
		ubLinear.setVisibility(isShow);
		ibLinear.setVisibility(isShow);
		pbLinear.setVisibility(isShow);
	}
	
	/**
	 * 是否显示B相数据
	 */
	public void isShowC(int isShow) {
		ucLinear.setVisibility(isShow);
		icLinear.setVisibility(isShow);
		pcLinear.setVisibility(isShow);
	}
	
	/**
	 * 更新A项label
	 * @param label
	 */
	public void updateLabelA(String label){
		uaLabel.setText("U" + label +": ");
		iaLabel.setText("I" + label +": ");
		paLabel.setText("P" + label +": ");
	}
	
	/**
	 * 更新B项label
	 * @param label
	 */
	public void updateLabelB(String label){
		ubLabel.setText("U" + label +": ");
		ibLabel.setText("I" + label +": ");
		pbLabel.setText("P" + label +": ");
	}
	

	/**
	 * 更新B项label
	 * @param label
	 */
	public void updateLabelC(String label){
		ucLabel.setText("U" + label +": ");
		icLabel.setText("I" + label +": ");
		pcLabel.setText("P" + label +": ");
	}
	
	public void clearUnit(){
		uaUnit.getUnitText().setText("");
		ubUnit.getUnitText().setText("");
		ucUnit.getUnitText().setText("");
		iaUnit.getUnitText().setText("");
		ibUnit.getUnitText().setText("");
		icUnit.getUnitText().setText("");
		paUnit.getUnitText().setText("");
		pbUnit.getUnitText().setText("");
		pcUnit.getUnitText().setText("");
	}
}
