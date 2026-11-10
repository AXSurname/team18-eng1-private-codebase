package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

/** The player avatar which the user contols, and is moved around the map to evade geese. */
public class Player extends GameObject {
    private static final float SCALE = 1/32f;
    private static final int LIFE = 5;
    private static final float SPEED = 2f;
    private static final float INTERACT_COOLDOWN = 1.3f;

    // EnergyDrinkItem constants
    private static final float BOOSTED_SPEED = 4f;
    private static final float BOOST_DURATION = 5f;

    // Goose drag multipliers
    private static final float SLOW_PER_GOOSE = 0.8f;     // Multiply speed per touching Goose
    private static final float MIN_SPEED_MODIFIER = 0.3f; // Never slower than 30% speed
    
    // General
    private final Viewport viewport;

    private Vector2 origin;
    private final Vector2 moveDirection = new Vector2();
    private final Vector2 lastDirection = new Vector2(1, 0); // Facing; default is right
    private static final float MEGAPHONE_DURATION = 5f;

    private float boostTimer;
    private float megaphoneTimer;
    private float speedModifier = 1f;
    private float terrainModifier = 1f;   // puddles; multiplied with the geese slowdown
    private float life = LIFE;
    private final PlayerStats stats = new PlayerStats();
    
    // Interaction variables
    private float interactTimer;
    private boolean isThrowingBread = false;
    private final Array<Interaction> interactions = new Array<>();
    private final Animation<Texture> interactAnimation;

    /**
     * Creates a player avatar for the user to control.
     * 
     * @param x        the initial x-position of the player
     * @param y        the initial y-position of the player
     * @param viewport the current screen's viewport
     * @param texture  the image representing the player
     * @param interactAnimation
     * the animation frames for the player's possible interaction with the geese.
     */
    public Player(float x, float y, Viewport viewport,
                  Texture texture, Animation<Texture> interactAnimation) {
        super(x, y, texture.getWidth() * SCALE, texture.getHeight() * SCALE, texture);
        this.viewport = viewport;
        this.origin = new Vector2(x, y);
        this.interactAnimation = interactAnimation;
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

    public float getSpeedModifier() {
        return speedModifier;
    }

    public boolean isAlive() {
        return life > 0f;
    }

    public void setIsThrowingBread(boolean bool) {
        isThrowingBread = bool;
    }

    /**
     * Resets the player state, including its position, life points and any modifiers.
     * @param pos
     */
    public void reset(Vector2 pos) {
        rect.setPosition(pos);
        life = LIFE;
        speedModifier = 0f;
        boostTimer = 0f;
        interactTimer = 0f;
        interactions.clear();
        stats.reset();
        speedModifier = 1f;
        terrainModifier = 1f;
        megaphoneTimer = 0f;
    }

    /**
     * Updates the player state, such as its position, interactions and modifiers.
     * 
     * @param dt deltatime
     */
    @Override
    void update(float dt) {
        // Count down the energy drink boost
        boostTimer = Math.max(0f, boostTimer - dt);
        megaphoneTimer = Math.max(0f, megaphoneTimer - dt);
        interactTimer -= dt;

        // Update interaction animation if throwing bread
        if (isThrowingBread) {
            updateThrowBread(dt);
        }

        // Move player
        move(dt);
    }

    /**
     * Checks if the player can interact by updating and
     * checking the interaction cooldown timer.
     *
     * @param dt the amount of time passed from last frame
     * @return true if the cooldown has expired, false otherwise
     */
    public boolean getCanInteract() {
        if (interactTimer <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Updates player's position based on their current move,
     * direction and speed. Adds a speed boost and ensures
     * player stays visible on the screen.
     *
     * @param dt the amount of time passed from last frame
     */
    private void move(float dt) {
        if (moveDirection.isZero()) {
            return;
        }

        float speed = (boostTimer > 0f ? BOOSTED_SPEED : SPEED) * speedModifier * terrainModifier;
        float newX = rect.getX() + moveDirection.x * speed * dt;
        float newY = rect.getY() + moveDirection.y * speed * dt;

        // Clamp to screen bounds
        newX = MathUtils.clamp(newX, 0, viewport.getWorldWidth() - rect.getWidth());
        newY = MathUtils.clamp(newY, 0, viewport.getWorldHeight() - rect.getHeight());

        rect.setPosition(newX, newY);
    }

    /**
     * Changes the direction the player is moving toward. The player therefore faces
     * this direction.
     * @param direction the new direction the player faces
     */
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

    public void subLife(float amount) {
        this.life -= amount;
    }

    // Called by BreadItem: allows player to feed geese with infinite bread
    public void startThrowBread() {
        var playerCentre = getCentre(TMP_VEC2);
        interactions.add(new BreadInteraction(playerCentre, lastDirection, interactAnimation));

        interactTimer = INTERACT_COOLDOWN;
    }

    private void updateThrowBread(float dt) {
        var iterator = interactions.iterator();
        
        while (iterator.hasNext()) {
            var interaction = iterator.next();
            interaction.update(dt);

            if (interaction.isDone()) {
                iterator.remove();
                isThrowingBread = false;
            }
        }
    }

    // Called by EnergyDrinkItem: double speed for 5 seconds
    public void activateBoost() {
        boostTimer = BOOST_DURATION;
    }

    public boolean isBoosted() {
        return boostTimer > 0f;
    }

    public void applySpeedModifier(float modifier) {
        terrainModifier = modifier;
    }

    // Called by MegaphoneItem: scares geese away for 5 seconds
    public void activateMegaphone() {
        megaphoneTimer = MEGAPHONE_DURATION;
    }

    public boolean megaphoneActive() {
        return megaphoneTimer > 0f;
    }

    // Increment player statistics methods
    public void eventTriggered() {
        stats.incrementEventsTriggered();
    }

    public void itemUsed() {
        stats.incrementItemsUsed();
    }
}
