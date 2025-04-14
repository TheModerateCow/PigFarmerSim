package com.example.pigfarmersim;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.pigfarmersim.entities.Customer;
import com.example.pigfarmersim.environments.MapManager;
import com.example.pigfarmersim.environments.TableManager;
import com.example.pigfarmersim.helpers.GameConstants;
import com.example.pigfarmersim.inputs.TouchEvents;

import java.util.ArrayList;
import java.util.List;
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
    private float cameraTargetX = cameraX;
    private float lastTouchX;
    private final float dragFactor = 0.9f;         // Reduces touch sensitivity
    private final float minDragThreshold = 10f;      // Ignore tiny finger twitches
    private ArrayList<PointF> skeletons = new ArrayList<>();
    private List<PointF> table_pos = new ArrayList<>();
    private List<PointF> cust_pos = new ArrayList<>();
    private List<CustomerGroup> customers = new ArrayList<>();
    private int table_idx = 0;
    private final PointF skeletonPos;
    private int skeletonDir = GameConstants.Face_Dir.DOWN;
    private long lastDirChange = System.currentTimeMillis();
    private int customerDir = GameConstants.Face_Dir.DOWN;
    private int customerFrame = 0;
    private long frameTime = System.currentTimeMillis();
    private int playerAniIndexY, playerFaceDir = GameConstants.Face_Dir.RIGHT;
    private int aniTick;
    private int aniSpeed = 10;
    private MapManager mapManager;

    private TableManager tableManager;

    private ArrayList<PointF> customer_queue = new ArrayList<>();

    // Pause button and menu elements
    private boolean isPaused = false;
    private final RectF pauseButton;
    private final Paint pauseButtonPaint;
    private final Paint pauseIconPaint;
    private final Paint overlayPaint;
    private final Paint menuPaint;
    private final Paint buttonPaint;
    private final Paint buttonTextPaint;
    private final RectF resumeButton;
    private final RectF quitButton;
    private boolean showEndScreen = false;
    private final Paint endOverlayPaint;
    private final Paint endScreenBgPaint;
    private final Paint titleTextPaint;
    private final Paint scoreTextPaint;
    private final Paint endButtonTextPaint;
    private final RectF endScreenBgRect;
    private final RectF menuButtonRect;
    private final RectF endGameButton;
    private final float cameraXMin = 0; // Right-most
    private final float cameraXMax = MainActivity.GAME_HEIGHT; // Left-most



    // Add these state constants near the top of GamePanel class
    private static final int numberOfCustomers = 5;

    private static final float initialX = 0;

    private static final float initialY = 0;
    private static final int STATE_IDLE = 0;
    private static final int STATE_MOVING_TO_TABLE = 1; // Example state
    private static final int STATE_ORDERING = 2;        // Example state
    private static final int STATE_MOVING_TO_WAIT_AREA = 3;
    private static final int STATE_WAITING_IO = 4;
    private static final int STATE_MOVING_FROM_WAIT_AREA = 5;
    private static final int STATE_LEAVING = 6;         // Example state
    private int score = 0; // Or start with your desired initial score
    private final Paint scorePaint;

    // for customer spawner
    private CustomerSpawner customerSpawner;

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
        tableManager = new TableManager();

        skeletonPos = new PointF((random.nextInt(MainActivity.GAME_WIDTH)), (random.nextInt(MainActivity.GAME_HEIGHT)));

        // Initialize pause button
        pauseButton = new RectF(MainActivity.GAME_WIDTH - 100, 20, MainActivity.GAME_WIDTH - 20, 100);
        pauseButtonPaint = new Paint();
        pauseButtonPaint.setColor(Color.LTGRAY);
        pauseButtonPaint.setAlpha(180);

        pauseIconPaint = new Paint();
        pauseIconPaint.setColor(Color.WHITE);
        pauseIconPaint.setStyle(Paint.Style.FILL);

        // Initialize pause menu overlay
        overlayPaint = new Paint();
        overlayPaint.setColor(Color.BLACK);
        overlayPaint.setAlpha(128); // 50% opacity

        menuPaint = new Paint();
        menuPaint.setColor(Color.DKGRAY);
        menuPaint.setAlpha(230);

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.LTGRAY);

        buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.BLACK);
        buttonTextPaint.setTextSize(50);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);

        // Menu buttons
        int buttonWidth = 300;
        int buttonHeight = 100;
        int centerX = MainActivity.GAME_WIDTH / 2;
        int centerY = MainActivity.GAME_HEIGHT / 2;

        resumeButton = new RectF(centerX - buttonWidth / 2, centerY - buttonHeight - 20,
                centerX + buttonWidth / 2, centerY - 20);
        quitButton = new RectF(centerX - buttonWidth / 2, centerY + 20,
                centerX + buttonWidth / 2, centerY + buttonHeight + 20);

        // Overlay paint (semi-transparent black)
        endOverlayPaint = new Paint();
        endOverlayPaint.setColor(Color.argb(180, 0, 0, 0)); // 180/255 transparency

        // End screen background
        endScreenBgPaint = new Paint();
        endScreenBgPaint.setColor(Color.argb(220, 30, 30, 40)); // Dark bluish-gray
        endScreenBgPaint.setStyle(Paint.Style.FILL);

        // Text paints
        titleTextPaint = new Paint();
        titleTextPaint.setColor(Color.WHITE);
        titleTextPaint.setTextSize(120);
        titleTextPaint.setTextAlign(Paint.Align.CENTER);
        titleTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        scoreTextPaint = new Paint();
        scoreTextPaint.setColor(Color.BLACK);
        scoreTextPaint.setTextSize(100);
        scoreTextPaint.setTextAlign(Paint.Align.CENTER);
        scoreTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));


        endButtonTextPaint = new Paint();
        endButtonTextPaint.setColor(Color.BLACK);
        endButtonTextPaint.setTextSize(60);
        endButtonTextPaint.setTextAlign(Paint.Align.CENTER);

        // End screen background rectangle
        endScreenBgRect = new RectF(
                centerX - 400, centerY - 300,
                centerX + 400, centerY + 300
        );

        // Menu button rectangle
        menuButtonRect = new RectF(
                centerX - 200, centerY + 150,
                centerX + 200, centerY + 250
        );

        endGameButton = new RectF(centerX - buttonWidth / 2, centerY + buttonHeight + 60,
                centerX + buttonWidth / 2, centerY + 2 * buttonHeight + 60);

        // for customer spawner
        customerSpawner = new CustomerSpawner();

        //Score pain
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(100);
        scorePaint.setTextAlign(Paint.Align.CENTER); // Center alignment
        scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    public GameLoop getGameLoop() {
        return gameLoop;
    }

    public void render() {

        // Clamp AFTER easing to avoid sharp stops mid-drag
        cameraX = Math.max(cameraXMin, Math.min(cameraXMax, cameraTargetX));

        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        touchEvents.draw(c);

        mapManager.draw(c);

        tableManager.drawAll(c, cameraX, cameraY);

        table_pos = tableManager.getTablePoints();

        List<CustomerGroup> customers = customerSpawner.getCustomerGroups();

//        c.drawBitmap(Table.TABLE.getSprite(),32 + playerX + cameraX, 96 + playerY+ cameraY, null);
        // Draw pause button
        c.drawRoundRect(pauseButton, 10, 10, pauseButtonPaint);

        // *** Draw the scoreboard in the top-right corner ***
        int margin = 20;  // Padding from the edge
        // Using MainActivity.GAME_WIDTH here or you could use c.getWidth()
        String scoreText = "Score: " + score;
        c.drawText(scoreText, MainActivity.GAME_WIDTH / 2, 60 + margin, scorePaint);

        // Draw pause icon (two vertical bars)
        float barWidth = 12;
        float pauseIconLeft = pauseButton.left + (pauseButton.width() - 2 * barWidth - 10) / 2;
        float pauseIconTop = pauseButton.top + pauseButton.height() * 0.25f;
        float pauseIconBottom = pauseButton.bottom - pauseButton.height() * 0.25f;

        c.drawRect(pauseIconLeft, pauseIconTop, pauseIconLeft + barWidth, pauseIconBottom, pauseIconPaint);
        c.drawRect(pauseIconLeft + barWidth + 10, pauseIconTop, pauseIconLeft + 2 * barWidth + 10, pauseIconBottom, pauseIconPaint);

        // Draw pause menu if paused
        if (isPaused && !showEndScreen) {
            // Draw semi-transparent overlay
            c.drawRect(0, 0, MainActivity.GAME_WIDTH, MainActivity.GAME_HEIGHT, overlayPaint);

            // Draw menu background
            float menuWidth = MainActivity.GAME_WIDTH * 0.7f;
            float menuHeight = MainActivity.GAME_HEIGHT * 0.5f;
            float menuLeft = (MainActivity.GAME_WIDTH - menuWidth) / 2;
            float menuTop = (MainActivity.GAME_HEIGHT - menuHeight) / 2;
            RectF menuRect = new RectF(menuLeft, menuTop, menuLeft + menuWidth, menuTop + menuHeight);
            c.drawRoundRect(menuRect, 20, 20, menuPaint);

            // Draw "PAUSED" text
            Paint titlePaint = new Paint();
            titlePaint.setColor(Color.WHITE);
            titlePaint.setTextSize(70);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            c.drawText("PAUSED", MainActivity.GAME_WIDTH / 2, menuTop + 100, titlePaint);

            // Draw buttons
            c.drawRoundRect(resumeButton, 15, 15, buttonPaint);
            c.drawRoundRect(quitButton, 15, 15, buttonPaint);

            // Draw button text
            float textY = resumeButton.centerY() + 15; // Adjust for text vertical centering
            c.drawText("RESUME", resumeButton.centerX(), textY, buttonTextPaint);
            c.drawText("QUIT", quitButton.centerX(), quitButton.centerY() + 15, buttonTextPaint);

            // Add End Game button
            c.drawRoundRect(endGameButton, 15, 15, buttonPaint);
            c.drawText("END GAME", endGameButton.centerX(), endGameButton.centerY() + 15, buttonTextPaint);
        }

        if (showEndScreen) {
            // Draw translucent overlay
            c.drawRect(0, 0, MainActivity.GAME_WIDTH, MainActivity.GAME_HEIGHT, endOverlayPaint);

            // Draw end screen background
            c.drawRoundRect(endScreenBgRect, 30, 30, endScreenBgPaint);

            // Draw title
            c.drawText("GAME OVER",
                    MainActivity.GAME_WIDTH / 2,
                    endScreenBgRect.top + 150,
                    titleTextPaint);


            // Draw menu button
            Paint buttonPaint = new Paint();
            buttonPaint.setColor(Color.LTGRAY);
            buttonPaint.setStyle(Paint.Style.FILL);
            c.drawRoundRect(menuButtonRect, 20, 20, buttonPaint);
            c.drawText("MAIN MENU",
                    menuButtonRect.centerX(),
                    menuButtonRect.centerY() + 20,
                    endButtonTextPaint);
        }

        // Draw customer groups in a waiting queue near the top
        Paint groupTextPaint = new Paint();
        groupTextPaint.setColor(Color.WHITE);
        groupTextPaint.setTextSize(40);
        groupTextPaint.setTextAlign(Paint.Align.CENTER);

        for (CustomerGroup group : customerSpawner.getCustomerGroups()) {
            PointF pos = group.getCoords();

            // draw sprite at position
            c.drawBitmap(Customer.getSprite(customerDir, customerFrame), pos.x, pos.y, null);

            // draw group size above
            c.drawText("x" + group.getGroupSize(), pos.x + 32, pos.y - 10, groupTextPaint);
        }

        PointF baseWorldPos = new PointF(50, 100); // fixed world position where the queue starts
        float spacing = 150; // horizontal spacing between each customer

        int i = 0;
        for (CustomerGroup group : customerSpawner.getCustomerGroups()) {
            float drawX = baseWorldPos.x + i * spacing + cameraX - 1024;
            float drawY = baseWorldPos.y + cameraY;

            c.drawBitmap(Customer.getSprite(customerDir, customerFrame), drawX, drawY, null);
            c.drawText("x" + group.getGroupSize(), drawX + 32, drawY - 10, groupTextPaint);

            i++;
        }
        // for customer spawner
        customerSpawner.update();

        holder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        // Only update game state if not paused
        if (!isPaused) {
            updatePlayerMove(delta);
            mapManager.setCameraValues(cameraX, cameraY);
        }

        if (System.currentTimeMillis() - lastDirChange >= 3000) {
            lastDirChange = System.currentTimeMillis();
            customerDir = (customerDir + 1) % 4;
        }

        if (System.currentTimeMillis() - frameTime >= 1000) {
            customerFrame = (customerFrame + 1) % 4;
            frameTime = System.currentTimeMillis();
        }

        updatedAnimation(aniSpeed);
    }

    private void updatePlayerMove(double delta) {
        if (!movePlayer) {
            return;
        }

        float baseSpeed = (float) (delta * 300);
        float ratio = abs(lastTouchDiff.y) / abs(lastTouchDiff.x);
        double angle = Math.atan(ratio);

        float xSpeed = (float) Math.cos(angle);
        float ySpeed = (float) Math.sin(angle);

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
        if (showEndScreen && event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            if (menuButtonRect.contains(x, y)) {
                returnToMainMenu();
                return true;
            }
        }
        // If paused, handle pause menu touch events
        if (isPaused && !showEndScreen && event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            if (resumeButton.contains(touchX, touchY)) {
                isPaused = false;
                return true;
            } else if (quitButton.contains(touchX, touchY)) {
                // Quit to main menu
                gameLoop.running = false;
                returnToMainMenu();
                return true;
            } else if (endGameButton.contains(touchX, touchY)) {
                gameLoop.running = false;
                showEndScreen();
                return true;
            }
            return true;
        }

        // Check for pause button press
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            if (pauseButton.contains(touchX, touchY)) {
                isPaused = true;
                return true;
            }
        }

        // Main touch events (non-menu actions)

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("Touch down");

            customers = customerSpawner.getCustomerGroups();

            float touchX = event.getX();
            float touchY = event.getY();
            int spriteSize = GameConstants.Sprite.SIZE;

            // Check for pause button press if touch isn't on the player.
            if (pauseButton.contains(touchX, touchY)) {
                isPaused = true;
                return true;
            }
            for (CustomerGroup customer : customers) {
                PointF custPos = customer.getCoords();
                // Define the bounding box dimensions for the customer
                float left = custPos.x;
                float top = custPos.y;
                float right = left + 100;   // customerWidth could be a constant or a customer property.
                float bottom = top + 300;    // Same for customerHeight.

                // Check if the touch is within this customer's bounds
                if (touchX >= left && touchX <= right && touchY >= top && touchY <= bottom) {
                    // Teleport customer to a table, or take the desired action
                    // For example:
                    teleportPlayer(customer,touchX - 75, touchY - 75);
                    System.out.println("On the player");
                    break;
                }
            }
        }
            return true;
    }

