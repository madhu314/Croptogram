package com.appmogli.croptogram;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
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
	private RectF canvasRect;

	public static interface GridTappedListener {
		public void onGridTapped(RectF rect);

		public Bitmap getBitmap(RectF rect);
	}

	public PhotoGridMakerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PhotoGridMakerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PhotoGridMakerView(Context context) {
		super(context);
		init();
	}

	private void init() {
		borderPaint.setColor(Color.WHITE);
		borderPaint.setStrokeWidth(4);
		borderPaint.setStyle(Style.STROKE);

		fillPaint.setColor(Color.WHITE);
		fillPaint.setStyle(Style.FILL);

		gridFillPaint.setColor(Color.DKGRAY);
		gridFillPaint.setStyle(Style.FILL);
//		if (!isInEditMode() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        }

	}

	public void setCanvasDimens(float width, float height, List<RectF> grids,
			GridTappedListener listener) {
		this.originalWidth = width;
		this.originalHeight = height;
		this.photoGrids = grids;
		photoGrids.addAll(grids);
		if(listener != null) {
			gestureDetector = new GestureDetector(getContext(), this);
			this.gridTappedListener = listener;
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(gridTappedListener != null) {
			return gestureDetector.onTouchEvent(event);
		} else {
			return super.onTouchEvent(event);
		}

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		translatedGrids.clear();
		float scaledWidth = 0;
		float scaledHeight = 0;

		float viewWidth = w;
		float viewHeight = h;

		if (originalWidth == 0) {
			originalWidth = viewWidth;
		}

		if (originalHeight == 0) {
			originalHeight = viewHeight;
		}

		if (originalWidth > originalHeight) {
			// landscape canvas
			// scale the canvas width and height
			scaledWidth = viewWidth;
			scaledHeight = scaledWidth * originalHeight / originalWidth;

			if (scaledHeight > viewHeight) {
				scaledHeight = viewHeight;
				scaledWidth = scaledHeight * originalWidth / originalHeight;
			}
		} else {
			// portrait canvas
			scaledHeight = viewHeight;
			scaledWidth = scaledHeight * originalWidth / originalHeight;

			if (scaledWidth > viewWidth) {
				scaledWidth = viewWidth;
				scaledHeight = scaledWidth * originalHeight / originalWidth;
			}

		}
		float viewCenterX = (getRight() + getLeft()) / 2f;
		float viewCenterY = (getBottom() + getTop()) / 2f;

		canvasRect = new RectF(viewCenterX - scaledWidth / 2f, viewCenterY
				- scaledHeight / 2f, viewCenterX + scaledWidth / 2f,
				viewCenterY + scaledHeight / 2f);
		for (RectF origRect : photoGrids) {
			RectF translatedRect = translateRect(origRect, canvasRect,
					scaledWidth, scaledHeight);
			translatedGrids.add(translatedRect);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		// draw the canvas of the required size

		canvas.drawRect(canvasRect, fillPaint);
		canvas.drawRect(canvasRect, borderPaint);
		// now draw rectangles or bitmaps
		for (RectF translatedRect : translatedGrids) {

			canvas.drawRect(translatedRect, gridFillPaint);
			
			if(gridTappedListener != null) {
				// get the translated rect
				Bitmap bm = gridTappedListener.getBitmap(photoGrids
						.get(translatedGrids.indexOf(translatedRect)));
				if (bm != null) {
					bm = Bitmap.createScaledBitmap(bm,
							Math.round(translatedRect.width()),
							Math.round(translatedRect.height()), true);
					canvas.drawBitmap(bm, translatedRect.left, translatedRect.top,
							fillPaint);
				}
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
			gridTappedListener.onGridTapped(photoGrids.get(translatedGrids
					.indexOf(tappedRect)));
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int minw = getPaddingLeft() + getPaddingRight()
				+ getSuggestedMinimumWidth();
		int w = resolveSizeAndState(minw, widthMeasureSpec, 1);
		int minh = getPaddingTop() + getPaddingBottom()
				+ getSuggestedMinimumHeight();
		int h = resolveSizeAndState(minh, heightMeasureSpec, 1);
		
		setMeasuredDimension(w, h);
	}

	public void destroy() {
		gestureDetector = null;
		photoGrids.clear();
		translatedGrids.clear();
		gridTappedListener = null;
	}

}
