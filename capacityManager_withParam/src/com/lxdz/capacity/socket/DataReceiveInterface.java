package com.lxdz.capacity.socket;

/**
 * 所有UI界面都需要实现此接口，当有数据接收完成后，根据此方法，更新界面数据
 * @author Administrator
 *
 */
public interface DataReceiveInterface {
	
	
	public void updataUI(byte[] data);

}
