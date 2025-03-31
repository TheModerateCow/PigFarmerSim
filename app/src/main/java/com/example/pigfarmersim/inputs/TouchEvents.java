package com.example.pigfarmersim.inputs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.pigfarmersim.GamePanel;
import com.example.pigfarmersim.MainActivity;

/**
 * Handles touch input events for the game's movement control.
 * <p>
 * This class manages a virtual joystick for player movement,
 * drawing a touch-responsive circular control area and
 * calculating movement vectors based on touch interactions.
 */
public class TouchEvents {
    /**
     * Reference to the game panel for updating player movement
     */
    private GamePanel gamePanel;

    /**
     * X-coordinate, Y-coordinate and Radius of the joystick's center
     */
    private float xCenter = 400, yCenter = 700, radius = 150;

    /**
     * X-coordinate of the current touch point
     */
    private float xTouch, yTouch;

    /**
     * Indicates whether the joystick is currently being touched
     */
    private boolean touchDown;

    /**
     * Paint for drawing the joystick circle outline and Paint for drawing touch
     * movement lines
     */
    private final Paint circlePaint, yellowPaint;

    /**
     * Constructs a new TouchEvents instance.
     *
     * @param gamePanel The GamePanel to which touch events will be applied
     */
    public TouchEvents(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5);
        circlePaint.setColor(Color.RED);
        yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
    }

    /**
     * Draws the joystick control area and touch indicators.
     *
     * @param c The canvas on which to draw the touch controls
     */
    public void draw(Canvas c) {
        c.drawCircle(xCenter, yCenter, radius, circlePaint);

        // Pause Menu Rectangle
        RectF rect = new RectF(MainActivity.GAME_WIDTH - 200, 50, MainActivity.GAME_WIDTH - 50, 150);
        c.drawRect(rect, circlePaint);

        if (touchDown) {
            c.drawLine(xCenter, yCenter, xTouch, yTouch, yellowPaint);
            c.drawLine(xCenter, yCenter, xTouch, yCenter, yellowPaint);
            c.drawLine(xTouch, yTouch, xTouch, yCenter, yellowPaint);
        }
    }

    /**
     * Handles touch events for player movement.
     *
     * @param event The motion event to process
     * @return Always returns true to indicate event handling
     */
    public boolean touchEvent(MotionEvent event) {

        switch (event.getAction()) {
            // Handle the initial touch down event
            case MotionEvent.ACTION_DOWN -> {
                float x = event.getX();
                float y = event.getY();

                // Calculate the distance between the touch point and the center of the joystick
                float a = Math.abs(x - xCenter);
                float b = Math.abs(y - yCenter);
                float c = (float) Math.hypot(a, b);

                if (c <= radius) {
                    System.out.println("Inside!");
                    touchDown = true;
                    xTouch = x;
                    yTouch = y;
                } else {

                }
            }
            case MotionEvent.ACTION_MOVE -> {
                // Update the touch position and calculate the movement vector
                if (touchDown) {
                    xTouch = event.getX();
                    yTouch = event.getY();

                    float xDiff = xTouch - xCenter;
                    float yDiff = yTouch - yCenter;

                    gamePanel.setPlayerMoveTrue(new PointF(xDiff, yDiff));
                }

            }
            case MotionEvent.ACTION_UP -> {
                // Reset the touch down state and player movement when the touch is released
                touchDown = false;
                gamePanel.setPlayerMoveFalse();
            }
        }
        return true;
    }
}
