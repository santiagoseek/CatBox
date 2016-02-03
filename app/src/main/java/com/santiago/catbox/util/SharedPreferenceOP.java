package com.santiago.catbox.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Created by test on 16/2/2.
 */
public class SharedPreferenceOP {
	private static String tag = "SharedPreferenceOP";

	private static volatile SharedPreferenceOP instance = null;

	private SharedPreferences settings = null;

	private SharedPreferenceOP(Context context){
		if(context != null){
			this.settings = context.getSharedPreferences("CatBox_SP",Context.MODE_MULTI_PROCESS);
		}
	}

	public static SharedPreferenceOP getInstance(Context context){
		if(instance == null){
			synchronized (SharedPreferenceOP.class){
				if(instance == null){
					instance = new SharedPreferenceOP(context);
				}
			}
		}
		return instance;
	}

	public void commit(String key, String value){
		if(key == null || key.trim().length() < 1 || value == null){
			return;
		}
		if(settings != null){
			SharedPreferences.Editor editor = this.settings.edit();
			editor.putString(key,value);
			editor.commit();
		}
	}

	public void commit(Map<String,String> map){
		if(this.settings != null && map != null && !map.isEmpty()){
			SharedPreferences.Editor editor = this.settings.edit();
			for(Map.Entry<String,String> entry : map.entrySet()){
				editor.putString(entry.getKey(),entry.getValue());
			}
			editor.commit();
		}
	}

	public String getStringProperty(String key, String defVal){
		try{
			if(this.settings == null){
				return defVal;
			}else{
				String value = settings.getString(key,defVal);
				if(value == null || value.trim().length() < 1){
					value = defVal;
				}
				return value;
			}
		}catch(Exception e){
			Log.e(tag, e.getMessage());
			return defVal;
		}
	}

	public int getIntProperty(String key, int defVal){
		try{
			String value = getStringProperty(key,String.valueOf(defVal));
			return Integer.parseInt(value);
		}catch(Exception e){
			Log.e(tag,e.getMessage());
			return defVal;
		}
	}

	public long getLongProperty(String key, long defVal){
		try{
			String value = getStringProperty(key,String.valueOf(defVal));
			return Long.getLong(value);
		}catch(Exception e){
			Log.e(tag,e.getMessage());
			return defVal;
		}
	}
}
