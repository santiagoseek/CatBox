package com.santiago.catbox.activity;

import android.database.ContentObserver;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

import com.santiago.catbox.Component.ScreenShotObserver;

/**
 * Created by test on 16/3/21.
 */
public class BaseActivity extends FragmentActivity {
	private ContentObserver mObserver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mObserver = new ScreenShotObserver(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mObserver);
	}

	@Override
	protected void onStop() {
		super.onStop();

		getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}
}
