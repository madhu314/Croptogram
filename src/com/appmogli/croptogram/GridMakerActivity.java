package com.appmogli.croptogram;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GridMakerActivity extends Activity {

	private PhotoGridMakerView gridMakerView = null;
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
		Set<RectF> rectSet = new HashSet<RectF>();
		rectSet.add(rect1);
		rectSet.add(rect2);
		rectSet.add(rect3);
		gridMakerView.setCanvasDimens(815, 315, rectSet);
		
	}
}
