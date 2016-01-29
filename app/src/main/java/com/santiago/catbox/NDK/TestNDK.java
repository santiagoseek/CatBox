package com.santiago.catbox.NDK;

/**
 * Created by test on 16/1/26.
 * http://www.cnblogs.com/meadow-glog/p/5092629.html
 * http://blog.chinaunix.net/uid-20680966-id-4961553.html
 */
public class TestNDK {
	static {
		System.loadLibrary("TestNDK");
	}

	public static native String ndkReturnHelloWorld();
}
