package com.appmogli.croptogram;

import io.filepicker.FPService;
import io.filepicker.FilePicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PickAndCropActivity extends FragmentActivity {

	private static final int ACTION_PICKUP_FROM_GALLERY = 1003;
	private static final int ACTION_CROP_PICTURE = 1004;
	private boolean returnedFromPickup;
	private boolean returnedFromCrop;
	private Uri pickedUri;
	// private ImageView imageView = null;
	private String cropPath;

	public final static String INENT_REQUIRED_HEIGHT = "requiredHeight";
	public final static String INENT_REQUIRED_WIDTH = "requiredWidth";

	private float requiredWidth = -1f;
	private float requiredHeight = -1f;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_and_crop);
		if (savedInstanceState == null) {
			requiredHeight = getIntent().getFloatExtra(INENT_REQUIRED_HEIGHT,
					-1f);
			requiredWidth = getIntent()
					.getFloatExtra(INENT_REQUIRED_WIDTH, -1f);

			if (requiredHeight == -1f || requiredWidth == -1f) {
				setResult(RESULT_CANCELED);
				finish();
			}
		} else {
			requiredHeight = savedInstanceState.getFloat(INENT_REQUIRED_HEIGHT);
			requiredWidth = savedInstanceState.getFloat(INENT_REQUIRED_WIDTH);
		}
		pickUpAndCrop();
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
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yy-MM-dd-HH-mm-ss");
			File toPath = new File(getExternalCacheDir(),
					formatter.format(new Date()) + ".jpeg");
			try {
				toPath.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i.putExtra(CropActivity.INTENT_KEY_CROP_TO_PATH, toPath.toString());
			i.putExtra(CropActivity.INTENT_KEY_CROP_WIDTH, requiredWidth);
			i.putExtra(CropActivity.INTENT_KEY_CROP_HEIGHT, requiredHeight);
			// imageView.setImageDrawable(new ColorDrawable(Color.GRAY));
			startActivityForResult(i, ACTION_CROP_PICTURE);
		}

		if (returnedFromCrop) {
			returnedFromCrop = false;
			// imageView.setImageURI(Uri.parse("file://" + cropPath));
			Intent i = new Intent();
			i.setData(Uri.parse("file://" + cropPath));
			setResult(RESULT_OK, i);
			finish();
			cropPath = null;
		}
	}

	private void pickUpAndCrop() {

		// present local gallery and cloud service dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose From").setItems(
				new String[] { "Local Gallery", "Cloud Services" },
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							 Intent intent = new Intent();
							 intent.setType("image/jpeg");
							 intent.setAction(Intent.ACTION_GET_CONTENT);
							 startActivityForResult(intent, ACTION_PICKUP_FROM_GALLERY);
						} else {
							// give an intent to gallery picker
							Intent intent = new Intent(PickAndCropActivity.this, FilePicker.class);
							intent.setType("image/jpeg");
							intent.putExtra("services", new String[] { FPService.DROPBOX,
									FPService.GDRIVE, FPService.BOX, FPService.FACEBOOK });
							startActivityForResult(intent, ACTION_PICKUP_FROM_GALLERY);
						}
					}
				});

		builder.show();

		
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
