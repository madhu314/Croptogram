package com.appmogli.croptogram;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.appmogli.croptogram.PhotoGridMakerView.GridTappedListener;

public class GridMakerActivity extends Activity implements GridTappedListener {

	private static final String TAG = "GridMakerActivity";
	private PhotoGridMakerView gridMakerView = null;
	private List<RectF> gridList = new ArrayList<RectF>();;
	private float canvasWidth = 851;
	private float canvasHeight = 315;
	
	private static final int RQUEST_CROP_ACTIVITY = 01;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_grid_maker);
		
		gridMakerView = (PhotoGridMakerView) findViewById(R.id.activity_grid_maker_photo_grid_view);
		
		RectF rect1 = new RectF(0, 0, 564, 315);
		RectF rect2 = new RectF(566, 0, 851, 218);
		RectF rect3 = new RectF(566, 220, 851, 315);
		gridList.add(rect1);
		gridList.add(rect2);
		gridList.add(rect3);
		gridMakerView.setCanvasDimens(canvasWidth, canvasHeight, gridList, this);
		
	}
	
	@Override
	public void onGridTapped(RectF rect) {
		Log.d(TAG, "rect tapped is:" + rect.toString());
		Intent intent = new Intent(this, PickAndCropActivity.class);
		intent.putExtra(PickAndCropActivity.INENT_REQUIRED_HEIGHT, rect.height());
		intent.putExtra(PickAndCropActivity.INENT_REQUIRED_WIDTH, rect.width());
		startActivityForResult(intent, RQUEST_CROP_ACTIVITY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RQUEST_CROP_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				Uri uri = data.getData();
				Log.d(TAG, "Uri received is:" + uri);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
