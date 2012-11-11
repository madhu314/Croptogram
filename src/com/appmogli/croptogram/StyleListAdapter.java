package com.appmogli.croptogram;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

public class StyleListAdapter extends BaseAdapter {

	private ArrayList<ArrayList<RectF>> styles = new ArrayList<ArrayList<RectF>>();
	private Context context = null;
	public static final float CANVAS_WIDTH = 851f;
	public static final float CANVAS_HEIGHT = 315f;

	public StyleListAdapter(Context context) {
		this.context = context;

		// load styles
		ArrayList<RectF> style1 = new ArrayList<RectF>();
		TestStyles.oneToTwo(style1);
		styles.add(style1);

		ArrayList<RectF> style2 = new ArrayList<RectF>();
		TestStyles.oneToFour(style2);
		styles.add(style2);

		ArrayList<RectF> style3 = new ArrayList<RectF>();
		TestStyles.oneToNine(style3);
		styles.add(style3);

		ArrayList<RectF> style4 = new ArrayList<RectF>();
		TestStyles.twoThirds(style4);
		styles.add(style4);

		ArrayList<RectF> style5 = new ArrayList<RectF>();
		TestStyles.centeredSquare(style5);
		styles.add(style5);

		ArrayList<RectF> style6 = new ArrayList<RectF>();
		TestStyles.profileSplitter(style6);
		styles.add(style6);

		ArrayList<RectF> style7 = new ArrayList<RectF>();
		TestStyles.twoThirdsEqual(style7);
		styles.add(style7);

		ArrayList<RectF> style8 = new ArrayList<RectF>();
		TestStyles.threes(style8);
		styles.add(style8);

		ArrayList<RectF> style9 = new ArrayList<RectF>();
		TestStyles.twoPowerGp(style9);
		styles.add(style9);

	}

	@Override
	public int getCount() {
		return styles.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//this is really bad, we gotta reuse the view, but for some reason our dear view is not redrawing itself
		FrameLayout view = null;
		view = (FrameLayout) LayoutInflater.from(context).inflate(
				R.layout.activity_style_list_item, null);
		PhotoGridMakerView girdMaker = (PhotoGridMakerView) view
				.findViewById(R.id.activity_style_list_item_item);
		girdMaker.setCanvasDimens(CANVAS_WIDTH, CANVAS_HEIGHT,
				styles.get(position), null);
		return view;
	}

	public ArrayList<RectF> getStyle(int position) {
		return styles.get(position);
	}

}
