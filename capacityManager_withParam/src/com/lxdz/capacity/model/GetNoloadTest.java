package com.lxdz.capacity.model;

import android.widget.Toast;

import com.lxdz.capacity.socket.SocketClient;

public class GetNoloadTest extends Thread {

	public static boolean loadResult = true;
	
	public static boolean isSingle = false;
	
	private DataModel sendData;

	public void run() {
		// if(isSingle) {
			while (loadResult) {
				DataModel param = new DataModel();
				
				param.setRequestCode(DataModel.NOLOAD_COMMAND,
						DataModel.NO_COMMAND, DataModel.NO_DATA);
				SocketClient.isTesting = true;
				SocketClient.write(param);
				try {
					Thread.sleep(SocketClient.longCommandSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		/*}else {
			SocketClient.write(sendData);
			try {
				Thread.sleep(SocketClient.shortCommandSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		
	}

	public DataModel getSendData() {
		return sendData;
	}

	public void setSendData(DataModel sendData) {
		this.sendData = sendData;
	}
}
