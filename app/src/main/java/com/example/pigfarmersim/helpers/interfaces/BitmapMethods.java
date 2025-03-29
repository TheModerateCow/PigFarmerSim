package com.example.pigfarmersim.helpers.interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public interface BitmapMethods {

    BitmapFactory.Options options = new BitmapFactory.Options();

    default Bitmap getScaledBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 6, bitmap.getHeight() * 6, false);
    }
}
