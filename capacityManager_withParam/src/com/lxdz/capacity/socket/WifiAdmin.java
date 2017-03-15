package com.lxdz.capacity.socket;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.lxdz.capacity.util.CacheManager;

public class WifiAdmin { 
	private static final String TAG = WifiAdmin.class.getSimpleName();
	
    // 定义WifiManager对象  
    private WifiManager mWifiManager; 
    // 定义WifiInfo对象  
    private WifiInfo mWifiInfo; 
    // 扫描出的网络连接列表  
    private List<ScanResult> mWifiList; 
    // 网络连接列表  
 // private List<WifiConfiguration> mWifiConfiguration; 
    // 定义�?��WifiLock  
    WifiLock mWifiLock; 
    
    public static String wifiSSID ;
	
	public static String wifiPassword ;	
    
    /**
     * 没有加密
     */
    public static final int WIFI_NO_PASSWORD = 1;
    
    /**
     * WEP 加密
     */
    public static final int WIFI_PASSWORD_WEP = 2;
    
    /**
     * WPA 加密 
     */
    public static final int WIFI_PASSWORD_WPA = 3;
    
    /**
     * WIFI断开连接
     */
	public static final int DISCONNECT_WIFI = 4;
	
	/**
	 * 检查是否有连接着的WIFI
	 */
	public static final int EXIST_CONNECTED_WIFI = 5;
	
	/**
	 * WIFI是否连接
	 */
	public static  boolean isConnected = false;

 
    // 构�?�? 
    public WifiAdmin(Context context) { 
        // 取得WifiManager对象  
        mWifiManager = (WifiManager) context 
                .getSystemService(Context.WIFI_SERVICE); 
        // 取得WifiInfo对象  
        mWifiInfo = mWifiManager.getConnectionInfo(); 
        wifiSSID = CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_NAME, "cy305001");
        wifiPassword = CacheManager.getString( CacheManager.PARAM_CONFIG, CacheManager.WIFI_PASSWORD, "87654321");
    } 
 
    // 打开WIFI  
    public void openWifi() { 
        if (!mWifiManager.isWifiEnabled()) { 
            mWifiManager.setWifiEnabled(true); 
        } 
    } 
 
    // 关闭WIFI  
    public void closeWifi() { 
        if (mWifiManager.isWifiEnabled()) { 
            mWifiManager.setWifiEnabled(false); 
        } 
    } 
 
    // �?��当前WIFI状�?  
    public int checkState() { 
        return mWifiManager.getWifiState(); 
    } 
 
    // 锁定WifiLock  
    public void acquireWifiLock() { 
        mWifiLock.acquire(); 
    } 
 
    // 解锁WifiLock  
    public void releaseWifiLock() { 
        // 判断时�?锁定  
        if (mWifiLock.isHeld()) { 
            mWifiLock.acquire(); 
        } 
    } 
 
    // 创建�?��WifiLock  
    public void creatWifiLock() { 
        mWifiLock = mWifiManager.createWifiLock("Test"); 
    } 
 
    // 得到配置好的网络  
    /*public List<WifiConfiguration> getConfiguration() { 
        return mWifiConfiguration; 
    } */
 
    
 
    public void startScan() { 
        mWifiManager.startScan(); 
        // 得到扫描结果  
        mWifiList = mWifiManager.getScanResults(); 
        // 得到配置好的网络连接  
        // mWifiConfiguration = mWifiManager.getConfiguredNetworks(); 
    } 
 
    // 得到网络列表  
    public List<ScanResult> getWifiList() { 
        return mWifiList; 
    } 
 
    // 查看扫描结果  
    /*public StringBuilder lookUpScan() { 
        StringBuilder stringBuilder = new StringBuilder(); 
        for (int i = 0; i < mWifiList.size(); i++) { 
            stringBuilder 
                    .append("Index_" + new Integer(i + 1).toString() + ":"); 
            // 将ScanResult信息转换成一个字符串�? 
            // 其中把包括：BSSID、SSID、capabilities、frequency、level  
            stringBuilder.append((mWifiList.get(i)).toString()); 
            stringBuilder.append("/n"); 
        } 
        return stringBuilder; 
    }*/
 
