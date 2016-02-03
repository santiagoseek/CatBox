package com.santiago.catbox.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.santiago.catbox.R;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);


		String version = "";
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(SplashActivity.this.getPackageName(),PackageManager.GET_ACTIVITIES);
			if(pi != null){
				version = pi.versionName;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		//ImageView imageView = (ImageView) findViewById(R.id.splash_imageView);
		//imageView.setImageResource(R.drawable.begin);
		TextView versionTV = (TextView) findViewById(R.id.splash_version_textView);
		versionTV.setText(version);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
				SplashActivity.this.finish();
			}
		},3000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_splash, menu);
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
}
