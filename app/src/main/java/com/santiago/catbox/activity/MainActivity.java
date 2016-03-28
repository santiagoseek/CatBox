package com.santiago.catbox.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.santiago.catbox.Component.SimpleDialog;
import com.santiago.catbox.NDK.TestNDK;
import com.santiago.catbox.R;
import com.santiago.catbox.common.Constant;
import com.santiago.catbox.util.DataStorageUtil;
import com.santiago.catbox.util.Map.BaiduMapLocationUtils;
import com.santiago.catbox.util.NetWork.ConnectionIPWeight;
import com.santiago.catbox.util.NetWork.HttpsCheck;
import com.santiago.catbox.util.NetWork.NetworkUtil;
import com.santiago.catbox.util.SystemInfo;
import com.santiago.catbox.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

	private String LOG_TAG = Constant.TAG + "-" + MainActivity.class.getSimpleName();
	private ListViewAdapter listViewAdapter;
	private Context context;
	private int notificationID = 0;

	private BroadcastReceiver networkChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String access = SystemInfo.getNetType(context);
			Toast.makeText(context,"Network change to: " + access, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;

		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(networkChangedReceiver,filter);

		listViewAdapter = new ListViewAdapter();
		ListView listView = (ListView) findViewById(R.id.mainListView);
		listView.setAdapter(listViewAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				switch (i) {
					case 0: //FileAccess
					{
						if (NetworkUtil.isNetworkConnected(context)) {
							ConnectionIPWeight.getInstance().refreshIPWeightByPing();
							Toast.makeText(context, ConnectionIPWeight.getInstance().getIPWeight().toString(), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(context, "No Network Access.", Toast.LENGTH_LONG).show();
						}
						break;
					}
					case 1: {  //test
						String testString = BaiduMapLocationUtils.getInstance(context).getLocationAddress();
						//String testString = ConnectionIPWeight.getInstance().getIPWeight().toString();
						Toast.makeText(context, testString, Toast.LENGTH_LONG).show();
						break;
					}
					case 2: { //SystemInfo
						Map<String, String> systemInfo = SystemInfo.getSystemInfo(context);
						systemInfo.putAll(SystemInfo.getWifiNetInfo(context));
						Dialog displayAlertDialog = new AlertDialog.Builder(context)
								.setTitle("DisplayInfo")
								.setMessage(SystemInfo.getSystemInfo(context).toString())
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
									}
								})
								.create();
						displayAlertDialog.show();
						break;
					}
					case 3: { // grid UI
						Intent intent = new Intent(MainActivity.this, GridUIActivity.class);
						MainActivity.this.startActivity(intent);
						break;
					}
					case 4: { //How old
						Intent intent = new Intent(MainActivity.this, HowOldActivity.class);
						MainActivity.this.startActivity(intent);
						break;
					}
					case 5: { //NDK Test
						Toast.makeText(context, TestNDK.ndkReturnHelloWorld(), Toast.LENGTH_LONG).show();
						break;
					}
					case 6: {
						Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
						MainActivity.this.startActivity(intent);
						break;
					}
					case 7: {
						showNotification("Test", "This is Testing for Notification.");
						break;
					}
					case 8: { //StorageTest
						ToastUtil.showToast(context, DataStorageUtil.getInternalFilePath(context), Toast.LENGTH_LONG);
						ToastUtil.showToast(context, DataStorageUtil.getExternalStoragePath(context), Toast.LENGTH_LONG);
						break;
					}
					case 9: { //QRCode
						MainActivity.this.startActivity(new Intent(MainActivity.this, QRCodeActivity.class));
						break;
					}
					case 10: { //TrackingActivity
						MainActivity.this.startActivity(new Intent(MainActivity.this, TrackingActivity.class));
						break;
					}
					case 11: {
						new Thread(new Runnable() {
							@Override
							public void run() {
								//HttpsCheck.testUrl();
								HttpsCheck.mainTest();
							}
						}).start();
						break;
					}
					case 12: { //TopActivity
//						Dialog displayAlertDialog = new AlertDialog.Builder(context)
//								.setTitle("TopActivity")
//								.setMessage(getTopActivity(context))
//								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialogInterface, int i) {
//									}
//								})
//								.create();
//						displayAlertDialog.show();

						SimpleDialog displayDialog = new SimpleDialog.Builder(context)
								.setTitle("提示")
								.setMessage(getTopActivity(context))
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								})
								.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								})
								.create();
						displayDialog.show();
						break;
					}
					case 13:{
						MainActivity.this.startActivity(new Intent(MainActivity.this,MapActivity.class));
						break;
					}
					case 14:{
						MainActivity.this.startActivity(new Intent(MainActivity.this,TestActivity.class));
						break;
					}
				}
			}
		});

		Log.d(LOG_TAG, "onCreate has been called.");

		//Log.e(LOG_TAG, String.valueOf(getAcccessibilityService()));
		//touchPath();

		//this.startService(new Intent(this, TrackingTouchService.class));

