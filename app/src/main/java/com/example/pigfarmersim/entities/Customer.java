package com.example.pigfarmersim.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.R;
import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.helpers.interfaces.BitmapMethods;

public class Customer implements BitmapMethods{
    private Bitmap spriteSheet;
    private static Bitmap[][] sprites = new Bitmap[4][4];

    {int resID = R.drawable.customer_new_spritesheet;
        options.inScaled = false;
        spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        for (int j = 0; j < sprites.length; j++)
            for (int i = 0; i < sprites[j].length; i++)
                sprites[j][i] = getScaledCharacterBitmap(Bitmap.createBitmap(spriteSheet, GameConstants.Sprite.DEFAULT_SIZE * i, GameConstants.Sprite.DEFAULT_SIZE * j, GameConstants.Sprite.DEFAULT_SIZE, GameConstants.Sprite.DEFAULT_SIZE));
    }

    public Customer() {
        int resID = R.drawable.customer_new_spritesheet;
        options.inScaled = false;
        spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        for (int j = 0; j < sprites.length; j++)
            for (int i = 0; i < sprites[j].length; i++)
                sprites[j][i] = getScaledCharacterBitmap(Bitmap.createBitmap(spriteSheet, GameConstants.Sprite.DEFAULT_SIZE * i, GameConstants.Sprite.DEFAULT_SIZE * j, GameConstants.Sprite.DEFAULT_SIZE, GameConstants.Sprite.DEFAULT_SIZE));
    }

    public static Bitmap getSprite(int yPos, int xPos) {
        return sprites[yPos][xPos];
    }
}
