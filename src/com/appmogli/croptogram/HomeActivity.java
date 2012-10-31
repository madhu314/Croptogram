package com.appmogli.croptogram;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class HomeActivity extends FragmentActivity {

	private static final int ACTION_PICKUP_FROM_GALLERY = 1003;
	private static final int ACTION_CROP_PICTURE = 1004;
	private boolean returnedFromPickup;
	private boolean returnedFromCrop;
	private Uri pickedUri;
	private ImageView imageView = null;
	private String cropPath;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_home);
		imageView = (ImageView) findViewById(R.id.activity_home_image);
		pickUpAndCrop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_PICKUP_FROM_GALLERY) {
			if (resultCode == Activity.RESULT_OK) {
				returnedFromPickup = true;
				pickedUri = data.getData();
			}
		} else if (requestCode == ACTION_CROP_PICTURE) {
			if (resultCode == Activity.RESULT_OK) {
				returnedFromCrop = true;
				cropPath = data
						.getStringExtra(CropActivity.INTENT_KEY_CROP_TO_PATH);
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
			i.putExtra(CropActivity.INTENT_KEY_CROP_WIDTH, 300f);
			i.putExtra(CropActivity.INTENT_KEY_CROP_HEIGHT, 500f);
			startActivityForResult(i, ACTION_CROP_PICTURE);
		}

		if (returnedFromCrop) {
			imageView.setImageURI(Uri.parse("file://" + cropPath));
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

}
