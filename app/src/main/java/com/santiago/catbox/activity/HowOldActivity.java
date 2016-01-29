package com.santiago.catbox.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.santiago.catbox.R;
import com.santiago.catbox.common.Constant;
import com.santiago.catbox.util.FaceRecognize;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HowOldActivity extends AppCompatActivity {

	private String tag = Constant.TAG + "-" + HowOldActivity.class.getSimpleName();

	//从相册选择照片
	private static final int PICK_CODE = 0X110;
	//照相
	private static final int TAKE_PICTURE = 0X114;
	//识别成功
	private static final int MSG_SUCCESS = 0X111;
	//识别失败
	private static final int MSG_ERROR = 0X112;
	//剪裁图片
	private static final int CROP_PHOTO = 0x115;

	private ImageButton detect,camera,photo;
	private TextView tip, ageAndGender;
	//private CustomProgressDialog dialog;
	private ImageView imageView;
	private String currentPhotoPath = "";
	private Bitmap photoImage;
	private Canvas canvas;
	private Paint paint;

	private Uri imageUri;
	private String fileName;
	private boolean isCamera = false;
	private Dialog dialog;

	private ProgressDialog progressDialog;

	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what){
				case MSG_SUCCESS:
					progressDialog.dismiss();
					JSONObject result = (JSONObject) msg.obj;
					parseResult(result);
					imageView.setImageBitmap(photoImage);
					break;
				case MSG_ERROR:
					progressDialog.dismiss();
					String errorMsg = (String) msg.obj;
					if(TextUtils.isEmpty(errorMsg)){
						tip.setText("Error! ! !");
					}
					break;
				default:
					break;
			}
		}
	};

	private void parseResult(JSONObject result) {
		Bitmap bitmap = Bitmap.createBitmap(photoImage.getWidth(), photoImage.getHeight(), photoImage.getConfig());
		canvas = new Canvas(bitmap);
		canvas.drawBitmap(photoImage, 0, 0, null);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3);
		paint.setStrokeCap(Paint.Cap.ROUND);
		JSONArray faces;
		try {
			faces = result.getJSONArray("face");
			int faceCount = faces.length();
			if(faceCount == 0){
				dialog = new AlertDialog.Builder(this)
						.setMessage("So abstract, Detect fail, please go to Retry.")
						.setNegativeButton("ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								dialog.dismiss();
							}
						}).create();
				dialog.show();
				return;
			}
			tip.setText("Detect " + faceCount + "Face");
			for(int i= 0;i<faceCount;i++){
				JSONObject face = faces.getJSONObject(i);
				JSONObject position = face.getJSONObject("position");

				float x = (float) position.getJSONObject("center").getDouble("x");
				float y = (float) position.getJSONObject("center").getDouble("y");

				float width = (float) position.getDouble("width");
				float height = (float) position.getDouble("height");

				x = x /100 * bitmap.getWidth();
				y = y /100 * bitmap.getHeight();
				width = width / 100 * bitmap.getWidth();
				height = height/100 * bitmap.getHeight();

				canvas.drawLine(x-width/2,y-height/2,x-width/2,y+height/2,paint);
				canvas.drawLine(x-width/2,y-height/2,x+width/2,y-height/2,paint);
				canvas.drawLine(x+width/2,y-height/2,x+width/2,y+height/2,paint);
				canvas.drawLine(x-width/2,y+height/2,x+width/2,y+height/2,paint);

				int age = face.getJSONObject("attribute").getJSONObject("age").getInt("value");
				String gender = face.getJSONObject("attribute").getJSONObject("gender").getString("value");

				Bitmap ageBitmap = buildAgeBitmap(age,gender.equals("Male"));
				int ageWidth = ageBitmap.getWidth();
				int ageHeight = ageBitmap.getHeight();
				if(bitmap.getWidth() < imageView.getWidth() && bitmap.getHeight() < imageView.getHeight())
				{
					float ratio = Math.max(bitmap.getWidth() * 1.0f / imageView.getWidth(),
							bitmap.getHeight() * 1.0f / imageView.getHeight());
					ageBitmap = Bitmap.createScaledBitmap(ageBitmap, (int)(ageWidth*ratio),(int)(ageHeight*ratio),false);
				}
				canvas.drawBitmap(ageBitmap, x-ageBitmap.getWidth()/2,y-height/2-ageBitmap.getHeight(),null);
				photoImage = bitmap;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(tag, e.toString());
		}
	}

	private Bitmap buildAgeBitmap(int age, boolean isMale) {
		ageAndGender = (TextView) getLayoutInflater().inflate(R.layout.face_age_layout, null);
		String gender = isMale? "male":"female";
		ageAndGender.setText(age + gender);
		ageAndGender.setDrawingCacheEnabled(true);
		ageAndGender.measure(
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		ageAndGender.layout(0,0,ageAndGender.getMeasuredWidth(),ageAndGender.getMeasuredHeight());
		ageAndGender.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(ageAndGender.getDrawingCache());
		return bitmap;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_old);

		imageView = (ImageView) findViewById(R.id.imageView);
		tip = (TextView) findViewById(R.id.tipTextView);

		ImageButton openPhoto = (ImageButton) findViewById(R.id.open_photo);
		openPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent,PICK_CODE);
			}
		});

		ImageButton openCamera = (ImageButton) findViewById(R.id.open_camera);
		openCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = new Date(System.currentTimeMillis());
				fileName = dateFormat.format(date);

				File filePath = Environment.getExternalStorageDirectory();
				File outputImage = new File(filePath, fileName+".jpg");
				try {
					if(outputImage.exists()){
						outputImage.delete();
					}
					outputImage.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				imageUri = Uri.fromFile(outputImage);
				Intent cameras = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				cameras.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
				startActivityForResult(cameras,TAKE_PICTURE);
			}
		});

		ImageButton detect = (ImageButton) findViewById(R.id.detect);
		detect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(currentPhotoPath != null && !currentPhotoPath.trim().equals("")){
					resizePhoto();
				} else if(!isCamera){
					photoImage = BitmapFactory.decodeResource(getResources(), R.drawable.defaultimage);
				}
				progressDialog = new ProgressDialog(HowOldActivity.this);
				progressDialog.setMessage("Detecting...");
				progressDialog.show();
				FaceRecognize.detect(photoImage, new FaceRecognize.CallBack() {

					@Override
					public void success(JSONObject result) {
						Message msg = Message.obtain();
						msg.what = MSG_SUCCESS;
						msg.obj = result;
						handler.sendMessageDelayed(msg, 500);
					}

					@Override
					public void error(FaceppParseException e) {
						Message msg = Message.obtain();
						msg.what = MSG_ERROR;
						msg.obj = e.getErrorMessage();
						handler.sendMessageDelayed(msg, 500);
					}
				});
			}
		});
	}

	private void resizePhoto() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(currentPhotoPath,options);
		double scaleRatio = Math.max(options.outWidth * 1.0d /1024f, options.outHeight * 1.0d /1024f);
		options.inSampleSize = (int) Math.ceil(scaleRatio);
		options.inJustDecodeBounds = false;
		photoImage = BitmapFactory.decodeFile(currentPhotoPath, options);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
			case PICK_CODE:
				if(data != null){
					Uri uri = data.getData();
					Cursor cursor = getContentResolver().query(uri,null,null,null,null);
					cursor.moveToFirst();
					int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
					currentPhotoPath = cursor.getString(index);
					cursor.close();
					resizePhoto();
					imageView.setImageBitmap(photoImage);
					tip.setText("Go To Detect...");
				}
				break;
			case TAKE_PICTURE:
				if(resultCode == RESULT_OK)
				{
					Intent intent  = new Intent("com.android.camera.action.CROP");
					intent.setDataAndType(imageUri,"image/*");
					intent.putExtra("scale",true);
					intent.putExtra("aspectX",1);
					intent.putExtra("aspectY",1);
					intent.putExtra("outputX",340);
					intent.putExtra("outputY",340);

					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					Toast.makeText(HowOldActivity.this, "clip the image", Toast.LENGTH_LONG).show();

					Intent intentBC = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					intentBC.setData(imageUri);
					this.sendBroadcast(intentBC);
					startActivityForResult(intent,CROP_PHOTO);
				}
				break;
			case CROP_PHOTO:
				try {
					photoImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
					Toast.makeText(HowOldActivity.this,imageUri.toString(),Toast.LENGTH_LONG).show();
					isCamera = true;
					imageView.setImageBitmap(photoImage);
					tip.setText("Go To Detect...");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
		}
		super.onActivityResult(requestCode,resultCode,data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_how_old, menu);
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
