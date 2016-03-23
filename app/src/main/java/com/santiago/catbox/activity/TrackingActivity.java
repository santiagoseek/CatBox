package com.santiago.catbox.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.santiago.catbox.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackingActivity extends Activity {

	private final static String tag = "TrackingActivity";

	private EditText displayEditText;
	private EditText inputEditText;
	private ImageButton addButton;
	private ImageButton timeButton;
	private ImageButton locationButton;
	private Context context;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking);

		context = this;

		displayEditText = (EditText) findViewById(R.id.tracking_display_et);
		inputEditText = (EditText) findViewById(R.id.tracking_input_et);

		timeButton = (ImageButton) findViewById(R.id.tracking_time_bt);
		timeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String inputText = inputEditText.getText().toString().trim();

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
				String currentTime = format.format(new Date(System.currentTimeMillis()));

				inputEditText.setText(inputText + currentTime);
			}
		});

		locationButton = (ImageButton) findViewById(R.id.tracking_location_bt);
		locationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String inputText = inputEditText.getText().toString().trim();

				inputEditText.setText(inputText + "百度地图当前位置");
			}
		});

		addButton = (ImageButton) findViewById(R.id.tracking_add_bt);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String displayText = displayEditText.getText().toString().trim();
				String inputText = inputEditText.getText().toString().trim();

				displayEditText.setText(displayText + "\r\n" + inputText);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_tracking, menu);
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
