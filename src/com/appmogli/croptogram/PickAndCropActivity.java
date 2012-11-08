package com.appmogli.croptogram;

import java.io.File;
import java.io.IOException;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class PickAndCropActivity extends FragmentActivity {

	private static final int ACTION_PICKUP_FROM_GALLERY = 1003;
	private static final int ACTION_CROP_PICTURE = 1004;
	private boolean returnedFromPickup;
	private boolean returnedFromCrop;
	private Uri pickedUri;
//	private ImageView imageView = null;
	private String cropPath;
	
	public final static String INENT_REQUIRED_HEIGHT = "requiredHeight";
	public final static String INENT_REQUIRED_WIDTH = "requiredWidth";
	
	private float requiredWidth = -1f;
	private float requiredHeight = -1f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState == null) {
			requiredHeight = getIntent().getFloatExtra(INENT_REQUIRED_HEIGHT, -1f);
			requiredWidth = getIntent().getFloatExtra(INENT_REQUIRED_WIDTH, -1f);
			
			if(requiredHeight == -1f || requiredWidth == -1f) {
				setResult(RESULT_CANCELED);
				finish();
			}
			pickUpAndCrop();
		} else {
			requiredHeight = savedInstanceState.getFloat(INENT_REQUIRED_HEIGHT);
			requiredWidth = savedInstanceState.getFloat(INENT_REQUIRED_WIDTH);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_PICKUP_FROM_GALLERY) {
			if (resultCode == Activity.RESULT_OK) {
				returnedFromPickup = true;
				pickedUri = data.getData();
			} else {
				finish();
			}
		} else if (requestCode == ACTION_CROP_PICTURE) {
			if (resultCode == Activity.RESULT_OK) {
				returnedFromCrop = true;
				cropPath = data
						.getStringExtra(CropActivity.INTENT_KEY_CROP_TO_PATH);
			} else {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (returnedFromPickup) {
			returnedFromPickup = false;
			// Log.d(TAG, "pickedUp image at " + pickedUri);
			Intent i = new Intent(this, CropActivity.class);
			i.putExtra(CropActivity.INTENT_KEY_IMAGE_FILE_PATH,
					Utils.getFilePathFromUri(pickedUri, this));
			File toPath = new File(getExternalCacheDir(), "gen.jpeg");
			try {
				toPath.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i.putExtra(CropActivity.INTENT_KEY_CROP_TO_PATH, toPath.toString());
			i.putExtra(CropActivity.INTENT_KEY_CROP_WIDTH, requiredWidth);
			i.putExtra(CropActivity.INTENT_KEY_CROP_HEIGHT, requiredHeight);
//			imageView.setImageDrawable(new ColorDrawable(Color.GRAY));
			startActivityForResult(i, ACTION_CROP_PICTURE);
		}

		if (returnedFromCrop) {
			returnedFromCrop = false;
//			imageView.setImageURI(Uri.parse("file://" + cropPath));
			Intent i = new Intent();
			i.setData(Uri.parse("file://" + cropPath));
			setResult(RESULT_OK, i);
			finish();
			cropPath = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.activity_home_menu_crop) {
			pickUpAndCrop();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void pickUpAndCrop() {
		// give an intent to gallery picker
		Intent intent = new Intent();
		intent.setType("image/jpeg");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, ACTION_PICKUP_FROM_GALLERY);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putFloat(INENT_REQUIRED_WIDTH, requiredWidth);
		outState.putFloat(INENT_REQUIRED_HEIGHT, requiredHeight);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		requiredHeight = savedInstanceState.getFloat(INENT_REQUIRED_HEIGHT);
		requiredWidth = savedInstanceState.getFloat(INENT_REQUIRED_WIDTH);
	}

}