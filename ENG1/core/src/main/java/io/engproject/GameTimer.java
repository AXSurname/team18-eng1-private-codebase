package io.engproject;

/**
 * This tracks the 5minute survival timer and whether the game is paused.
 * Implements FR_TIMER, FR_START_PAUSED, FR_PAUSE_TOGGLE and FR_WIN_CONDITION
 */

public class GameTimer {
    private static final float GAME_LENGTH = 300f; //5 minutes in seconds
    private float timeRemaining;
    private boolean paused;

    public GameTimer() {
        reset();
    }

    /**Puts the timer back to 5:00, paused. Used at the start and when the game is completed/restarts */
    public void reset () {
        timeRemaining = GAME_LENGTH;
        paused = true; //the game starts paused (FR_START_PAUSED)
    }

    /**Counts down by dt seconds, unless paused or finished. */
    public void update(float dt) {
        if (paused || isFinished()) {
            return;
        }
        timeRemaining = Math.max(0f, timeRemaining - dt);
    }

    /**Pauses the game if itis running, or resumes it if it is paused */
    public void togglePause() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isFinished() {
        return timeRemaining <= 0;
    }

    /**Returns the time left as M:SS */
    public String getFormattedTime() {
        int totalSeconds = (int) Math.ceil(timeRemaining);
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}
