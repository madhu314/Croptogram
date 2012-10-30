package com.appmogli.croptogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CropView extends View {

	private static final String TAG = null;
	private RectF imageRect = null;
	private final Paint mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint profileBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint profileFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private RectF cropRect = null;

	private int dragMode = 1;
	private int mode = 0;

	private PointF startPoint = null;
	private Bitmap profileBitmap = null;
	private CropStatusListener listener = null;
	private float origWidth;
	private float origHeight;

	public static interface CropStatusListener {
		public void startedDragging();

		public void stoppedDragging();
	}

	public CropView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
	}

	public CropView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public CropView(Context context) {
		super(context);
		initPaint();
	}

	public void setCropStatusListener(CropStatusListener listener) {
		this.listener = listener;
	}

	private void initPaint() {
		mFocusPaint.setARGB(0xA0, 0x05, 0x05, 0x05);
		whitePaint.setColor(Color.WHITE);
		whitePaint.setStrokeWidth(2);
		whitePaint.setStyle(Style.STROKE);

		profileBorderPaint.setColor(Color.WHITE);
		profileBorderPaint.setStrokeWidth(4);
		profileBorderPaint.setStyle(Style.STROKE);

		profileFillPaint.setColor(Color.LTGRAY);
		profileFillPaint.setStyle(Style.FILL);

	}

	public RectF getCropRect() {
		return cropRect;
	}

	public RectF getImageRect() {
		return imageRect;
	}

	public void setImageDrawnRect(RectF rect, float width, float height) {
		this.origWidth = width;
		this.origHeight = height;
		this.imageRect = rect;
		float cropWidth = rect.width();
		float cropHeight = rect.width() * origHeight / origWidth;
		
		if (origWidth < origHeight) {
			cropHeight = rect.height();
			cropWidth = rect.height() * origWidth / origHeight;
		}

		
		cropRect = new RectF();
		cropRect.left = imageRect.centerX() - cropWidth / 2;
		cropRect.right = imageRect.centerX() + cropWidth / 2;
		cropRect.top = imageRect.centerY() - cropHeight / 2;
		cropRect.bottom = imageRect.centerY() + cropHeight / 2;

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (cropRect != null) {
			canvas.save();
			Path path = new Path();
			path.addRect(cropRect, Direction.CW);
			canvas.clipPath(path, Op.DIFFERENCE);
			canvas.drawRect(imageRect, mFocusPaint);

			canvas.restore();
			canvas.drawRect(cropRect, whitePaint);

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();
		boolean cropRectHit = cropRect.contains(x, y);

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// Log.d(TAG, "action down received");
			startPoint = new PointF(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.d(TAG, "action move received");
			if (cropRectHit) {
				// Log.d(TAG, "action move received with rect hit");
				// calculate the distance moved in y direction
				if (updateRects(startPoint, x, y)) {
					startPoint = new PointF(x, y);
					if (listener != null) {
						listener.startedDragging();
					}
					invalidate();
				} else {
					// Log.d(TAG, "cannot redraw now");
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// Log.d(TAG, "action up received");
			if (listener != null) {
				listener.stoppedDragging();
			}
			break;
		}

		return true;
	}

	private boolean updateRects(PointF startPoint, float x, float y) {

		float yMovedDist = y - startPoint.y;
		boolean yToRet = true;
		boolean xToRet = true;

		if (yMovedDist > 0) {
			if (cropRect.bottom == imageRect.bottom) {
				yToRet = false;
			}
			// moving down -- so check bottom border
			if (cropRect.bottom + yMovedDist > imageRect.bottom) {
				yMovedDist = imageRect.bottom - cropRect.bottom;
			}

		} else {
			// moving up -- check top border

			if (cropRect.top == imageRect.top) {
				yToRet = false;
			}

			if (cropRect.top + yMovedDist < imageRect.top) {
				yMovedDist = imageRect.top - cropRect.top;
			}

		}

		float xMovedDist = x - startPoint.x;

		if (xMovedDist > 0) {
			if (cropRect.right == imageRect.right) {
				xToRet = false;
			}
			// moving right -- so check right border
			if (cropRect.right + xMovedDist > imageRect.right) {
				xMovedDist = imageRect.right - cropRect.right;
			}

		} else {
			// moving left -- check left border

			if (cropRect.left == imageRect.left) {
				xToRet = false;
			}

			if (cropRect.left + xMovedDist < imageRect.left) {
				xMovedDist = imageRect.left - cropRect.left;
			}

		}

		if (yToRet) {
			cropRect.top += yMovedDist;
			cropRect.bottom += yMovedDist;

		}

		if (xToRet) {
			cropRect.left += xMovedDist;
			cropRect.right += xMovedDist;
		}

		return xToRet || yToRet;

	}

}
