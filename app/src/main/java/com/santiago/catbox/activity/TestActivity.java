package com.santiago.catbox.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.santiago.catbox.R;
import com.santiago.catbox.common.Constant;

public class TestActivity extends AppCompatActivity {
	private String LOG_TAG = Constant.TAG + "-" + TestActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		Log.d(LOG_TAG,"onCreate has been called");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(LOG_TAG, "onStart has been called");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(LOG_TAG,"onRestart has been called");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG,"onDestroy has been called");
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(LOG_TAG,"onResume has been called");
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(LOG_TAG,"onPause has been called");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(LOG_TAG, "onStop has been called");
	}
}
