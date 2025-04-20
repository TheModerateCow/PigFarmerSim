package com.example.pigfarmersim;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.WindowMetrics;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    private static GamePanel gamePanel;
    public static int GAME_WIDTH, GAME_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();
        GAME_WIDTH = bounds.width();
        GAME_HEIGHT = bounds.height();

        // Make the activity fullscreen using modern WindowInsets API
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(),
                getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getWindow()
                .getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        setContentView(new GamePanel(this));
    }

    public void finishGame() {
        if (gamePanel != null) {
            // Clean up resources
            gamePanel.setPaused(true);
            if (gamePanel.getGameLoop() != null) {
                gamePanel.getGameLoop().stopGameLoop();
            }
            gamePanel.getCustomerManager().stopThread();
        }

        Intent intent = new Intent(this, MainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
        startActivity(intent);
        finish(); // This will close the current activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gamePanel != null) {
            gamePanel.setPaused(true);
        }
        // Clear static GamePanel reference to prevent memory leaks
        gamePanel = null;
    }

    public static Context getGameContext() {
        return PigFarmerApplication.getAppContext();
    }
}
