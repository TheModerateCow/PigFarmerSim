package com.example.pigfarmersim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.pigfarmersim.entities.Customer;
import com.example.pigfarmersim.entities.CustomerThread;
import com.example.pigfarmersim.environments.MapLoader;
import com.example.pigfarmersim.managers.CustomerManager;
import com.example.pigfarmersim.managers.QueueManager;
import com.example.pigfarmersim.managers.ScoreManager;
import com.example.pigfarmersim.managers.TableManager;
import com.example.pigfarmersim.helpers.GameConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A custom SurfaceView that serves as the game panel.
 * It implements the {@link SurfaceHolder.Callback} interface to handle changes
 * to the surface, which is used for rendering game graphics.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private final SurfaceHolder holder;
    private final GameLoop gameLoop;
    private int customerDir = GameConstants.Face_Dir.DOWN;
    private long lastDirChange = System.currentTimeMillis();
    private int customerFrame = 0;
    private long lastFrameChange = System.currentTimeMillis();
    private final MapLoader mapLoader = new MapLoader();
    private final QueueManager queueManager = new QueueManager();
    private final TableManager tableManager = new TableManager();
    private final ScoreManager scoreManager = new ScoreManager();
    private final CustomerManager customerManager = new CustomerManager(scoreManager);
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
    private final Paint scorePaint;
    private List<CustomerThread> noOfProcesses;
    private static final int MAX_PROCESSES = 3;
    // for max process size flashing
    private boolean shouldFlash = false;
    private long flashStartTime = 0;
    private boolean flashOn = false;
    private static final long FLASH_DURATION = 1000; // total duration of flashing (e.g., 1s)
    private static final long FLASH_INTERVAL = 200; // how often it blinks

    /**
     * Constructs a new GamePanel with the specified context.
     *
     * @param context the Context the view is running in, through which it can access resources.
     */
    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        gameLoop = new GameLoop(this);
        customerManager.startThread();

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

        resumeButton = new RectF(centerX - buttonWidth / 2f, centerY - buttonHeight - 20,
                centerX + buttonWidth / 2f, centerY - 20);
        quitButton = new RectF(centerX - buttonWidth / 2f, centerY + 20,
                centerX + buttonWidth / 2f, centerY + buttonHeight + 20);

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

        endGameButton = new RectF(centerX - buttonWidth / 2f, centerY + buttonHeight + 60,
                centerX + buttonWidth / 2f, centerY + 2 * buttonHeight + 60);

        //Score pain
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(100);
        scorePaint.setTextAlign(Paint.Align.CENTER); // Center alignment
        scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // for max process size
        noOfProcesses = new ArrayList<>();
    }

    public CustomerManager getCustomerManager() { return customerManager; }

    public GameLoop getGameLoop() {
        return gameLoop;
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        mapLoader.draw(c);

        tableManager.drawAll(c);

        // Draw pause button
        c.drawRoundRect(pauseButton, 10, 10, pauseButtonPaint);

        // *** Draw the scoreboard in the top-right corner ***
        int margin = 20;  // Padding from the edge
        // Using MainActivity.GAME_WIDTH here or you could use c.getWidth()
        String scoreText = "Score: " + scoreManager.getScore();
        c.drawText(scoreText, MainActivity.GAME_WIDTH / 2f, 60 + margin, scorePaint);

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
            c.drawText("PAUSED", MainActivity.GAME_WIDTH / 2f, menuTop + 100, titlePaint);

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
                    (float) MainActivity.GAME_WIDTH / 2,
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

        List<CustomerThread> customersCopy = new ArrayList<>(customerManager.getCustomerGroups());

        for (CustomerThread group : customersCopy) {
            if (!group.inQueue) {
                for (PointF pos : group.listPoints) {
                    c.drawBitmap(Customer.CUSTOMER.getSprite(customerDir, customerFrame), pos.x, pos.y, null);
                }
                continue;
            }

            while (group.queuePoint == null) {
                group.queuePoint = queueManager.giveFreeQueue();
            }

            PointF pos = group.queuePoint;

            // draw sprite at position
            c.drawBitmap(Customer.CUSTOMER.getSprite(customerDir, customerFrame), pos.x, pos.y, null);

            // draw group size above
            c.drawText("x" + group.groupSize, pos.x + 32, pos.y - 10, groupTextPaint);
        }

        // for customer timer
        Paint timerPaint = new Paint();

        for (CustomerThread group : customersCopy) {
            group.drawTimer(c, timerPaint);
        }

        // for max process size flashing
        if (shouldFlash) {
            scorePaint.setColor(flashOn ? Color.YELLOW : Color.WHITE);

            if (noOfProcesses.size() >= MAX_PROCESSES) {
                c.drawText("Maximum number of customers served", MainActivity.GAME_WIDTH / 2f, MainActivity.GAME_HEIGHT / 2f, scorePaint);
            } else if (queueManager.queuePool.isEmpty()){
                c.drawText("Maximum number of tables served", MainActivity.GAME_WIDTH / 2f, MainActivity.GAME_HEIGHT / 2f, scorePaint);
            }
        }

        holder.unlockCanvasAndPost(c);
    }

    public void update() {
        if (System.currentTimeMillis() - lastDirChange >= 3000) {
            lastDirChange = System.currentTimeMillis();
            customerDir = (customerDir + 1) % 4;
        }

        if (System.currentTimeMillis() - lastFrameChange >= 1000) {
            customerFrame = (customerFrame + 1) % 4;
            lastFrameChange = System.currentTimeMillis();
        }

        List<CustomerThread> customersToRemove = new ArrayList<>();
        // for customer timer
        for (CustomerThread customer : customerManager.getCustomerGroups()) {
            // Check for waiting timer expiration
            if (customer.waitExpire) {
                // Customer left because they waited too long
                customersToRemove.add(customer);
                queueManager.returnFreeQueue(customer.queuePoint);
            }

            // Check for job completion
            if (customer.jobDone) {
                // Customer served successfully
                customersToRemove.add(customer);
                queueManager.returnFreeQueue(customer.queuePoint);
                tableManager.returnFreeTables(customer);
                noOfProcesses.remove(customer);
            }
        }

        customerManager.customerPool.removeAll(customersToRemove);

        // for max process size flashing
        if (shouldFlash) {
            MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.error);
            mp.start();
            // Optionally, release the MediaPlayer when done to free resources:
            mp.setOnCompletionListener(MediaPlayer::release);
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - flashStartTime;

            if (elapsed >= FLASH_DURATION) {
                shouldFlash = false; // Stop flashing
                scorePaint.setColor(Color.BLACK); // Reset to default
            } else {
                if ((elapsed / FLASH_INTERVAL) % 2 == 0) {
                    scorePaint.setColor(Color.YELLOW);
                } else {
                    scorePaint.setColor(Color.WHITE);
                }
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


            float touchX = event.getX();
            float touchY = event.getY();

            // Check for pause button press if touch isn't on the player.
            if (pauseButton.contains(touchX, touchY)) {
                isPaused = true;
                return true;
            }

            List<CustomerThread> customerCopy = List.copyOf(customerManager.customerPool);
            Iterator<CustomerThread> iterator = customerCopy.iterator();
            while (iterator.hasNext()) {
                CustomerThread customer = iterator.next();
                PointF customerPos = customer.getCurrent();

                while (iterator.hasNext() && customerPos == null) {
                    customer = iterator.next();
                    customerPos = customer.getCurrent();
                }

                if (customerPos == null) {
                    return true;
                }
                // Define the bounding box dimensions for the customer
                float left = customerPos.x;
                float top = customerPos.y;
                float right = left + 100;   // customerWidth could be a constant or property.
                float bottom = top + 150;   // Same for customerHeight.

                // Check if the touch is within this customer's bounds
                if (touchX >= left && touchX <= right && touchY >= top && touchY <= bottom) {
                    if (customer.inQueue) {
                        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.mysound);
                        mp.start();
                        // Optionally, release the MediaPlayer when done to free resources:
                        mp.setOnCompletionListener(MediaPlayer::release);
                        if (tableManager.tablePool.size() > customer.groupSize && noOfProcesses.size() < MAX_PROCESSES) {
                            tableManager.giveFreeTables(customer);
                            customer.inQueue = false;
                            noOfProcesses.add(customer);
                        } else {
                            shouldFlash = true;
                            flashStartTime = System.currentTimeMillis();
                            flashOn = true; // optional: start with yellow
                        }
                    } else {
                        tableManager.returnFreeTables(customer);
                        customer.inQueue = true;
                        noOfProcesses.remove(customer);
                    }
                }
            }
        }
        return true;
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
        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.gamend);
        mp.start();
        // Optionally, release the MediaPlayer when done to free resources:
        mp.setOnCompletionListener(MediaPlayer::release);
        showEndScreen();
        // You might want to save the score here
    }
}