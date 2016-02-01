package com.santiago.catbox.activity;

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
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.santiago.catbox.NDK.TestNDK;
import com.santiago.catbox.R;
import com.santiago.catbox.common.Constant;
import com.santiago.catbox.util.ConnectionIPWeight;
import com.santiago.catbox.util.DataStorageUtil;
import com.santiago.catbox.util.NetworkUtil;
import com.santiago.catbox.util.SystemInfo;
import com.santiago.catbox.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
					case 1: {
						Toast.makeText(context, ConnectionIPWeight.getInstance().getIPWeight().toString(), Toast.LENGTH_LONG).show();
						break;
					}
					case 2: { //SystemInfo
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
						showNotification("Test","This is Testing for Notification.");
						break;
					}
					case 8: { //StorageTest
						ToastUtil.showToast(context, DataStorageUtil.getInternalFilePath(context),Toast.LENGTH_LONG);
						ToastUtil.showToast(context, DataStorageUtil.getExternalStoragePath(context),Toast.LENGTH_LONG);
						break;
					}
				}
			}
		});

		Log.d(LOG_TAG, "onCreate has been called.");
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
		Log.e(LOG_TAG, "onDestroy has been called");
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
			items.add("test");
			items.add("DisplayInfo");
			items.add("MultiProcessServiceStart");//11
			items.add("ServiceStop");
			items.add("PushProcessServiceStart");
			items.add("PushProcessServiceStop");
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
