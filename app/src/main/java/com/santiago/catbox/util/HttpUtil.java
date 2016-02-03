package com.santiago.catbox.util;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by test on 16/2/2.
 */
public class HttpUtil {
	private static final String LOG_TAG = "HttpUtil";

	private static final int CONNECT_TIMEOUT =  30 * 1000;

	private static final int READ_TIMEOUT = 30 * 1000;

	static {
		disableConnectionReuseIfNecessary();
	}

	private static void disableConnectionReuseIfNecessary() {
		// Work around pre-Froyo bugs in HTTP connection reuse.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	public static String download(String urlStr){
		BufferedReader bufReader = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.connect();

			int code = connection.getResponseCode();
			if(code<200 || code>=300){
				return null;
			}
			bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = bufReader.readLine()) != null){
				sb.append(line);
			}
			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if(bufReader != null){
				try{
					bufReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(connection != null){
				connection.disconnect();
			}
		}
	}

	public static int upload(String uri) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.connect();

			int code = connection.getResponseCode();
			return code;
		}catch(Throwable t){
			Log.e(LOG_TAG, t.getMessage(), t);
			return 0;
		}
		finally {
			if(connection != null){
				connection.disconnect();
			}
		}
	}

	public static String sendPost(String url, String params){
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Content-Type","application/json");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.write(params);
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line="";
			while((line = in.readLine()) != null){
				result += "\n" + line;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(out != null){
					out.close();
				}
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
