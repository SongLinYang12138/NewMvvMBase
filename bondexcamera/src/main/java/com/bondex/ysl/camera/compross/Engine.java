package com.bondex.ysl.camera.compross;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
    private InputStreamProvider srcImg;
    private File tagImg;
    private int srcWidth;
    private int srcHeight;
    private boolean focusAlpha;
    private int compressRatio;

    Engine(InputStreamProvider srcImg, File tagImg, boolean focusAlpha, int compressRatio) {
        this.tagImg = tagImg;
        this.srcImg = srcImg;
        this.focusAlpha = focusAlpha;
        this.compressRatio = compressRatio;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        try {
            BitmapFactory.decodeStream(srcImg.open(), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.srcWidth = options.outWidth;
        this.srcHeight = options.outHeight;
    }

    private int computeSize() {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    private Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);
        matrix.postScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    File compress() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();

        try {
            Bitmap tagBitmap = null;
            if (compressRatio == 100) {
                tagBitmap = BitmapFactory.decodeStream(srcImg.open());
            } else {
                tagBitmap = BitmapFactory.decodeStream(srcImg.open(), null, options);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            if (Checker.SINGLE.isJPG(srcImg.open())) {
                tagBitmap = rotatingImage(tagBitmap, Checker.SINGLE.getOrientation(srcImg.open()));
            }

//                相机直接拍照保存下来的图片需要旋转90.
            tagBitmap = rotatingImage(tagBitmap, 270);


            tagBitmap.compress(focusAlpha ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, compressRatio, stream);
            tagBitmap.recycle();


            FileOutputStream fos = new FileOutputStream(tagImg);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
            stream.close();

            Log.i("aaa"," 压缩时间 ");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tagImg;
    }
}