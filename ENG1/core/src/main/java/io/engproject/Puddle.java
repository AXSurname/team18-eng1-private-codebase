package io.engproject;

import com.badlogic.gdx.graphics.Texture;

public class Puddle extends GameObject {
    private static final float SPEED_MODIFIER = 0.5f;
    
    private boolean isVisible = false;
    private boolean isPlayerInside = false;

    public Puddle(float x, float y, float w, float h, Texture texture) {
        super(x, y, w, h, texture);
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean bool) {
        isVisible = bool;
    }

    @Override 
    public void update(float dt) {}

    public float getSpeedModifier() {
        return SPEED_MODIFIER;
    }

    public boolean isPlayerInside() {
        return isPlayerInside;
    }

    public void setPlayerInside(boolean playerInside) {
        this.isPlayerInside = playerInside;
    }

    

}
