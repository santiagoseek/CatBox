package com.santiago.catbox;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.baidu.mapapi.SDKInitializer;
import com.santiago.catbox.util.SystemInfo;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by test on 16/1/26.
 */
public class BaseApplication extends Application {
	final String tag = "CatBoxBaseApplication";



	public void onCreate(){
		super.onCreate();
		initBugly();
		//this.startService(new Intent(this, RobMoney.class));
		//this.startService(new Intent(this, TrackingTouchService.class));
		addTouchListener();

		SDKInitializer.initialize(getApplicationContext()); //baidu map sdk init
	}

	private void initBugly() {
		Map<String,Object> systemInfo = new HashMap<>();
		systemInfo.putAll(SystemInfo.getSystemInfo(this));
		String userid = systemInfo.get("model").toString() + "--" + systemInfo.get("imei").toString();

		CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
		strategy.setAppChannel("mychannel");
		CrashReport.initCrashReport(getApplicationContext(), "900018870", true, strategy);
		CrashReport.setUserId(userid);

		BuglyLog.i("SystemInfo", systemInfo.toString());
	}


	public void addTouchListener() {
		new View.OnTouchListener() {
			@SuppressLint("LongLogTag")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//v.getClass().getSimpleName();
				Log.e(tag + "xxxOnTouchListener", v.getClass().getSimpleName());
				Log.e(tag + "xxxOnTouchListener", v.getParent().getClass().getSimpleName());
				return false;
			}
		};
	}

}
