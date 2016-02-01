package com.santiago.catbox.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.santiago.catbox.common.Constant;

/**
 * Created by test on 16/2/1.
 */
public class DataStorageUtil {
	private static final String tag = Constant.TAG + "-" + DataStorageUtil.class.getSimpleName();

	private static float formatBytes(long bytes){
		float mb = bytes/1024f/1024f/1024f;
		return mb;
	}

	public static String getInternalFilePath(Context context){
		String path = context.getFilesDir().getAbsolutePath();
		float internalTotalSpace = formatBytes(context.getFilesDir().getTotalSpace());
		float internalFreeSpace =  formatBytes(context.getFilesDir().getFreeSpace());
		float internalUsableSpace = formatBytes(context.getFilesDir().getUsableSpace());
		Log.e(tag,"internalTotalSpace: " + internalTotalSpace + "internalFreeSpace: " + internalFreeSpace + "internalUsableSpace: " + internalUsableSpace);
		String TFU = String.format("InternalStorage Total:%f;Free:%f;Usable:%f",internalTotalSpace,internalFreeSpace,internalUsableSpace);
		return TFU;
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable(){
		String state = Environment.getExternalStorageState();
		Log.e(tag,"ExternalStorageState: " + state);
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static String getExternalStoragePath(Context context) {
		if(isExternalStorageWritable() == false){
			ToastUtil.showToast(context,"无SD卡，SD卡不可用", Toast.LENGTH_LONG);
			return "";
		}
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		float externalTotalSpace = formatBytes(Environment.getExternalStorageDirectory().getTotalSpace());
		float externalFreeSpace = formatBytes(Environment.getExternalStorageDirectory().getFreeSpace());
		float externalUsableSpace = formatBytes(Environment.getExternalStorageDirectory().getUsableSpace());
		String tfu = String.format("ExternalStorage Total:%f;Free:%f;Usable:%f",externalTotalSpace,externalFreeSpace,externalUsableSpace);
		Log.e(tag,tfu);
		return tfu;
	}
}
