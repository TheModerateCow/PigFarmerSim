package com.example.pigfarmersim.environments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.R;
import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.helpers.interfaces.BitmapMethods;

import java.util.Arrays;

public enum Floor implements BitmapMethods {

    OUTSIDE(R.drawable.restaurant_floor, 64, 64);
//    OUTSIDE(R.drawable.restaurant_floor, 16, 16);
//    OUTSIDE(R.drawable.restaurant_floor, 1, 1);


    private Bitmap[] sprites;

    Floor(int resID, int tilesInWidth, int tilesInHeight) {
        options.inScaled = false;
        sprites = new Bitmap[tilesInHeight * tilesInWidth];
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        for (int j = 0; j < tilesInHeight; j++)
            for (int i = 0; i < tilesInWidth; i++) {
                int index = j * tilesInWidth + i;
                sprites[index] = getScaledBitmap(Bitmap.createBitmap(spriteSheet, GameConstants.Sprite.DEFAULT_SIZE * i, GameConstants.Sprite.DEFAULT_SIZE * j, GameConstants.Sprite.DEFAULT_SIZE, GameConstants.Sprite.DEFAULT_SIZE));
            }
        System.out.println(sprites.length);
    }

//    Floor(int resID, int tilesInWidth, int tilesInHeight) {
//        options.inScaled = false;
//        sprites = new Bitmap[tilesInHeight * tilesInWidth];  // This will be 1
//        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
//        // Only one iteration (i = 0, j = 0)
//        sprites[0] = getScaledBitmap(
//                Bitmap.createBitmap(spriteSheet, 0, 0, spriteSheet.getWidth(), spriteSheet.getHeight())
//        );
//        System.out.println(sprites.length);  // Should print 1
//    }


    public Bitmap getSprite(int id){
        return sprites[id];
    }

}
