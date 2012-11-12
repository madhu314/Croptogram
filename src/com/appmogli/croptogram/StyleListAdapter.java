package com.appmogli.croptogram;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class StyleListAdapter extends BaseAdapter {

	private ArrayList<ArrayList<RectF>> styles = new ArrayList<ArrayList<RectF>>();
	private Context context = null;
	public static final float CANVAS_WIDTH = 851f;
	public static final float CANVAS_HEIGHT = 315f;

	public StyleListAdapter(Context context) {
		this.context = context;

		ArrayList<RectF> style = new ArrayList<RectF>();
		TestStyles.theOne(style);
		styles.add(style);

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
		TestStyles.twoPowerGp(style8);
		styles.add(style8);
		
		ArrayList<RectF> style9 = new ArrayList<RectF>();
		TestStyles.invTwoPowerGp(style9);
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
		// this is really bad, we gotta reuse the view, but for some reason our
		// dear view is not redrawing itself
		LinearLayout view = null;
		if (convertView == null) {
			view = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.activity_style_list_item, null);
			PhotoGridMakerView girdMaker = (PhotoGridMakerView) view
					.findViewById(R.id.activity_style_list_item_item);
			girdMaker.setCanvasDimens(CANVAS_WIDTH, CANVAS_HEIGHT,
					styles.get(position), null);

		} else {
			view = (LinearLayout) convertView;
			PhotoGridMakerView girdMaker = (PhotoGridMakerView) view
					.findViewById(R.id.activity_style_list_item_item);
			girdMaker.reset(CANVAS_WIDTH, CANVAS_HEIGHT, styles.get(position));

		}

		view.postInvalidate();
		return view;
	}

	public ArrayList<RectF> getStyle(int position) {
		return styles.get(position);
	}

}
