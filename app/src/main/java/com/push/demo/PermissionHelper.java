package com.push.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;

public class PermissionHelper {

    public static int CODE_REQUEST_DCIM = 505;
    public static int CODE_REQUEST_STORAGE = 506;
    public static int CODE_REQUEST_VOICE = 507;


    public static String[] READ_PHONE_STATE = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };
    public static String[] CAMERA_PERMISSION = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.VIBRATE
    };
    public static String[] STORAGE_PERMISSION = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String[] BLUETOOTH_PERMISSION = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    public static String[] DCIM_PERMISSION = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.VIBRATE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String[] LOCATION_PERMISSION = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    };

    public static String[] VOICE_PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    public static final boolean checkGPSOpen(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
                return false;
            }
        }

        return true;
    }


    public static final boolean checkBluetoothPermissions(Activity activity) {
        return checkPermissions(activity, BLUETOOTH_PERMISSION);
    }

    public static final void requestBluetoothPermissions(Activity activity, int requestCode) {
        requestPermissions(activity, BLUETOOTH_PERMISSION, requestCode);
    }

    public static final boolean checkVoicePermissions(Activity activity) {
        return checkPermissions(activity, VOICE_PERMISSION);
    }

    public static final void requestVoicePermissions(Activity activity, int requestCode) {
        requestPermissions(activity, VOICE_PERMISSION, requestCode);
    }

    public static final boolean checkLocationPermissions(Activity activity) {
        return checkPermissions(activity, LOCATION_PERMISSION);
    }

    public static final void requestLocationPermissions(Activity activity, int requestCode) {
        requestPermissions(activity, LOCATION_PERMISSION, requestCode);
    }


    public static final boolean checkPhoneStatePermissions(Activity activity) {
        return checkPermissions(activity, READ_PHONE_STATE);
    }


    public static final boolean checkDCIMPermission(Activity activity) {
        return checkPermissions(activity, DCIM_PERMISSION);
    }

    public static final boolean checkStoragePermissions(Activity activity) {
        return checkPermissions(activity, STORAGE_PERMISSION);
    }

    public static final boolean checkCameraPermissions(Activity activity) {
        return checkPermissions(activity, CAMERA_PERMISSION);
    }

    public static final void requestDCIMPermissions(Activity activity, int requestCode) {
        requestPermissions(activity, DCIM_PERMISSION, requestCode);
    }

    public static final void requestStoragePermissions(Activity activity, int requestCode) {
        requestPermissions(activity, STORAGE_PERMISSION, requestCode);
    }

    public static final void requestCameraPermissions(Activity activity, int requestCode) {
        requestPermissions(activity, CAMERA_PERMISSION, requestCode);
    }

    public static final void requestPermissions(Activity activity, String[] permissions,
                                                int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, requestCode);
        }
    }

    public static final boolean checkPermissions(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(permission)) {
                    return false;
                }
            }
        }
        return true;
    }
}
