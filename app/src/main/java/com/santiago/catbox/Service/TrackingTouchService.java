package com.santiago.catbox.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class TrackingTouchService extends Service {
	private static final String tag = "TrackingTouchService";

 	private WindowManager.LayoutParams params = new WindowManager.LayoutParams();

	private View v = null;
	private WindowManager mgr = null;

	public TrackingTouchService() {
	}


	public String getParentView(View view,String path){
		if(view != null){
			if(view.getParent() != null){
				return getParentView((View) view.getParent(),path);
			}
		}

		return path + "/" + view.getClass().getSimpleName();
	}

	@Override
	public void onCreate(){
		super.onCreate();

		v = new View(this);
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.e(tag + "xxxxx", "current touch view parent is:" + v.getParent().getClass().getSimpleName());

				//event.getButtonState()
				//ToastUtil.showToast(this,"service onTouch", Toast.LENGTH_LONG);

				Log.e(tag + "xxxxx","current touch is:" + event.getX() + "---" + event.getY());
				return false;
			}
		});
		mgr = (WindowManager)  this.getSystemService(Context.WINDOW_SERVICE);

//		params = new WindowManager.LayoutParams(
//				WindowManager.LayoutParams.MATCH_PARENT,
//				WindowManager.LayoutParams.MATCH_PARENT,
//				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//				PixelFormat.TRANSPARENT);

		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
		params.format = PixelFormat.TRANSLUCENT;// 支持透明
		//mParams.format = PixelFormat.RGBA_8888;
		params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
		params.width = 490;//窗口的宽和高
		params.height = 160;
		params.x = 0;//窗口位置的偏移量
		params.y = 0;



		//params.gravity = Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL;
		params.gravity = Gravity.CENTER;

		Log.e(tag + "xxxxx", "TrackingTouchService onCreate.");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(tag + "xxxxx", "TrackingTouchService onStartCommand.");
		// TODO Auto-generated method stub
		mgr.addView(v,params);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy(){
		mgr.removeView(v);

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

//	@SuppressLint("LongLogTag")
//	public boolean onTouch(View v, MotionEvent event) {
//		Log.e(tag + "xxxxx", "current touch view parent is:" + v.getParent().toString());
//
//		//event.getButtonState()
//		ToastUtil.showToast(this,"service onTouch", Toast.LENGTH_LONG);
//
//		Log.e(tag + "xxxxx","current touch is:" + event.getX() + "---" + event.getY());
//		return false;
//	}
}
