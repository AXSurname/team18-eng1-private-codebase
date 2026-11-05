package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player extends GameObject {
    private static final float SCALE = 1/32f;
    private static final int LIFE = 5;
    private static final float SPEED = 2f;
    private static final float BOOSTED_SPEED = 4f;
    private static final float BOOST_DURATION = 5f;
    private static final float INTERACT_COOLDOWN = 1.3f;
    
    private final PlayerStats stats = new PlayerStats();
    private final Viewport viewport;
    private final Vector2 moveDirection = new Vector2();
    private final Vector2 lastDirection = new Vector2(1, 0);
    private Vector2 origin;
    private float life = LIFE;
    private float interactTimer;
    private boolean recievedInput = false;
    private final Array<Interaction> interactions = new Array<>();
    private final Animation<Texture> interactAnimation;
    private float boostTimer;

    public Player(float x, float y, Viewport viewport, Texture texture, Animation<Texture> interactAnimation) {
        super(x, y, texture.getWidth() * SCALE, texture.getHeight() * SCALE, texture);
        this.viewport = viewport;
        this.interactAnimation = interactAnimation;
        this.origin = new Vector2(x, y);
    }

    public float getLife() {
        return Math.max(life, 0f);
    }

    public int getItemsUsed() {
        return stats.getItemsUsed();
    }

    public int getEventsTriggered() {
        return stats.getEventsTriggered();
    }

    public Vector2 getResetPos() {
        return origin;
    }

    public Array<Interaction> getInteractions() {
        return interactions;
    }

    public boolean isAlive() {
        return life > 0f;
    }

    public void setRecievedInput(boolean bool) {
        this.recievedInput = bool;
    }

    public void reset(Vector2 pos) {
        rect.setPosition(pos);
        life = LIFE;
        recievedInput = false;
        boostTimer = 0f;
        interactTimer = 0f;
        interactions.clear();
        stats.reset();
    }

    @Override
    void update(float dt) {
        boostTimer = Math.max(0f, boostTimer - dt);
        
        if (canInteract(dt) && recievedInput) {
            var playerCentre = getCentre(TMP_VEC2);
            interactions.add(new Interaction(playerCentre, lastDirection, interactAnimation));

            interactTimer = INTERACT_COOLDOWN;
            recievedInput = false;
        }

        // Update interaction
        var iterator = interactions.iterator();
        
        while (iterator.hasNext()) {
            var interaction = iterator.next();
            interaction.update(dt);

            if (interaction.isDone()) {
                iterator.remove();
            }
        }

        move(dt);
    }

    private boolean canInteract(float dt) {
        interactTimer -= dt;

        if (interactTimer <= 0) {
            return true;
        }

        return false;
    }

    private void move(float dt) {
        float newX;
        float newY;

        if (moveDirection.isZero()) {
            return;
        }

        if (boostTimer > 0f) {
             newX = rect.getX() + moveDirection.x * BOOSTED_SPEED * dt;
             newY = rect.getY() + moveDirection.y * BOOSTED_SPEED * dt;
        } 
        else 
        {
            newX = rect.getX() + moveDirection.x * SPEED * dt;
            newY = rect.getY() + moveDirection.y * SPEED * dt;
        }
        newX = MathUtils.clamp(newX, 0, viewport.getWorldWidth() - rect.getWidth());
        newY = MathUtils.clamp(newY, 0, viewport.getWorldHeight() - rect.getHeight());

        rect.setPosition(newX, newY);
    }

    public void changeDirection(Vector2 direction) {
        if (!direction.isZero()) {
            lastDirection.set(direction);
        }

        moveDirection.set(direction);
    }

    public void subLife(float amount) {
        this.life -= amount;
    }

    public void activateBoost() {
        boostTimer = BOOST_DURATION;
        stats.incrementItemsUsed();;
    }

    public void eventTriggered() {
        stats.incrementEventsTriggered();
    }

    public void itemUsed() {
        stats.incrementItemsUsed();
    }
}
