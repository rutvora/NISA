package com.nisaapp.AdditionalFunctions;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class ImageRelated {
    public static Bitmap createThumb(Bitmap realImage, float maxImageSize,
                                     boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static Uri writeBitmapToFile(Bitmap bitmap, File file) {
        file.delete();
        try {
            if (file.createNewFile()) {
                OutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }


}
