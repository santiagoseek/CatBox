package com.santiago.catbox.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.santiago.catbox.R;
import com.santiago.catbox.common.Constant;
import com.santiago.catbox.util.NetWork.DownloadFile;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

	private WebView webView;
	private String tag = Constant.TAG + "-" + WebViewActivity.class.getSimpleName();

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("");
		progressDialog.setMessage("Loading...");
		progressDialog.show();

		webView = (WebView) findViewById(R.id.webview_webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.loadUrl("http://wufazhuce.com");
		webView.requestFocus();
		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
				Log.d(tag,"begin to download, url is " + s);
				new Thread(new DownloadFile(s)).start();
			}
		});

		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				view.loadUrl(url);
				return true;
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon){
				super.onPageStarted(view, url, favicon);
			}

			public void onPageFinished(WebView view, String url){
				super.onPageFinished(view,url);
				progressDialog.dismiss();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_web_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCoder, KeyEvent event){
		if(keyCoder == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
			webView.goBack();
			return true;
		}else{
			return super.onKeyDown(keyCoder,event);
		}
	}
}
