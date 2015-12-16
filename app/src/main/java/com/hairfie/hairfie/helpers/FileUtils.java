package com.hairfie.hairfie.helpers;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by stephh on 16/12/15.
 */
public class FileUtils {

    public static File saveInputStream(Context context, InputStream inputStream, String extension) throws IOException {
        try {
            File outputDir = context.getCacheDir(); // context being the Activity pointer
            File file = File.createTempFile("inputstream", extension, outputDir);
            OutputStream output = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;

                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            } finally {
                output.close();
                return file;
            }

        } finally {
            inputStream.close();
        }
    }
}