package com.santiago.catbox.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.santiago.catbox.common.Constant;
import com.santiago.catbox.util.NetWork.CDNCheck;
import com.santiago.catbox.util.NetWork.NetworkUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by test on 15/9/21.
 */
public class SystemInfo {

	/**
	 * 时区Key值
	 */
	public final static String TIME_ZONE = "timezone";
	/**
	 * 系统版本key值
	 */
	public final static String SYSTEM_VERSION = "osver";
	/**
	 * 模块Key值
	 */
	public final static String MODEL = "model";
	/**
	 * 制造商Key值
	 */
	public final static String MANUFACTURER = "mfr";
	/**
	 * SDK版本key值
	 */
	public final static String SDK_VERSION = "sdkver";
	/**
	 * APP包名Key值
	 */
	public final static String APP_PACKAGE_NAME = "pkg";
	/**
	 * APP版本Key值
	 */
	public final static String APP_VERSION_NAME = "appver";
	/**
	 * 屏幕大小Key值
	 */
	public final static String SCREEN_SIZE = "screen";
	/**
	 * WIFI网卡物理地址Key值
	 */
	public final static String MAC = "mac";
	/**
	 * 运营商Key值
	 */
	public final static String CARRIER = "carrier";
	/**
	 * 网络访问类型Key值
	 */
	public final static String ACCESS = "access";
	/**
	 * 语言Key值
	 */
	public final static String LANG = "lang";
	/**
	 * 手机IMEI值
	 * 会根据不同的手机设备返回IMEI，MEID或者ESN码
	 * 少数手机设备上，由于该实现有漏洞，会返回垃圾，如:zeros或者asterisks
	 */
	public final static String IMEI = "imei";
	/*
	* 2.3之后可以用，对于没有通话功能的设备，它会返回一个唯一的device ID
	 */
	public final static String SERIANUM = "serialNumber";
	/*
	* 64位，设备初始化之后会改变。
	* 厂商定制系统的Bug：不同的设备可能会产生相同的ANDROID_ID：9774d56d682e549c。
	* 厂商定制系统的Bug：有些设备返回的值为null。
	* 设备差异：对于CDMA设备，ANDROID_ID和TelephonyManager.getDeviceId() 返回相同的值。
	 */
	public final static String ANDROIDID = "androidID";

	private static volatile Map<String, String> builderInfo = null;  //缓存固定的信息，线程安全对象
	private static volatile  Map<String, String> deviceInfo = null;
	private static volatile  Map<String, String> systemInfo = null;
	private static volatile  Map<String, String> appInfo = null;
	private static volatile DisplayMetrics displayMetrics = null;
	private static volatile  String mac = null;
	private static volatile  String ProvidersName = "";
	private static volatile  String imeiValue = "";


