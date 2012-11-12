package com.appmogli.croptogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.appmogli.croptogram.PhotoGridMakerView.GridTappedListener;
import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.filters.FilterLoaderFactory;

public class GridMakerActivity extends Activity implements GridTappedListener {

	private static final String TAG = "GridMakerActivity";
	private PhotoGridMakerView gridMakerView = null;
	private List<RectF> gridList = new ArrayList<RectF>();;
	private float canvasWidth = 851;
	private float canvasHeight = 315;

	private static final int ACTION_RQUEST_CROP = 01;
	private static final int ACTION_REQUEST_FEATHER = 02;;

	private RectF tappedRect = null;

	private static final String SAVED_TAPPED_RECT = "tappedRectF";
	private static final String SAVE_PICKED_URI = "pickedUri";

	private Uri pickedUri;
	private HashMap<RectF, Bitmap> bitmapCache = new HashMap<RectF, Bitmap>();
	private HashMap<RectF, String> uriMap = new HashMap<RectF, String>();
	private final String imageName = "collage.jpeg";
	private boolean returnedFromFeather = false;

	private Button doneButton = null;
	private Button cancelButton = null;
	
	public static final String EXTRA_INTENT_GRID_STYLE = "gridStyle";
	public static final String EXTRA_STYLE_CREATED_PATH = "styleCreatedAt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_grid_maker);

