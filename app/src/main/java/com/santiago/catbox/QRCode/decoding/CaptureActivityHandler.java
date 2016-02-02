package com.santiago.catbox.QRCode.decoding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.santiago.catbox.QRCode.ViewfinderResultPointCallback;
import com.santiago.catbox.QRCode.camera.CameraManager;
import com.santiago.catbox.R;
import com.santiago.catbox.activity.QRCaptureActivity;

import java.util.Vector;



/**
 * Created by test on 15/10/30.
 */
public class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class.getSimpleName();

	private final QRCaptureActivity activity;
	private final DecodeThread decodeThread;
	private State state;

	private enum State {
		PREVIEW,
		SUCCESS,
		DONE
	}

	public CaptureActivityHandler(QRCaptureActivity activity, Vector<BarcodeFormat> decodeFormats,
	                              String characterSet) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
				new ViewfinderResultPointCallback(activity.getViewfinderView()));
		decodeThread.start();
		state = State.SUCCESS;
		// Start ourselves capturing previews and decoding.
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	private void restartPreviewAndDecode() {
		if(state == State.SUCCESS){
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
			CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
			activity.drawViewfinder();
		}
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what){
			case R.id.auto_focus:
				if(state == State.PREVIEW){
					CameraManager.get().requestAutoFocus(this,R.id.auto_focus);
				}
				break;
			case R.id.restart_preview:
				Log.d(TAG, "Got restart preview message");
				restartPreviewAndDecode();
				break;
			case R.id.decode_succeeded:
				Log.d(TAG, "Got decode succeeded message");
				state = State.SUCCESS;
				Bundle bundle = msg.getData();

				/***********************************************************************/
				Bitmap barcode = bundle == null ? null :
						(Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);

				activity.handleDecode((Result) msg.obj, barcode);
				/***********************************************************************/
				break;
			case R.id.decode_failed:
				// We're decoding as fast as possible, so when one decode fails, start another.
				state = State.PREVIEW;
				CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
				break;
			case R.id.return_scan_result:
				Log.d(TAG, "Got return scan result message");
				activity.setResult(Activity.RESULT_OK, (Intent) msg.obj);
				activity.finish();
				break;
			case R.id.launch_product_query:
				Log.d(TAG, "Got product query message");
				String url = (String) msg.obj;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				activity.startActivity(intent);
				break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get().stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			decodeThread.join();
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}
}
