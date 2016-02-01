package com.santiago.catbox.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by test on 16/2/1.
 */
public class ToastUtil {
	private static Toast toast = null;

	/*
	一个Activity 可以显示多次 但是如果mToast已经显示了则只需改变内容,无须等待上次隐藏再次显示
	 */
	public static void showToast(Context context, String text, int duration){
		if(toast != null){
			toast.setText(text);
		}else{
			toast = Toast.makeText(context,text,duration);
		}
		toast.show();
	}
}
