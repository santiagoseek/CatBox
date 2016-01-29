package com.santiago.catbox.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.santiago.catbox.R;
import com.santiago.catbox.common.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GridUIActivity extends AppCompatActivity {

	private static final String tag = Constant.TAG + GridUIActivity.class.getSimpleName();

	static int[] icon_ids = {
			R.drawable.icon1,
			R.drawable.icon2,
			R.drawable.icon3,
			R.drawable.icon4,
			R.drawable.icon5,
			R.drawable.icon6,
			R.drawable.icon7,
			R.drawable.icon8,
			R.drawable.icon9,
			R.drawable.icon10,
			R.drawable.icon11,
			R.drawable.icon12,
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gridview_ui);

		String[] icon_text = getResources().getStringArray(R.array.gridview_icon_text);
		GridView gridView = (GridView) findViewById(R.id.gridView);

		ArrayList<HashMap<String,Object>> icon_text_list = new ArrayList<>();
		for(int i = 0;i<12;i++){
			HashMap<String,Object> map = new HashMap<>();
			map.put("itemIcon",icon_ids[i]);
			map.put("itemText",icon_text[i]);
			icon_text_list.add(map);
		}

		SimpleAdapter gridAdapter = new SimpleAdapter(this, icon_text_list, R.layout.griditemui, new String[]{"itemIcon","itemText"}, new int[]{R.id.gridView_item_icon, R.id.gridView_item_text});

		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				switch (i){
					case 0:
						break;
					case 1:
						break;
					default:
						break;
				}
			}
		});
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_grid_ui, menu);
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

	public void goCtripPage(){
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.ctrip.com"));
		startActivity(intent);
	}

	public void goCallNumber(){
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:888"));
		startActivity(intent);
	}

	public void goMap(){
		Uri location = Uri.parse("");
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
		boolean isIntentSafe = activities.size() > 0;

		if(isIntentSafe){
			startActivity(mapIntent);
		}
	}
}
