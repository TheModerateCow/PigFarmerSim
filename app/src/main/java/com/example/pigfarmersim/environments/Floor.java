package com.example.pigfarmersim.environments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.R;
import com.example.pigfarmersim.helpers.interfaces.BitmapMethods;

public enum Floor implements BitmapMethods {

    OUTSIDE(R.drawable.restaurant);

    private Bitmap background;

    Floor(int resID) {
        // Disable auto-scaling from density
        options.inScaled = false;

        // Load the bitmap from resources
        Bitmap original = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);

        // Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) MainActivity.getGameContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Scale the bitmap to fit the screen size
        background = Bitmap.createScaledBitmap(original, screenWidth, screenHeight, true);
    }

    public Bitmap getBackground() {
        return background;
    }
}
