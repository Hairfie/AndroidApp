package com.hairfie.hairfie.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by stephh on 16/12/15.
 */
public class CameraUtil {

    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int which){
        Camera c = null;
        try {
            c = Camera.open(which); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d("Hairfie", "Could not get camera", e);
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
