package com.appmogli.croptogram;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private RectF tappedRect = null;
	
	private static final String SAVED_TAPPED_RECT = "tappedRectF";
	
	private Uri pickedUri;
	private HashMap<RectF, Bitmap> bitmapCache = new HashMap<RectF, Bitmap>();
	private HashMap<RectF, String> uriMap = new HashMap<RectF, String>();
	
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
		
		if(savedInstanceState != null) {
			reloadSavedData(savedInstanceState);
		}
		
		
	}
	
	@Override
	public void onGridTapped(RectF rect) {
		Log.d(TAG, "rect tapped is:" + rect.toString());
		tappedRect = rect;
		Intent intent = new Intent(this, PickAndCropActivity.class);
		intent.putExtra(PickAndCropActivity.INENT_REQUIRED_HEIGHT, rect.height());
		intent.putExtra(PickAndCropActivity.INENT_REQUIRED_WIDTH, rect.width());
		startActivityForResult(intent, RQUEST_CROP_ACTIVITY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RQUEST_CROP_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				pickedUri = data.getData();
				Log.d(TAG, "Uri received is:" + pickedUri);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(tappedRect != null) {
			outState.putParcelable(SAVED_TAPPED_RECT, tappedRect);
		}
		for(RectF rect : gridList) {
			outState.putString(String.valueOf(rect.hashCode()), uriMap.get(rect));
			
		}
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		reloadSavedData(savedInstanceState);
		//realod 
	}
	
	private void reloadSavedData(Bundle savedInstanceState) {
		tappedRect = savedInstanceState.getParcelable(SAVED_TAPPED_RECT);
		
		for(RectF rect : gridList) {
			String uriStr = savedInstanceState.getString(String.valueOf(rect.hashCode()));
			uriMap.put(rect, uriStr);
			
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if(pickedUri != null) {
			try {
				uriMap.put(tappedRect, pickedUri.toString());
				processBitmap(pickedUri, tappedRect);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "Cannot draw image:" + e);
			}
			
			pickedUri = null;
		}
	}
	
	private void processBitmap(Uri uri, RectF rect) throws FileNotFoundException {
		InputStream stream = getContentResolver().openInputStream(uri);
		try {
			Bitmap bm = BitmapFactory.decodeStream(stream);
			bitmapCache.put(rect, bm);
			gridMakerView.invalidate();
		} finally {
			if(stream !=  null) {
				try {
					stream.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
	}

	@Override
	public Bitmap getBitmap(RectF rect) {
		// TODO Auto-generated method stub
		Bitmap bm = bitmapCache.get(rect);
		if(bm == null) {
			String uriStr = uriMap.get(rect);
			if(uriStr != null) {
				Uri uri = Uri.parse(uriStr);
				try {
					processBitmap(uri, rect);
				} catch (FileNotFoundException e) {
					return null;
				}
			}
		}
		
		return bitmapCache.get(rect);
	}
}
