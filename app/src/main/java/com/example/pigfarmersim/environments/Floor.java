package com.example.pigfarmersim.environments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.R;
import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.helpers.interfaces.BitmapMethods;

public enum Floor implements BitmapMethods {

    OUTSIDE(R.drawable.restaurant);

    private Bitmap background;
    public int height;
    public int width;
    public float sx;
    public float sy;

    Floor(int resID) {
        // Disable auto-scaling from density
        options.inScaled = false;

        // Load the bitmap from resources
        Bitmap original = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        height = original.getHeight();
        width = original.getWidth();
        sy = MainActivity.GAME_HEIGHT / (float) height;
        sx = MainActivity.GAME_WIDTH/ (float) width;

        // Scale the bitmap to fit the screen size
        background = Bitmap.createScaledBitmap(original, MainActivity.GAME_WIDTH, MainActivity.GAME_HEIGHT, true);
    }

    public Bitmap getBackground() {
        return background;
    }
}
