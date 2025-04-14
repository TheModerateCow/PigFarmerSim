package com.example.pigfarmersim.helpers.interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.helpers.GameConstants;

public interface BitmapMethods {

    BitmapFactory.Options options = new BitmapFactory.Options();

    default Bitmap getScaledBitmap(Bitmap bitmap) {
        float scaleFactor = (float) MainActivity.GAME_HEIGHT / (GameConstants.Sprite.DEFAULT_SIZE * bitmap.getHeight());
        int newWidth = (int) (bitmap.getWidth() * scaleFactor);
        int newHeight = (int) (bitmap.getHeight() * scaleFactor);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

//    default Bitmap getScaledCharacterBitmap(Bitmap bitmap) {
//        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 8, bitmap.getHeight() * 8, false);
//    }

    public static Bitmap getScaledCharacterBitmap(Bitmap bitmap) {
        // scaling logic here
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 8, bitmap.getHeight() * 8, false);
    }
}
