package com.santiago.catbox.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by test on 16/3/23.
 */
public class BitmapUtil {
	private static BitmapUtil sInstance;

	private SparseArray<Bitmap> mBitmapCachedMap;

	private BitmapUtil() {
		if (mBitmapCachedMap == null) {
			mBitmapCachedMap = new SparseArray<Bitmap>();
		}
	}

	public static BitmapUtil getInstance() {
		if (sInstance == null) {
			sInstance = new BitmapUtil();
		}
		return sInstance;
	}

//	public Bitmap getBitmap(int id) {
//		if (mBitmapCachedMap.get(id) != null&&!mBitmapCachedMap.get(id).isRecycled()) {
//			return mBitmapCachedMap.get(id);
//		} else {
//			mBitmapCachedMap.remove(id);
//			Bitmap bitmap = BitmapFactory.decodeResource(FoundationContextHolder.context.getResources(), id);
//			mBitmapCachedMap.put(id, bitmap);
//			return bitmap;
//		}
//	}

	/**
	 * 判断Bitmap对象是否有效
	 *
	 * @param bmp
	 *            Bitmap对象
	 * @return true if bitmap is not null and not be recycled
	 */
	public static boolean isBitmapAvailable(Bitmap bmp) {
		return bmp != null && !bmp.isRecycled();
	}

	/**
	 * Bitmap对象转换为二进制数据
	 *
	 * @param srcBmp
	 *            Bitmap对象
	 * @return 二进制数据
	 */
	public static byte[] convertBitmapToBytes(Bitmap srcBmp) {
		if (isBitmapAvailable(srcBmp)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			srcBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		}
		return null;
	}

	/**
	 * 二进制数值转换为Bitmap对象
	 *
	 * @param srcBytes
	 *            二进制数据
	 * @return Bitmap对象, 使用完后注意回收
	 */
	public static Bitmap convertBytesToBitmap(byte[] srcBytes) {
		if (srcBytes != null && srcBytes.length > 0) {
			return BitmapFactory.decodeByteArray(srcBytes, 0, srcBytes.length);
		}
		return null;
	}

	/**
	 * 缩放Bitmap对象
	 *
	 * @param srcBmp
	 *            Bitmap对象
	 * @param width
	 *            缩放后的宽度
	 * @param height
	 *            缩放后的高度
	 * @return 缩放后的Bitmap对象
	 */
	public static Bitmap resizeBitmap(Bitmap srcBmp, int width, int height) {
		Bitmap dstBmp = null;
		if (isBitmapAvailable(srcBmp)) {
			int w = srcBmp.getWidth();
			int h = srcBmp.getHeight();
			Matrix matrix = new Matrix();
			matrix.postScale(((float) width / w), ((float) height / h));
			dstBmp = Bitmap.createBitmap(srcBmp, 0, 0, w, h, matrix, true);
		}
		return dstBmp;
	}

	/**
	 * 读取Bitmap文件，并作缩放处理
	 *
	 * @param srcBmp
	 *            Bitmap对象
	 * @param width
	 *            缩放后的宽度
	 * @param height
	 *            缩放后的高度
	 * @return 缩放后的Bitmap对象, 使用完后注意回收
	 */
	public static Bitmap getScaledBitmapFromFile(String filePath, int width, int height) {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inPreferredConfig = Bitmap.Config.RGB_565;
		op.inPurgeable = true;
		op.inInputShareable = true;
		op.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, op);

