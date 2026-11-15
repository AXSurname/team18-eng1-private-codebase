package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A goose that needs to be evaded as it chases the player.
 * 
 * It moves away from the player for {@value #FEEDING_DURATION} seconds when it
 * has either been fed bread and becomes friendly, or has been scared with the
 * megaphone. When angry, it follows the player's centre at an increasing speed
 * as the game progresses.
 * 
 * Textures are owned by the screen that created them, not by this class.
*/
public class Goose extends GameObject {
    private static final float SCALE = 1/32f;
    private static final float STARTING_SPEED = 0.7f;
    private static final float MAX_SPEED = 2.0f;
    private static final float FEEDING_DURATION = 3f;
    
    private final Player player;
    private final GameTimer gameTimer;
    private final Texture angryGooseTexture;
    private boolean friendly;
    private float feedingTimer;
    private boolean isOverlapping = false;
    private float terrainModifier = 1f;   // Multiplies speed, e.g. 0.5 while in a puddle

    /**
     * Creates a Goose that needs to be evaded as it chases the player.
     * 
     * @param x       the initial x-position of the goose
     * @param y       the initial y-position of the goose
     * @param texture the image representing the goose which is displayed
     * @param player  the target the goose moves towards
     */
    public Goose(float x, float y, Texture texture, Player player, GameTimer gameTimer) {
        super(x, y, texture.getWidth() * SCALE, texture.getHeight() * SCALE, texture);
        this.player = player;
        this.gameTimer = gameTimer;
        this.angryGooseTexture = texture;
    }

    public Player getPlayer() {
        return player;
    }

    protected float getCurrentSpeed() {
        float passedTime = 300f - gameTimer.getTimeRemaining();
        float progress = MathUtils.clamp(passedTime/300f, 0f, 1f);
        return STARTING_SPEED + (MAX_SPEED - STARTING_SPEED) * progress;
    }
  
    /**
     * Sets the terrain speed multiplier (1 = normal, e.g. a puddle's modifier while
     * inside one).
     * */
    public void applySpeedModifier(float modifier) {
        terrainModifier = modifier;
    }

    protected float getTerrainModifier() {
        return terrainModifier;
    }

    /**
     * Makes this goose friendly and changes its colour. A friendly goose
     * stops to feed before turning angry and chasing the player again.
     *
     * @param texture the Texture representing a friendly goose
     */
    public void makeFriendly(Texture texture) {
        if (friendly) {
            return;
        }

        this.texture = texture;
        friendly = true;
        feedingTimer = FEEDING_DURATION;
    }

    public boolean isFriendly() {
        return friendly;
    }

    /**
     * Updates whether a goose has been fed recently and returns if a goose should
     * move this frame.
     *
     * @param dt deltatime
     * @return {@code false} while the goose has been fed recently
     */
    protected boolean isFeeding(float dt) {
        if (!friendly) {
            return true;
        }

        feedingTimer -= dt;
        if (feedingTimer <= 0f) {
            friendly = false;
            texture = angryGooseTexture;
        }
        return false;
    }

    /**
     * Updates the goose's movement.
     * 
     * @param dt deltatime
     */
    @Override
    public void update(float dt) {
        if (!isFeeding(dt)) {
            return;
        }

        Vector2 gooseCentre = new Vector2(rect.x + rect.width/2, rect.y + rect.height/2);
        Vector2 direction = player.rect.getCenter(TMP_VEC2).sub(gooseCentre).nor()
                                  .scl(getCurrentSpeed() * terrainModifier * dt);

        if (player.megaphoneActive()) {
            // Move away from player centre.
            direction.scl(-1);
        }
        rect.setPosition(rect.getX() + direction.x, rect.getY() + direction.y);
    }

    /**
     * Prevents geese from overlapping each other on the map.
     * 
     * @param other some other goose
     */
    public void separate(Goose other) {
        if (!overlaps(other)) {
            return;
        }

        float pushX;
        float pushY;

        if (rect.x < other.rect.x) {
            pushX = -0.01f;
        } else {
            pushX = 0.01f;
        }

        if (rect.y < other.rect.y) {
            pushY = -0.01f;
        } else {
            pushY = 0.01f;
        }

        rect.x += pushX;
        rect.y += pushY;
        other.rect.x -= pushX;
        other.rect.y -= pushY;
    }

    public boolean isOverlapping() {
        return isOverlapping;
    }

    /**
     * Spawns the goose outside of the map's boundaries, which is determined by the viewport.
     * 
     * @param viewport the current screen's viewport
     * @param texture  the image representing the goose
     * @param player   the target the goose moves towards
     * @return the spawned goose
     */
    public static Goose spawn(Viewport viewport, Texture texture, Player player, GameTimer gameTimer) {
        float[] spawnPos = calcSpawn(viewport, texture);

        return new Goose(spawnPos[0], spawnPos[1], texture, player, gameTimer);
    }

    // Helper method for spawn that calculates the position of the goose spawning.
    protected static float[] calcSpawn(Viewport viewport, Texture texture) {
        int edge = MathUtils.random(0, 3);
        float camX = viewport.getCamera().position.x;
        float camY = viewport.getCamera().position.y;
        float x, y;

        switch (edge) {
            // Spawn above the camera-view
            case 0:
                x = camX + viewport.getWorldWidth() * (MathUtils.random(0f, 1f)-0.5f);
                y = camY + viewport.getWorldHeight()/2;
                break;

            // Spawn right of the camera-view
            case 1:
                x = camX + viewport.getWorldWidth()/2;
                y = camY + viewport.getWorldHeight() * (MathUtils.random(0f, 1f)-0.5f);
                break;

            // Spawn below the camera-view
            case 2:
                x = camX + viewport.getWorldWidth() * (MathUtils.random(0f, 1f)-0.5f);
                y = camY - viewport.getWorldHeight()/2 - texture.getHeight()*SCALE;
                break;

            // Spawn left of the camera-view
            default:
                x = camX - viewport.getWorldWidth()/2 - texture.getWidth()*SCALE;
                y = camY + viewport.getWorldHeight() * (MathUtils.random(0f, 1f)-0.5f);
        }
        
        return new float[] {x, y};
    }
}