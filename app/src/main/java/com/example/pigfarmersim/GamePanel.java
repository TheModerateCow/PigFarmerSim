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
    /**
     * The SurfaceHolder object for the game panel
     */
    private final SurfaceHolder holder;

    /**
     * The Random object for generating random numbers
     */
    private final Random random = new Random();

    /**
     * The GameLoop object for the game loop
     */
    private final GameLoop gameLoop;

    /**
     * The TouchEvents object for the touch events
     */
    private final TouchEvents touchEvents;

    /**
     * Indicates whether the player is moving
     */
    private boolean movePlayer;

    /**
     * The last touch difference
     */
    private PointF lastTouchDiff;

    /**
     * Player's x position, y position, camera x position, camera y position
     */
    private float playerX = (float) MainActivity.GAME_WIDTH / 2, playerY = (float) MainActivity.GAME_HEIGHT / 2;

    /**
     * The camera's x position, y position
     */
    private float cameraX, cameraY;

    /**
     * The skeletons
     */
    private ArrayList<PointF> skeletons = new ArrayList<>();

    /**
     * The skeleton's position
     */
    private final PointF skeletonPos;

    /**
     * The skeleton's direction
     */
    private int skeletonDir = GameConstants.Face_Dir.DOWN;

    /**
     * The last time the skeleton changed direction
     */
    private long lastDirChange = System.currentTimeMillis();

    /**
     * The player's animation index, player's face direction
     */
    private int playerAniIndexY, playerFaceDir = GameConstants.Face_Dir.RIGHT;

    /**
     * The animation tick
     */
    private int aniTick;

    /**
     * The animation speed
     */
    private int aniSpeed = 10;

    /**
     * The MapManager object for the map manager
     */
    private MapManager mapManager;

    /**
     * Indicates whether the game is paused
     */
    private boolean isPaused = false;

    /**
     * Constructs a new GamePanel with the specified context.
     *
     * @param context the Context the view is running in, through which it can
     *                access resources.
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

    /**
     * Renders the game graphics on the surface.
     * <p>
     * This method locks the canvas, clears it with a black color, and then draws
     * the game elements in the correct order.
     */
    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        mapManager.draw(c);

        touchEvents.draw(c);

        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAniIndexY, playerFaceDir), playerX, playerY, null);
        c.drawBitmap(GameCharacters.SKELETON.getSprite(playerAniIndexY, skeletonDir), skeletonPos.x + cameraX,
                skeletonPos.y + cameraY, null);

        holder.unlockCanvasAndPost(c);
    }

    /**
     * Updates the game state based on the elapsed time.
     * <p>
     * This method updates the player's movement and camera position based on the
     * elapsed time.
     * <p>
     * Note that the player is not moving but the camera is moving.
     *
     * @param delta the elapsed time in seconds between the current and previous
     *              frame.
     */
    public void update(double delta) {
        if (!isPaused) {

            updatePlayerMove(delta);
            mapManager.setCameraValues(cameraX, cameraY);

            // So every 3 seconds the skeleton will change direction
            if (System.currentTimeMillis() - lastDirChange >= 3000) {
                skeletonDir = random.nextInt(4);
                lastDirChange = System.currentTimeMillis();
            }

            // Skeleton movement
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
    }

    /**
     * Updates the player's movement based on the touch input.
     * <p>
     * This method updates the player's movement vector based on the touch input
     * and camera position.
     *
     * @param delta the elapsed time in seconds between the current and previous
     */
    private void updatePlayerMove(double delta) {
        // If the player is not moving, then return
        if (!movePlayer) {
            return;
        }

        // Calculates the base speed
        float baseSpeed = (float) (delta * 300);

        // Calculates the ratio of the y speed to the x speed
        float ratio = Math.abs(lastTouchDiff.y) / Math.abs(lastTouchDiff.x);

        // Calculates the angle of the player's movement
        double angle = Math.atan(ratio);

        // Calculates the x speed and y speed
        float xSpeed = (float) Math.cos(angle);
        float ySpeed = (float) Math.sin(angle);

        // If the x speed is greater than the y speed, then the player is moving
        // right or left
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

        // If the last touch difference is negative, then the player is moving left
        if (lastTouchDiff.x < 0) {
            xSpeed *= -1;
        }
        // If the last touch difference is negative, then the player is moving up
        if (lastTouchDiff.y < 0) {
            ySpeed *= -1;
        }

        // Gets the player's width and height
        int pWidth = GameConstants.Sprite.SIZE;
        int pHeight = GameConstants.Sprite.SIZE;

        if (xSpeed <= 0) {
            pWidth = 0;
        }

        if (ySpeed <= 0) {
            pHeight = 0;
        }

        // Calculates the delta x and delta y
        float deltaX = xSpeed * baseSpeed * -1;
        float deltaY = ySpeed * baseSpeed * -1;

        // Prevents user to go out of bounds
        if (mapManager.canMoveHere(playerX + cameraX * -1 + deltaX * -1 + pWidth,
                playerY + cameraY * -1 + deltaY * -1 + pHeight)) {
            cameraX += deltaX;
            cameraY += deltaY;
        }

    }

    /**
     * Updates the player's animation based on the elapsed time.
     * <p>
     * This method updates the player's animation index based on the elapsed time.
     *
     * @param aniSpeed the speed of the animation.
     */
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

    /**
     * Handles touch events on the game panel.
     * <p>
     * This method overrides the onTouchEvent method to handle touch events on the
     * game panel.
     *
     * @param event the MotionEvent object containing the touch event details.
     * @return true if the touch event was handled, false otherwise.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchEvents.touchEvent(event);
    }

    /**
     * This method is called when the surface is created.
     * It is typically used to start the game loop or initialize game resources.
     *
     * @param surfaceHolder the {@link SurfaceHolder} whose surface is being
     *                      created.
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
     * It is important to stop any threads or release resources here to avoid memory
     * leaks.
     *
     * @param holder the {@link SurfaceHolder} whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Clean up resources and stop the game loop safely
    }

    /**
     * Sets the player movement to false and resets the animation.
     */
    public void setPlayerMoveFalse() {
        movePlayer = false;
        resetAnimation();
    }

    /**
     * Sets the player movement to true and updates the last touch difference.
     *
     * @param lastTouchDiff the last touch difference.
     */
    public void setPlayerMoveTrue(PointF lastTouchDiff) {
        movePlayer = true;
        this.lastTouchDiff = lastTouchDiff;
    }

    /**
     * Resets the animation.
     */
    public void resetAnimation() {
        aniTick = 0;
        playerAniIndexY = 0;
    }

    public void pauseGame() {
        isPaused = !isPaused;
    }
}
