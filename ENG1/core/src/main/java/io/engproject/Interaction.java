package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/**
 * An animated interaction which a player triggers to affect a goose.
 * 
 * The {@link #DURATION} is set in accordance with the length of the passed-in animation
 * (i.e. the number of images converted into frames). The {@link #SIZE} sets how large
 * the interaction will be in the game relative to the size of its images.
 * */
public abstract class Interaction extends GameObject {
    private final float DURATION;
    private final float SIZE;

    private float lifespan;
    private final Animation<Texture> animation;

    /**
     * Creates an interation.
     * 
     * @param pos       the player's current position
     * @param direction the direction the player is facing
     * @param animation the frames of the animation representing the interaction
     */
    public Interaction(Vector2 pos, Vector2 direction,
                       float duration, float size,
                       Animation<Texture> animation) {
        super(pos.x - size/2 + direction.x * size * 0.75f,
              pos.y - size/2 + direction.y * size * 0.75f,
              size, size);
              
        DURATION = duration;
        SIZE = size;

        lifespan = DURATION;
        this.animation = animation;
    }

    public boolean isDone() {
        return lifespan <= 0;
    }

    /**
     * Decreases the time left for the interaction to play.
     * 
     * @param dt deltatime
     */
    @Override
    void update(float dt) {
        this.lifespan -= dt;
    }

    /**    (non-Javadoc)
     * Draws the current frame of the interaction onto the screen.
     * @see io.engproject.GameObject#draw(com.badlogic.gdx.graphics.g2d.Batch)
     */
    @Override
    public void draw(Batch batch) {
        float animationDuration = animation.getAnimationDuration();
        float animationPercentage = 1f - (Math.max(0f, lifespan) / DURATION);
        float stateTime = animationDuration * animationPercentage;
        
        texture = animation.getKeyFrame(stateTime, true);
        super.draw(batch);
    }
}
