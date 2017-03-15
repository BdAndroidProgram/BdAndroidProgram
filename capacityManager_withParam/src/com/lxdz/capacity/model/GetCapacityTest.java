package com.lxdz.capacity.model;

import com.lxdz.capacity.socket.SocketClient;


public class GetCapacityTest extends Thread{
	public static boolean isTest = true;
	public static boolean isSendStart = false;
	
	
	@Override
	public void run() {
		while(isTest){
			System.out.println("容量数据测试中。。。");
			SocketClient.isTesting = true;
			if(!isSendStart) {
				DataModel start = new DataModel();
				start.setRequestCode(DataModel.CAPACITY_COMMAND, "01", DataModel.NO_DATA);
				
				SocketClient.write(start);
				isSendStart = true;
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			DataModel model = new DataModel();
			model.setRequestCode(DataModel.CAPACITY_COMMAND, DataModel.NO_COMMAND, DataModel.NO_DATA);
			//SocketClient.isTesting = true;
			SocketClient.write(model);
			try {
				Thread.sleep(SocketClient.longCommandSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
