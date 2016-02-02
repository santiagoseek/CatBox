package com.santiago.catbox.QRCode;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

/**
 * Created by test on 16/2/2.
 */
public class ViewfinderResultPointCallback implements ResultPointCallback {

	private final ViewfinderView viewfinderView;

	public ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
		this.viewfinderView = viewfinderView;
	}

	@Override
	public void foundPossibleResultPoint(ResultPoint resultPoint) {
		viewfinderView.addPossibleResultPoint(resultPoint);
	}
}
