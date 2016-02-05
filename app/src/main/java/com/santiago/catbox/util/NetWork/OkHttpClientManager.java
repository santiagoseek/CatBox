package com.santiago.catbox.util.NetWork;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by test on 16/2/4.
 * http://blog.csdn.net/lmj623565791/article/details/47911083
 */
public class OkHttpClientManager {
	private static OkHttpClientManager mInstance;
	private OkHttpClient mOkHttpClient;
	private Handler mDelivery;
	//private Gson mGson;

	private static final String TAG = "Catbox_OkHttpClientManager";

	private OkHttpClientManager(){
		mOkHttpClient = new OkHttpClient();
		//mOkHttpClient.setCookieHandler();
		mDelivery = new Handler(Looper.getMainLooper());
	}

	private static enum InstanceEnum {
		Initiator(new OkHttpClientManager());

		private InstanceEnum(OkHttpClientManager okHttpClientManager) {
			this.instance = okHttpClientManager;
		}

		private OkHttpClientManager instance;
	}

	private static OkHttpClientManager getInstance(){
		return InstanceEnum.Initiator.instance;
	}

	/**
	 * 同步get请求
	 * @param url
	 * @return Response
	 */
	private Response _getSync(String url){
		Response execute = null;
		final Request request = new Request.Builder().url(url).build();
		Call call = mOkHttpClient.newCall(request);
		try {
			execute = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return execute;
	}

	private String _getAsString(String url){
		String bodyStr = null;
		Response execute = _getSync(url);
		try {
			bodyStr = execute.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bodyStr;
	}

	/**
	 * 异步的get请求
	 * @param url
	 * @param callback
	 */
	private void _getAsync(String url, final Callback callback){
		final Request request = new Request.Builder().url(url).build();
		deliveryResult(callback,request);
	}

	private void deliveryResult(Callback callback, Request request) {
	}
}
