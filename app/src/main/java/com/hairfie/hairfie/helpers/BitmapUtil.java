package com.hairfie.hairfie.helpers;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by stephh on 16/12/15.
 */
public class BitmapUtil {
    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }

    public static File saveToFile(Context context, Bitmap bitmap) throws IOException {
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File file = File.createTempFile("bitmap", ".jpg", outputDir);
        FileOutputStream out = new FileOutputStream(file);

        if (null != out) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
            out.close();
            return file;
        }
        return null;
    }
}