/*    // 得到MAC地址  
    public String getMacAddress() { 
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress(); 
    } 
 
    // 得到接入点的BSSID  
    public String getBSSID() { 
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID(); 
    } 
    
    *//**
     * 得到SSID
     * @return
     *//*
    public String getSSID() {
    	return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID(); 
    }
 
    // 得到IP地址  
    public int getIPAddress() { 
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress(); 
    } 
 
    // 得到连接的ID  
    public int getNetworkId() { 
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId(); 
    } 
 
    // 得到WifiInfo的所有信息包  
    public String getWifiInfo() { 
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString(); 
    } */
 
    // 添加�?��网络并连�? 
    public int addNetwork(WifiConfiguration wcg) { 
	 int wcgID = mWifiManager.addNetwork(wcg); 
     boolean b =  mWifiManager.enableNetwork(wcgID, true); 
     System.out.println("a--" + wcgID);
     System.out.println("b--" + b);
     return wcgID;
    } 
 
    // 断开指定ID的网�? 
    public void disconnectWifi(int netId) { 
        mWifiManager.disableNetwork(netId); 
        mWifiManager.disconnect(); 
    } 
    
    /**
     * 连接到指定的网络
     * @param netId
     */
    public void connectWifi(int netId) {
    	mWifiManager.enableNetwork(netId, true);
    }
 
    //然后是一个实际应用方法，只验证过没有密码的情况：
 
    /**
     * 分为三种情况�?没有密码2用wep加密3用wpa加密
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) 
    { 
          WifiConfiguration config = new WifiConfiguration();   
           config.allowedAuthAlgorithms.clear(); 
           config.allowedGroupCiphers.clear(); 
           config.allowedKeyManagement.clear(); 
           config.allowedPairwiseCiphers.clear(); 
           config.allowedProtocols.clear(); 
          config.SSID = "\"" + SSID + "\"";   
          
          WifiConfiguration tempConfig = this.isExistWifiConfig(SSID);           
          if(tempConfig != null) {  
        	  mWifiManager.removeNetwork(tempConfig.networkId);  
          }
          
          if(Type == 1) //WIFICIPHER_NOPASS
          { 
               config.wepKeys[0] = ""; 
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
               config.wepTxKeyIndex = 0; 
          } 
          if(Type == 2) //WIFICIPHER_WEP
          { 
              config.hiddenSSID = true;
              config.wepKeys[0]= "\""+Password+"\""; 
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40); 
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104); 
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
              config.wepTxKeyIndex = 0; 
          } 
          if(Type == 3) //WIFICIPHER_WPA
          { 
          config.preSharedKey = "\""+Password+"\""; 
          config.hiddenSSID = true;   
          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);   
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                         
          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                         
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                    
          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
          config.status = WifiConfiguration.Status.ENABLED;   
          }
           return config; 
    } 
    
    /**
     * 是否存在指定的SSID，的WIFIConfig
     * @param SSID
     * @return
     */
    public WifiConfiguration isExistWifiConfig(String SSID)  
    {  
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();  
           for (WifiConfiguration existingConfig : existingConfigs)   
           {  
             if (existingConfig.SSID.equals("\""+SSID+"\""))  
             {  
                 return existingConfig;  
             }  
           }  
        return null;   
    }
    
    /**
     * 是否能扫描到指定的WIFI
     * @param SSID
     * @return
     */
    public ScanResult isExistWifi(String SSID) {
    	// if(this.mWifiList == null || this.mWifiList.size() == 0)
    	openWifi();
    		this.startScan();
    	for(ScanResult scan : this.mWifiList) {
    		
    		if(scan.SSID.equals(SSID)) return scan;
    	}
    	return null;
    }
    
    

	public WifiInfo getmWifiInfo() {
		mWifiInfo = mWifiManager.getConnectionInfo(); 
		return mWifiInfo;
	}

	public void setmWifiInfo(WifiInfo mWifiInfo) {
		this.mWifiInfo = mWifiInfo;
	}

	public WifiManager getmWifiManager() {
		return mWifiManager;
	}

	public void setmWifiManager(WifiManager mWifiManager) {
		this.mWifiManager = mWifiManager;
	}
  
}
//