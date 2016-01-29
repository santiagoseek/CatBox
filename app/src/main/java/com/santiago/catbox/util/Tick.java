package com.santiago.catbox.util;


import com.santiago.catbox.common.Constant;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 记录方法/功能块调用的开始和结束时间。支持多线程。
 *
 * Created by jacky on 15/6/2.
 */
public final class Tick {

	private static boolean enable = Constant.IS_TEST;

	/**
	 * 一个调用记录。支持级联。
	 */
	private static class CallLog{
		long startTime;
		long endTime;
		long duration;
		String threadName;  //线程
		int indent;
		String funcName;
		CallLog parent;
	}

	/**
	 * 针对某一线程的全部调用栈管理
	 */
	private static class ThreadStack {
		CallLog curCall;
		final LinkedList<CallLog> callHistory= new LinkedList<CallLog>();
	}

	/**
	 * log初始时间，以Tick被加载开始算起。
	 */
	private static final long BIG_BANG_TIME = System.currentTimeMillis();

	/**
	 * 所有线程的调用栈管理
	 */
	private static final HashMap<String,ThreadStack> stacktraces = new HashMap<String,ThreadStack>();

	/**
	 * 输出开始时间（并通过方法调用确保初始化时间生成）
	 * 可以在所有记录开始之前调用
	 */
	public static void bigbang(){
		if(enable) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
			log("BIG BANG at " + sdf.format(new Date(BIG_BANG_TIME)));
		}
	}

	/**
	 * 在当前线程开始一个调用方法或者逻辑块
	 *
	 * @param funcName  方法或逻辑块名
	 */
	public static void start(String funcName) {
		if(enable) {
			String threadName = Thread.currentThread().getName();
			long now = System.currentTimeMillis() - BIG_BANG_TIME;
			ThreadStack stack = getThreadStack(threadName);
			CallLog parent = stack.curCall;
			CallLog newCall = new CallLog();
			newCall.threadName = threadName;
			newCall.startTime = now;
			newCall.funcName = funcName;
			if (parent != null) {
				newCall.indent = parent.indent + 1;
				newCall.parent = parent;
			}
			stack.curCall = newCall;
			stack.callHistory.addLast(newCall);
			log(newCall.indent, threadName, "|", now, "|", funcName);
		}
	}

	/**
	 * 结束最近一个调用
	 */
	public static void end() {
		if (enable){
			String threadName = Thread.currentThread().getName();
			long now = System.currentTimeMillis() - BIG_BANG_TIME;
			ThreadStack stack = getThreadStack(threadName);
			CallLog finishedCall = stack.curCall;
			stack.curCall = finishedCall.parent;
			finishedCall.endTime = now;
			finishedCall.duration = finishedCall.endTime - finishedCall.startTime;
			log(finishedCall.indent, threadName, "|", now, "|", finishedCall.funcName, "|END. Cost ", finishedCall.duration);
		}
	}

	/**
	 * 以线程为单位依次输出所有记录
	 */
	public static void playback(){
		if(enable) {
			Collection<ThreadStack> threads = stacktraces.values();
			for (ThreadStack stack : threads) {
				log("----------------------------------------------------------------");
				for (CallLog call : stack.callHistory) {
					log(call.indent, call.threadName, "|", call.funcName, "|", call.startTime, "-", call.endTime, "|", call.duration);
				}
			}
		}
	}

	/**
	 * 根据线程名得到调用栈对象
	 *
	 * @param threadName    现查明
	 * @return  调用栈
	 */
	private static ThreadStack getThreadStack(String threadName){
		if(stacktraces.containsKey(threadName)){    //有实例则直接返回
			return stacktraces.get(threadName);
		}
		synchronized (stacktraces){ //同步创建过程确保线程安全
			if(stacktraces.containsKey(threadName)){
				return stacktraces.get(threadName);
			}
			ThreadStack stack = new ThreadStack();
			stacktraces.put(threadName, stack);
			return stack;
		}
	}

	private static final int INDENT_SIZE = 4;   //输出缩进控制

	/**
	 * 无缩进输出日志行
	 *
	 * @param values    日志内容
	 */
	private static void log(String... values){
		log(0,(Object[])values);
	}

	/**
	 * 带缩进输出日志
	 *
	 * @param indent    缩进个数
	 * @param values    日志内容
	 */
	private static void log(int indent,Object... values){
		char[] indents = new char[indent * INDENT_SIZE];
		for(int i=0;i<indents.length;i++){
			indents[i]=' ';
		}
		StringBuilder sb = new StringBuilder();
		sb.append(indents);
		for(Object s:values){
			sb.append(String.valueOf(s));
		}
		android.util.Log.d("JTIME", sb.toString());
//        System.out.println(sb.toString());
	}
}
