package com.lxdz.capacity.socket;

import com.lxdz.capacity.model.DataModel;

/**
 * 数据分发
 * @author liuniu
 *
 */
public interface DataDistribute {
	public abstract void updateReceivedData(DataModel data) ;
}
