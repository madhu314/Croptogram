package com.appmogli.croptogram;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appmogli.croptogram.CropView.CropStatusListener;

public class CropActivity extends Activity implements CropStatusListener {

	private static final String TAG = "CropActivity";
	public static final String INTENT_KEY_IMAGE_FILE_PATH = "imageFilePath";
	public static final String INTENT_KEY_CROP_WIDTH = "cropWidth";
	public static final String INTENT_KEY_CROP_HEIGHT = "cropHeight";
	public static final String INTENT_KEY_CROP_TO_PATH = "cropToPath";
	
	 
	private ImageView imageView = null;
	private float[] matrixValues = new float[9];
	private LinearLayout controlPanel = null;
	private boolean controlPanelHidden;
	private Button doneText = null;
	private Button cancelText = null;
	private String filePath;
	private CropView cropView = null;
	private float cropWidth;
	private float cropHeight;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_crop);
		
		imageView = (ImageView) findViewById(R.id.activity_crop_image_view);
		cropView = (CropView) findViewById(R.id.activity_crop_crop_view);
		controlPanel = (LinearLayout) findViewById(R.id.activity_crop_control_panel);
		doneText = (Button) findViewById(R.id.activity_crop_done_button);
		cancelText = (Button) findViewById(R.id.activity_crop_cancel_button);
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int screenHeight = getResources().getDisplayMetrics().heightPixels;
		int reqSize = screenHeight;
		if (screenHeight > screenWidth) {
			reqSize = screenWidth;
		}
		filePath = getIntent().getStringExtra(INTENT_KEY_IMAGE_FILE_PATH);
		cropWidth = getIntent().getFloatExtra(INTENT_KEY_CROP_WIDTH, 0f);
		cropHeight = getIntent().getFloatExtra(INTENT_KEY_CROP_HEIGHT, 0f);
		String cropToFilePath = getIntent().getStringExtra(INTENT_KEY_CROP_TO_PATH);
		
		//if filepath is null, send the user a message
		if(filePath == null || cropWidth == 0.0 || cropHeight == 0.0 || cropToFilePath == null) {
			// picture does not meet minimum size requirements
			// alert dialog
			Builder builder = new Builder(this);
			builder.setMessage(getString(R.string.could_not_load_file));
			builder.setCancelable(false);
			builder.setPositiveButton("Ok", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			builder.show();

		}
		
		/*
		 * get the size of the image, make sure that it is atleast greater than
		 * fb cover size
		 */
		Options justDecodeBounds = new Options();
		justDecodeBounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, justDecodeBounds);
		if (justDecodeBounds.outWidth < cropWidth
				|| justDecodeBounds.outHeight < cropHeight) {
			// picture does not meet minimum size requirements
			// alert dialog
			Builder builder = new Builder(this);
			builder.setMessage(getString(R.string.picture_too_small));
			builder.setCancelable(false);
			builder.setPositiveButton("Ok", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			builder.show();
		} else {
			Options opts = Utils.decodeBitmapOptionsGeeky(filePath, reqSize);
			Bitmap bm = BitmapFactory.decodeFile(filePath, opts);
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(filePath);
			} catch (IOException e) {
			}
			if (exif != null) {
				bm = Utils.rotateIfNeeded(bm, exif);
			}
			imageView.setImageBitmap(bm);
			bm = null;

			imageView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {
							imageView.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
							Matrix imageMatrix = imageView.getImageMatrix();
							imageMatrix.getValues(matrixValues);
							float currentY = matrixValues[Matrix.MTRANS_Y];
							float currentX = matrixValues[Matrix.MTRANS_X];
							float currentScale = matrixValues[Matrix.MSCALE_X];
							int height = imageView.getDrawable()
									.getIntrinsicHeight();
							int width = imageView.getDrawable()
									.getIntrinsicWidth();
							float currentHeight = height * currentScale;
							float currentWidth = width * currentScale;
							float newX = currentX;
							float newY = currentY;

							RectF drawingRect = new RectF(newX, newY, newX
									+ currentWidth, newY + currentHeight);

							Log.d(TAG, "drawing rect is:" + drawingRect);
							cropView.setImageDrawnRect(drawingRect, cropWidth, cropHeight);
							cropView.setCropStatusListener(CropActivity.this);
						}
					});

			doneText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cropAndSaveBitmap();
				}
			});

			cancelText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});

		}

	}

	protected void cropAndSaveBitmap() {
		RectF imageRectF = cropView.getImageRect();
		RectF cropRectF = cropView.getCropRect();
		Bitmap orig = null;
		Bitmap cropped = null;
		if(cropWidth > cropHeight) {
			float percentageFromTop = (float) (cropRectF.top - imageRectF.top)
					/ (float) (imageRectF.height());
			Options opts = Utils.decodeBitmapOptionsGeeky(filePath,
					(int) cropWidth);
			opts.inSampleSize = opts.inSampleSize / 2;
			orig = BitmapFactory.decodeFile(filePath, opts);
			int yOffset = (int) (orig.getHeight() * percentageFromTop);
			int width = orig.getWidth();
			int height = (int) (cropHeight * width / cropWidth);
			cropped = Bitmap.createBitmap(orig, 0, yOffset, width, height);
		} else {
			float percentageFromLeft = (float) (cropRectF.left - imageRectF.left)
					/ (float) (imageRectF.width());
			Options opts = Utils.decodeBitmapOptionsGeeky(filePath,
					(int) cropHeight);
			opts.inSampleSize = opts.inSampleSize / 2;
			orig = BitmapFactory.decodeFile(filePath, opts);
			int xOffset = (int) (orig.getWidth() * percentageFromLeft);
			int height = orig.getHeight();
			int width = (int) (cropWidth * height / cropHeight);
			cropped = Bitmap.createBitmap(orig, xOffset, 0, width, height);
		}
		
		orig.recycle();
		orig = null;
		Bitmap scaledBm = Bitmap.createScaledBitmap(cropped,
				(int) cropWidth,
				(int) cropHeight, true);
		cropped.recycle();
		cropped = null;

		String imagePathStr = getIntent().getStringExtra(INTENT_KEY_CROP_TO_PATH);
		File imagePath = new File(imagePathStr);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imagePath);
			scaledBm.compress(CompressFormat.JPEG, 100, fos);
			scaledBm.recycle();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_crop, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
	

	@Override
	protected void onDestroy() {
		cropView.setCropStatusListener(null);
		super.onDestroy();
	}

	@Override
	public void stoppedDragging() {
		Animation fadeIn = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in);
		fadeIn.setDuration(40);
		fadeIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				controlPanel.setVisibility(View.VISIBLE);
				controlPanelHidden = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		controlPanel.startAnimation(fadeIn);

	}

	@Override
	public void startedDragging() {
		if (!controlPanelHidden) {
			controlPanelHidden = true;
			// if the view is visible
			Animation fadeOut = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out);
			fadeOut.setDuration(40);
			fadeOut.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					controlPanel.setVisibility(View.GONE);
				}
			});
			controlPanel.startAnimation(fadeOut);
		}

	}
	
	

	
	
}
