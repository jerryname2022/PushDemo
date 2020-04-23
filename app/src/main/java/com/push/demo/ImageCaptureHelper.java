//MIT License
//Copyright (c) 2015 Karol Wrótniak, Droids On Roids
package com.push.demo;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;

/**
 * Helper for sending ACTION_IMAGE_CAPTURE intent and retrieve its results. Handles all low level operations
 * <br>
 * Usage:<br>
 * <ol>
 * <li>Launch camera app by calling {@link #launchCameraApp(int, Activity, String)}, save resulting Uri, handle (or ignore) exceptions.</li>
 * <li>In your onActivityResult(int requestCode, int resultCode, Intent data) call {@link #retrievePhotoResult(Activity, String) ) }. You may want to inform user if photo cannot be read despite the result is RESULT_OK.</li>
 * </ol>
 *
 * @author koral--
 */
public class ImageCaptureHelper {
    private ImageCaptureHelper() {
    }

    private static final String[] PROJECTION = new String[]{MediaColumns.DATA};
    private static final ContentValues CONTENT_VALUES = new ContentValues(1);
    private static final String PHOTO_SHARED_PREFS_NAME = "photo_shared";
    private static final String PHOTO_URI = "photo_uri";
    /**
     * Description text inserted into @link{ImageColumns.DESCRIPTION} column
     */
    public static final String DESCRIPTION = "Photo taken with example application";

    static {
        CONTENT_VALUES.put(ImageColumns.DESCRIPTION, DESCRIPTION);
    }

    /**
     * Tries to obtain File containing taken photo. Perform cleanups if photo was not taken or it is empty.
     *
     * @param caller   caller Activity
     * @param photoKey key in Shared Preferences for taken image, can be null
     * @return File containing photo or null if no or empty photo was saved by camera app.
     */
    public static File retrievePhotoResult(Activity caller, String photoKey) {
        try {
            if (photoKey == null) {
                photoKey = PHOTO_URI;
            }
            SharedPreferences prefs = caller.getSharedPreferences(PHOTO_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            String takenPhotoUriString = prefs.getString(photoKey, null);
            prefs.edit().remove(photoKey).commit();

            if (takenPhotoUriString == null) {
                return null;
            }

            Uri takenPhotoUri = Uri.parse(takenPhotoUriString);
            ContentResolver cr = caller.getContentResolver();
            File out = new File(getPhotoFilePath(takenPhotoUri, cr));
            if (!out.isFile() || (out.length() == 0)) {
                cr.delete(takenPhotoUri, null, null);
            } else {
                return out;
            }
        } catch (Exception ex) {
            // no-op
        }
        return null;
    }

    /**
     * Tries to create photo placeholder and launch camera app
     *
     * @param requestCode your unique code that will be returned in onActivityResult
     * @param caller      caller Activity
     * @param photoKey    key in Shared Preferences for taken image, can be null
     * @throws ActivityNotFoundException if no camera app was found
     * @throws Exception                 if there is a problem with create photo eg. SDcard is not mounted. It may be eg. {@link IllegalStateException} or {@link UnsupportedOperationException} depending on {@link ContentResolver}.
     */
    public static Uri launchCameraApp(int requestCode, Activity caller, String photoKey, boolean capture) throws ActivityNotFoundException, Exception {
        if (photoKey == null) {
            photoKey = PHOTO_URI;
        }

        ContentResolver cr = caller.getContentResolver();
        Uri takenPhotoUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CONTENT_VALUES);
        if (takenPhotoUri == null) {
            throw new IllegalStateException("Photo insertion failed");
        }
        Intent intent = new Intent(capture ? MediaStore.ACTION_IMAGE_CAPTURE : MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getIntentUri(takenPhotoUri, cr));
        caller.startActivityForResult(intent, requestCode);

        SharedPreferences prefs = caller.getSharedPreferences(PHOTO_SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit().putString(photoKey, takenPhotoUri.toString()).commit();

        return takenPhotoUri;
    }

    private static Uri getIntentUri(Uri takenPhotoUri, ContentResolver cr) {
        String path = getPhotoFilePath(takenPhotoUri, cr);
        if (path == null) {
            throw new IllegalStateException("Photo resolution failed");
        }
        return Uri.fromFile(new File(path));
    }

    private static String getPhotoFilePath(Uri takenPhotoUri, ContentResolver cr) {
        Cursor cursor = cr.query(takenPhotoUri, PROJECTION, null, null, null);
        String res = null;
        if (cursor != null) {
            int dataIdx = cursor.getColumnIndex(MediaColumns.DATA);
            if (dataIdx >= 0 && cursor.moveToFirst())
                res = cursor.getString(dataIdx);
            cursor.close();
        }
        return res;
    }

    /**
     * Dirty hack for API level <8 to get a top-level public external storage directory where Camera photos should be placed.<br>
     * Empty photo is inserted, path of its parent directory is retrieved and then photo is deleted.<br>
     * If photo cannot be inserted eg. external storage is not mounted, then "DCIM" folder in root of the external storage is used as a fallback.
     *
     * @param cr {@link ContentResolver} used to resolve image Uris
     * @return path to directory where camera app places photos (may be fallback)
     */
    public static String getPhotoDirPath(ContentResolver cr) {
        String fallback = Environment.getExternalStorageDirectory() + "/DCIM";
        Uri takenPhotoUri = null;
        String photoFilePath = null;
        try {
            takenPhotoUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CONTENT_VALUES);
            if (takenPhotoUri == null)
                return fallback;
            photoFilePath = getPhotoFilePath(takenPhotoUri, cr);
            cr.delete(takenPhotoUri, null, null);
        } catch (Exception ex) {
            //igonred
        }
        if (photoFilePath == null)
            return fallback;
        String parent = new File(photoFilePath).getParent();
        Matcher m = Pattern.compile("/DCIM(/|$)").matcher(parent);
        if (m.find())
            parent = parent.substring(0, m.end());

        return parent;
    }
}