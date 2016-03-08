package com.santiago.catbox.util.NetWork;

import android.util.Log;

import com.santiago.catbox.common.Constant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by test on 15/9/21.
 */
public class ConnectionIPWeight {
	private static final String LOG_TAG = Constant.TAG + "-" + ConnectionIPWeight.class.getSimpleName();

	private ArrayList<String> ipList;
	private HashMap<String,Integer> ipWeight;
	private long lastRefreshIPTimestamp = 0;

	private ConnectionIPWeight(){
		ipList = new ArrayList<String>();
		ipWeight = new HashMap<String,Integer>();
	}

	private static enum InstanceEnum{
		ConnectionIPWeight(new ConnectionIPWeight());
		private InstanceEnum(ConnectionIPWeight instance){
			this.instance = instance;
		}
		private ConnectionIPWeight instance = null;
	}

	public static ConnectionIPWeight getInstance(){
		return InstanceEnum.ConnectionIPWeight.instance;
	}

	public synchronized void refreshIPWeightByPing(){
		String ipString = "123.125.65.82,101.71.72.151";//baidu,ctrip
		String[] ipwithPort = ipString.split(",");
		if(System.currentTimeMillis() - lastRefreshIPTimestamp >= 120 * 1000){
			lastRefreshIPTimestamp = System.currentTimeMillis();
			for(String ipPort : ipwithPort){
				ipList.add(ipPort);
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(String ip : ipList) {
						String host = ip.split(":")[0].trim();
						new HostPinger(new HostPinger.HostPingFinishedListener() {
							@Override
							public void onHostPingFinished(String host, float pingInterval) {
								int weight = 0;
								if(pingInterval > 0){
									weight = Math.round(Constant.MAX_IP_WEIGHT - pingInterval);
								}else{
									weight = Constant.MIN_IP_WEIGHT;
								}
								Log.e(LOG_TAG, host + "-----" + weight);
								reportIP(host,weight);
							}
						}).pingHost(host);
					}
				}
			}).start();
		}
	}

	public void reportIP(String ip, int adjustedWeight){
		if(ipList.isEmpty()) return;
		for(String ipWithPort : ipList){
			if(ipWithPort.contains(ip)){
				adjustedWeight = Math.min(adjustedWeight, Constant.MAX_IP_WEIGHT);
				adjustedWeight = Math.max(adjustedWeight, Constant.MIN_IP_WEIGHT);
				ipWeight.put(ipWithPort, adjustedWeight);
			}
		}
		Log.e(LOG_TAG, ipWeight.toString());
	}

	public HashMap<String,Integer> getIPWeight(){
		return ConnectionIPWeight.getInstance().ipWeight;
	}
}
