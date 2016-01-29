package com.santiago.catbox.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class used to determine functionality
 * specific to the Android QEmu.
 * 
 * @author tstrazzere
 */
public class EmulatorCheck {
	private static volatile int emulatorState = 2;

	private static String[] known_pipes = {
		"/dev/socket/qemud",
		"/dev/qemu_pipe"
	};
	private static String[] known_files = {
		"/system/lib/libc_malloc_debug_qemu.so",
		"/sys/qemu_trace"
	};
	private static String[] known_qemu_drivers = {
		"goldfish"
	};
	/**
	 * Check the existence of known pipes used
	 * by the Android QEmu environment.
	 * 
	 * @return {@code true} if any pipes where found to
	 * 		exist or {@code false} if not.
	 */
	public static boolean hasPipes() {
		for(String pipe : known_pipes) {
	        File qemu_socket = new File(pipe);
			if (qemu_socket.exists())
				return true;
		}
		return false;
	}
	/**
	 * Check the existence of known files used
	 * by the Android QEmu environment.
	 * 
	 * @return {@code true} if any files where found to
	 * 		exist or {@code false} if not.
	 */
	public static boolean hasQEmuFiles() {
		for(String pipe : known_files) {
	        File qemu_file = new File(pipe);
			if (qemu_file.exists())
				return true;
		}
		return false;
	}
	/**
	 * Reads in the driver file, then checks a list for
	 * known QEmu drivers.
	 * 
	 * @return {@code true} if any known drivers where
	 * 		found to exist or {@code false} if not.
	 */
	public static boolean hasQEmuDriver() {
		File drivers_file = new File("/proc/tty/drivers");
		if(drivers_file.exists() && drivers_file.canRead()) {
			byte[] data =  new byte[(int) drivers_file.length()];
			try {
				InputStream is = new FileInputStream(drivers_file);
				is.read(data);
				is.close();
			} catch (IOException exception) {
				return false;
			}
			String driver_data = new String(data);
			for(String known_qemu_driver : EmulatorCheck.known_qemu_drivers) {
				if(driver_data.indexOf(known_qemu_driver) != -1)
					return true;
			}
		}
		return false;
	}
	public static boolean hasKernelQEmu() {
		try {
			Process proc = Runtime.getRuntime().exec("getprop ro.kernel.qemu");
			InputStream stderr = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			br.close();
			if (line.length() > 0 && Integer.parseInt(line) == 1) {
	            return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean hasEmulatorBuild(Context context) {
		String BOARD = android.os.Build.BOARD; //The name of the underlying board, like "unknown".    
		String BOOTLOADER = android.os.Build.BOOTLOADER; //  The system bootloader version number.
		String BRAND = android.os.Build.BRAND; //The brand (e.g., carrier) the software is customized for, if any. "generic"
		String DEVICE = android.os.Build.DEVICE; //  The name of the industrial design. "generic"
		String HARDWARE = android.os.Build.HARDWARE; //The name of the hardware (from the kernel command line or /proc). "goldfish"
		String MODEL = android.os.Build.MODEL; //The end-user-visible name for the end product. "sdk"
		String PRODUCT = android.os.Build.PRODUCT; //The name of the overall product.
		String TAGS = android.os.Build.TAGS;
		if (TAGS == "test-keys" || BOARD == "unknown" || BOOTLOADER == "unknown" || BRAND == "generic"
				|| DEVICE == "generic" || MODEL == "sdk" || PRODUCT == "sdk" || HARDWARE == "goldfish") {
			return true;
		}
		return false;
	}
	/**
	 * 检测系统是否是运行在模拟器中
	 * @param context 上下文
	 * @return	true 	是运行在模拟器中的
	 * 			false	不是运行在模拟器中的
	 */
	public static boolean isQEmuEnvDetected(Context context) {
		switch(emulatorState){
			case 0:
				return false;
			case 1:
				return true;
		}
		if(EmulatorCheck.hasKernelQEmu() ||
				EmulatorCheck.hasEmulatorBuild(context) ||
				EmulatorCheck.hasPipes() ||
				EmulatorCheck.hasQEmuDriver() ||
				EmulatorCheck.hasQEmuFiles()){
			emulatorState = 1;
			return true;
		}else{
			emulatorState = 0;
			return false;
		}
	}
}