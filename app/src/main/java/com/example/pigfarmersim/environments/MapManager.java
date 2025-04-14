package com.example.pigfarmersim.environments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.pigfarmersim.MainActivity;
import com.example.pigfarmersim.R;
import com.example.pigfarmersim.helpers.GameConstants;

public class MapManager {

    private GameMap currentMap;
    private float cameraX, cameraY;
    private Bitmap backgroundImage;
    private int mapWidth, mapHeight;

    public MapManager() {
        initMap();
    }

    public void setCameraValues(float cameraX, float cameraY) {
        this.cameraX = cameraX;
        this.cameraY = cameraY;
    }

    public boolean canMoveHere(float x, float y) {
        if (x < 0 || y < 0) {
            return false;
        }

        if (x >= mapWidth || y >= mapHeight) {
            return false;
        }

        return true;
    }

    public int getMaxWidthCurrentMap() {
        return mapWidth;
    }

    public int getMaxHeightCurrentMap() {
        return mapHeight;
    }

    public void draw(Canvas c) {
        // Draw the background image at the camera position
        c.drawBitmap(backgroundImage, cameraX, cameraY, null);
    }

    private void initMap() {
        // Load the entire background image directly
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap originalImage = BitmapFactory.decodeResource(
                MainActivity.getGameContext().getResources(),
                R.drawable.restaurant,
                options);

        // Set map dimensions based on the image
        mapWidth = originalImage.getWidth();
        mapHeight = originalImage.getHeight();

        // Create a properly scaled background image
        backgroundImage = Bitmap.createScaledBitmap(
                originalImage,
                (int) (mapWidth * 1.7),
                (int) (mapHeight * 1.7),
                false);

        // Create a simple map structure for compatibility
        int[][] spriteIds = new int[1][1];
        spriteIds[0][0] = 0;
        currentMap = new GameMap(spriteIds);
    }
}
