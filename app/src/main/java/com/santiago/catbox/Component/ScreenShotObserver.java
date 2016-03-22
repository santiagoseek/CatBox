package com.santiago.catbox.Component;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.santiago.catbox.util.StringUtil;

import java.io.File;

/**
 * Created by test on 16/3/21.
 */
public class ScreenShotObserver extends ContentObserver {
	private Context mContext;
	private Handler mHandler;
	/**
	 * Creates a content observer.
	 *
	 * @param handler The handler to run {@link #onChange} on, or null if none.
	 */
	public ScreenShotObserver(Context context, Handler handler) {
		super(handler);

		mContext = context;
		mHandler = handler;
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		super.onChange(selfChange, uri);

		Cursor cursor = null;
		String filePath = "";
		ContentResolver resolver = mContext.getContentResolver();
		try{
			cursor = resolver.query(uri,null,null,null,null);
			if(cursor != null && cursor.getCount() > 0){
				if(cursor.moveToLast()){
					String[] array = cursor.getColumnNames();
					String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
					int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE));
					String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE));
					String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
					filePath = data;
					File file = new File(data);
					//data = /storage/emulated/0/Pictures/Screenshots/Screenshot_2016-03-21-20-43-56.png title = Screenshot_2016-03-21-20-43-56.png  displayName = Screenshot_2016-03-21-20-43-56.png  true
					Log.e("alex", "data = " + data + " title = " + title + "  displayName = " + displayName + "  " + file.exists());
					if(file == null || !file.exists()){
						return;
					}
					if((!filePath.contains("Screenshot") && !filePath.contains("截屏")) || size <= 0){
						return;
					}
				}
			}else{
				return;
			}
		} catch (Exception e){
			e.printStackTrace();
			Log.e("ScreenShotObserver",e.getMessage());
		} finally {
			if(cursor != null){
				cursor.close();
			}
		}
		if(StringUtil.emptyOrNull(filePath)){
			return;
		}
		final String fileP = filePath;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				simpleDialog(fileP);
			}
		});
	}

//	private void showDialog(String filePath){
//		Bitmap bitmap = null;
//		if()
//	}

	private void simpleDialog(String filePath){
		AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("filePath")
				.setMessage(filePath)
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
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
}
