package com.example.pigfarmersim;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static Context gameContext;
    private static GamePanel gamePanel;
    public static int GAME_WIDTH, GAME_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameContext = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);

        GAME_WIDTH = dm.widthPixels;
        GAME_HEIGHT = dm.heightPixels;

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        System.out.println("Width: " + dm.widthPixels + "  Height: " + dm.heightPixels);

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
        finish();  // This will close the current activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gamePanel != null) {
            gamePanel.setPaused(true);
        }
        // Any other cleanup
    }
    public static Context getGameContext() {
        return gameContext;
    }
}