package com.lxdz.capacity.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdz.capacity.R;
import com.lxdz.capacity.activity.IndexActivity;
import com.lxdz.capacity.dbService.CapacityDBService;
import com.lxdz.capacity.dbService.LoadDBService;
import com.lxdz.capacity.dbService.NoloadDBService;
import com.lxdz.capacity.dbService.TransformerDBService;
import com.lxdz.capacity.model.CapacityResultInfo;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.LoadResultInfo;
import com.lxdz.capacity.model.NoloadResultInfo;
import com.lxdz.capacity.model.TransformerInfo;
import com.lxdz.capacity.socket.SocketClient;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;
import com.lxdz.capacity.widget.DataInputLayout;
import com.lxdz.capacity.widget.MyHScrollView;
import com.lxdz.capacity.widget.MyHScrollView.OnScrollChangedListener;

/**
 * 容量测试
 * @author Administrator
 *
 */
public class DataManagerAllFragment extends Fragment{
	
	private int selectedTransformerId = -1;
	
	private CapacityDBService capacityDB;
	
	private NoloadDBService noloadDB;
	
	private LoadDBService loadDB;
	
	private Spinner searchField;
	
	private EditText searchCondition;
	
	private TabHost tabHost;
	private RelativeLayout mHead;
	
	ArrayAdapter<CharSequence> searchAdapter;
	
	private Button searchButton;
	private Button deleteButton;
	private Button closeButton;
	private Button printButton;
	
	private EditText startDate;
	private EditText endDate;
	
	private View containerFather;
	
	private ListView transformerListView;
	private TransformerAdapter transformerAdapter; 
	
	private List<TransformerInfo> transformers;
	
	private TransformerDBService transformerDB;
	
	public static ProgressDialog progress;
		
	private CapacityResultInfo capacityInfo;
	private NoloadResultInfo noloadInfo;
	private LoadResultInfo loadInfo;
		
		
	public static final int REQUEST_CODE = 1;
	public static final String SELECTED_TRANSFORMER = "selectedTransformer";
	
	private CapacityResultPublicFragment capacityFragment;
	private NoloadResultPublicFragment noloadFragment;
	private LoadResultPublicFragment loadFragment;
	private ParamSettingFragment paramFragment;
	private TransformerInfo transformerInfo;
	FragmentManager fm ;
	
	private static String tabName;
	
	private View selectedTransformerView;
	
	private int selectedItemPosition = -1;
	private int colors[] = {Color.argb(250, 255, 255, 255), Color.argb(255, 224, 243, 250)};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(containerFather == null) {
			containerFather = inflater.inflate(R.layout.data_manager_all, container, false);
			fm = getFragmentManager();
			transformerDB = new TransformerDBService(getActivity());
			capacityDB = new CapacityDBService(getActivity());
			noloadDB = new NoloadDBService(getActivity());
			loadDB = new LoadDBService(getActivity());
			initTab();
			init();
			initCapacityFragment();
		}
		
