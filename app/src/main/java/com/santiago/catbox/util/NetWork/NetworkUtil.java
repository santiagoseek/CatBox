package com.santiago.catbox.util.NetWork;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.santiago.catbox.common.Constant;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by test on 15/9/21.
 */
public class NetworkUtil {

	private NetworkUtil() {
	}

	/**
	 * 判断网络是否连接
	 * @Title: isNetworkConnected
	 * @Description: TODO
	 * @param context
	 * @return
	 * @return: boolean
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}

	public static boolean isDataState(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		if(tm.getDataState() ==TelephonyManager.DATA_CONNECTED){
			return true;
		}else{
			return false;
		}
	}

	public static String getDeviceId(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}


	public static String getNetworkOperatorName(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperatorName();

	}

	/*
	 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	 */
	public static String getLocalMacAddress(Context context){
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();

	}

	public String getLocalIPAddress(Context context){
		try{
			for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();){
				NetworkInterface intf = en.nextElement();

				for(Enumeration<InetAddress> enIP = intf.getInetAddresses();enIP.hasMoreElements();){
					InetAddress inetAddress = enIP.nextElement();

					if(!inetAddress.isLoopbackAddress()){
						return inetAddress.getHostAddress().toString();
					}

				}

			}
		}catch(SocketException e){
			Log.e("NetworkUtil", e.toString());
		}

		return null;
	}

	public static int getNetworkType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		int netWorkType = Constant.NET_TYPE_NONE;
		if (networkInfo != null && networkInfo.isAvailable()) {
			int nType = networkInfo.getType();
			if (nType == ConnectivityManager.TYPE_MOBILE) {
				int subType = networkInfo.getSubtype();
				switch (subType) {
					case TelephonyManager.NETWORK_TYPE_1xRTT:
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_EDGE:
					case TelephonyManager.NETWORK_TYPE_GPRS:
					case TelephonyManager.NETWORK_TYPE_IDEN:
						netWorkType = Constant.NET_TYPE_2G;
						break;
					case TelephonyManager.NETWORK_TYPE_EHRPD:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_EVDO_B:
					case TelephonyManager.NETWORK_TYPE_HSDPA:
					case TelephonyManager.NETWORK_TYPE_HSPA:
					case TelephonyManager.NETWORK_TYPE_HSPAP:
					case TelephonyManager.NETWORK_TYPE_HSUPA:
					case TelephonyManager.NETWORK_TYPE_UMTS:
						netWorkType = Constant.NET_TYPE_3G;
						break;
					case TelephonyManager.NETWORK_TYPE_LTE:
						netWorkType = Constant.NET_TYPE_4G;
						break;
					default:
						netWorkType = Constant.NET_TYPE_OTHER;
						break;
				}
			} else if (nType == ConnectivityManager.TYPE_WIFI) {
				// WIFI
				netWorkType = Constant.NET_TYPE_WIFI;
			}
		} else {
			// 没有网络
			netWorkType = 0;
		}

		return netWorkType;
	}


	public static String getNetworkName(Context context){
		String name = "NA";
		int flag = getNetworkType(context);

		if(flag == Constant.NET_TYPE_2G){
			name = "2G";
		}else if(flag == Constant.NET_TYPE_3G){
			name = "3G";
		}else if(flag == Constant.NET_TYPE_WIFI){
			name = "wifi";
		} else if(flag == Constant.NET_TYPE_4G){
			name = "4G";
		}

		return name;
	}
}
