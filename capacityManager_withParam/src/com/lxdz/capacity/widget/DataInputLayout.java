package com.lxdz.capacity.widget;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lxdz.capacity.R;

public class DataInputLayout extends LinearLayout {
	
	private DatePickerDialog dateDialog;
	
	private EditText dateText;
	private Button dateBtn;
	
	private Context context;
	
	// 包含控件的窗口
	private View container;

	public DataInputLayout(Context context) {
		
		super(context);
		this.context = context;
	}
	public DataInputLayout(Context context, AttributeSet attr) {
		super(context, attr);
		this.context = context;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		container = li.inflate(R.layout.data_input, this, true);
		init();
	}
	
	private void init() {
		if(container == null) return;
		final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month,
					int dayOfMonth) {

				dateText.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
			}
		};
		dateText = (EditText)container.findViewById(R.id.dataText);
		dateBtn = (Button)container.findViewById(R.id.dataBtn);
		dateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				dateDialog = new DatePickerDialog(context,
						dateListener, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar
								.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();
				
			}
		});
		

		/*dateText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				dateDialog = new DatePickerDialog(context,
						dateListener, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar
								.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();
			}
		});*/
	}
	public EditText getDateText() {
		return dateText;
	}
	public void setDateText(EditText dateText) {
		this.dateText = dateText;
	}
	public Button getDateBtn() {
		return dateBtn;
	}
	public void setDateBtn(Button dateBtn) {
		this.dateBtn = dateBtn;
	}
	
	
	

}