		ViewGroup parent = (ViewGroup) containerFather.getParent();  
        if (parent != null) {  
              parent.removeView(containerFather);  
        }   
		
		
		return containerFather;
	}
	
	public void initCapacityFragment() {
		capacityFragment = (CapacityResultPublicFragment)fm.findFragmentById(R.id.capacity_loadAll);
		capacityFragment.hideResult(View.VISIBLE);
		noloadFragment = (NoloadResultPublicFragment) fm.findFragmentById(R.id.noload_loadAll);
		noloadFragment.setResultLabel(2,0);
		loadFragment = (LoadResultPublicFragment)fm.findFragmentById(R.id.load_loadAll);
		loadFragment.setResultLabel(1,0);
		paramFragment = (ParamSettingFragment)fm.findFragmentById(R.id.transformer_detail);
	}
	
	private DatePickerDialog dateDialog;
	
	final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker datePicker, int year, int month,
				int dayOfMonth) {

			startDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
		}
	};
	final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker datePicker, int year, int month,
				int dayOfMonth) {

			endDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
		}
	};
	/**
	 * 初始化控件
	 */
	private void init() {
		
		printButton = (Button) containerFather.findViewById(R.id.print_managerAll);
		printButton.setOnClickListener(new ButtonClickListener());
		
		mHead = (RelativeLayout) containerFather.findViewById(R.id.head);
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		searchField = (Spinner)containerFather.findViewById(R.id.searchField_managerAll);
		searchAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.searchCondition, R.layout.adapter_item);
		searchField.setAdapter(searchAdapter);
		
		searchCondition = (EditText)containerFather.findViewById(R.id.searchCondition_managerAll);
				
		DataInputLayout startDateTemp = (DataInputLayout)containerFather.findViewById(R.id.startDate_managerAll);
		startDate = startDateTemp.getDateText();
		// startDate = (EditText)containerFather.findViewById(R.id.startDate_managerAll);
		startDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				dateDialog = new DatePickerDialog(getActivity(),
						startDateListener, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar
								.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();
				
			}
		});
		DataInputLayout endDateTemp = (DataInputLayout)containerFather.findViewById(R.id.endDate_managerAll);
		endDate = endDateTemp.getDateText();
		
		// endDate = (EditText)containerFather.findViewById(R.id.endDate_managerAll);
		endDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				dateDialog = new DatePickerDialog(getActivity(),
						endDateListener, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar
								.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();
				
			}
		});
		searchButton = (Button)containerFather.findViewById(R.id.search_managerAll);
		searchButton.setOnClickListener(new ButtonClickListener());
		
		deleteButton = (Button)containerFather.findViewById(R.id.delete_managerAll);
		deleteButton.setOnClickListener(new ButtonClickListener());
		
		closeButton = (Button)containerFather.findViewById(R.id.close_managerAll);
		closeButton.setOnClickListener(new ButtonClickListener());
		
		transformerListView = (ListView)containerFather.findViewById(R.id.listView1);
		transformerAdapter = new TransformerAdapter(getActivity(), R.layout.transformer_header);
		if(transformers == null) transformers = new ArrayList<TransformerInfo>();
		transformerListView.setAdapter(transformerAdapter);
		
		transformerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(transformers == null || transformers.size() < 1) return ;
				TransformerAdapter.ViewHolder holder = (TransformerAdapter.ViewHolder)view.getTag();
				
				selectedTransformerId = Integer.parseInt(holder.id.getText().toString());
				transformerInfo = (TransformerInfo)holder.id.getTag();
				if(selectedTransformerView == null) {
					selectedTransformerView = view;
					selectedItemPosition = position;
					view.setBackgroundResource(R.drawable.btn_green_normal);
				}
				
				if(selectedTransformerView != view){
					selectedTransformerView.setBackgroundColor(colors[selectedItemPosition % 2]); // 颜色设置
					selectedTransformerView = view;
					selectedItemPosition = position;
					view.setBackgroundResource(R.drawable.btn_green_normal);
				}
				setTabValue(tabName);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(progress != null) progress.dismiss();
		Toast.makeText(getActivity(), "哈哈", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		capacityFragment.hideResult(View.VISIBLE);
		List<TransformerInfo> result = transformerDB.searchTransformer(null, null, null, null, null);

		if (result != null && result.size() > 0) {
			transformers.clear();
			transformers.addAll(result);
			transformerAdapter.notifyDataSetChanged();
		}
		View view = null;
		if(selectedItemPosition == -1) {
			selectedItemPosition = 0;
			view = transformerAdapter.getView(selectedItemPosition, null, null);
		}
		
		selectedTransformerView = new View(getActivity());
		if(view != null) transformerListView.performItemClick(view, selectedItemPosition, transformerAdapter.getItemId(selectedItemPosition));
		
		if(IndexActivity.openPageProgress != null) IndexActivity.openPageProgress.dismiss();
	}
	
	class ButtonClickListener implements OnClickListener{
		
		private long getStartTime() {
			if (startDate.getText().toString() == null
					|| "请选择日期".equals(startDate.getText().toString().trim()) || startDate.getText().toString().length() == 0){
					 
				return 0;
			}
			
			String startDateArray[] = startDate.getText().toString()
					.split("-");
			Date date = new Date(
					Integer.parseInt(startDateArray[0]) - 1900,
					Integer.parseInt(startDateArray[1]) - 1,
					Integer.parseInt(startDateArray[2]), 0, 0, 0);

			Log.d("", "请选择日期： " + date.toString() + date.getTime());
			return  date.getTime();
		}
		
		private long getEndTime() {
			if ( "请选择日期".equals(endDate.getText().toString().trim())
					|| endDate.getText().toString() == null || endDate.getText().toString().length() == 0) {
				return 0;
			}
			String endDateArray[] = endDate.getText().toString()
					.split("-");
			Date date = new Date(
					Integer.parseInt(endDateArray[0]) - 1900,
					Integer.parseInt(endDateArray[1]) - 1,
					Integer.parseInt(endDateArray[2]), 23, 59, 59);
			Log.d("",
					"请选择日期： " + date.toString() + "  " + date.getTime()
							+ "  " + date.getYear());
			return  date.getTime();
		}

		@Override
		public void onClick(View v) {
			
			switch(v.getId()){
			case R.id.search_managerAll:
				long startTemp = getStartTime();
				long endTemp = getEndTime();
				if(endTemp < startTemp) {
					Toast.makeText(getActivity(), "结束时间不能小于开始时间",
							Toast.LENGTH_LONG).show();;
					break;
				}
				String sql = "";
				ArrayList<String> sqlClause = new ArrayList<String>();
				if(startTemp != 0) {
					sql = " updateTime>=? ";
					sqlClause.add(startTemp + "");
				}
				if(endTemp != 0) {
					if(sql.length() == 0){
						sql += " updateTime<=?";
					}else {
						sql += " and updateTime<=?";
					}
					
					
					sqlClause.add(endTemp + "");
				}
				
				String  conditionValue = searchCondition.getText().toString();
				int conditionField = searchField.getSelectedItemPosition();
				if(conditionValue != null && conditionValue.length() != 0 && conditionField == 0) {
					Toast.makeText(getActivity(), "当选择条件字段", Toast.LENGTH_SHORT).show();
					break;
				}else if(conditionValue != null && conditionValue.length() != 0 && conditionField != 0){
					String fieldName = "";
					switch(conditionField){
					case 1:
						fieldName = "transformerCode";
						break;
					case 2:
						fieldName = "userName";
						break;
					case 3:
						fieldName = "userAddress";
						break;
					case 4:
						fieldName = "testUser";
						break;
					}
					if(sql.length() == 0){
						sql += " " + fieldName + " like ?";
					}else {
						sql += " and " + fieldName + " like ?";
					}
					// sql += " 1=1 and " + fieldName + " like ?";
					sqlClause.add("%" + conditionValue + "%");
					
				}
				
				String[] condition = new String[sqlClause.size()];
				
				
				List<TransformerInfo> result = transformerDB.searchTransformer(sql, sqlClause.toArray(condition), null, null, null);

				if (result == null || result.size() < 0)
					break;
				transformers.clear();
				transformers.addAll(result);
				transformerAdapter.notifyDataSetChanged();
				selectedItemPosition = 0;
				View view = transformerAdapter.getView(selectedItemPosition, null, null);
				transformerListView.performItemClick(view, selectedItemPosition, transformerAdapter.getItemId(selectedItemPosition));
				break;
			case R.id.delete_managerAll:
				TransformerAdapter.ViewHolder holder = (TransformerAdapter.ViewHolder)selectedTransformerView.getTag();
				
				int transformerId = Integer.parseInt(holder.id.getText().toString());
				if(CacheManager.getInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1) == transformerId) {
					CacheManager.putInt( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_ID, -1);
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TRANSFORMER_CODE, "");
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.TEST_USER, "");
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_NAME, "");
					CacheManager.putString( CacheManager.PARAM_CONFIG, CacheManager.USER_ADDRESS, "");
				}
				transformerDB.deleteTransformer(transformerId);
				Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
				searchButton.performClick();
				break;
			case R.id.print_managerAll:
				if(transformerInfo == null ){
					Toast.makeText(getActivity(), "当前没有选中任何变压器", Toast.LENGTH_SHORT).show();
					break;
				}
				DataModel data = null;
				String str = "";
				if(tabName.equals("容量测试")) {
					if(capacityInfo == null) {
						Toast.makeText(getActivity(), "当前没有可打印的容量信息", Toast.LENGTH_SHORT).show();
						break;
					}
					capacityInfo.setFinishTest(true);
					str += HexDump.getLowStartData((int)transformerInfo.getCurrentTemperature());
					str += HexDump.intToByteString_one(transformerInfo.getTapGear());
					str += HexDump.getLowStartData((int)(transformerInfo.getFirstVoltage() * 10));
					str += HexDump.getLowStartData((int)(capacityInfo.getLoadLoss() * 1000));
					str += HexDump.getLowStartData((int)(capacityInfo.getCorrectedLoss() * 1000));
					str += HexDump.getLowStartData((int)(capacityInfo.getNationalLoss() * 1000));
					str += HexDump.getLowStartData((int)(capacityInfo.getCorrectionImpendace() * 10));
					str += HexDump.getLowStartDataFour((int)(capacityInfo.getTrueCapacity() * 32768));
					str += HexDump.getLowStartDataFour((int)(capacityInfo.getDetemineCapacity()));
					long time = transformerInfo.getUpdateTime();
					Date date = new Date(time);
					str += HexDump.intToByteString_one(date.getYear() + 1900 - 2000);
					str += HexDump.intToByteString_one(date.getMonth() + 1);
					str += HexDump.intToByteString_one(date.getDate());
					str += HexDump.intToByteString_one(date.getHours());
					str += HexDump.intToByteString_one(date.getMinutes());
					str += HexDump.getLowStartData(transformerInfo.getRatedCapacity());
					
					str += HexDump.getLowStartData(Integer.parseInt(transformerInfo.getTransformerCode()));
					data = new DataModel();
					data.setRequestCode(DataModel.CAPACITY_SAVE_PRINT,  str);
				}else if(tabName.equals("空载测试")){
					if(noloadInfo == null) {
						Toast.makeText(getActivity(), "当前没有可打印的空载测试信息", Toast.LENGTH_SHORT).show();
						break;
					}
					str += HexDump.getLowStartData(Integer.parseInt(transformerInfo.getTransformerCode()));
					str += HexDump.intToByteString_one(transformerInfo.getTestMethod());
					str += HexDump.getLowStartData(transformerInfo.getRatedCapacity());
					//str += HexDump.getLowStartData((int)(transformerInfo.getRatedLowVoltage() * 10));
					str += HexDump.getLowStartData((int)(transformerInfo.getSecondVoltage()* 10));
					str += HexDump.getLowStartData(transformerInfo.getNoloadTransformerType());
					str += HexDump.getLowStartData((int)(transformerInfo.getCorrectionCoefficient() * 10));
					str += HexDump.getLowStartDataFour((int)(noloadInfo.getTrueLoadLoss() * 32768));
					str += HexDump.getLowStartDataFour((int)(noloadInfo.getCorrectionNoloadLoss() * 32768));
					str += HexDump.getLowStartDataFour((int)(noloadInfo.getNoloadCurrent() * 32768));
					
					str += HexDump.intToByteString_one(noloadInfo.getDetemineTransformerType());
					long time = transformerInfo.getUpdateTime();
					Date date = new Date(time);
					str += HexDump.intToByteString_one(date.getYear() + 1900 - 2000);
					str += HexDump.intToByteString_one(date.getMonth() + 1);
					str += HexDump.intToByteString_one(date.getDate());
					str += HexDump.intToByteString_one(date.getHours());
					str += HexDump.intToByteString_one(date.getMinutes());
					str += HexDump.intToByteString_one(transformerInfo.getConnectionGroup());
					System.out.println("变压器类型"+noloadInfo.getDetemineTransformerType());
					data = new DataModel();
					data.setRequestCode(DataModel.NOLOAD_SAVE_PRINT,  str);
				}
				if(data == null || data.getRequestCode() == null || data.getRequestCode().length < 1) {
					Toast.makeText(getActivity(), "当前没有可打印的信息", Toast.LENGTH_SHORT).show();
				}else {
					//SocketClient.isTesting = true;
					SocketClient.write(data);
				}
				
			}
		}
		
	}
	

	public class TransformerAdapter extends BaseAdapter {
		public List<ViewHolder> mHolderList = new ArrayList<ViewHolder>();

		int id_row_layout;
		LayoutInflater mInflater;

		public TransformerAdapter(Context context, int id_row_layout) {
			super();
			this.id_row_layout = id_row_layout;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (transformers == null)
				return 0;
			return transformers.size();
		}

		@Override
		public Object getItem(int position) {
			return transformers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			ViewHolder holder = null;
			if (convertView == null) {
				
				convertView = mInflater.inflate(id_row_layout, null);
				holder = new ViewHolder();

				MyHScrollView scrollView1 = (MyHScrollView) convertView
						.findViewById(R.id.horizontalScrollView1);

				holder.scrollView = scrollView1;
				holder.id = (TextView) convertView
						.findViewById(R.id.textView1);
				//holder.id.setTextSize(R.dimen.text_size_18);
				holder.transformerCode = (TextView) convertView
						.findViewById(R.id.textView2);
				// holder.transformerCode.setTextSize(R.dimen.text_size_18);
				holder.username = (TextView) convertView
						.findViewById(R.id.textView3);
				//holder.username.setTextSize(R.dimen.text_size_18);
				holder.userAddress = (TextView) convertView
						.findViewById(R.id.textView4);
				//holder.userAddress.setTextSize(R.dimen.text_size_18);
				holder.testUser = (TextView) convertView.findViewById(R.id.textView6);
				//holder.testUser.setTextSize(R.dimen.text_size_18);
				holder.testTime = (TextView) convertView.findViewById(R.id.textView7);
				//holder.testTime.setTextSize(R.dimen.text_size_18);
				MyHScrollView headSrcrollView = (MyHScrollView) mHead
						.findViewById(R.id.horizontalScrollView1);
				headSrcrollView
						.AddOnScrollChangedListener(new OnScrollChangedListenerImp(
								scrollView1));

				convertView.setTag(holder);
				mHolderList.add(holder);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (transformers != null && transformers.size() > 0) {
				TransformerInfo detail = transformers.get(position);
				holder.id.setText((detail.getTransformerId()) + "");
				holder.id.setTag(detail);
				holder.transformerCode.setText(detail.getTransformerCode());
				holder.username.setText(detail.getUserName());
				holder.userAddress.setText(detail.getUserAddress());
				holder.testUser.setText(detail.getTestUser());
				Date date = new Date(detail.getUpdateTime());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				holder.testTime.setText(format.format(date));
				
			}
				convertView.setBackgroundColor(colors[position % 2]); // 颜色设置
			return convertView;
		}

		class OnScrollChangedListenerImp implements OnScrollChangedListener {
			MyHScrollView mScrollViewArg;

			public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
				mScrollViewArg = scrollViewar;
			}

			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				mScrollViewArg.smoothScrollTo(l, t);
			}
		};

		class ViewHolder {
			TextView id;
			TextView transformerCode;
			TextView username;
			TextView userAddress;
			TextView testUser;
			TextView testTime;
			HorizontalScrollView scrollView;
		}
	}
	

	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
					.findViewById(R.id.horizontalScrollView1);
			headSrcrollView.onTouchEvent(arg1);
			return false;
		}
	}
	
	private void setTabValue(String tabName) {
		if(tabName.equals("容量测试")){
			// capacityFragment.hideResult(View.VISIBLE);
			List<CapacityResultInfo> capacities = capacityDB.searchCapacity(" transformerId=? ", new String[]{selectedTransformerId + ""}, null, null, null);
			if(capacities != null && capacities.size() > 0) {
				capacityInfo = capacities.get(0);
				capacityInfo.setFinishTest(true);
				capacityFragment.setValue(capacityInfo);
				// setValueCapacity();
			}else {
				capacityFragment.initData();
			}
		}else if(tabName.equals("负载测试")){
			List<LoadResultInfo> loads = loadDB.searchLoad(" transformerId=? ", new String[]{selectedTransformerId + ""}, null, null, null);
			if(loads != null && loads.size() > 0) {
				loadInfo = loads.get(0);
				loadFragment.setValue(loadInfo);
				// setValueLoad();
			}else {
				// 清空数据
				loadFragment.initData();
			}
		}else if(tabName.equals("空载测试")){
			
			List<NoloadResultInfo> noloads = noloadDB.searchNoload(" transformerId=? ", new String[]{selectedTransformerId + ""}, null, null, null);
			if(noloads != null && noloads.size() > 0) {
				noloadInfo = noloads.get(0);
				//noloadFragment.setValue(noloadInfo, 2);
				noloadFragment.setValue1(noloadInfo);
				// setValueNoload();
			}else {
				noloadFragment.initData();
			}
		}else if(tabName.equals("参数设置")) {
			if(transformerInfo != null) paramFragment.setValue(transformerInfo);
		}
	}
	
	/**
	 * 初始化TAB页签
	 */
	private void initTab() {
		if(tabHost != null) return;
		tabHost = (TabHost)containerFather.findViewById(R.id.dataDesc_managerAll);
		tabHost.setup();
		String titles[] = {"参数设置", "容量测试", "负载测试", "空载测试"};
		int ids[] = {R.id.transformer_detail, R.id.capacity_loadAll, R.id.load_loadAll, R.id.noload_loadAll};
		for(int i = 0; i < titles.length; i++) {
			tabHost.addTab(tabHost.newTabSpec(titles[i]).setIndicator(getTabView(i, titles[i]))
					.setContent(ids[i]));
			tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.btn_green_config);
		}
		tabName = "参数设置";
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				tabName = tabId;
				setTabValue(tabId);
			}
		});
	}
	
	private View getTabView(int index, String title){
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.inner_tab_item, null);
		TextView btn = (TextView)view.findViewById(R.id.innertabButton);
		btn.setTag(index);
		btn.setText(title);
		return view;
	}
	
}
