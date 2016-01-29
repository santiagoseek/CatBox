package com.santiago.catbox.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by test on 16/1/27.
 */
public class DownloadFile implements Runnable {
	private String downloadurl;
	public DownloadFile(String url) {
		this.downloadurl = url;
	}
	@Override
	public void run() {
		URL url = null;
		try{
			url = new URL(downloadurl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			InputStream inputStream = urlConnection.getInputStream();

			File downloadFile = null;
			File sdFile = null;
			FileOutputStream outputStream = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				downloadFile = Environment.getExternalStorageDirectory();
				sdFile = new File(downloadFile,"downloadFileName.file");
				outputStream = new FileOutputStream(sdFile);
			}

			byte[] buffer = new byte[1024 * 4];
			int len = 0;
			while((len = inputStream.read(buffer)) != -1){
				if(outputStream != null){
					outputStream.write(buffer,0,len);
				}
			}

			if(outputStream != null){
				outputStream.close();
			}
			if(inputStream != null){
				inputStream.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
