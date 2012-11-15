package com.appmogli.croptogram;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class StylePickerActivity extends Activity {
	
	private static final String TAG = "StylePickerActivity";
	private StyleListAdapter adapter = null;
	private ArrayList<RectF> selectedStyle = null;
	private final int ACTION_CREATE_STYLE = 01;
	private GridView gridView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_style_picker);
		gridView = (GridView) findViewById(R.id.activity_style_picker_gridview);
		setTitle(R.string.title_activity_style_list);
		adapter = new StyleListAdapter(this);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				List<RectF> selStyle = adapter.getStyle(position);
				selectedStyle = new ArrayList<RectF>();
				//add the first rect as entire canvas itself
				selectedStyle.add(new RectF(0,0, StyleListAdapter.CANVAS_WIDTH, StyleListAdapter.CANVAS_HEIGHT));
				selectedStyle.addAll(selStyle);
				Intent intent = new Intent(StylePickerActivity.this, GridMakerActivity.class);
				intent.putParcelableArrayListExtra(GridMakerActivity.EXTRA_INTENT_GRID_STYLE, selectedStyle);
				startActivityForResult(intent, ACTION_CREATE_STYLE);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == ACTION_CREATE_STYLE) {
			if(resultCode == RESULT_OK) {
				setResult(resultCode, data);
				finish();
				Log.d(TAG, "Style created at:" + data.toString());
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		gridView.setOnItemClickListener(null);
		gridView.setAdapter(null);
		super.onDestroy();
	}

}
