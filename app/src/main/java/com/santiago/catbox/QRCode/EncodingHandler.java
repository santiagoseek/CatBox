package com.santiago.catbox.QRCode;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * Created by test on 16/2/2.
 */
public class EncodingHandler {
	public static Bitmap createQRImage(String str, int widthAndHeight){
		if(str == null || str.length()<1) return null;
		Bitmap bitmap = null;
		Hashtable<EncodeHintType,String> hints = new Hashtable<>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		try {
			BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE,widthAndHeight,widthAndHeight);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			int[] pixels = new int[width * height];

			for(int y=0;y<height;y++){
				for(int x=0;x<width;x++){
					if(matrix.get(x,y)){
						pixels[y*width + x] = 0xff000000;
					}else{
						pixels[y*width + x] = 0xffffffff;
					}
				}
			}
			bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels,0,width,0,0,width,height);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
