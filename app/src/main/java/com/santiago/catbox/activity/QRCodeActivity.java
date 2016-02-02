package com.santiago.catbox.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.santiago.catbox.QRCode.EncodingHandler;
import com.santiago.catbox.R;
import com.santiago.catbox.util.ToastUtil;

import java.io.UnsupportedEncodingException;

public class QRCodeActivity extends AppCompatActivity {

	private TextView resultTextView;
	private EditText qrStrEditText;
	private ImageView qrImgImageView;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);
		context = this;

		resultTextView = (TextView) this.findViewById(R.id.qrcode_tv_result);
		qrStrEditText = (EditText) this.findViewById(R.id.qrcode_et_qrString);
		qrImgImageView = (ImageView) this.findViewById(R.id.qrcode_iv_qr);

		Button openCamera = (Button) this.findViewById(R.id.qrcode_bt_openCamera);
		openCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(QRCodeActivity.this,QRCaptureActivity.class),0);
			}
		});

		Button genQRcode = (Button) this.findViewById(R.id.qrcode_bt_generate);
		genQRcode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String contentString = qrStrEditText.getText().toString().trim();
				if(!contentString.equals("")){
					try {
						contentString = new String(contentString.getBytes("UTF-8"),"ISO-8859-1");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					Bitmap qrCodeBitmap = EncodingHandler.createQRImage(contentString,450);
					qrImgImageView.setImageBitmap(qrCodeBitmap);
				}else{
					ToastUtil.showToast(context,"Text can not be empty", Toast.LENGTH_LONG);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK){
			Bundle bundle = data.getExtras();
			String resultString = bundle.getString("result");
			resultTextView.setText(resultString);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_qrcode, menu);
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
}
