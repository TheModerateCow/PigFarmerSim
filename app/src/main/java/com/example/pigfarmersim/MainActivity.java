package com.example.pigfarmersim;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static Context gameContext;

    public static int GAME_WIDTH, GAME_HEIGHT;

    private GamePanel gamePanel;

    private FrameLayout rootLayout;      // Container for gamePanel and overlays

    private FrameLayout pauseMenuOverlay; // Reference to the pause menu overlay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameContext = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);

        GAME_WIDTH = dm.widthPixels;
        GAME_HEIGHT = dm.heightPixels;

        // Landscape
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        rootLayout = new FrameLayout(this);

        gamePanel = new GamePanel(this);
        rootLayout.addView(gamePanel);

        Button pauseButton = new Button(this);
        pauseButton.setText("Pause");

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        buttonParams.bottomMargin = 20; // Adjust margin as needed

        rootLayout.addView(pauseButton, buttonParams);

        System.out.println("Width: " + dm.widthPixels + "  Height: " + dm.heightPixels);

        // Set an OnClickListener for the button to perform an action (e.g. pause the game)
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPauseMenu();
                gamePanel.pauseGame();  // Make sure you implement this method in GamePanel
            }
        });

        setContentView(rootLayout);
    }

    // Method to display the pause menu overlay
    private void showPauseMenu() {
        // Create an overlay FrameLayout with a semi-transparent background
        pauseMenuOverlay = new FrameLayout(this);
        pauseMenuOverlay.setBackgroundColor(0x80000000); // 50% transparent black

        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        // Create a vertical LinearLayout to hold the buttons
        LinearLayout menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setGravity(Gravity.CENTER);

        // Define layout parameters for the buttons inside the menu layout
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(0, 20, 0, 20);

        // Resume Button
        Button resumeButton = new Button(this);
        resumeButton.setText("Resume");
        resumeButton.setLayoutParams(buttonLayoutParams);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePauseMenu();
                gamePanel.pauseGame(); // Resume the game loop
            }
        });
        menuLayout.addView(resumeButton);

        // End Game Button
        Button endGameButton = new Button(this);
        endGameButton.setText("End Game");
        endGameButton.setLayoutParams(buttonLayoutParams);
        endGameButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Ending Game", Toast.LENGTH_SHORT).show();
            // Add any cleanup or transition logic here
            finish(); // End the activity (game)
        });
        menuLayout.addView(endGameButton);

        // Add the menu layout to the overlay
        pauseMenuOverlay.addView(menuLayout, overlayParams);

        // Add the overlay to the root layout, ensuring it appears on top of everything else
        rootLayout.addView(pauseMenuOverlay, overlayParams);
    }

    private void hidePauseMenu() {
        if (pauseMenuOverlay != null) {
            rootLayout.removeView(pauseMenuOverlay);
            pauseMenuOverlay = null;
        }
    }

    public static Context getGameContext() {
        return gameContext;
    }
}