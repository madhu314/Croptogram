package com.appmogli.croptogram;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class PhotoGridMakerView extends View implements OnGestureListener {

	private static final String TAG = "PhotoGridMakerView";
	private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint gridFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private float originalWidth;
	private float originalHeight;

	private List<RectF> photoGrids = new ArrayList<RectF>();
	private List<RectF> translatedGrids = new ArrayList<RectF>();
	private GestureDetector gestureDetector = null;
	private GridTappedListener gridTappedListener = null;

	public static interface GridTappedListener {
		public void onGridTapped(RectF rect);

		public Bitmap getBitmap(RectF rect);
	}

	public PhotoGridMakerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
	}

	public PhotoGridMakerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public PhotoGridMakerView(Context context) {
		super(context);
		initPaint();
	}

	private void initPaint() {
		borderPaint.setColor(Color.WHITE);
		borderPaint.setStrokeWidth(4);
		borderPaint.setStyle(Style.STROKE);

		fillPaint.setColor(Color.WHITE);
		fillPaint.setStyle(Style.FILL);

		gridFillPaint.setColor(Color.RED);
		gridFillPaint.setStyle(Style.FILL);
		gestureDetector = new GestureDetector(getContext(), this);

	}

	public void setCanvasDimens(float width, float height, List<RectF> grids,
			GridTappedListener listener) {
		this.originalWidth = width;
		this.originalHeight = height;
		this.photoGrids = grids;
		photoGrids.addAll(grids);
		this.gridTappedListener = listener;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		// draw the canvas of the required size
		translatedGrids.clear();
		float scaledWidth = 0;
		float scaledHeight = 0;

		float viewWidth = getRight() - getLeft();
		float viewHeight = getBottom() - getTop();

		if (originalWidth == 0) {
			originalWidth = getRight() - getLeft();
		}

		if (originalHeight == 0) {
			originalHeight = getBottom() - getTop();
		}

		if (originalWidth > originalHeight) {
			// landscape canvas
			// scale the canvas width and height
			scaledWidth = getRight() - getLeft();
			scaledHeight = scaledWidth * originalHeight / originalWidth;

			if (scaledHeight > viewHeight) {
				scaledHeight = viewHeight;
				scaledWidth = scaledHeight * originalWidth / originalHeight;
			}
		} else {
			// portrait canvas
			scaledHeight = getBottom() - getTop();
			scaledWidth = scaledHeight * originalWidth / originalHeight;

			if (scaledWidth > viewWidth) {
				scaledWidth = viewWidth;
				scaledHeight = scaledWidth * originalHeight / originalWidth;
			}

		}
		float viewCenterX = (getRight() + getLeft()) / 2f;
		float viewCenterY = (getBottom() + getTop()) / 2f;

		RectF rect = new RectF(viewCenterX - scaledWidth / 2f, viewCenterY
				- scaledHeight / 2f, viewCenterX + scaledWidth / 2f,
				viewCenterY + scaledHeight / 2f);
		canvas.drawRect(rect, fillPaint);

		//now draw rectangles or bitmaps
		for (RectF origRect : photoGrids) {
			
			RectF translatedRect = translateRect(origRect, rect, scaledWidth,
					scaledHeight);
			translatedGrids.add(translatedRect);
			canvas.drawRect(translatedRect, gridFillPaint);
			
			// get the translated rect
			Bitmap bm = gridTappedListener.getBitmap(origRect);
			if (bm != null) {
				bm = Bitmap.createScaledBitmap(bm,
						Math.round(translatedRect.width()),
						Math.round(translatedRect.height()), true);
				canvas.drawBitmap(bm, translatedRect.left, translatedRect.top,
						fillPaint);
			}
		}

	}

	private RectF translateRect(RectF grid, RectF translatedOriginalRect,
			float scaledWidth, float scaledHeight) {
		RectF translatedRect = new RectF();
		// x are translated by width factor and y are translated by height
		// factor
		float widthFactor = scaledWidth / originalWidth;
		float heightFactor = scaledHeight / originalHeight;
		translatedRect.left = grid.left * widthFactor;
		translatedRect.right = grid.right * widthFactor;
		translatedRect.top = grid.top * heightFactor;
		translatedRect.bottom = grid.bottom * heightFactor;

		// now translate the origin
		float translatedRectWidth = translatedRect.width();
		float translatedRectHeight = translatedRect.height();

		translatedRect.left += translatedOriginalRect.left;
		translatedRect.top += translatedOriginalRect.top;

		translatedRect.right = translatedRect.left + translatedRectWidth;
		translatedRect.bottom = translatedRect.top + translatedRectHeight;

		return translatedRect;

	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// consume this event
		RectF tappedRect = null;
		for (RectF rect : translatedGrids) {
			if (rect.contains(e.getX(), e.getY())) {
				tappedRect = rect;
				break;
			}
		}
		if (tappedRect != null) {
			Log.d(TAG, "Rect at : " + translatedGrids.indexOf(tappedRect)
					+ " hit ");
			gridTappedListener.onGridTapped(photoGrids.get(translatedGrids
					.indexOf(tappedRect)));
		}
		return true;
	}

}
