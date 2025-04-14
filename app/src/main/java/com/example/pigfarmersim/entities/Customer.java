package com.example.pigfarmersim.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.R;
import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.helpers.interfaces.BitmapMethods;

public class Customer implements BitmapMethods {
    private Bitmap spriteSheet;
    private static Bitmap[][] sprites = new Bitmap[4][4];
    private static boolean waiting_IO = false;
    private int cust_ID;
    private int table_ID;
    private PointF pos;

    public int getCust_ID() {
        return this.cust_ID;
    }

    public void setCust_ID(int cust_ID) {
        this.cust_ID = cust_ID;
    }

    public int getTable_ID() {
        return this.table_ID;
    }

    public void setTable_ID(int table_ID) {
        this.table_ID = table_ID;
    }

    public PointF getPos() {
        return this.pos;
    }

    public void setPos(PointF pos) {
        this.pos = pos;
    }

    public static void setWaiting_IO(boolean waiting_IO) {
        Customer.waiting_IO = waiting_IO;
    }

    static {
        int resID = R.drawable.customer_new_spritesheet;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        for (int j = 0; j < sprites.length; j++) {
            for (int i = 0; i < sprites[j].length; i++) {
                sprites[j][i] = BitmapMethods.getScaledCharacterBitmap(Bitmap.createBitmap(
                        spriteSheet,
                        GameConstants.Sprite.DEFAULT_SIZE * i,
                        GameConstants.Sprite.DEFAULT_SIZE * j,
                        GameConstants.Sprite.DEFAULT_SIZE,
                        GameConstants.Sprite.DEFAULT_SIZE)
                );
            }
        }
    }

    public Customer() {
        int resID = R.drawable.customer_new_spritesheet;
        options.inScaled = false;
        spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        for (int j = 0; j < sprites.length; j++)
            for (int i = 0; i < sprites[j].length; i++)
                sprites[j][i] = BitmapMethods.getScaledCharacterBitmap(Bitmap.createBitmap(spriteSheet, GameConstants.Sprite.DEFAULT_SIZE * i, GameConstants.Sprite.DEFAULT_SIZE * j, GameConstants.Sprite.DEFAULT_SIZE, GameConstants.Sprite.DEFAULT_SIZE));
    }

    public static Bitmap getSprite(int yPos, int xPos) {
        return sprites[yPos][xPos];
    }

    public static boolean isWaiting_IO() {
        return waiting_IO;
    }


}
