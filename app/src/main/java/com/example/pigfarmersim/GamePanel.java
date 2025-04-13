package com.example.pigfarmersim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.pigfarmersim.entities.GameCharacters;
import com.example.pigfarmersim.entities.Table;
import com.example.pigfarmersim.environments.MapManager;
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
    private ArrayList<PointF> skeletons = new ArrayList<>();
    private List<PointF> table_pos = new ArrayList<>();
    private List<PointF> cust_pos = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
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

        resumeButton = new RectF(centerX - buttonWidth/2, centerY - buttonHeight - 20,
                centerX + buttonWidth/2, centerY - 20);
        quitButton = new RectF(centerX - buttonWidth/2, centerY + 20,
                centerX + buttonWidth/2, centerY + buttonHeight + 20);

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
        scoreTextPaint.setColor(Color.YELLOW);
        scoreTextPaint.setTextSize(80);
        scoreTextPaint.setTextAlign(Paint.Align.CENTER);

        endButtonTextPaint = new Paint();
        endButtonTextPaint.setColor(Color.BLACK);
        endButtonTextPaint.setTextSize(60);
        endButtonTextPaint.setTextAlign(Paint.Align.CENTER);

        // Calculate positions for end screen elements
        int endCenterX = MainActivity.GAME_WIDTH / 2;
        int endCenterY = MainActivity.GAME_HEIGHT / 2;

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

        endGameButton = new RectF(centerX - buttonWidth/2, centerY + buttonHeight + 60,
                centerX + buttonWidth/2, centerY + 2*buttonHeight + 60);
    }

    public GameLoop getGameLoop() {
        return gameLoop;
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        mapManager.draw(c);

        touchEvents.draw(c);

//        for (int i = 0; i < 2; i++) {
//            Customer customer = new Customer();
//            customer.setCust_ID(i);
//            if (customer.getPos() == null) {
//                c.drawBitmap(Customer.getSprite(customerDir, customerFrame), 32 + cameraX + 100 * i, 32 + cameraY, null);
//                customer.setPos(new PointF(32 + cameraX + 100 * i, 32 + cameraY));
//            } else {
//                c.drawBitmap(Customer.getSprite(customerDir, customerFrame), 32 + cameraX + 100 * i, 32 + cameraY, null);
//            }
//            customers.add(customer);
//        }
        for (Customer customer : customers) {
            PointF pos = customer.getPos();
            c.drawBitmap(Customer.getSprite(customerDir, customerFrame), pos.x + cameraX, pos.y + cameraY, null);
        }
//         Starting coordinates (adjust as needed)
        float startX = 100f;
        float startY = 350f;

        // Spacing between tables (adjust as needed)
        float spacingX = 185f;
        float spacingY = 175f;

        List<PointF> positions = new ArrayList<>();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 12; col++) {
                float x = startX + col * spacingX;
                float y = startY + row * spacingY;
                positions.add(new PointF(x, y));
            }
        }

        table_pos = positions;

        for (PointF pos : positions) { c.drawBitmap(Table.TABLE.getSprite(), pos.x, pos.y, null); }

        // Draw pause button
        c.drawRoundRect(pauseButton, 10, 10, pauseButtonPaint);

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

            // Draw score
            c.drawText("YOUR SCORE",
                    MainActivity.GAME_WIDTH / 2,
                    endScreenBgRect.top + 250,
                    scoreTextPaint);
            c.drawText(String.valueOf(0),
                    MainActivity.GAME_WIDTH / 2,
                    endScreenBgRect.top + 350,
                    scoreTextPaint);

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

        holder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        // Only update game state if not paused
        if (!isPaused) {
            updatePlayerMove(delta);
            mapManager.setCameraValues(cameraX, cameraY);
        }

        if (System.currentTimeMillis() - lastDirChange >= 3000) {
            skeletonDir = random.nextInt(4);
            lastDirChange = System.currentTimeMillis();
            customerDir = (customerDir + 1) % 4;
        }

        if (System.currentTimeMillis() - frameTime >= 1000) {
            customerFrame = (customerFrame + 1) % 4;
            frameTime = System.currentTimeMillis();
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
        System.out.println(event.getX());
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

            float touchX = event.getX();
            float touchY = event.getY();
            int spriteSize = GameConstants.Sprite.SIZE;

            // Check for pause button press if touch isn't on the player.
            if (pauseButton.contains(touchX, touchY)) {
                isPaused = true;
                return true;
            }

            for (Customer customer : customers) {
                PointF custPos = customer.getPos();
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

//            for (PointF pos: cust_pos) {
//                if (touchX >= pos.x && touchX <= pos.x + 30 && touchY >= pos.y && touchY <= pos.y + 50) {
////                    teleportPlayer(tou, touchY - 75);
////                    pos.x
//                }
//            }

//            teleportPlayer(customer,touchX - 75, touchY - 75);
        }
        return true;
    }

    /**
     * Teleports the player to a fixed destination.
     */
    private void teleportPlayer(Customer customer, float x, float y) {

        int table_ID = random.nextInt(table_pos.size());
        PointF target = table_pos.get(table_ID);
        for (Customer c : customers) {
            if (c.getTable_ID() == table_ID) {

            }
        }
        customer.setTable_ID(table_ID);
        customer.setPos(target);

        if (table_idx < table_pos.size()-1) {
            System.out.println("Table index is:" + table_idx);
            table_idx++;
        } else {
                table_idx = 0;
                customer.setPos(target);
        }

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
        // For instance, create all your customers once
        for (int i = 0; i < numberOfCustomers; i++) {
            Customer customer = new Customer();
            customer.setCust_ID(i);
            // Set a starting position according to your game design
            customer.setPos(new PointF(initialX + 100 * i, initialY));
            customers.add(customer);
        }
        // Initialize other game objects (tables, players, etc.)
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

    public void gameOver() {
        showEndScreen();
        // You might want to save the score here
    }
}