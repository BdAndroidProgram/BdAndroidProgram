package com.lxdz.capacity.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lxdz.capacity.R;

public class ConfirmDialog extends Dialog{
	
	private TextView message;
	private Button confirm;

	public ConfirmDialog(Context context) {
		super(context);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.confirm_dialog);
		init();
	}
	
	public void init() {
		setTitle("网络提示");
		message = (TextView)findViewById(R.id.messageShow_dialog);
		confirm = (Button)findViewById(R.id.confirmBtn_dialog);
		confirm.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		} );
	}
	
	public void setShowMessage(String info) {
		message.setText(info);
	}

}
