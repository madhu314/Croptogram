package com.appmogli.croptogram;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class HomeActivity extends FragmentActivity {

	private static final int ACTION_PICKUP_FROM_GALLERY = 1003;

	private boolean returnedFromPickup;
	private Uri pickedUri;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		// give an intent to gallery picker
		Intent intent = new Intent();
		intent.setType("image/jpeg");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, ACTION_PICKUP_FROM_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_PICKUP_FROM_GALLERY) {
			if (resultCode == Activity.RESULT_OK) {
				returnedFromPickup = true;
				pickedUri = data.getData();
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
			i.putExtra(CropActivity.INTENT_KEY_IMAGE_FILE_PATH, Utils.getFilePathFromUri(pickedUri, this));
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
			startActivity(i);
		}
	}

}
