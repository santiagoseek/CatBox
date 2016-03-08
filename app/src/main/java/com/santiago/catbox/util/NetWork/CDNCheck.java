package com.santiago.catbox.util.NetWork;

import android.content.Context;
import android.util.Log;

import com.santiago.catbox.common.Constant;
import com.santiago.catbox.util.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class CDNCheck {
	private static final String LOG_TAG = Constant.TAG + "-" + CDNCheck.class.getSimpleName();
	
    private static CDNCheck INSTANCE = new CDNCheck();
    private boolean isInited = false;
    
	private CDNCheck(){
	}
	/**
	 * 获取该CDNCheck实例，单例模式
	 * @return
	 */
	public static CDNCheck getInstance(){
		return INSTANCE;
	}
	
	public void init(Context context) {
		if (this.isInited == false) {
			if(!NetworkUtil.isNetworkConnected(context)){
				Log.d(LOG_TAG, "Network is not available, CDN check break.");
				return;
			}
//			long lastTestTime = DispatcherContext.getInstance().getConfigLong(Constant.CONFIG_TEST_CDN_TIME, 0);
//			if((System.currentTimeMillis() - lastTestTime) < 3600000*12){
//				Log.d(LOG_TAG, "Last CDN test is in 12h, so ignore this time.");
//				return;
//			}

			int random = new Random().nextInt(100) + 1; // [1,100]之间的随机数
			Log.e(LOG_TAG, String.valueOf(random));
//			if(random > 10){
//				LogCatUtil.d(LOG_TAG, "CDN check collecting rate is 10%. Not hit, so ignore this time.");
//				return;
//			}
			new Thread(new Runnable(){
				@Override
				public void run() {
					testCDN();
				}}).start();
			
			this.isInited = true;
		}
	}
	
	/**
	 * 计算相应时间，DNS解析时间，TCP建立连接时间，第一字节响应时间，内容下载的时间
	 * 得到字段信息，HTTP中的remoteIP，手机端的LocalDNS，响应HEADERS中的X-VARNISH、X-Via、X-Cache字段
	 */
	public void testCDN() {
		String testCDNUrl = "https://tecvpn.ctrip.com/,https://kyfw.12306.cn/otn/leftTicket/init,https://m.ctrip.com/html5/,http://youimg1.c-ctrip.com/do_not_delete/pic_alpha.gif,http://pages.ctrip.com/do_not_delete/pic";
		if(testCDNUrl == null || testCDNUrl.trim().length() < 1){
			Log.d(LOG_TAG, "Not found the remote config TEST_CDN, so CDN check break.");
			return;
		}
		String[] url = testCDNUrl.split(",");
		Socket socket = null;

		for (int i = 0; i < url.length; i++) {
			Map<String, String> metricTag = new HashMap<String, String>();
			metricTag.put("url", url[i].trim());
			try {
				URL realUrl = new URL(url[i].trim());
				int port = "https".equals(realUrl.getProtocol()) ? 443 : 80;
				String urlHost = realUrl.getHost();
				long dnslookupTimeStart = System.nanoTime();
				InetAddress remoteIP = InetAddress.getByName(urlHost);
				long dnslookupTimeEnd = System.nanoTime();
				if(null != remoteIP){
					metricTag.put("remoteIP", remoteIP.getHostAddress());
				}else{
					metricTag.put("remoteIP", "");
				}
				
				socket = new Socket();
				long connectTimeStart = System.nanoTime();
				socket.connect(new InetSocketAddress(urlHost, port),15000);
				long connectTimeEnd = System.nanoTime();

				socket.setTcpNoDelay(true);
				OutputStreamWriter outWriter = new OutputStreamWriter(socket.getOutputStream());
				String reqStr = String.format("GET %s HTTP/1.1\r\nHost:%s\r\nConnection: Keep-Alive\r\n\r\n", realUrl.getPath(), realUrl.getHost());
				outWriter.write(reqStr);
				outWriter.flush();
				
				byte readByte;
				long readLength = 0;
				ArrayList<Byte> byteList = new ArrayList<Byte>();
				long reqTimeStart = System.nanoTime();
				long ttfb = System.nanoTime();	
				
				InputStream inReader = socket.getInputStream();
				while((readByte = (byte)inReader.read())!=-1){
					if(readLength == 0){
						ttfb = System.nanoTime();
					}
					byteList.add(Byte.valueOf(readByte));
					readLength++;
				}			
				long content = System.nanoTime();
				
				String tempDNS = getLocalDNS();
				if(tempDNS != null && tempDNS.length() != 0){
					metricTag.put("LocalDNS", tempDNS);
				}
				
				byte[] tempByteArr = new byte[byteList.size()];
				for(int j=0;j<byteList.size();j++){
					tempByteArr[j] = ((Byte)byteList.get(j)).byteValue();
				}
				String responseStr = new String(tempByteArr,"UTF-8");
				if (responseStr.length() != 0) {
					String responseHead = responseStr.split("\r\n\r\n")[0].trim();
					for (String line : responseHead.split("\r\n")) {
						line = line.trim();
						if (line.startsWith("X-Varnish")) {
							metricTag.put("X-Varnish", splitResponseHeadKeyValue(line));
						}
						if (line.startsWith("X-Via")) {
							metricTag.put("X-Via", splitResponseHeadKeyValue(line));
						}
						if (line.startsWith("X-Cache")) {
							metricTag.put("X-Cache", splitResponseHeadKeyValue(line));
						}
					}
				}
				outWriter.close();
				inReader.close();
				
				float dnsTime = (dnslookupTimeEnd - dnslookupTimeStart) / 1000000.0f;
				float connectTime = (connectTimeEnd - connectTimeStart) / 1000000.0f;
				float ttfbTime = (ttfb - reqTimeStart) / 1000000.0f;
				float contentTime = (content - ttfb) / 1000000.0f;

				FileUtil.writeToSDCardFile("AAATest", "testLogFile.txt", System.currentTimeMillis() + "\r\n", true);
				String dns = "mobile.cdn.dns----" + dnsTime + "----" + metricTag.toString();
				String connect = "mobile.cdn.connect----" + connectTime + "----" + metricTag.toString();
				String ttfbStr = "mobile.cdn.ttfb----" + ttfbTime + "----" + metricTag.toString();
				String contentStr = "mobile.cdn.content----" + contentTime + "----" + metricTag.toString();

				FileUtil.writeToSDCardFile("AAATest", "testLogFile.txt", dns + "\r\n" + connect + "\r\n" + ttfbStr + "\r\n" + contentStr + "\r\n" , true);
			} catch (Throwable t) {
				String error = "mobile.cdn.fail----" + "----" + metricTag.toString();
				FileUtil.writeToSDCardFile("AAATest", "testLogFile.txt", error + "\r\n" , true);
				Log.e(LOG_TAG, t.getMessage());
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						Log.e(LOG_TAG, e.getMessage());
					}
				}
			}
		}
		//DispatcherContext.getInstance().updateConfig(Constant.CONFIG_TEST_CDN_TIME, String.valueOf(System.currentTimeMillis()));
	}
	
	private String splitResponseHeadKeyValue(String line){
		String value = line;
		String[] array = line.split(":",2);
		if(array.length >= 2){
			value = array[1].trim();
		}
		return value;
	}
	
	/**
	 * 手机端的LocalDNS
	 * @return 手机端的LocalDNS
	 */
	public static String getLocalDNS(){
		Process cmdProcess = null;
		BufferedReader reader = null;
		String dnsIP = "";
		try {
			cmdProcess = Runtime.getRuntime().exec("getprop net.dns1");
			reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
			dnsIP = reader.readLine();
			return dnsIP;
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
			return null;
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			cmdProcess.destroy();
		}
	}

}
