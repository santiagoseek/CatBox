package com.santiago.catbox.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.santiago.catbox.common.Constant;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


/**
 * Created by test on 15-6-3.
 */
public class FaceRecognize {

	private String tag = Constant.TAG + "-" + FaceRecognize.class.getSimpleName();

	public interface CallBack{
		void success(JSONObject result);
		void error(FaceppParseException e);
	}

	public static void detect(final Bitmap bitmap, final CallBack callBack)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpRequests requests = new HttpRequests(Constant.faceppKey, Constant.faceppSecret, true, true);
				Bitmap bitmapSmall = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				bitmapSmall.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

				byte[] datas = outputStream.toByteArray();
				PostParameters parameters = new PostParameters();
				parameters.setImg(datas);
				try {
					JSONObject result = requests.detectionDetect(parameters);
					if(callBack != null){
						callBack.success(result);
					}
				} catch (FaceppParseException e) {
					e.printStackTrace();
					if(callBack != null){
						callBack.error(e);
						Log.e("FaceRecognize", e.toString());
					}
				}
			}
		}).start();
	}
}
