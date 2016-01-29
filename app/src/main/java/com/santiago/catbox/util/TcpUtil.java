package com.santiago.catbox.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by test on 15-7-23.
 */
public class TcpUtil {

	public static Map<String, String> sendTCPRequest(String IP, String port, String reqData, String reqCharset){
		Map<String, String> respMap = new HashMap<String, String>();
		OutputStream out = null;      //写
		InputStream in = null;        //读
		String localPort = null;      //本地绑定的端口(java socket, client, /127.0.0.1:50804 => /127.0.0.1:9901)
		String respData = null;       //响应报文
		String respDataHex = null;    //远程主机响应的原始字节的十六进制表示
		Socket socket = new Socket(); //客户机
		try {
			socket.setTcpNoDelay(true);
			socket.setReuseAddress(true);
			socket.setSoTimeout(15000);
			socket.setSoLinger(true, 5);
			socket.setSendBufferSize(1024);
			socket.setReceiveBufferSize(1024);
			socket.setKeepAlive(true);
			socket.connect(new InetSocketAddress(IP, Integer.parseInt(port)), 15000);
			localPort = String.valueOf(socket.getLocalPort());
			/**
			 * 发送HTTP请求
			 */
			out = socket.getOutputStream();
			out.write(reqData.getBytes(reqCharset));
			/**
			 * 接收HTTP响应
			 */
			in = socket.getInputStream();
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int len = -1;
			while((len=in.read(buffer)) != -1){
				bytesOut.write(buffer, 0, len);
			}
			/**
			 * 解码HTTP响应的完整报文
			 */
			respData = bytesOut.toString(reqCharset);
			//respDataHex = formatToHexStringWithASCII(bytesOut.toByteArray());
		} catch (Exception e) {
			System.out.println("与[" + IP + ":" + port + "]通信遇到异常,堆栈信息如下");
			e.printStackTrace();
		} finally {
			if (null!=socket && socket.isConnected() && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("关闭客户机Socket时发生异常,堆栈信息如下");
					e.printStackTrace();
				}
			}
		}
		respMap.put("localPort", localPort);
		respMap.put("reqData", reqData);
		respMap.put("respData", respData);
		respMap.put("respDataHex", respDataHex);
		return respMap;
	}
}
