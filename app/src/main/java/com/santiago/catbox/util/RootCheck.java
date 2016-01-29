package com.santiago.catbox.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RootCheck {
	private static volatile int rootState = 2;

	/**
	 * 检测系统是否具有Root权限
	 * @return	true 具有root权限
	 * 			false 不具有root权限
	 */
	public static boolean isRoot() {
		switch(rootState){
		case 0:
			return false;
		case 1:
			return true;
		}
		String binPath = "/system/bin/su";
		String xBinPath = "/system/xbin/su";
		if ((new File(binPath).exists() && isExecutable(binPath))||
				(new File(xBinPath).exists() && isExecutable(xBinPath))){
			rootState=1;
			return true;
		}
		else{
			rootState=0;
			return false;
		}
	}

	private static boolean isExecutable(String filePath) {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("ls -l " + filePath);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String str = in.readLine();
			if (str != null && str.length() >= 4) {
				char flag = str.charAt(3);
				if (flag == 's' || flag == 'x')
					return true;
			}
		} catch (IOException e) {

		} finally {
			if (p != null) {
				p.destroy();
			}
		}
		return false;
	}
}
