package com.example.pigfarmersim.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.R;
import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.helpers.interfaces.BitmapMethods;

public enum Table implements BitmapMethods{

    TABLE(R.drawable.table_spritesheet);

    private Bitmap sprite;

    Table(int resID) {
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        sprite = getScaledCharacterBitmap(Bitmap.createBitmap(spriteSheet, 0, 0, GameConstants.Sprite.DEFAULT_SIZE, GameConstants.Sprite.DEFAULT_SIZE));
    }



    public Bitmap getSprite() { return sprite; }
}
