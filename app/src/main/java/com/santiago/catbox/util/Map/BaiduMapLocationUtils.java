package com.santiago.catbox.util.Map;


import android.content.Context;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.santiago.catbox.util.ToastUtil;

/**
 * Created by test on 16/3/14.
 */
public class BaiduMapLocationUtils implements BDLocationListener {

	private Context context;
	private static final String tag = "BaiduMapLocationUtils";
	private static LocationClient mLocationClient;
	private LocationClientOption mLocationClientOption;
	private static volatile BaiduMapLocationUtils mapLocationUtils = null;
	private String currentCity = "";
	private boolean isFirLoc = true;
	private String currentAddress = "";

	private BaiduMapLocationUtils(Context context){
		init(context);
	}

	public static BaiduMapLocationUtils getInstance(Context context){
		if(mapLocationUtils == null){
			synchronized (BaiduMapLocationUtils.class){
				if(mapLocationUtils == null){
					mapLocationUtils = new BaiduMapLocationUtils(context);
					mLocationClient.start();
				}
			}
		}
		return mapLocationUtils;
	}

	public void init(Context context){
		this.context = context;
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(this);

		mLocationClientOption = new LocationClientOption();
		mLocationClientOption.setCoorType("bd09ll");          //返回坐标类型
		mLocationClientOption.setScanSpan(1000 * 1);         //定位间隔时间
		mLocationClientOption.setAddrType("all");           //是否需要地址信息，默认为false
		mLocationClientOption.SetIgnoreCacheException(false);       //可选，默认false，设置是否收集CRASH信息，默认收集
		mLocationClientOption.setEnableSimulateGps(false);                 //可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClientOption.setIsNeedAddress(true);                     //可选，设置是否需要地址信息，默认不需要
		mLocationClientOption.setOpenGps(true);                   //可选，默认false,设置是否使用gps
		mLocationClientOption.setLocationNotify(true);            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		mLocationClientOption.setIsNeedLocationDescribe(true);    //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		mLocationClientOption.setIsNeedLocationPoiList(true);     //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		mLocationClientOption.setIgnoreKillProcess(false);        //可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死

		mLocationClient.setLocOption(mLocationClientOption);
	}
	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		if(bdLocation == null || bdLocation.getCity() == null){
			if(isFirLoc){
				ToastUtil.showToast(context,"定位失败，错误类型：" + BDLocation.TypeServerError, Toast.LENGTH_LONG);
				isFirLoc = false;
			}
			return;
		}
		if((currentCity == null && bdLocation.getCity() != null) || (bdLocation.getCity() != null && !bdLocation.getCity().equals(currentCity))){
			currentCity = bdLocation.getCity();
			ToastUtil.showToast(context,"" + currentCity,Toast.LENGTH_LONG);
			return;
		}
		currentAddress = bdLocation.getAddress().address;
	}

	public void onReceivePoi(BDLocation bdLocation){}

	public String getLocationAddress(){
		return currentAddress;
	}

	/**
	 * 关闭定位功能
	 */
	public void onStop(){
		mLocationClient.stop();
	}
}
