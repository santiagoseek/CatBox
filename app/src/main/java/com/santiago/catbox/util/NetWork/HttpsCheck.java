package com.santiago.catbox.util.NetWork;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by test on 16/2/23.
 */
public class HttpsCheck {
	private static final int HTTPS_PORT = 443;

	public static void mainTest(){
		String testHttpsUrl = "https://tecvpn.ctrip.com/,https://www.baidu.com/,https://kyfw.12306.cn/otn/leftTicket/init,https://m.ctrip.com/html5/";
		String[] url = testHttpsUrl.split(",");
		for(int i =0 ;i<url.length;i++){
			sampleHttpsUrlConnection(url[i].trim());
		}
	}

	@SuppressLint("LongLogTag")
	public static void sampleHttpsUrlConnection(String urlStr){
		URL realUrl = null;
		try {
			Log.e("sampleHttpsUrlConnection",urlStr);
			realUrl = new URL(urlStr);

			HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
			connection.setDoInput(true);
			connection.connect();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while((line = bufferedReader.readLine()) != null){
				sb.append(line);
			}
			Log.e("sampleHttpsUrlConnection-respCode", String.valueOf(connection.getResponseCode()));
			Log.e("sampleHttpsUrlConnection-resp",sb.toString());
		}catch (SSLHandshakeException ex){
			Log.e("sslhandsharkeException",ex.getMessage());
			trustAllCert(realUrl);
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void trustAllCert(URL realUrl){
		if(realUrl == null){
			return;
		}
		Log.e("trustAllCert", realUrl.toString());
		SSLContext sslContext = null;
		try {
			TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager(){
				@Override
				public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					//Log.e("trustAllCert_client", String.valueOf(x509Certificates[0].toString()));
				}
				@Override
				public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					Log.e("trustAllCert_1", String.valueOf(x509Certificates.length));
					for(X509Certificate cert : x509Certificates){
						cert.checkValidity();
						try {
							cert.verify(cert.getPublicKey());
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (InvalidKeyException e) {
							e.printStackTrace();
						} catch (NoSuchProviderException e) {
							e.printStackTrace();
						} catch (SignatureException e) {
							e.printStackTrace();
						}
					}
				}
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			}};
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustManagers, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) realUrl.openConnection();
			httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String s, SSLSession sslSession) {
					return true;
				}
			});
			httpsURLConnection.setDoInput(true);
			httpsURLConnection.connect();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while((line = bufferedReader.readLine()) != null){
				sb.append(line);
			}
			Log.e("trustAllCert-respCode", String.valueOf(httpsURLConnection.getResponseCode()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static void testUrl(String urlStr) {
		try {
			URL realUrl = new URL(urlStr);
			Log.e("HttpsCheck", realUrl.toString());
			httpsConnection(realUrl.getHost(), realUrl.getPath());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void httpsConnection(String host, String path) {
		Socket socket = null;
		try {
			X509TrustManager xtm = new SSLTrustManager();
			TrustManager mytm[] = {xtm};
			// 得到上下文
			SSLContext ctx = null;
			ctx = SSLContext.getInstance("SSL");
			// 初始化
			ctx.init(null, mytm, null);
			// 获得工厂
			SSLSocketFactory factory = ctx.getSocketFactory();
			// 从工厂获得Socket连接
			socket = factory.createSocket(host, HTTPS_PORT);
			socket.setTcpNoDelay(true);
			OutputStreamWriter outWriter = new OutputStreamWriter(socket.getOutputStream());
			String reqStr = String.format("GET %s HTTP/1.1\r\nHost:%s\r\nConnection: Keep-Alive\r\n\r\n", path, host);
			outWriter.write(reqStr);
			outWriter.flush();

			byte readByte;
			long readLength = 0;
			ArrayList<Byte> byteList = new ArrayList<Byte>();
			long reqTimeStart = System.nanoTime();
			long ttfb = System.nanoTime();

			InputStream inReader = socket.getInputStream();
			while ((readByte = (byte) inReader.read()) != -1) {
				if (readLength == 0) {
					ttfb = System.nanoTime();
				}
				byteList.add(Byte.valueOf(readByte));
				readLength++;
			}
			long content = System.nanoTime();
			byte[] tempByteArr = new byte[byteList.size()];
			for (int j = 0; j < byteList.size(); j++) {
				tempByteArr[j] = byteList.get(j).byteValue();
			}
			String responseStr = new String(tempByteArr, "UTF-8");
			outWriter.close();
			inReader.close();
			Log.e("HttpsCheck-response",responseStr);
		} catch (Exception t) {
			Log.e("HttpsCheck-exception",t.getMessage());
			t.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}



}

class SSLTrustManager implements X509TrustManager {
	SSLTrustManager() {
	}

	// 检查客户端的可信任状态
	@SuppressLint("LongLogTag")
	public void checkClientTrusted(X509Certificate chain[], String authType) throws CertificateException {
		Log.e("HttpsCheck", "检查客户端的可信任状态...");
		Log.e("HttpsCheck-checkClientTrusted", authType);
	}

	// 检查服务器的可信任状态
	@SuppressLint("LongLogTag")
	public void checkServerTrusted(X509Certificate chain[], String authType) throws CertificateException {
		Log.e("HttpsCheck", "检查服务器的可信任状态...");
		Log.e("HttpsCheck-checkServerTrusted", authType);
		if(chain == null || chain.length == 0){
			return;
		}
		if(authType == null || authType.length() == 0){
			return;
		}
		try{
			chain[0].checkValidity();
		}catch (CertificateExpiredException ex){
			Log.e("HttpsCheck-checkServerTrusted", ex.getMessage());
		}
	}

	// 返回接受的发行商数组
	public X509Certificate[] getAcceptedIssuers() {
		Log.e("HttpsCheck", "获取接受的发行商数组...");
		return null;
	}
}
