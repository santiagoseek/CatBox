package com.santiago.catbox.Component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.santiago.catbox.R;
import com.santiago.catbox.util.BitmapUtil;
import com.santiago.catbox.util.StringUtil;
import com.santiago.catbox.util.SystemInfo;

import java.io.File;

/**
 * Created by test on 16/3/21.
 */
public class ScreenShotDialog extends Dialog implements View.OnClickListener, Dialog.OnShowListener, Dialog.OnDismissListener {

	private ImageView mImageView, mShotBg;
	private Bitmap mBitmap;
	private String mFilePath, mPageId;
	private TextView mTvFeedBack, mTvShare;
	private Context mContext;

	public ScreenShotDialog(Context context, String filePath) {
		super(context,R.style.screen_shot_dialog);
		mFilePath = filePath;
		mContext = context;
	}

	CountDownTimer mTimer = new CountDownTimer(7000,7000) {
		@Override
		public void onTick(long millisUntilFinished) {
		}

		@Override
		public void onFinish() {
			dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_short);

		mShotBg = (ImageView) findViewById(R.id.shot_bg);
		mImageView = (ImageView) findViewById(R.id.iv_thumbnail);
		mTvFeedBack = (TextView) findViewById(R.id.tv_feedback);
		mTvShare = (TextView) findViewById(R.id.tv_share);

		mTvFeedBack.setOnClickListener(this);
		mTvShare.setOnClickListener(this);
		mImageView.setImageBitmap(mBitmap);
		getWindow().setGravity(Gravity.CENTER | Gravity.RIGHT);
		setOnShowListener(this);
		setOnDismissListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v != null){
			mTimer.cancel();
			int id = v.getId();
			if(!checkFileExist()){
				dismiss();
				return;
			}
			if(id == R.id.tv_feedback){
				dismiss();
			}else if(id == R.id.tv_share){
				dismiss();
			}
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if(mBitmap != null){
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	@Override
	public void onShow(DialogInterface dialog) {
		mTimer.start();
		TranslateAnimation animation = new TranslateAnimation(120,0,0,0);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setDuration(500);
		animation.setFillAfter(true);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		mShotBg.startAnimation(animation);
	}

	private boolean checkFileExist(){
		if(!StringUtil.emptyOrNull(mFilePath)){
			File file = new File(mFilePath);
			if(file != null && file.exists()){
				return true;
			}
		}
		return false;
	}

	public synchronized void setmBitmap(Bitmap originalBitmap){
		mBitmap = BitmapUtil.resizeBitmap(originalBitmap, SystemInfo.getPixelFromDip(mContext,79),SystemInfo.getPixelFromDip(mContext,136));
		originalBitmap.recycle();
	}

	private synchronized Bitmap getmBitmap(){
		Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
		return bitmap;
	}
}