		gridMakerView = (PhotoGridMakerView) findViewById(R.id.activity_grid_maker_photo_grid_view);
		doneButton = (Button) findViewById(R.id.activity_grid_maker_done_button);
		cancelButton = (Button) findViewById(R.id.activity_grid_maker_cancel_button);
		ArrayList<RectF> rects = getIntent().getParcelableArrayListExtra(EXTRA_INTENT_GRID_STYLE);
		RectF theCanvas = rects.get(0);
		canvasWidth = theCanvas.width();
		canvasHeight = theCanvas.height();
		for(int i = 1; i < rects.size(); i++) {
			gridList.add(rects.get(i));
		}
		gridMakerView
				.setCanvasDimens(canvasWidth, canvasHeight, gridList, this);
		if (savedInstanceState != null) {
			reloadSavedData(savedInstanceState);
		}
		setUpButtons();

	}

	private void setUpButtons() {
		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				File toPath = new File(getExternalCacheDir(), imageName);
				saveImage(toPath);
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	protected void saveImage(final File toPath) {
		final ProgressDialog loadingDialog = ProgressDialog.show(this,
				getString(R.string.processing),
				getString(R.string.saving_image), true);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// create a bitmap with the size of canvas
				Bitmap bm = Bitmap.createBitmap((int) canvasWidth,
						(int) canvasHeight, Config.ARGB_8888);
				Canvas canvas = new Canvas(bm);
				Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				backgroundPaint.setColor(Color.WHITE);
				canvas.drawRect(0, 0, bm.getWidth(), bm.getHeight(),
						backgroundPaint);
				// now add images one after the other
				for (RectF rect : gridList) {
					if (bitmapCache.containsKey(rect)) {
						canvas.drawBitmap(bitmapCache.get(rect), rect.left,
								rect.top, backgroundPaint);
					}
				}

				canvas.save();
				FileOutputStream outStream = null;

				try {
					outStream = new FileOutputStream(toPath);
					bm.compress(CompressFormat.JPEG, 100, outStream);
					
					//remove all cropped bitmaps
					for(RectF rect : uriMap.keySet()) {
						String uriString = uriMap.get(rect);
						if(uriString != null) {
							String filePath = Utils.getFilePathFromUri(Uri.parse(uriString), GridMakerActivity.this);
							File file = new File(filePath);
							file.delete();
						}
					}
				} catch (FileNotFoundException e) {
					// inform user
					Log.e(TAG, "Could not save the image");
				} finally {
					if (outStream != null) {
						try {
							outStream.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}

				bm.recycle();

				return null;
			}

			protected void onPostExecute(Void result) {
				loadingDialog.dismiss();
				Intent i = new Intent();
				i.setData(Uri.parse("file://" + toPath.toString()));
				setResult(RESULT_OK, i);
				finish();
			};
		}.execute();

	}

	@Override
	public void onGridTapped(RectF rect) {
		tappedRect = rect;
		Intent intent = new Intent(this, PickAndCropActivity.class);
		intent.putExtra(PickAndCropActivity.INENT_REQUIRED_HEIGHT,
				rect.height());
		intent.putExtra(PickAndCropActivity.INENT_REQUIRED_WIDTH, rect.width());
		startActivityForResult(intent, ACTION_RQUEST_CROP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_RQUEST_CROP) {
			if (resultCode == RESULT_OK) {
				pickedUri = data.getData();
				Log.d(TAG, "Uri received is:" + pickedUri);
			}
		} else if (requestCode == ACTION_REQUEST_FEATHER) {
			if (resultCode == RESULT_OK) {
				returnedFromFeather = true;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (tappedRect != null) {
			outState.putParcelable(SAVED_TAPPED_RECT, tappedRect);
		}
		for (RectF rect : gridList) {
			outState.putString(String.valueOf(rect.hashCode()),
					uriMap.get(rect));

		}

		if (pickedUri != null) {
			outState.putString(SAVE_PICKED_URI, pickedUri.toString());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		reloadSavedData(savedInstanceState);
		// realod
	}

	private void reloadSavedData(Bundle savedInstanceState) {
		tappedRect = savedInstanceState.getParcelable(SAVED_TAPPED_RECT);

		for (RectF rect : gridList) {
			String uriStr = savedInstanceState.getString(String.valueOf(rect
					.hashCode()));
			uriMap.put(rect, uriStr);

		}
		String pickedUriStr = savedInstanceState.getString(SAVE_PICKED_URI);
		if (pickedUriStr != null) {
			pickedUri = Uri.parse(pickedUriStr);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (pickedUri != null) {

			if (!returnedFromFeather) {
				returnedFromFeather = false;
				// start feather
				startFeather(pickedUri);

			} else {
				try {
					uriMap.put(tappedRect, pickedUri.toString());
					processBitmap(pickedUri, tappedRect);
				} catch (FileNotFoundException e) {
					Log.e(TAG, "Cannot draw image:" + e);
				} finally {
					pickedUri = null;
					returnedFromFeather = false;
				}

			}
		}
	}

	private void processBitmap(Uri uri, RectF rect)
			throws FileNotFoundException {
		InputStream stream = getContentResolver().openInputStream(uri);
		try {
			Bitmap bm = BitmapFactory.decodeStream(stream);
			bitmapCache.put(rect, bm);
			gridMakerView.invalidate();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	@Override
	public Bitmap getBitmap(RectF rect) {
		// TODO Auto-generated method stub
		Bitmap bm = bitmapCache.get(rect);
		if (bm == null) {
			String uriStr = uriMap.get(rect);
			if (uriStr != null) {
				Uri uri = Uri.parse(uriStr);
				try {
					processBitmap(uri, rect);
				} catch (FileNotFoundException e) {
					return null;
				}
			}
		}

		return bitmapCache.get(rect);
	}

	@Override
	protected void onDestroy() {
		gridMakerView.destroy();
		doneButton.setOnClickListener(null);
		cancelButton.setOnClickListener(null);
		bitmapCache.clear();
		super.onDestroy();

	}

	private void startFeather(Uri outputFilePath) {

		// Create the intent needed to start feather
		Intent newIntent = new Intent(this, FeatherActivity.class);

		// set the source image uri
		newIntent.setData(outputFilePath);

		// pass the required api_key and secret ( see
		// http://developers.aviary.com/ )
		// newIntent.putExtra( "API_KEY", API_KEY );

		// pass the uri of the destination image file (optional)
		// This will be the same uri you will receive in the onActivityResult
		newIntent.putExtra("output", outputFilePath);

		// format of the destination image (optional)
		newIntent.putExtra("output-format", Bitmap.CompressFormat.JPEG.name());

		// output format quality (optional)
		newIntent.putExtra("output-quality", 100);

		newIntent.putExtra("max-image-size",
				(int) Math.max(canvasWidth, canvasHeight));

		newIntent.putExtra("tools-list", new String[] {
				FilterLoaderFactory.Filters.ENHANCE.name(),
				FilterLoaderFactory.Filters.EFFECTS.name(),
				FilterLoaderFactory.Filters.BRIGHTNESS.name(),
				FilterLoaderFactory.Filters.CONTRAST.name(),
				FilterLoaderFactory.Filters.SATURATION.name(),
				FilterLoaderFactory.Filters.SHARPNESS.name(),
				FilterLoaderFactory.Filters.DRAWING.name(),
				FilterLoaderFactory.Filters.TEXT.name(),
				FilterLoaderFactory.Filters.MEME.name(),
				FilterLoaderFactory.Filters.WHITEN.name(),
				FilterLoaderFactory.Filters.BLEMISH.name(), });

		newIntent.putExtra("effect-enable-borders", false);
		newIntent.putExtra("effect-enable-external-pack", false);
		newIntent.putExtra("stickers-enable-external-pack", false);

		startActivityForResult(newIntent, ACTION_REQUEST_FEATHER);

	}
}
