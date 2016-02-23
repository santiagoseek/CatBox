package com.santiago.catbox.Service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.santiago.catbox.util.ToastUtil;

import java.util.List;

public class RobMoney extends AccessibilityService {

	private static final String tag = "RobMoeny";
	public RobMoney() {
	}

	private int redCount = 6;
	/**
	 * 微信的包名
	 */
	static final String WECHAT_PACKAGENAME = "com.tencent.mm";
	/**
	 * 拆红包类
	 */
	static final String WECHAT_RECEIVER_CALSS = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
	/**
	 * 红包详情类
	 */
	static final String WECHAT_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
	/**
	 * 微信主界面或者是聊天界面
	 */
	static final String WECHAT_LAUNCHER = "com.tencent.mm.ui.LauncherUI";


	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		ToastUtil.showToast(this,"抢红包开始。。。", Toast.LENGTH_LONG);
		Log.e(tag,"抢红包 service connected");
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
		int eventType = accessibilityEvent.getEventType();
		switch (eventType){
			//监听通知栏消息
			case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
			{
				List<CharSequence> texts = accessibilityEvent.getText();
				if(!texts.isEmpty()){
					for(CharSequence text:texts){
						String content = text.toString();
						Log.i(tag,"text:" + content);
						if(content.contains("[微信红包]")){
							if(accessibilityEvent.getParcelableData() != null && accessibilityEvent.getParcelableData() instanceof Notification){
								Notification notification = (Notification) accessibilityEvent.getParcelableData();
								PendingIntent pendingIntent = notification.contentIntent;
								try {
									pendingIntent.send();
								} catch (PendingIntent.CanceledException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				break;
			}
			//监听是否进入微信红包消息界面
			case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			{
				String className = accessibilityEvent.getClassName().toString();
				if(className.equals("com.tencent.mm.ui.LauncherUI")){   //微信主界面或者是聊天界面
					//开始抢红包
					//for(int i =0;i<6;i++){}
					getPacket();
				}else if(className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")){  //拆红包类
					//开始打开红包
					openPacket();
				}else if(className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")){
					performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
				}
			}
		}

	}

	@SuppressLint("NewApi")
	private void openPacket() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if(nodeInfo != null){
			List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b43");
			for(AccessibilityNodeInfo n : list){
				n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
			performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
			Log.i(tag,"抢完红包, back");
		}
	}

	private void findAndPerformAction(String text){
		if(getRootInActiveWindow() == null) return;
		List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
		for(int i= 0;i<nodes.size();i++){
			AccessibilityNodeInfo node = nodes.get(i);
			if(node.getClassName().equals("android.widget.Button") && node.isEnabled()){
				node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		}
	}

	private void getPacket() {
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		if(rootNode == null){
			return;
		}

		recycle(rootNode);
	}

//	private void recycle(AccessibilityNodeInfo nodeInfo) {
//		performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP);
//		if(nodeInfo.getChildCount() == 0){
//			Log.e(tag,"==== root nodeInfo.getChildCount() == 0");
//			if(nodeInfo.getText() != null){
//				if("领取红包".equals(nodeInfo.getText().toString())) {
//					//需要找到一个可以点击的view
//					Log.i(tag, "click" + ",isClick:" + nodeInfo.isClickable());
//					//nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//					AccessibilityNodeInfo parent = nodeInfo.getParent();
//					while(parent != null){
//						Log.i(tag,"parent isClick:" + parent.isClickable());
//						if(parent.isClickable()){
//							parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//							break;
//						}
//						parent = parent.getParent();
//					}
//				}
//			}
//		} else {
//			Log.e(tag,"!!!! root nodeInfo.getChildCount() != 0");
//			for(int i = 0;i<nodeInfo.getChildCount();i++){
//				if(nodeInfo.getChild(i) != null){
//					recycle(nodeInfo.getChild(i));
//				}
//			}
//		}
//	}

	private void recycle(AccessibilityNodeInfo nodeInfo) {
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包"); //找到聊天界面中包含  领取红包  字符的控件
		if (list.isEmpty()) {
			list = nodeInfo.findAccessibilityNodeInfosByText("微信红包");
			for (AccessibilityNodeInfo n : list) {
				Log.i(tag, "-->微信红包:" + n);
				n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				break;
			}
		} else {
			// 最新的红包领起
			for (int i = list.size() - 1; i >= 0; i--) {
				AccessibilityNodeInfo parent = list.get(i).getParent();
				Log.i(tag, "-->领取红包:" + parent);
				if (parent != null) {
					parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					try {  //根据  Dump View Hierarchy For UI Automator 可以知道得到的控件的.getParent().getParent().getParent().getParent()
						//才是要点击的根布局， 调用performAction(AccessibilityNodeInfo.ACTION_CLICK)触发点击事件
						parent.getParent().getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
						System.out.println("click------");
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	@Override
	public void onInterrupt() {

	}
}
