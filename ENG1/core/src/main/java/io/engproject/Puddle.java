package io.engproject;

import com.badlogic.gdx.graphics.Texture;

/**
 * A puddle that exists in the game's world. It is invisible until a player collides
 * with it, and then will slow the player down for any further collision.
 */
public class Puddle extends GameObject {
    private static final float SPEED_MODIFIER = 0.5f;
    public static final float WIDTH = 2f;
    public static final float HEIGHT = 1f;
    
    private boolean isVisible = false;
    private boolean isPlayerInside = false;

    /**
     * Creates a puddle.
     * 
     * @param x       the puddle's x-position
     * @param y       the puddle's y-position
     * @param w       the puddle's width
     * @param h       the puddle's height
     * @param texture the image representing the puddle
     */
    public Puddle(float x, float y, float w, float h, Texture texture) {
        super(x, y, w, h, texture);
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean bool) {
        isVisible = bool;
    }

    public float getSpeedModifier() {
        return SPEED_MODIFIER;
    }

    public boolean isPlayerInside() {
        return isPlayerInside;
    }

    public void setPlayerInside(boolean playerInside) {
        this.isPlayerInside = playerInside;
    }

    @Override 
    public void update(float dt) {}
}