		op.inSampleSize = (int) Math.max(((float) op.outWidth / width), ((float) op.outHeight / height));
		op.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, op);
	}

	/**
	 * Bitmap对象转换为Drawable对象
	 *
	 * @param bmp
	 *            Bitmap对象
	 * @return Drawable对象
	 */
	@SuppressWarnings("deprecation")
	public static Drawable convertBitmapToDrawable(Bitmap bmp) {
		return new BitmapDrawable(bmp);
	}

	/**
	 * 无拉伸图压缩，并截取中间部分
	 * @param source 原图
	 * @param newHeight 缩略图高度
	 * @param newWidth	缩略图宽度
	 * @return 缩略图
	 */
	public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
		if(source==null || source.isRecycled()){
			throw new IllegalArgumentException("source bitmap for scale is not available");
		}

		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		// Compute the scaling factors to fit the new height and width, respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.
		float xScale = (float) newWidth / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		// Now get the size of the source bitmap when scaled
		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		// Let's find out the upper left coordinates if the scaled bitmap
		// should be centered in the new size give by the parameters
		float left = (newWidth - scaledWidth) / 2;
		float top = (newHeight - scaledHeight) / 2;

		// The target rectangle for the new, scaled version of the source bitmap will now
		// be
		RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		// Finally, we create a new bitmap of the specified size and draw our new,
		// scaled bitmap onto it.
		Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, null, targetRect, null);

		return dest;
	}

	/**
	 * Drawable对象转换为Bitmap对象
	 *
	 * @param drawable
	 *            Drawable对象
	 * @return Bitmap对象, 使用完后注意回收
	 */
	public static Bitmap convertDrawbaleToBitmap(Drawable drawable) {
		Bitmap dstBmp = null;
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();

			Bitmap.Config config =
					(drawable.getOpacity() != PixelFormat.OPAQUE) ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
			dstBmp = Bitmap.createBitmap(width, height, config);
			Canvas canvas = new Canvas(dstBmp);
			drawable.setBounds(0, 0, width, height);
			drawable.draw(canvas);
		}
		return dstBmp;
	}

	/**
	 * 获取带圆角的图片
	 *
	 * @param srcBmp
	 *            原图
	 * @param round
	 *            圆角半径
	 * @param roundColor
	 *            圆角填充颜色
	 * @return 带圆角的Bitmap图片
	 */
	public static Bitmap createRoundCornerBitmap(Bitmap srcBmp, float round, int roundColor) {
		Bitmap dstBmp = null;
		if (isBitmapAvailable(srcBmp)) {
			int width = srcBmp.getWidth();
			int height = srcBmp.getHeight();

			dstBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			Rect rect = new Rect(0, 0, width, height);
			Canvas canvas = new Canvas(dstBmp);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(roundColor);

			canvas.drawARGB(0, 0, 0, 0);
			canvas.drawRoundRect(new RectF(rect), round, round, paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(srcBmp, rect, rect, paint);
		}
		return dstBmp;
	}

	/**
	 * 获取带倒影的图片
	 *
	 * @param srcBmp
	 *            原图
	 * @param gap
	 *            原图与倒影间的空隙
	 * @return 带倒影效果的Bitmap图片
	 */
	public static Bitmap createReflectionBitmap(Bitmap srcBmp, int gap) {
		Bitmap dstBmp = null;
		if (isBitmapAvailable(srcBmp)) {
			int width = srcBmp.getWidth();
			int height = srcBmp.getHeight();
			int tmpGap = Math.min(gap, height / 2);

			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);

			Bitmap tmpBmp = Bitmap.createBitmap(srcBmp, 0, height / 2, width, height / 2, matrix, false);
			dstBmp = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.RGB_565);

			Canvas canvas = new Canvas(dstBmp);
			Paint paint = new Paint();
			canvas.drawBitmap(srcBmp, 0, 0, null);
			canvas.drawRect(0, height, width, height + tmpGap, paint);
			canvas.drawBitmap(tmpBmp, 0, height + tmpGap, null);

			LinearGradient shader = new LinearGradient(0, srcBmp.getHeight(),
					0, dstBmp.getHeight() + tmpGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
			paint.setShader(shader);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
			canvas.drawRect(0, height, width, dstBmp.getHeight() + tmpGap, paint);

			if (tmpBmp != null) {
				tmpBmp.recycle();
				tmpBmp = null;
			}
		}
		return dstBmp;
	}

	public void recycleBitmap(int iconEyeVisible) {
		// TODO Auto-generated method stub
		if (mBitmapCachedMap.get(iconEyeVisible) != null) {
			Bitmap bitmap =  mBitmapCachedMap.get(iconEyeVisible);
			mBitmapCachedMap.remove(iconEyeVisible);
			try {
				bitmap.recycle();
				bitmap=null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
