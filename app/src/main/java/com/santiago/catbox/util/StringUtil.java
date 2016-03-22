package com.santiago.catbox.util;

/**
 * Created by test on 16/3/21.
 */
public class StringUtil {

	public static boolean checkIp(String str) {
		boolean flag = false;
		String[] s = new String[4];
		// 检查是否只有数字和'.'。
		if (!str.matches("[0-9[\\.]]{1,16}")) {
			flag = false;
		}
		else {
			// 字符串中的数字字符分开存储一个数组中
			s = str.split("\\.");
			for (int i = 0; i < s.length; i++) {
				int a = Integer.parseInt(s[i]);
				// 转换为二进制进行匹配
				if (Integer.toBinaryString(a).matches("[0-1]{1,8}")) {
					flag = true;
				}
				else {
					flag = false;
					break;
				}
			}
		}
		// if(flag){
		// // System.out.println("ip is right");
		// }else{
		// // System.out.println("ip is wrong");
		// }
		return flag;
	}

	/**
	 * 判断字串是否为空
	 *
	 * @param str
	 * @return
	 */
	public static boolean emptyOrNull(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean emptyOrNull(String... arrStr) {
		for (String str : arrStr) {
			if (emptyOrNull(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将string转为int ，异常时返回-1
	 *
	 * @param s
	 * @return
	 */
	public static int toInt(String s) {
		int i = 0;
		try {
			i = Integer.parseInt(s);
		}
		catch (Exception e) {
			i = -1;
		}
		return i;
	}

	/**
	 * 将String转换为int，异常时，返回传入的{@code #defaultValue}
	 *
	 * @param str          需要转换为int的String
	 * @param defaultValue 异常时的默认值
	 * @return int
	 */
	public static int toInt(String str, int defaultValue) {
		int i;
		try {
			i = Integer.parseInt(str);
		}
		catch (Exception e) {
			i = defaultValue;
		}
		return i;
	}

	/**
	 * 将两个string转化成int并比较
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int compareStrToInt(String s1, String s2) {
		int i = 0;
		try {
			i = Integer.parseInt(s1) - Integer.parseInt(s2);
		}
		catch (Exception e) {
		}
		return i;
	}
}
