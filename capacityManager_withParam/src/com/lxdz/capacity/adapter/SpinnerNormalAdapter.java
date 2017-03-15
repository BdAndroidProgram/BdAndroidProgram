package com.lxdz.capacity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerNormalAdapter<T> extends ArrayAdapter<T>{
	    Context context;  
	    T[] items ;  
	    List<T> objItems;
	    boolean isList = false;
	    
	    @Override
	    public T getItem(int position) {
	    	T obj = null;
	    if(isList) {
	    	obj = objItems.get(position);
	    }else {
	    	obj = items[position];
	    }
	    return obj;
	    }
	    
	    public List<T> getDataList() {
	    	return objItems;
	    }
	  
	   public SpinnerNormalAdapter(final Context context,  
	            final int textViewResourceId, final T[] objects) {  
		   
	        super(context, textViewResourceId, objects);  
	        this.isList = false;
	        this.items = objects;  
	        this.context = context;  
	    }  
	  
	   
	   public SpinnerNormalAdapter(Context context, int textViewResourceId, List<T> objects){
		   super(context, textViewResourceId, objects);  
		   this.isList = true;
	        this.objItems = objects;  
	        this.context = context;  
		 }
	   
	   
	  
	    @Override  
	    public View getDropDownView(int position, View convertView,  
	            ViewGroup parent) {  
	  
	    	if((items == null || items.length < 1) && (objItems == null || objItems.size() < 1)) return null;
	        if (convertView == null) {  
	            LayoutInflater inflater = LayoutInflater.from(context);  
	            convertView = inflater.inflate(  
	                    android.R.layout.simple_spinner_item, parent, false);  
	        }  
	  
	        TextView tv = (TextView) convertView  
	                .findViewById(android.R.id.text1);  
	        if(isList) {
	        	tv.setText(objItems.get(position).toString()); 
	        	tv.setTag(objItems.get(position));
	        } else {
	        	tv.setText(items[position].toString()); 
	        	tv.setTag(items[position]);
	        }
	        
	        //tv.setHeight(25);
	       tv.setPadding(3, 8, 3, 8);
	        tv.setTextSize(22);  
	        return convertView;  
	    }  
	  
	    @Override  
	    public View getView(int position, View convertView, ViewGroup parent) {  
	    	if((items == null || items.length < 1) && (objItems == null || objItems.size() < 1)) return null;
	        if (convertView == null) {  
	            LayoutInflater inflater = LayoutInflater.from(context);  
	            convertView = inflater.inflate(  
	                    android.R.layout.simple_spinner_item, parent, false);  
	        }  
	  
	        TextView tv = (TextView) convertView  
	                .findViewById(android.R.id.text1);  
	        if(isList) {
	        	tv.setText(objItems.get(position).toString()); 
	        	tv.setTag(objItems.get(position));
	        } else {
	        	tv.setText(items[position].toString()); 
	        	tv.setTag(items[position]);
	        }
	        
	       
	        tv.setTextSize(22);  
	        return convertView;  
	    }  
	}


