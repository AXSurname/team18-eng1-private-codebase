package io.engproject;

/**
 * This tracks the 5 minute survival timer and whether the game is paused.
 * Implements FR_TIMER, FR_START_PAUSED, FR_PAUSE_TOGGLE and FR_WIN_CONDITION.
 */
public class GameTimer {
    private static final float GAME_LENGTH = 300f; // 5 minutes in seconds
    private float timeRemaining;
    private boolean paused;

    /** Creates a GameTimer in its resetted state. */
    public GameTimer() {
        reset();
    }

    /**
     * Reverts the timer back to 5:00, and pauses it. Used at the start and when the game
     * is completed or is restarted.
     */
    public void reset () {
        timeRemaining = GAME_LENGTH;
        paused = true; //the game starts paused (FR_START_PAUSED)
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Returns the time left (in minutes and seconds) until the player wins.
     * 
     * @return the time as format M:SS
     */
    public String getFormattedTime() {
        int totalSeconds = (int) Math.ceil(timeRemaining);
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isFinished() {
        return timeRemaining <= 0;
    }

    /** Pauses the game if it is running, or resumes it if it is paused. */
    public void togglePause() {
        paused = !paused;
    }

    /** Counts down by dt seconds, unless paused or finished. */
    public void update(float dt) {
        if (paused || isFinished()) {
            return;
        }

        timeRemaining = Math.max(0f, timeRemaining - dt);
    }
}
