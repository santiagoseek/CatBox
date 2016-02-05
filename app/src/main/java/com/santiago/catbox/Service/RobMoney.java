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
				if(className.equals("com.tencent.mm.ui.LauncherUI")){
					//开始抢红包
					getPacket();
				}else if(className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")){
					//开始打开红包
					openPacket();
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
		}
	}

	private void getPacket() {
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		recycle(rootNode);
	}

	private void recycle(AccessibilityNodeInfo nodeInfo) {
		if(nodeInfo.getChildCount() == 0){
			if(nodeInfo.getText() != null){
				if("领取红包".equals(nodeInfo.getText().toString())) {
					//需要找到一个可以点击的view
					Log.i(tag,"click" + ",isClick:" + nodeInfo.isClickable());
					nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					AccessibilityNodeInfo parent = nodeInfo.getParent();
					while(parent != null){
						Log.i(tag,"parent isClick:" + parent.isClickable());
						if(parent.isClickable()){
							parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							break;
						}
						parent = parent.getParent();
					}
				}
			}
		} else {
			for(int i = 0;i<nodeInfo.getChildCount();i++){
				if(nodeInfo.getChild(i) != null){
					recycle(nodeInfo.getChild(i));
				}
			}
		}
	}

	@Override
	public void onInterrupt() {

	}
}