/**
 * Teleports the player to a fixed destination.
 */
    private void teleportPlayer(CustomerGroup customer, float x, float y) {

        int table_ID = random.nextInt(table_pos.size());
        PointF target = table_pos.get(table_ID);

        customer.setCoords(target);

        // If desired, reset animations.
        resetAnimation();
        System.out.println("Player teleported to: playerX = " + target.x + ", playerY = " + target.y);
    }

    public void resetAnimation() {
        aniTick = 0;         // Reset the counter that schedules frame changes
        playerAniIndexY = 0; // Reset the animation frame index to the first frame
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
        initializeGameObjects();
        gameLoop.startGameLoop();
    }

    private void initializeGameObjects() {
        Paint groupTextPaint = new Paint();
        groupTextPaint.setColor(Color.WHITE);
        groupTextPaint.setTextSize(40);
        groupTextPaint.setTextAlign(Paint.Align.CENTER);

        for (CustomerGroup group : customerSpawner.getCustomerGroups()) {

        }

        // for customer spawner
        customerSpawner.update();
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

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void showEndScreen() {
        showEndScreen = true;
        isPaused = true;
        if (gameLoop != null) {
            gameLoop.stopGameLoop();
        }
    }

    public void returnToMainMenu() {
        Context context = getContext();
        if (context instanceof MainActivity) {
            ((MainActivity) context).finishGame();
        }
    }

    public void update_score(double delta) {
            if (!isPaused) {
                // Update game logic
                updatePlayerMove(delta);
                // Update the score as an example:
                score += 1;  // You could base this on game events rather than a simple increment
                mapManager.setCameraValues(cameraX, cameraY);
            }

            // ... existing logic for enemy movement, skeletonDir adjustments, etc.

            updatedAnimation(aniSpeed);
    }


    public void gameOver() {
        showEndScreen();
        // You might want to save the score here
    }
}