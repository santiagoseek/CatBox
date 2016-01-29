package com.santiago.catbox.util;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by test on 15/9/29.
 */
public class IOProperties {

	public Properties loadConfig(Context context, String file){
		Properties properties = new Properties();
		try{
			FileInputStream fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public void saveConfig(Context context, String file, Properties properties){
		try{
			FileOutputStream fileOutputStream = new FileOutputStream(file,false);
			properties.store(fileOutputStream, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testWrite(Context context){
		Properties properties = new Properties();
		properties.put("prop1","abc");
		properties.put("prop2",1);
		properties.put("prop3",3.14);
		saveConfig(context,"/sdcard/config.dat",properties);
	}

	public void testRead(Context context){
		Properties properties = loadConfig(context,"/sdcard/config.dat");
		String prop1 = properties.getProperty("prop1");

	}
}