	private SystemInfo(){
	}
	/**
	 * 获取系统信息，包括包名、版本名、系统名、系统版本、SDK版本、模块、制造商、CPU类型、屏幕分辨率、运营商、IMEI和网卡物理地址。
	 * @param context	上下文
	 * @return	系统信息
	 */
	public static Map<String, String> getSystemInfo(Context context) {
		CDNCheck.getInstance().init(context);
		if (systemInfo == null) {
			Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息
			info.put(TIME_ZONE, TimeZone.getDefault().getID());
			info.putAll(getAppInfo(context));
			info.putAll(getBuilderInfo(context));
			info.putAll(getDeviceInfo(context));
			info.put(CARRIER, getProvidersName(context));
			info.put(ACCESS, getNetType(context));
			info.put(LANG, getLang(context));
			info.put(IMEI, getTelePhoneIMEI(context));
			info.put(SERIANUM, Build.SERIAL);
			info.put(ANDROIDID,getAndroidID(context));
			info.put("root",String.valueOf(RootCheck.isRoot()));
			info.put("emu",String.valueOf(EmulatorCheck.isQEmuEnvDetected(context)));
			systemInfo = info;
		}
		return systemInfo;
	}
	/**
	 * 获取APP信息，包括包名、版本名、版本代码
	 * @param context	上下文
	 * @return APP信息
	 */
	public static Map<String, String> getAppInfo(Context context) {
		if (appInfo == null) {
			Map<String, String> info = new HashMap<String, String>();// 用来存储app信息
			try {
				PackageManager pm = context.getPackageManager();// 获得包管理器
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
						PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
				if (pi != null) {
					String versionName = pi.versionName == null ? "null"
							: pi.versionName;
					info.put(APP_PACKAGE_NAME, pi.packageName);
					info.put(APP_VERSION_NAME, versionName);
					appInfo = info;
				}
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return appInfo;
	}
	/**
	 * 获取builder信息，包括系统名、系统版本、SDK版本、模块、制造商、CPU类型
	 * @param context	上下文
	 * @return builder信息
	 */
	public static Map<String, String> getBuilderInfo(Context context) {
		if (builderInfo == null) {
			HashMap<String, String> info = new HashMap<String, String>();// 用来存储app信息
			info.put(SYSTEM_VERSION, Build.VERSION.RELEASE);
			info.put(SDK_VERSION, "" + Build.VERSION.SDK_INT);
			info.put(MODEL, Build.MODEL);
			info.put(MANUFACTURER, Build.MANUFACTURER);
			builderInfo = info;
		}
		return builderInfo;
	}
	/**
	 * 获取设备信息，包括屏幕分辨率和网卡物理地址
	 * @param context	上下文
	 * @return 设备信息
	 */
	public static Map<String, String> getDeviceInfo(Context context) {
		if (deviceInfo == null) {
			Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息
			DisplayMetrics dm = getDisplayMetrics(context);
			info.put(SCREEN_SIZE, new String(dm.widthPixels + "*" + dm.heightPixels));
			info.put(MAC, getMacAddress(context));
			deviceInfo = info;
		}
		return deviceInfo;
	}
	/**
	 * 获取屏幕分辨率
	 * @param context	上下文
	 * @return 屏幕分辨率
	 */
	public static DisplayMetrics getDisplayMetrics(Context context){
		if (displayMetrics == null) {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			displayMetrics = dm;
		}
		return displayMetrics;
	}
	/**
	 * 获取WIFI网卡物理地址
	 * @param context	上下文
	 * @return WIFI网卡物理地址
	 */
	public static String getMacAddress(Context context) {
		if (mac == null) {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String tempMac = info.getMacAddress();
			if(tempMac != null){
				mac = tempMac;
			}else{
				mac = "";
			}
		}
		return mac;
	}

	/**
	 * Role:Telecom service providers获取手机服务商信息 <BR>
	 * 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。<BR>
	 * Date:2012-3-12 <BR>
	 */
	public static String getProvidersName(Context context) {
		if (ProvidersName == "") {
			String pn = "";
			String IMSI = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE))
					.getSubscriberId();
			if (IMSI != null) {
				if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007") || IMSI.startsWith("46020")) {
					pn = "中国移动";
				} else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
					pn = "中国联通";
				} else if (IMSI.startsWith("46003") || IMSI.startsWith("46005") || IMSI.startsWith("46011")) {
					pn = "中国电信";
				}
			}
			ProvidersName = pn;
		}
		return ProvidersName;
	}

	/**
	 * 获得手机IMEI，需加入权限<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
	 * @param context 上下文
	 * @return IMEI
	 */
	public static String getTelePhoneIMEI(Context context){
		if (imeiValue == "") {
			TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephony != null){
				imeiValue = telephony.getDeviceId();
			} else {
				imeiValue = "";
			}
		}
		return imeiValue;
	}

	/**
	 * 获得当前的网络类型
	 * @param context 上下文
	 * @return 当前的网络类型
	 */
	public static String getNetType(Context context){
		String netType = null;
		int type = NetworkUtil.getNetworkType(context);
		if(type == Constant.NET_TYPE_WIFI){
			netType = "wifi";
		} else if(type == Constant.NET_TYPE_3G){
			netType = "3G";
		} else if(type == Constant.NET_TYPE_2G){
			netType = "2G";
		} else if(type == Constant.NET_TYPE_4G){
			netType = "4G";
		} else if(type == Constant.NET_TYPE_OTHER){
			netType = "other";
		} else if(type == Constant.NET_TYPE_NONE){
			netType = "none";
		}
		return netType;
	}
	/**
	 * 获得系统语言
	 * @param context 上下文
	 * @return 系统语言
	 */
	private static String getLang(Context context) {
		return Locale.getDefault().toString();
	}
	/*
	* AndroidID 64位，设备初始化之后会改变。
	* 厂商定制系统的Bug：不同的设备可能会产生相同的ANDROID_ID：9774d56d682e549c。
	* 厂商定制系统的Bug：有些设备返回的值为null。
	* 设备差异：对于CDMA设备，ANDROID_ID和TelephonyManager.getDeviceId() 返回相同的值。
	 */
	private static String getAndroidID(Context context) {
		String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return androidID;
	}
	/**
	 * 生成UBT发送数据的User-Agent 来方便排错，
	 * 格式 ${应用名}/${版本} SDK/${版本}
	 * CTRIP_WIRELESS/6.5.0 SDK/1.3.6
	 * @param context 上下文
	 * @return 生成的User-Agent
	 */
	public static String createReqUserAgentString(Context context){
		String appName = "";
		String versionName = "";
		try{
			PackageManager pm = context.getPackageManager();// 获得包管理器
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
			if (pi != null) {
				appName = pm.getApplicationLabel(pi.applicationInfo).toString();
				versionName = pi.versionName == null ? "null" : pi.versionName;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		String userAgent = String.format("%s/%s SDK/%s", appName, versionName, Constant.tempVersion);
		return userAgent;
	}
}
