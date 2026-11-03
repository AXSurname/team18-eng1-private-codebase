package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player extends GameObject {
    private static final float SCALE = 1 / 32f;
    private static final float SPEED = 2f;
    private static final float BOOSTED_SPEED = 4f;        // used by EnergyDrinkItem
    private static final float BOOST_DURATION = 5f;
    private static final float SLOW_PER_GOOSE = 0.8f;     // each touching goose multiplies speed by this
    private static final float MIN_SPEED_MODIFIER = 0.3f; // never slower than 30% speed

    private final Vector2 moveDirection = new Vector2(0, 0);
    private final Vector2 lastDirection = new Vector2(1, 0); // facing; default is right
    private float speedModifier = 1f;
    private float boostTimer;
    private final Viewport gameViewport;

    public Player(float x, float y,
                  Viewport gameViewport,
                  Texture texture) {
        super(x, y, texture.getWidth() * SCALE, texture.getHeight() * SCALE, texture);
        reset(x, y);
        this.gameViewport = gameViewport;
    }

    public void reset(float x, float y) {
        rect.setPosition(x, y);
        speedModifier = 1f;
        boostTimer = 0f;
    }

    @Override
    void update(float deltaTime) {
        // Count down the energy drink boost
        boostTimer = Math.max(0f, boostTimer - deltaTime);

        // Move player
        move(deltaTime);
    }

    private void move(float deltaTime) {
        if (moveDirection.isZero()) return;

        float speed = (boostTimer > 0f ? BOOSTED_SPEED : SPEED) * speedModifier;
        float newX = rect.getX() + moveDirection.x * speed * deltaTime;
        float newY = rect.getY() + moveDirection.y * speed * deltaTime;

        // Clamp to screen bounds
        newX = MathUtils.clamp(newX, 0, gameViewport.getWorldWidth() - rect.getWidth());
        newY = MathUtils.clamp(newY, 0, gameViewport.getWorldHeight() - rect.getHeight());

        rect.setPosition(newX, newY);
    }

    public void changeDirection(Vector2 direction) {
        if (!direction.isZero()) {
            lastDirection.set(direction);
        }
        moveDirection.set(direction);
    }

    // Called every frame with the number of geese overlapping the player
    public void setGeeseTouching(int count) {
        speedModifier = Math.max(MIN_SPEED_MODIFIER, (float) Math.pow(SLOW_PER_GOOSE, count));
    }

    // Called by EnergyDrinkItem (feature/item-implementation): double speed for 5 seconds
    public void activateBoost() {
        boostTimer = BOOST_DURATION;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }
}
