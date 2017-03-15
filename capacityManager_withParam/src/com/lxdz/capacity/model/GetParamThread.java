package com.lxdz.capacity.model;

import com.lxdz.capacity.socket.SocketClient;

/**
 * 获取服务器参数线程
 * @author Administrator
 *
 */
public class GetParamThread extends Thread{
	
	public static boolean isReceiveParam = false;
	
	public void run() {
		while(!isReceiveParam) {
			DataModel param = new DataModel();
			param.setRequestCode(DataModel.GET_PARAM, "00", DataModel.NO_DATA);
			// SocketClient.isTesting = true;
			SocketClient.write(param);
			try {
				Thread.sleep(SocketClient.longCommandSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
