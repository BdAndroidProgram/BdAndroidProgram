package com.lxdz.capacity.model;

import com.lxdz.capacity.socket.SocketClient;

public class GetLoadTest extends Thread {

	public static boolean loadResult = true;
	
	public void run() {
			while (loadResult) {
				DataModel param = new DataModel();

				param.setRequestCode(DataModel.LOAD_COMMAND,
						DataModel.NO_COMMAND, DataModel.NO_DATA);
				SocketClient.isTesting = true;
				SocketClient.write(param);
				try {
					Thread.sleep(SocketClient.longCommandSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		
		
	}

	
}
