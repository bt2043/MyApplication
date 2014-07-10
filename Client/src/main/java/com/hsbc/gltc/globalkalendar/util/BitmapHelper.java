package com.hsbc.gltc.globalkalendar.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Henyue-GZ on 2014/7/10.
 */
public class BitmapHelper {

    public static Bitmap createScaledBMP(Bitmap sourceImg, int maxLength) {
        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();
        int scaleWidth = width > height ? maxLength : width*maxLength/height;
        int scaleHeight = width > height ? height*maxLength/width : maxLength;
        return Bitmap.createScaledBitmap(sourceImg, scaleWidth, scaleHeight, true);
    }

    /**
     * Compress the image size with JPEG format limit to the maxSize(kb).
     * @param image
     * @param maxSize
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int maxSize) {
        byte[] bytes = compressImage(image, Bitmap.CompressFormat.JPEG, maxSize);
        ByteArrayInputStream isBm = new ByteArrayInputStream(bytes);
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * Compress the image size with JPEG format limit to the maxSize(kb), and convert to byte[] return.
     * @param image
     * @param maxSize
     * @return
     */
    public static byte[] compressImageToBytes(Bitmap image, int maxSize) {
        return compressImage(image, Bitmap.CompressFormat.JPEG, maxSize);
    }

    /**
     * Compress the image size limit to the maxSize(kb).
     * @param image
     * @param compressFormat
     * @param maxSize
     * @return
     */
    public static byte[] compressImage(Bitmap image, Bitmap.CompressFormat compressFormat, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        image.compress(compressFormat, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxSize) {
            baos.reset();
            image.compress(compressFormat, options, baos);
            if (options <= 10) {
                options -= 2;
            } else {
                options -= 10;
            }
        }
        return baos.toByteArray();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("WeChat: Size of thumBmp is " + result.length);
        return result;
    }
}
