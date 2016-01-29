package com.santiago.catbox.util;

import android.util.Log;

import com.santiago.catbox.common.Constant;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by test on 15/9/21.
 */
public class HostPinger {
	public interface HostPingFinishedListener {
		public void onHostPingFinished(String host, float pingInterval);
	}

	private static final String TAG = Constant.TAG + "-HostPinger";
	private static final String COMMAND_SH = "sh";
	private static final String COMMAND_LINE_END = "\n";
	private static final String COMMAND_EXIT = "exit\n";
	private static final String COMMAND_PING_PRE = "ping -c 1 -W 1 ";

	private HostPingFinishedListener _hostPingFinishedListener;

	public HostPinger(HostPingFinishedListener hostPingFinishedListener) {
		this._hostPingFinishedListener = hostPingFinishedListener;
	}

	public void pingHost(String host) {
		// int status = -1;
		// debug("execute command start : " + commands);
		Process process = null;
		BufferedReader successReader = null;
		BufferedReader errorReader = null;
		// StringBuilder errorMsg;
		DataOutputStream dos = null;
		try {
			process = Runtime.getRuntime().exec(COMMAND_SH);
			dos = new DataOutputStream(process.getOutputStream());
			String command = COMMAND_PING_PRE + host;
			dos.write(command.getBytes());
			dos.writeBytes(COMMAND_LINE_END);
			dos.flush();
			dos.writeBytes(COMMAND_EXIT);
			dos.flush();
			process.waitFor();
			// errorMsg = new StringBuilder();
			successReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			errorReader = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			String lineStr;
			List<String> results = new ArrayList<String>();
			boolean hasError = false;
			while ((lineStr = successReader.readLine()) != null) {
				results.add(lineStr);
			}
			while ((lineStr = errorReader.readLine()) != null) {
				Log.e(TAG, "errorReader is not null " + lineStr);
				hasError = true;
				if (_hostPingFinishedListener != null) {
					_hostPingFinishedListener.onHostPingFinished(host, -1);
				}
				break;
				// errorMsg.append(lineStr);
			}
			if (!hasError) {
				float time = analysisCommandLine(results);
				if (_hostPingFinishedListener != null) {
					_hostPingFinishedListener.onHostPingFinished(host, time);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "pingHost IOException.");
			if (_hostPingFinishedListener != null) {
				_hostPingFinishedListener.onHostPingFinished(host, -1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "pingHost Exception.");
			if (_hostPingFinishedListener != null) {
				_hostPingFinishedListener.onHostPingFinished(host, -1);
			}
		} finally {
			try {
				if (dos != null) {
					dos.close();
				}
				if (successReader != null) {
					successReader.close();
				}
				if (errorReader != null) {
					errorReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "pingHost IOException.");
			}

			if (process != null) {
				process.destroy();
			}
		}
	}

	// 64 bytes from 180.149.132.47: icmp_seq=0 ttl=47 time=38.013 ms
	private float analysisCommandLine(List<String> commandLines) {
		for (String command : commandLines) {
			int timeIndex = command.indexOf("time=");
			int msIndex = command.indexOf("ms");
			if (timeIndex > -1 && msIndex > -1) {
				String timeValue = command.substring(timeIndex + 5, msIndex);
				try {
					return Float.parseFloat(timeValue.trim());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					Log.e(TAG, "analysisCommandLine Exception.");
					return -1;
				}

			} else {
				continue;
			}
		}
		return -1;
	}
}
