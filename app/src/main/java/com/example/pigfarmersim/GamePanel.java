package com.example.pigfarmersim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.pigfarmersim.entities.GameCharacters;
import com.example.pigfarmersim.environments.MapManager;
import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.inputs.TouchEvents;

import java.util.ArrayList;
import java.util.Random;

/**
 * A custom SurfaceView that serves as the game panel.
 * It implements the {@link SurfaceHolder.Callback} interface to handle changes
 * to the surface, which is used for rendering game graphics.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private final SurfaceHolder holder;
    private final Random random = new Random();
    private final GameLoop gameLoop;
    private final TouchEvents touchEvents;
    private boolean movePlayer;
    private PointF lastTouchDiff;
    private float playerX = (float) MainActivity.GAME_WIDTH / 2, playerY = (float) MainActivity.GAME_HEIGHT / 2;
    private float cameraX, cameraY;
    private ArrayList<PointF> skeletons = new ArrayList<>();
    private final PointF skeletonPos;
    private int skeletonDir = GameConstants.Face_Dir.DOWN;
    private long lastDirChange = System.currentTimeMillis();
    private int playerAniIndexY, playerFaceDir = GameConstants.Face_Dir.RIGHT;
    private int aniTick;
    private int aniSpeed = 10;
    private MapManager mapManager;


    /**
     * Constructs a new GamePanel with the specified context.
     *
     * @param context the Context the view is running in, through which it can access resources.
     */
    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        touchEvents = new TouchEvents(this);
        gameLoop = new GameLoop(this);
        mapManager = new MapManager();

        skeletonPos = new PointF((random.nextInt(MainActivity.GAME_WIDTH)), (random.nextInt(MainActivity.GAME_HEIGHT)));
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        mapManager.draw(c);

        touchEvents.draw(c);

        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAniIndexY, playerFaceDir), playerX, playerY, null);
        c.drawBitmap(GameCharacters.SKELETON.getSprite(playerAniIndexY, skeletonDir), skeletonPos.x + cameraX, skeletonPos.y + cameraY, null);

        holder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        updatePlayerMove(delta);
        mapManager.setCameraValues(cameraX, cameraY);

        if (System.currentTimeMillis() - lastDirChange >= 3000) {
            skeletonDir = random.nextInt(4);
            lastDirChange = System.currentTimeMillis();
        }

        switch (skeletonDir) {
            case GameConstants.Face_Dir.DOWN:
                skeletonPos.y += (float) (300 * delta);
                if (skeletonPos.y >= 1920) {
                    skeletonDir = GameConstants.Face_Dir.UP;
                }
                break;
            case GameConstants.Face_Dir.UP:
                skeletonPos.y -= (float) (300 * delta);
                if (skeletonPos.y <= 0) {
                    skeletonDir = GameConstants.Face_Dir.DOWN;
                }
                break;
            case GameConstants.Face_Dir.LEFT:
                skeletonPos.x -= (float) (300 * delta);
                if (skeletonPos.x <= 0) {
                    skeletonDir = GameConstants.Face_Dir.RIGHT;
                }
                break;
            case GameConstants.Face_Dir.RIGHT:
                skeletonPos.x += (float) (300 * delta);
                if (skeletonPos.x >= 1080) {
                    skeletonDir = GameConstants.Face_Dir.LEFT;
                }
                break;
        }

        updatedAnimation(aniSpeed);
    }

    private void updatePlayerMove(double delta) {
        if (!movePlayer) {
            return;
        }

        float baseSpeed = (float) (delta * 300);
        float ratio = Math.abs(lastTouchDiff.y) / Math.abs(lastTouchDiff.x);
        double angle = Math.atan(ratio);

        float xSpeed = (float) Math.cos(angle);
        float ySpeed = (float) Math.sin(angle);

//        System.out.println("Angle: " + Math.toDegrees(angle));
//        System.out.println("xSpeed: " + xSpeed + " | ySpeed: " + ySpeed);

        if (xSpeed > ySpeed) {
            if (lastTouchDiff.x > 0) {
                playerFaceDir = GameConstants.Face_Dir.RIGHT;
            } else {
                playerFaceDir = GameConstants.Face_Dir.LEFT;

            }
        } else {
            if (lastTouchDiff.y > 0) {
                playerFaceDir = GameConstants.Face_Dir.DOWN;
            } else {
                playerFaceDir = GameConstants.Face_Dir.UP;
            }
        }

        if (lastTouchDiff.x < 0) {
            xSpeed *= -1;
        }
        if (lastTouchDiff.y < 0) {
            ySpeed *= -1;
        }

        int pWidth = GameConstants.Sprite.SIZE;
        int pHeight = GameConstants.Sprite.SIZE;

        if (xSpeed <= 0) {
            pWidth = 0;
        }

        if (ySpeed <= 0) {
            pHeight = 0;
        }

        float deltaX = xSpeed * baseSpeed * -1;
        float deltaY = ySpeed * baseSpeed * -1;

        if (mapManager.canMoveHere(playerX + cameraX * -1 + deltaX * -1 + pWidth, playerY + cameraY * -1 + deltaY * -1 + pHeight)) {
            cameraX += deltaX;
            cameraY += deltaY;
        }

    }

    private void updatedAnimation(int aniSpeed) {
        if (!movePlayer) {
            return;
        }
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            playerAniIndexY++;
            if (playerAniIndexY >= 4) {
                playerAniIndexY = 0;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchEvents.touchEvent(event);
    }

    /**
     * This method is called when the surface is created.
     * It is typically used to start the game loop or initialize game resources.
     *
     * @param surfaceHolder the {@link SurfaceHolder} whose surface is being created.
     */
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        // Initialize resources and start the game loop
        gameLoop.startGameLoop();
    }

    /**
     * This method is called when the surface changes, such as size or format.
     * You can adjust your rendering code here if the surface dimensions change.
     *
     * @param holder the {@link SurfaceHolder} whose surface has changed.
     * @param format the new PixelFormat of the surface.
     * @param width  the new width of the surface.
     * @param height the new height of the surface.
     */
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Handle changes to the surface, such as size or orientation changes
    }

    /**
     * This method is called immediately before a surface is being destroyed.
     * It is important to stop any threads or release resources here to avoid memory leaks.
     *
     * @param holder the {@link SurfaceHolder} whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Clean up resources and stop the game loop safely
    }

    public void setPlayerMoveFalse() {
        movePlayer = false;
        resetAnimation();
    }

    public void setPlayerMoveTrue(PointF lastTouchDiff) {
        movePlayer = true;
        this.lastTouchDiff = lastTouchDiff;
    }

    public void resetAnimation() {
        aniTick = 0;
        playerAniIndexY = 0;
    }
}