//		new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				//v.getClass().getSimpleName();
//				Log.e(LOG_TAG + "xxxOnTouchListener", v.getClass().getSimpleName());
//				Log.e(LOG_TAG + "xxxOnTouchListener", v.getParent().getClass().getSimpleName());
//				return false;
//			}
//		};
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return super.onInterceptTouchEvent(ev);
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		String action = "";
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				action = "action_down";
				break;
			case MotionEvent.ACTION_MOVE:
				action = "action_move";
				break;
			case MotionEvent.ACTION_UP:
				action = "action_up";
				break;
		}
		ToastUtil.showToast(context, action + x + "---" + y,Toast.LENGTH_LONG);

		return super.onTouchEvent(event);
	}

	public void touchPath(){
		if (((AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE)).isEnabled()) {
			AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPES_ALL_MASK);
			Log.e(LOG_TAG + "touchPath", event.getSource().getClassName().toString());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(LOG_TAG, "onStop has been called");
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(networkChangedReceiver);
		super.onDestroy();
		killProcesser();
		Log.d(LOG_TAG, "onDestroy has been called");
	}

	private void killProcesser() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		String pkgName = getPackageName();
		for(ActivityManager.RunningAppProcessInfo rap : runningAppProcesses){
			if(rap.processName.startsWith(pkgName)){
				android.os.Process.killProcess(rap.pid);
			}
		}
	}

	public String getTopActivity(Context context){
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		Log.e(LOG_TAG, runningTaskInfos.get(0).topActivity.getClassName());

		if(runningTaskInfos != null){
			return runningTaskInfos.get(0).topActivity.toString();
		}else{
			return null;
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public boolean getAcccessibilityService(){
		AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
		Log.e(LOG_TAG, accessibilityManager.isTouchExplorationEnabled() + "----" + accessibilityManager.isEnabled());
		return accessibilityManager.addTouchExplorationStateChangeListener(new AccessibilityManager.TouchExplorationStateChangeListener() {
			@Override
			public void onTouchExplorationStateChanged(boolean enabled) {
				ToastUtil.showToast(context,"this is test",Toast.LENGTH_LONG);
				Log.e(LOG_TAG, "addTouchExplorationStateChangeListener");
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(LOG_TAG, "onPause has been called");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "onResume has been called");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(LOG_TAG, "onStart has been called");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(LOG_TAG, "onRestart has been called");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			this.showClosedTips();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showClosedTips() {
		AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("Exit")
				.setMessage("Are you Sure to Exit?")
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						MainActivity.this.finish();
					}
				})
				.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				})
				.create();
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	class ListViewAdapter extends BaseAdapter implements Filterable {

		private ArrayList<String> items;
		private ArrayList<String> originalItems;

		public ListViewAdapter(){
			items = new ArrayList<String>();
			items.add("HostPing"); //0
			items.add("test");
			items.add("DisplayInfo");
			items.add("GridUI");
			items.add("HowOldAreYou");
			items.add("NDK");
			items.add("WebView");//6
			items.add("showNotification");
			items.add("StorageTest");
			items.add("QRCode");
			items.add("TrackingActivity");
			items.add("HttpsCheck");//11
			items.add("TopActivity");//12
			items.add("BaiduMap");//13
			items.add("testActivity");
			items.add("ProtoBufferActivity");//15
			items.add("UBTFvtTest");

		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int i) {
			return items.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			TextView textView = new TextView(context);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setTextSize(20);
			textView.setText(items.get(i));
			return textView;
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence charSequence) {
					FilterResults results = new FilterResults();
					List<String> filteredArrList = new ArrayList<String>();
					if(originalItems == null){
						originalItems = new ArrayList<String>(items);
					}
					if(charSequence == null || charSequence.length() == 0){
						results.count = originalItems.size();
						results.values = originalItems;
					} else {
						charSequence = charSequence.toString().toLowerCase();
						for(int i=0;i<originalItems.size();i++){
							String data = originalItems.get(i);
							if(data.toLowerCase().contains(charSequence)){
								filteredArrList.add(data);
							}
						}
						results.count = filteredArrList.size();
						results.values = filteredArrList;
					}
					return results;
				}

				@Override
				protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
					items = (ArrayList<String>) filterResults.values;
					notifyDataSetChanged();
				}
			};
			return filter;
		}
	}

	public void showNotification(String title, String message){
		notificationID++;
		Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.succeed);
		NotificationManager notificationmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		//Notification notification = new Notification(R.drawable.ic_launcher, message,System.currentTimeMillis());
		//notification.flags = Notification.FLAG_AUTO_CANCEL;
		//notification.defaults = notification.DEFAULT_SOUND|notification.FLAG_SHOW_LIGHTS;
		//.setLargeIcon(R.drawable.ic_launcher)
		//.setContentInfo("setContentInfo")

		Intent notifyIntent = new Intent(context,MainActivity.class);
		PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

		Notification notification = new NotificationCompat.Builder(context)
				.setLargeIcon(icon).setSmallIcon(R.drawable.succeed)
				.setTicker("Reminder, " + title)
				.setContentTitle(title).setContentText(message)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
				.setContentIntent(notifyPendingIntent)
				.setNumber(notificationID)
				.build();
		notificationmanager.notify(notificationID,notification);
	}

}
