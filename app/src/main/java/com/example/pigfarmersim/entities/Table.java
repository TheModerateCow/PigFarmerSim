package com.example.pigfarmersim.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.R;
import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.helpers.interfaces.BitmapMethods;

public enum Table implements BitmapMethods {

    TABLE(R.drawable.table_spritesheet);

    private Bitmap sprite;
    public int spriteWidth;
    public int spriteHeight;

    Table(int resID) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);

        // Get the table sprite dimensions (might be larger than the default size)
        spriteWidth = spriteSheet.getWidth();
        spriteHeight = spriteSheet.getHeight();

        // Create bitmap from the entire spritesheet
        sprite = BitmapMethods.getScaledCharacterBitmap(
                Bitmap.createBitmap(spriteSheet, 0, 0, spriteWidth, spriteHeight));
    }

    public Bitmap getSprite() {
        return sprite;
    }
}
