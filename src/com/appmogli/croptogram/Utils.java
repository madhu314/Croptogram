package com.appmogli.croptogram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class Utils {

	private static final String TAG = "Utils";

	public static final Uri getUriFromFilepath(String filePath) {
		return Uri.parse(new File(filePath).toString());
	}

	public static final String getFilePathFromUri(Uri contentUri,
			Context context) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = null;
		if (contentUri.getScheme().startsWith("file")) {
			String filePath = contentUri.getSchemeSpecificPart();
			return filePath;
		}
		try {
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			if (cursor == null) {
				return null;
			}
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static Bitmap rotateIfNeeded(Bitmap bm, ExifInterface exif) {
		if (bm == null) {
			return bm;
		}
		float rotation = getRotation(exif);

		// Getting width & height of the given image.
		int w = bm.getWidth();
		int h = bm.getHeight();
		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(rotation);
		// Rotating Bitmap
		Bitmap rotatedBMP = Bitmap.createBitmap(bm, 0, 0, w, h, mtx, true);
		return rotatedBMP;
	}

	public static float getRotation(ExifInterface exif) {
		float rotation = 0;
		int exifOrientation = exif.getAttributeInt(
				ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_UNDEFINED);
		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotation = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotation = 270;
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotation = 90;
			break;
		case ExifInterface.ORIENTATION_NORMAL:
		case ExifInterface.ORIENTATION_TRANSPOSE:
		case ExifInterface.ORIENTATION_TRANSVERSE:
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
		default:
			return rotation;
		}

		return rotation;
	}

	public static Bitmap rotateIfNeeded(Bitmap bm, int rotationGiven) {
		if (bm == null) {
			return bm;
		}
		float rotation = 0;
		switch (rotationGiven) {
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotation = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotation = 270;
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotation = 90;
			break;
		case ExifInterface.ORIENTATION_NORMAL:
		case ExifInterface.ORIENTATION_TRANSPOSE:
		case ExifInterface.ORIENTATION_TRANSVERSE:
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
		default:
			return bm;
		}

		// Getting width & height of the given image.
		int w = bm.getWidth();
		int h = bm.getHeight();
		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(rotation);
		// Rotating Bitmap
		Bitmap rotatedBMP = Bitmap.createBitmap(bm, 0, 0, w, h, mtx, true);
		bm.recycle();
		return rotatedBMP;
	}

	public static Options decodeBitmapOptionsGeeky(String filePath, int reqSize) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		int scale = 1;
		if (o.outHeight > reqSize && o.outWidth > reqSize) {
			double scaleTmp = Math.pow(
					2.0,
					(int) Math.round(Math.log((double) reqSize
							/ (double) Math.max(o.outHeight, o.outWidth))
							/ Math.log(0.5)));
			scale = (int) Math.ceil(scaleTmp);
		}
		o = null;
		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();

		o2.inSampleSize = scale;
		o2.inPurgeable = true;
		o2.inInputShareable = false;
		o2.inDither = true;

		return o2;
	}

	public static Options decodeBitmapOptionsGeeky(InputStream is, int reqSize) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is);
		try {
			is.reset();
		} catch (IOException e) {
			Log.e(TAG, "decodeBitmapOptionsGeeky(InputStream)", e);
		}

		int scale = 1;
		if (o.outHeight > reqSize && o.outWidth > reqSize) {
			double scaleTmp = Math.pow(
					2.0,
					(int) Math.round(Math.log((double) reqSize
							/ (double) Math.max(o.outHeight, o.outWidth))
							/ Math.log(0.5)));
			scale = (int) Math.ceil(scaleTmp);
		}
		o = null;
		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();

		o2.inSampleSize = scale;
		o2.inPurgeable = true;
		o2.inInputShareable = false;
		o2.inDither = true;

		return o2;
	}

	public static Bitmap getThumbnail(String filePath) {
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		Bitmap bm = null;
		if (exif != null && exif.hasThumbnail()) {
			final byte[] thumbnail = exif.getThumbnail();
			bm = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length,
					null);
			bm = rotateIfNeeded(bm, exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED));

		} else {
			Options opts = new Options();
			opts.inSampleSize = 8; // powers of 2 are often good
									// for decoding
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inDither = true;
			bm = BitmapFactory.decodeFile(filePath, opts);
			bm = rotateIfNeeded(bm, exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED));
		}

		return bm;
	}

	public static boolean isSdCardAvailable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			return true;
		}

		else
			return false;
	}

	public static boolean copyFile(File srcFile, File destFile) {
		boolean bSuccess = false;

		if (srcFile.exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(srcFile);
				FileOutputStream fos = new FileOutputStream(destFile);

				byte[] b = new byte[1024 * 5];
				int len = -1;

				while ((len = fis.read(b)) != -1) {
					fos.write(b, 0, len);
				}

				fos.flush();
				fos.close();
				fis.close();

				bSuccess = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return bSuccess;
	}

}
