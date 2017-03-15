package com.lxdz.capacity.socket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lxdz.capacity.activity.BaseActivity;
import com.lxdz.capacity.activity.MainActivity;
import com.lxdz.capacity.fragment.BaseFragment;
import com.lxdz.capacity.model.DataModel;
import com.lxdz.capacity.model.GetParamThread;
import com.lxdz.capacity.util.CacheManager;
import com.lxdz.capacity.util.HexDump;

/**
 * socket连接客户端，主要任务 1. 接收数据 2. 发送数据 3. 连接服务器 4. 断开后重新连接
 * 
 * @author Administrator
 * 
 */
public class SocketClient extends Service {
	
	public static BaseFragment currentPage;

	private static final String TAG = SocketClient.class.getSimpleName();

	public static final String ON_NEW_DATA = "com.lxdz.capacity.socket.OnNewdata";


	public static final int longCommandSleep = 2000;

	public static final int shortCommandSleep = 100;

	/**
	 * 参数接收y广播
	 */
	public static final String ON_PARAM_RECEIVE = "com.lxdz.capacity.socket.OnParamReceive";

	public static final String COMMAND_TYPE = "commandType";

	public static final String NEW_DATA = "newData";


	// 接收数据缓存
	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();

	private String serverIp = "192.168.1.44";

	private int port = 25000;

	private Socket clientSocket;

	private static WifiAdmin wifi;

	/**
	 * 是否连接着服务 器
	 */
	public static boolean isConnectedServer = false;

	/**
	 * 向服务器发送数据
	 */
	private static DataOutputStream out;

	/**
	 * 接收服务器数据
	 */
	private DataInputStream in;


	private static OverTimeCheck overTimeCheck;
	
	private  WifiListener wifiListener;
	public static SendHeartPackage sendPackage;

	// wifi是否打开过滤器
	IntentFilter wifiIsOpen = new IntentFilter(
			WifiManager.WIFI_STATE_CHANGED_ACTION);

	// wifi是否连接过滤器
	IntentFilter wifiConnected = new IntentFilter(
			ConnectivityManager.CONNECTIVITY_ACTION);

	
	public static boolean isWifiCorrect = false;
	
	public static boolean isTesting = false;

	private Object lock = new Object();
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 连接WIFI
		wifi = new WifiAdmin(this);
		Log.i(TAG, "service 进入onCreate");
		registerReceiver(receiver, wifiIsOpen);
		registerReceiver(wifiIsConnected, wifiConnected);
		wifiListener = new WifiListener();
		wifiListener.start();
		
		// 心跳包控制程序
		sendPackage = new SendHeartPackage();
		sendPackage.start();
		// isOverTimeCheck = true;
		timeCounter = 0;
		overTimeCheck = new OverTimeCheck();
		overTimeCheck.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// wifiHandler.sendEmptyMessage(WifiAdmin.EXIST_CONNECTED_WIFI);
		
