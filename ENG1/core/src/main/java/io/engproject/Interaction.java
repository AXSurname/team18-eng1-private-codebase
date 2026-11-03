package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Interaction extends GameObject {
    private static final float DURATION = 0.4f;
    private static final float SIZE = 1f;

    private float lifespan = DURATION;
    private final Animation<Texture> animation;

    public Interaction(Vector2 pos, Vector2 direction, Animation<Texture> animation) {
        super(pos.x - SIZE/2 + direction.x * SIZE * 0.75f,
              pos.y - SIZE/2 + direction.y * SIZE * 0.75f,
              SIZE, SIZE);
        this.animation = animation;
    }

    @Override
    void update(float dt) {
        this.lifespan -= dt;
    }

    public boolean isDone() {
        return lifespan <= 0;
    }

    @Override
    public void draw(Batch batch) {
        float animationDuration = animation.getAnimationDuration();
        float animationPercentage = 1f - (Math.max(0f, lifespan) / DURATION);
        float stateTime = animationDuration * animationPercentage;
        
        texture = animation.getKeyFrame(stateTime, true);
        super.draw(batch);
    }
}
