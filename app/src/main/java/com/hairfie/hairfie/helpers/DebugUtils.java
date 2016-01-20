package com.hairfie.hairfie.helpers;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.hairfie.hairfie.Application;

/**
 * Created by stephh on 20/01/16.
 */
public class DebugUtils {
    public static void printFileImageInfo(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
//If you want, the MIME type will also be decoded (if possible)
        String type = options.outMimeType;
        Log.d(Application.TAG, "file=" + path + "width=" + width + " height=" + height + " mime:" + type);

    }
}
