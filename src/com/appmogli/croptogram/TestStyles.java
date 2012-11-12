package com.appmogli.croptogram;

import java.util.List;

import android.graphics.RectF;

public class TestStyles {

	public static final void twoThirds(List<RectF> gridList) {
		RectF rect1 = new RectF(0, 0, 564, 315);
		RectF rect2 = new RectF(566, 0, 851, 218);
		RectF rect3 = new RectF(566, 220, 851, 315);
		gridList.add(rect1);
		gridList.add(rect2);
		gridList.add(rect3);
	}

	public static final void twoThirdsEqual(List<RectF> gridList) {
		RectF rect1 = new RectF(0, 0, 564, 315);
		RectF rect2 = new RectF(566, 0, 851, 157);
		RectF rect3 = new RectF(566, 159, 851, 315);
		gridList.add(rect1);
		gridList.add(rect2);
		gridList.add(rect3);
	}

	public static final void oneToNine(List<RectF> gridList) {
		RectF rect = new RectF(5, 5, 531, 310);
		gridList.add(rect);
		float startLeft = 531;
		for (int i = 0; i < 3; i++) {
			float startTop = 0;
			for (int j = 0; j < 3; j++) {
				float left = startLeft + 5;
				float right = left + 100;
				float top = startTop + 2.5f;

				if (j == 0) {
					top = top + 2.5f;
				}
				float bottom = top + 100;
				rect = new RectF(left, top, right, bottom);
				gridList.add(rect);
				startTop = bottom;

			}
			startLeft += 105;
		}
	}

	public static final void threes(List<RectF> gridList) {
		float left = 1f;
		float top = 1f;
		float right = 283f;
		float bottom = 314f;
		RectF rect = new RectF(left, top, right, bottom);
		gridList.add(rect);
		for (int i = 0; i < 2; i++) {
			left = right + 1.5f;
			right = left + 282f;
			rect = new RectF(left, top, right, bottom);
			gridList.add(rect);
		}
	}

	public static final void twoPowerGp(List<RectF> gridList) {
		float left = 1f;
		float top = 1f;
		float right = 480f;
		float bottom = 314f;
		RectF rect = new RectF(left, top, right, bottom);
		gridList.add(rect);

		left = right + 6f;
		right = left + 240f;
		rect = new RectF(left, top, right, bottom);
		gridList.add(rect);

		left = right + 3f;
		right = left + 120f;

		rect = new RectF(left, top, right, bottom);
		gridList.add(rect);
	}
	
	public static final void invTwoPowerGp(List<RectF> gridList) {
		float left = 1f;
		float top = 1f;
		float right = 120f;
		float bottom = 314f;
		RectF rect = new RectF(left, top, right, bottom);
		gridList.add(rect);

		left = right + 3f;
		right = left + 240f;
		rect = new RectF(left, top, right, bottom);
		gridList.add(rect);

		left = right + 6f;
		right = left + 480f;

		rect = new RectF(left, top, right, bottom);
		gridList.add(rect);
	}


	public static final void centeredSquare(List<RectF> gridList) {
		float left = 0f;
		float top = 0f;
		float right = 266f;
		float bottom = 315f;

		RectF rect = new RectF(left, top, right, bottom);
		gridList.add(rect);

		left = right + 2f;
		right = left + 315f;
		rect = new RectF(left, top, right, bottom);
		gridList.add(rect);

		left = right + 2f;
		right = left + 266f;
		rect = new RectF(left, top, right, bottom);
		gridList.add(rect);

	}

	public static final void profileSplitter(List<RectF> gridList) {
		float left = 0f;
		float top = 0f;
		float right = 188f;
		float bottom = 315f;

		RectF rect = new RectF(left, top, right, bottom);
		gridList.add(rect);

		left = right + 4f;
		right = left + 659f;
		rect = new RectF(left, top, right, bottom);
		gridList.add(rect);
	}

	public static final void oneToFour(List<RectF> gridList) {
		RectF rect = new RectF(5, 5, 536, 310);
		gridList.add(rect);
		float startLeft = 536;
		for (int i = 0; i < 2; i++) {
			float startTop = 0f;
			for (int j = 0; j < 2; j++) {
				float left = startLeft + 5;
				float right = left + 150;
				float top = startTop + 5f;
				float bottom = top + 150;
				rect = new RectF(left, top, right, bottom);
				gridList.add(rect);
				startTop = bottom;
			}
			startLeft += 155;
		}
	}

	public static final void oneToTwo(List<RectF> gridList) {
		RectF rect = new RectF(5, 5, 691, 310);
		gridList.add(rect);
		float startLeft = 691;
		for (int i = 0; i < 1; i++) {
			float startTop = 0f;
			for (int j = 0; j < 2; j++) {
				float left = startLeft + 5;
				float right = left + 150;
				float top = startTop + 5f;
				float bottom = top + 150;
				rect = new RectF(left, top, right, bottom);
				gridList.add(rect);
				startTop = bottom;
			}
			startLeft += 155;
		}
	}
	
	public static final void theOne(List<RectF> gridList) {
		RectF rect = new RectF(0, 0, 851, 315);
		gridList.add(rect);

	}
}
