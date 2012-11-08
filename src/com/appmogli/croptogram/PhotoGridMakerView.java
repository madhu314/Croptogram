package com.appmogli.croptogram;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PhotoGridMakerView extends View {

	private static final String TAG = "PhotoGridMakerView";
	private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint gridFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private float originalWidth;
	private float originalHeight;

	Set<RectF> photoGrids = new HashSet<RectF>();

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
		
		fillPaint.setColor(Color.BLUE);
		fillPaint.setStyle(Style.FILL);
		
		gridFillPaint.setColor(Color.RED);
		gridFillPaint.setStyle(Style.FILL);
	}

	public void setCanvasDimens(float width, float height, Set<RectF> grids) {
		this.originalWidth = width;
		this.originalHeight = height;
		this.photoGrids = grids;
		photoGrids.addAll(grids);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		// draw the canvas of the required size
		float scaledWidth = 0;
		float scaledHeight = 0;
		
		float viewWidth = getRight() - getLeft();
		float viewHeight = getBottom() - getTop();
		
		if(originalWidth == 0) {
			originalWidth = getRight() - getLeft();
		}
		
		if(originalHeight == 0) {
			originalHeight = getBottom() - getTop();
		}
		
		if (originalWidth > originalHeight) {
			// landscape canvas
			// scale the canvas width and height
			scaledWidth = getRight() - getLeft();
			scaledHeight = scaledWidth * originalHeight
					/ originalWidth;
			
			if(scaledHeight > viewHeight) {
				scaledHeight = viewHeight;
				scaledWidth = scaledHeight * originalWidth / originalHeight;
			}
		} else {
			// portrait canvas
			scaledHeight = getBottom() - getTop();
			scaledWidth = scaledHeight * originalWidth
					/ originalHeight;
			
			if(scaledWidth > viewWidth) {
				scaledWidth = viewWidth;
				scaledHeight = scaledWidth * originalHeight /originalWidth;
			}

		}
		float viewCenterX = (getRight() + getLeft()) / 2f;
		float viewCenterY = (getBottom() + getTop()) / 2f;

		RectF rect = new RectF(viewCenterX - scaledWidth / 2f, viewCenterY
				- scaledHeight / 2f, viewCenterX + scaledWidth / 2f,
				viewCenterY + scaledHeight / 2f);
		canvas.drawRect(rect, fillPaint);
		
		//now draw rectangles
		for(RectF grid : photoGrids) {
			RectF translatedRect = translateRect(grid, rect, scaledWidth, scaledHeight);
			canvas.drawRect(translatedRect, gridFillPaint);
		}
	}

	private RectF translateRect(RectF grid, RectF translatedOriginalRect, float scaledWidth,
			float scaledHeight) {
		RectF translatedRect = new RectF();
		//x are translated by width factor and y are translated by height factor
		float widthFactor = scaledWidth /originalWidth;
		float heightFactor = scaledHeight /originalHeight;
		translatedRect.left = grid.left * widthFactor;
		translatedRect.right = grid.right * widthFactor;
		translatedRect.top = grid.top * heightFactor;
		translatedRect.bottom = grid.bottom * heightFactor;
		
		//now translate the origin
		float translatedRectWidth = translatedRect.width();
		float translatedRectHeight = translatedRect.height();
		
		translatedRect.left += translatedOriginalRect.left;
		translatedRect.top += translatedOriginalRect.top;
		
		translatedRect.right = translatedRect.left + translatedRectWidth;
		translatedRect.bottom = translatedRect.top + translatedRectHeight;
		
		return translatedRect;
		
		
		
	}
}