		Log.i(TAG, "service 进入onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	

	/**
	 * 向服务器发送数据
	 * 
	 * @param data
	 */
	public static boolean write(DataModel data) {
		if (out == null || data == null){
			Log.i(TAG,"数据输出流已经关闭" );
			if(wifi.getmWifiInfo() != null) {
				wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
			}
			return false;
		}
			
		try {
			Log.i(TAG, new Date().getTime() + "");
			out.write(data.getRequestCode());
			out.flush();
			
			Log.i(TAG,
					"数据发送成功" + HexDump.BytesToHexString(data.getRequestCode()));
			if(overTimeCheck == null || !overTimeCheck.isAlive()) {
				overTimeCheck = new OverTimeCheck();
				overTimeCheck.start();
			}
			return true;
		} catch (IOException e) {
			Log.i(TAG, "发送数据失败" + data.getRequestCode());
			System.out.println("发送数据失败");
			e.printStackTrace();
			return false;
		}
	}

	// 是否存在包头，包头+命令码
	private static boolean existPackageStart = false;
	
	/**
	 * 数据接收线程
	 * 
	 * @author Administrator
	 * 
	 */
	class Read implements Runnable {

		/**
		 * 发送数据接收完成消息
		 */
		public void sendReceiveFinish(int type) {
			Intent intent = new Intent(SocketClient.ON_NEW_DATA);
			intent.putExtra(COMMAND_TYPE, type);
			intent.putExtra(NEW_DATA, outStream.toByteArray());
			sendBroadcast(intent);
		}

		public boolean isRigntData(byte[] data, int length, String srcCheck) {
			byte[] temp = new byte[length - 1];
			System.arraycopy(data, 0, temp, 0, length - 1);
			String checkNum = HexDump.getCheckNumberReverse(temp, length - 1);
			System.out.println("当前检验位：" + checkNum);
			return checkNum.equals(srcCheck);
		}

		@Override
		public void run() {
			
			while (isConnectedServer) {
				if(in == null ){
					isConnectedServer = false;
					Log.i(TAG, "接收数据流已经关闭");
					return;
				}
				int command = 0;

				byte[] buffer = new byte[1024];
				int readLen = 0;
				try {
					synchronized (lock) {
						if(in != null) {
							readLen = in.read(buffer);
						}
						if(readLen < 1) continue;
					}
						
					System.out.println("超时计数已经清零。。。。");
					timeCounter = 0;
					int index = -1;
					// 检查是否存在包头和主命令
					if (!existPackageStart) {
						for (int i = 0; i < readLen; i++) {
							if (buffer[i] == DataModel.START_INT) {
								existPackageStart = true;
								index = i;
								break;
							}
						}
					}

					// 理想情况
					if (existPackageStart && index < readLen && index != -1) {
						command = buffer[index + 1];
					}
					int len = readLen - index;
					switch (command) {
					// 容量测试返回数据
					case DataModel.CAPACITY_COMMAND_INT:
						// 1. 判断数据是否接收完成
						if (len >= 65) {

							outStream.write(buffer, index, 65);
							Log.i(TAG, HexDump.toHexString(outStream
									.toByteArray()));
							// 对接收的数据进行验证
							if (isRigntData(buffer, 65,
									HexDump.toHexString(buffer[65 - 1]))) {
								/*cacheManager.setCapacityData(outStream
										.toByteArray());*/
								Log.i(TAG, "容量数据接收成功");
								sendReceiveFinish(DataModel.CAPACITY_COMMAND_INT);
							} else {
								Log.i(TAG, "容量数据接收失败，校验位不正确");
							}
							outStream.reset();
							existPackageStart = false;

						} else {
							// 假设一次能接收完所有数据
							outStream.reset();
							existPackageStart = false;
							continue;
						}
						break;
					case DataModel.LOAD_COMMAND_INT:
						if (len >= 48) {
							
							outStream.write(buffer, index, 48);
							Log.i(TAG, HexDump.toHexString(outStream
									.toByteArray()));
							// 对接收的数据进行验证
							if (isRigntData(buffer, 48,
									HexDump.toHexString(buffer[48 - 1]))) {
								
								sendReceiveFinish(DataModel.LOAD_COMMAND_INT);
							} else {
								Log.i(TAG, "负载数据接收失败，校验位不正确");
							}
							outStream.reset();
							existPackageStart = false;

						} else {
							// 假设一次能接收完所有数据
							outStream.reset();
							existPackageStart = false;
							continue;
						}
						break;
					case DataModel.NOLOAD_COMMAND_INT:
						if (len >= 56) {
							
							outStream.write(buffer, index, 56);
							Log.i(TAG, HexDump.toHexString(outStream
									.toByteArray()));
							// 对接收的数据进行验证
							if (isRigntData(buffer, 56,
									HexDump.toHexString(buffer[56 - 1]))) {
								/*cacheManager.setNoloadData(outStream
										.toByteArray());*/
								sendReceiveFinish(DataModel.NOLOAD_COMMAND_INT);
							} else {
								Log.i(TAG, "空载数据接收失败，校验位不正确");
							}
							outStream.reset();
							existPackageStart = false;

						} else {
							// 假设一次能接收完所有数据
							outStream.reset();
							existPackageStart = false;
							continue;
						}
						break;
					case DataModel.GET_PARAM_INT:
						if (len >= 33) {
							outStream.write(buffer, index, 33);
							Log.i(TAG, HexDump.toHexString(outStream
									.toByteArray()));
							if (isRigntData(buffer, 33,
									HexDump.toHexString(buffer[33 - 1]))) {
								CacheManager.params = outStream.toByteArray();
								GetParamThread.isReceiveParam = true;

								Log.i(TAG, "参数数据接收成功");
								BaseActivity.paramSetHandler.sendEmptyMessage(0);
							} else {
								Log.i(TAG, "参数数据接收失败，校验位不正确");
							}
							outStream.reset();
							existPackageStart = false;

						} else {
							outStream.write(buffer, index, 33 + len);
							continue;
						}
						break;
					case DataModel.DEBUG_CAPACITY_COEFFICIENT_INT:
						if (len >= 30) {
							outStream.write(buffer, index, 30);
							Log.i(TAG, "容量调试系数" + HexDump.toHexString(outStream
									.toByteArray()));
							// 对接收的数据进行验证
							if (isRigntData(buffer, 30,
									HexDump.toHexString(buffer[30 - 1]))) {
								sendReceiveFinish(DataModel.DEBUG_CAPACITY_COEFFICIENT_INT);
							} else {
								Log.i(TAG, "容量调试系数接收失败，校验位不正确");
							}
							outStream.reset();
							existPackageStart = false;

						} else {
							outStream.reset();
							existPackageStart = false;
							continue;
						}
						break;
					case DataModel.DEBUG_NOLOAD_COEFFICIENT_INT:
						if (len >= 30) {
							
							outStream.write(buffer, index, 30);
							Log.i(TAG, "空载调试系数" + HexDump.toHexString(outStream
									.toByteArray()));
							// 对接收的数据进行验证
							if (isRigntData(buffer, 30,
									HexDump.toHexString(buffer[30 - 1]))) {
								sendReceiveFinish(DataModel.DEBUG_NOLOAD_COEFFICIENT_INT);
							} else {
								Log.i(TAG, "空载调试系数接收失败，校验位不正确");
							}
							outStream.reset();
							existPackageStart = false;

						} else {
							outStream.reset();
							existPackageStart = false;
							continue;
						}
						break;
					case DataModel.GET_PARAMTEST_INT:
						if (len >= 94) {
							outStream.write(buffer, index, 94);
							Log.i(TAG, "参数测量" + HexDump.toHexString(outStream
									.toByteArray()));
							// 对接收的数据进行验证
							if (isRigntData(buffer, 94,
									HexDump.toHexString(buffer[94 - 1]))) {
								sendReceiveFinish(DataModel.GET_PARAMTEST_INT);
							} else {
								Log.i(TAG, "参数测量接收失败，校验位不正确");
							}
							outStream.reset();
							existPackageStart = false;

						} else {
							outStream.reset();
							existPackageStart = false;
							continue;
						}
						break;
					}
					
					// 如果存在清空主命令
				} catch (IOException e) {
					e.printStackTrace();
					Log.i(TAG, "数据接收失败");
				}
			}
		}
	}

	/**
	 * 初始化客户端
	 */
	public void initClient() {
		if (clientSocket == null) {
			try {
				clientSocket = new Socket(serverIp, port);
				Log.i(TAG, "服务器连接成功");
				isConnectedServer = true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	

	/**
	 * 初始化发送流
	 */
	public void initOut() {
		if (out == null && clientSocket != null) {
			try {
				out = new DataOutputStream(clientSocket.getOutputStream());
				Log.i(TAG, "发送流初始化成功");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 初始化接收流
	 */
	public void initIn() {
		if (in == null && clientSocket != null) {
			try {
				in = new DataInputStream(clientSocket.getInputStream());
				Log.i(TAG, "接收数据初始化成功");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 关闭输入流
	 */
	public void closeIn() {
		synchronized (lock) {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					in = null;
				}
			}
		}
		
	}

	/**
	 * 关闭输出流
	 */
	public void closeOut() {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				out = null;
			}
		}
	}

	/**
	 * 关闭socket连接
	 */
	public void closeSocket() {
		if (clientSocket != null) {
			try {
				clientSocket.close();
				isConnectedServer = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				clientSocket = null;
			}
		}
	}

	/**
	 * WIFI连接处理
	 */
	public Handler wifiHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (wifi == null)
				wifi = new WifiAdmin(SocketClient.this);
			switch (msg.what) {
			case WifiAdmin.EXIST_CONNECTED_WIFI:
				// 判断WIFI是否连接，如果连接是否为指定的WIFI
				if (wifi.getmWifiInfo() == null
						|| wifi.getmWifiInfo().getNetworkId() == -1) {
					// 没有连接的WIFI
					wifiHandler.sendEmptyMessage(WifiAdmin.DISCONNECT_WIFI);
				} else {
					String connectedSSID = wifi.getmWifiInfo().getSSID();
					connectedSSID = connectedSSID.replace("\"", "");
					if (WifiAdmin.wifiSSID.length() == 0)
						break;
					if (connectedSSID.equals(WifiAdmin.wifiSSID)) {
						isConnectedServer = true;
						break;
					}
						
					ScanResult scanWifi = wifi.isExistWifi(WifiAdmin.wifiSSID);
					if (scanWifi == null) {
						Toast.makeText(SocketClient.this, "扫描不到指定的WIFI",
								Toast.LENGTH_SHORT).show();
						break;
					}
					// 如果连接的WIFI，不是指定的热点，断开连接，重新进行连接
					wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());

				}

				break;
			case WifiAdmin.DISCONNECT_WIFI:
				// 扫描WIFI热点
				wifi.startScan();
				if (WifiAdmin.wifiSSID.length() == 0)
					break;
				// 判断是否能扫描到指定的WIFI
				ScanResult scanWifi = wifi.isExistWifi(WifiAdmin.wifiSSID);
				if (scanWifi == null) {
					Toast.makeText(SocketClient.this, "扫描不到指定的WIFI",
							Toast.LENGTH_SHORT).show();
					break;
				}
				// 创建新的WIFIconfig
				WifiConfiguration config = wifi
						.isExistWifiConfig(WifiAdmin.wifiSSID);
				int networkId = -1;
				if (config == null) {
					config = wifi
							.createWifiInfo(WifiAdmin.wifiSSID,
									WifiAdmin.wifiPassword,
									WifiAdmin.WIFI_PASSWORD_WPA);
					networkId = wifi.addNetwork(config);
				}
				// 判断WIFI是否连接，如果连接是否为指定的WIFI
				if (wifi.getmWifiInfo() == null) {
					wifi.connectWifi(config.networkId);
				} else {
					String connectedSSID = wifi.getmWifiInfo().getSSID()
							.replace("\"", "");
					if (wifi.getmWifiInfo().getNetworkId() != -1) {
						if (connectedSSID.equals(WifiAdmin.wifiSSID)) {
							wifi.setmWifiInfo(wifi.getmWifiManager()
									.getConnectionInfo());
							break;
						}
						// 如果连接的WIFI，不是指定的热点，断开连接，重新进行连接
						wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
					}

					if (networkId != -1) {
						wifi.connectWifi(networkId);
					} else {
						wifi.connectWifi(config.networkId);
					}
				}

			}
		}
	};

	/**
	 * 用来监听WIFI连接的打开和关闭
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				switch (wifi.checkState()) {
				case WifiManager.WIFI_STATE_DISABLED:
					Log.i(TAG, "WIFI连接关闭");
					wifi.openWifi();
					break;
				case WifiManager.WIFI_STATE_DISABLING:
					Toast.makeText(SocketClient.this, "WIFI正在关闭",
							Toast.LENGTH_SHORT).show();
					closeIn();
					closeOut();
					closeSocket();
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					/*Toast.makeText(SocketClient.this, "WIFI正在打开",
							Toast.LENGTH_SHORT).show();*/
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					Toast.makeText(SocketClient.this, "WIFI打开成功",
							Toast.LENGTH_SHORT).show();
					wifiHandler
							.sendEmptyMessage(WifiAdmin.EXIST_CONNECTED_WIFI);
					break;
				}
			}
		}
	};

	private class ConnectServer implements Runnable {

		@Override
		public void run() {
			initClient();
			initIn();
			initOut();
			new Thread(new Read()).start();
			if(sendPackage == null || !sendPackage.isAlive()) {
				sendPackage = new SendHeartPackage();
				sendPackage.start();
			}
			if(overTimeCheck == null || !overTimeCheck.isAlive()) {
				overTimeCheck  = new OverTimeCheck();
				overTimeCheck.start();
			}
		}

	}

	/**
	 * 用来监听WIFI是否连接
	 */
	BroadcastReceiver wifiIsConnected = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				if (cm == null) {
					isConnectedServer = false;
					wifiHandler
							.sendEmptyMessage(WifiAdmin.EXIST_CONNECTED_WIFI);
					return;
				}
				NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (WifiAdmin.wifiSSID.length() == 0)
					return;
				// 如果连接WIFI
				if (networkInfo != null
						&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					
					// 如果连接的wifi和指定的wifi不一样，则重新进行连接
					String ssid = wifi.getmWifiInfo().getSSID()
							.replace("\"", "");
					if (!ssid.equals(WifiAdmin.wifiSSID)) {
						// wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
						wifiHandler
								.sendEmptyMessage(WifiAdmin.EXIST_CONNECTED_WIFI);
					} else {
						Toast.makeText(SocketClient.this, "WIFI连接成功",
								Toast.LENGTH_SHORT).show();
						Log.i(TAG, "WIFI连接成功");
						// WIFI连接后，连接服务器
						new Thread(new ConnectServer()).start();
						isWifiCorrect = true;
					}

				} else {
					// 如果wifi断开连接
					wifiHandler.sendEmptyMessage(WifiAdmin.DISCONNECT_WIFI);
					isConnectedServer = false;
					closeSocket();
					closeIn();
					closeOut();
					isWifiCorrect = false;
					Toast.makeText(SocketClient.this, "WIFI连接断开",
							Toast.LENGTH_SHORT).show();
					Log.i(TAG, "WIFI连接断开");
					if(currentPage != null){
						currentPage.initData();
						isTesting = false;
						
					} 
				}

			}
		}
	};

	class WifiListener extends Thread {
		@Override
		public void run() {
			while(true){
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null) {
				isConnectedServer = false;
				wifiHandler.sendEmptyMessage(WifiAdmin.EXIST_CONNECTED_WIFI);
				continue;
			}
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (WifiAdmin.wifiSSID.length() == 0) continue;
				
			// 如果连接WIFI
			if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				
				// 如果连接的wifi和指定的wifi不一样，则重新进行连接
				String ssid = wifi.getmWifiInfo().getSSID().replace("\"", "");
				if (!ssid.equals(WifiAdmin.wifiSSID)) {
					wifiHandler.sendEmptyMessage(WifiAdmin.EXIST_CONNECTED_WIFI);
				} else {
					if(clientSocket == null || !clientSocket.isConnected()) initClient();
					
					if(in == null ) initIn();
					
					if(out == null) initOut();
					
				}
			}else {
				wifiHandler.sendEmptyMessage(WifiAdmin.EXIST_CONNECTED_WIFI);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}

		}

	}
	private static int timeCounter = 0;
	 static class  OverTimeCheck extends Thread{
		@Override
		public void run() {
			while(isConnectedServer && (MainActivity.isMainPage || isTesting )){
				timeCounter++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(timeCounter > 100){
					timeCounter = 0;
					System.out.println("超时检测中");
					if(wifi.getmWifiInfo() != null) {
						wifi.disconnectWifi(wifi.getmWifiInfo().getNetworkId());
						
					}
					
				}
			}
		}
	}
	
	public boolean isSendHeartCode = true;
	GetParamThread getParam; 
	public class SendHeartPackage extends Thread{
		
		public void run() {
			while(isConnectedServer && MainActivity.isMainPage) {
				DataModel param = new DataModel();
				param.setRequestCode(DataModel.GET_PARAM, "00", DataModel.NO_DATA);
				SocketClient.isTesting = true;
				SocketClient.write(param);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}


	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
}
