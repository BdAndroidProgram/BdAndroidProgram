package com.lxdz.capacity.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxdz.capacity.R;

/**
 * 带单位的文本输入框
 * @author Administrator
 *
 */
public class UnitView extends LinearLayout {
	
	private String unitStr;
	private EditText dataText;
	private TextView unitText;
	private int textColor;
	private boolean enabled;
	
	/**
	 * 输入类型
	 */
	private String inputType;
	
	// 改变键盘的样式，比较增加发送按钮，或者下一项
	private String imeOption;
	public UnitView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public UnitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.unit);
		unitStr = typedArray.getString(R.styleable.unit_unitStr);
		textColor = typedArray.getColor(R.styleable.unit_textColor, Color.BLACK);
		inputType = typedArray.getString(R.styleable.unit_inputType);
		
		enabled = typedArray.getBoolean(R.styleable.unit_enabled, true);
		
		imeOption = typedArray.getString(R.styleable.unit_imeOption);
		
		/*textHintLabel = typedArray
				.getString(R.styleable.SelfImageTextAttr_text_hint_label);
		image_id = typedArray.getResourceId(
				R.styleable.SelfImageTextAttr_image_id, 0);
		edttext_inputtype = typedArray
				.getString(R.styleable.SelfImageTextAttr_edttext_inputtype);
		label_width = Math.round(typedArray.getDimension(
				R.styleable.SelfImageTextAttr_label_width, 0));
		defaultText = typedArray
				.getString(R.styleable.SelfImageTextAttr_default_text);
		String txtReadOnly = typedArray
				.getString(R.styleable.SelfImageTextAttr_text_readonly);
		if (txtReadOnly != null && txtReadOnly.equals("true")) {// comment by
																// danielinbiti
			isReadOnly = false;
		}*/
		this.init(context);
		typedArray.recycle();
	}
	
	public void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.edit_with_unit, this, true);
		unitText = (TextView)view.findViewById(R.id.dataUnit);
		dataText = (EditText)view.findViewById(R.id.dataValue);
		dataText.setTextColor(textColor);
		unitText.setText(unitStr);
		dataText.setEnabled(enabled);		
		if("number".equals(inputType)) {
			dataText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
			
		}else if("decimal".equals(inputType)) {
			int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
			dataText.setRawInputType(inputType);
		}
		
		if(null == imeOption){
			dataText.setImeOptions(0);
		}else if("actionNext".endsWith(imeOption)) {
			dataText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		} else if("actionSend".equals(imeOption)) {
			dataText.setImeOptions(EditorInfo.IME_ACTION_SEND);
		}
	}

	public String getUnitStr() {
		return unitStr;
	}

	public void setUnitStr(String unitStr) {
		this.unitStr = unitStr;
	}

	public EditText getDataText() {
		return dataText;
	}

	public void setDataText(EditText dataText) {
		this.dataText = dataText;
	}

	public TextView getUnitText() {
		return unitText;
	}

	public void setUnitText(TextView unitText) {
		this.unitText = unitText;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

}
