package com.lxdz.capacity.fragment;

import java.text.DecimalFormat;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lxdz.capacity.R;
import com.lxdz.capacity.activity.IndexActivity;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.ParamTestInfo;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.socket.TranslateData;
import com.lxdz.capacity.socket.WifiAdmin;
import com.lxdz.capacity.util.NumberUtil;
import com.lxdz.capacity.widget.UnitView;

/**
 * 参数设置
 * @author Administrator
 *
 */
public class ParamTestingFragment extends Fragment{
	private View containerFather;	
	
	WifiAdmin wifi = null;
	ParamTestInfo param = null;
	GetParamTest getParamTest = null;
	
	private EditText ua;
	private EditText ub;
	private EditText uc;
	private EditText uaAverage;
	private EditText ubAverage;
	private EditText ucAverage;
	private EditText ia;
	private EditText ib;
	private EditText ic;
	private EditText pa;
	private EditText pb;
	private EditText pc;
	private EditText cosa;
	private EditText cosb;
	private EditText cosc;
	private EditText frequence;
	private EditText p;
	private EditText factor;
	private EditText sa;
	private EditText sb;
	private EditText sc;
	private EditText s;
	private boolean isReceiveParamTest = false;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		containerFather = inflater.inflate(R.layout.param_test, container, false);
		init();
		return containerFather;
	}
	
	/**
	 * 初始化
	 */
	public void init() {		
		UnitView unit = (UnitView)(UnitView)containerFather.findViewById(R.id.ua_paramTest);
		ua = unit.getDataText();
		ua.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.ub_paramTest);
		ub = unit.getDataText();
		ub.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.uc_paramTest);
		uc = unit.getDataText();
		uc.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.ua_average);
		uaAverage = unit.getDataText();
		uaAverage.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.ub_average);
		ubAverage = unit.getDataText();
		ubAverage.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.uc_average);
		ucAverage = unit.getDataText();
		ucAverage.setText("0.0000");
		
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.ia_paramTest);
		ia = unit.getDataText();
		ia.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.ib_paramTest);
		ib = unit.getDataText();
		ib.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.ic_paramTest);
		ic = unit.getDataText();
		ic.setText("0.0000");
		
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.pa_paramTest);
		pa = unit.getDataText();
		pa.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.pb_paramTest);
		pb = unit.getDataText();
		pb.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.pc_paramTest);
		pc = unit.getDataText();
		pc.setText("0.0000");
		
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.cosa_paramTest);
		cosa = unit.getDataText();
		cosa.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.cosb_paramTest);
		cosb = unit.getDataText();
		cosb.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.cosc_paramTest);
		cosc = unit.getDataText();
		cosc.setText("0.0000");
		
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.p_paramTest);
		p = unit.getDataText();
		p.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.frequence_paramTest);
		frequence = unit.getDataText();
		frequence.setText("0.000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.factor_paramTest);
		factor = unit.getDataText();
		factor.setText("0.0000");
		
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.sa_paramTest);
		sa = unit.getDataText();
		sa.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.sb_paramTest);
		sb = unit.getDataText();
		sb.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.sc_paramTest);
		sc = unit.getDataText();
		sc.setText("0.0000");
		unit = (UnitView)(UnitView)containerFather.findViewById(R.id.s_paramTest);
		s = unit.getDataText();
		s.setText("0.0000");
	}
	
	IntentFilter filter = new IntentFilter(SocketClient.ON_NEW_DATA);
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra(SocketClient.COMMAND_TYPE, 0) != DataModel.GET_PARAMTEST_INT)
				return;
			byte[] receiveData = intent.getByteArrayExtra(SocketClient.NEW_DATA);
			// isReceiveParamTest = true;
			param = TranslateData.translateParamTest(receiveData);
			setValue();
		}
	};
	
	public void setValue() {
		double d[] = {0.005,0.009,-0.01, -0.0026, -0.0068,0.0087, 0.0062,0.0039,0.0048};
		if(param == null) return;
		DecimalFormat decimalFormat  = new DecimalFormat("##0.0000");
		DecimalFormat threedecimalFormat  = new DecimalFormat("##0.000");
		
		ua.setText(NumberUtil.getDisplayNumber(param.getUa()));
		ub.setText(NumberUtil.getDisplayNumber(param.getUb()));
		uc.setText(NumberUtil.getDisplayNumber(param.getUc()));
		uaAverage.setText(NumberUtil.getDisplayNumber(param.getUaAverage()));
		ubAverage.setText(NumberUtil.getDisplayNumber(param.getUbAverage()));
		ucAverage.setText(NumberUtil.getDisplayNumber(param.getUcAverage()));
		ia.setText(NumberUtil.getDisplayNumber(param.getIa()));
		ib.setText(NumberUtil.getDisplayNumber(param.getIb()));
		ic.setText(NumberUtil.getDisplayNumber(param.getIc()));
		pa.setText(NumberUtil.getDisplayNumber(param.getPa()));
		pb.setText(NumberUtil.getDisplayNumber(param.getPb()));
		pc.setText(NumberUtil.getDisplayNumber(param.getPc()));
		p.setText(NumberUtil.getDisplayNumber(param.getP()));
		cosa.setText(threedecimalFormat.format(param.getCosa()));
		cosb.setText(threedecimalFormat.format(param.getCosb()));
		cosc.setText(threedecimalFormat.format(param.getCosc()));
		sa.setText(NumberUtil.getDisplayNumber(param.getSa()));
		sb.setText(NumberUtil.getDisplayNumber(param.getSb()));
		sc.setText(NumberUtil.getDisplayNumber(param.getSc()));
		s.setText(NumberUtil.getDisplayNumber(param.getS()));
		
		if(param.getUa() > 30) {
			/*double v = 50 + d[new Random().nextInt(9)];
			frequence.setText(v+"");*/
			frequence.setText("50.00");
		}else {
			frequence.setText("0.00");
		}
		
		factor.setText(threedecimalFormat.format(param.getFactor()));
	}

	@Override
	public void onResume() {
		super.onResume();
		if(IndexActivity.openPageProgress != null) IndexActivity.openPageProgress.dismiss();
		this.getActivity().registerReceiver(receiver, filter);
		isReceiveParamTest = true;
		SocketClient.isTesting = true;
		if(getParamTest == null || !getParamTest.isAlive()) {
			getParamTest = new GetParamTest();
			getParamTest.start();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		isReceiveParamTest = false;
		SocketClient.isTesting = false;
	}
	
	
	int count = 0;
	class GetParamTest extends Thread{
		public void run() {
			while(isReceiveParamTest) {
				DataModel param = new DataModel();
				param.setRequestCode(DataModel.GET_PARAMTEST, "00", DataModel.NO_DATA);
				SocketClient.write(param);
				try {
					// 每隔两秒刷新一次
					Thread.sleep(SocketClient.longCommandSleep);
					count++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
