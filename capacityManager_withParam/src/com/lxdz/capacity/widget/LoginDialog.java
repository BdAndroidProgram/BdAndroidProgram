package com.lxdz.capacity.widget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lxdz.capacity.R;
import com.lxdz.capacity.activity.IndexActivity;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.util.CacheManager;

public class LoginDialog extends Dialog{
	
	private Button positive;
	private Button negative;
	private EditText password;
	private Spinner testUser;
	private TextView message;
	
	//是否展示断开三相电源的提示框
	// public static boolean isShowBreakDialog = true;
	
	private Context context;

	public LoginDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.password_confirm);
		init();
	}
	
	public void init(){
		testUser = (Spinner)findViewById(R.id.testUser_confirm);
		setTitle("登陆提示");
		String[] users = {"测试员"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.adapter_item, users);
		testUser.setAdapter(adapter);
		password = (EditText)findViewById(R.id.password_confirm);
		message = (TextView)findViewById(R.id.message_confirm);
		password.setOnKeyListener(new View.OnKeyListener(){

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				message.setVisibility(View.GONE);
				return false;
			}
			
		});
		
		positive = (Button)findViewById(R.id.login_confirm);
		positive.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				if(testUser.getSelectedItemPosition() == 0) {
					String str = password.getText().toString().trim();
					if("bdlx1111".equals(str) ) {
						CacheManager.debugPassword = "bdlx1111";
							
						if(CacheManager.currentTest == -1 || CacheManager.currentTest != DataModel.NOLOAD_COMMAND_INT){
					
						
						IndexActivity.openPageProgress = ProgressDialog.show(context, IndexActivity.CHANGE_PAGE, "设备调试");
						Intent intent = new Intent(context, IndexActivity.class);
						IndexActivity.defaultSelected = 4;
						IndexActivity.preClickTab = 4;
						context.startActivity(intent);
						
						}else {
							IndexActivity.openPageProgress = ProgressDialog.show(context, IndexActivity.CHANGE_PAGE, "设备调试");
							Intent intent = new Intent(context, IndexActivity.class);
							IndexActivity.defaultSelected = 4;
							IndexActivity.preClickTab = 4;
							context.startActivity(intent);
						}
						CacheManager.currentTest = DataModel.NOLOAD_COMMAND_INT;
						dismiss();
					}else {
						//Toast.makeText(context, "密码输入错误，请重新输入", Toast.LENGTH_SHORT).show();
						message.setText("密码输入错误，请重新输入");
						message.setVisibility(View.VISIBLE);
					}
				}
				
			}});
		negative = (Button)findViewById(R.id.cancel_confirm);
		negative.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				cancel();
				
			}});
	}
	
	

}
