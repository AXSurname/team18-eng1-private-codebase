package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/** An enemy for the player to evade. */
public class Goose extends GameObject {
    private static final float SCALE = 1/32f;
    private static final float SPEED = 0.7f; // makes it so geese are esasier to outrun
    private static final float FEEDING_DURATION = 3f;
    
    private final Player player;
    private final Texture angryGooseTexture;
    private boolean friendly;
    private float feedingTimer;
    private boolean isOverlapping = false;

    /**
     * Creates a Goose.
     * 
     * @param x       the initial x-position of the goose
     * @param y       the initial y-position of the goose
     * @param texture the image representing the goose which is displayed
     * @param player  the target the goose moves towards
     */
    public Goose(float x, float y, Texture texture, Player player) {
        super(x, y, texture.getWidth() * SCALE, texture.getHeight() * SCALE, texture);
        this.player = player;
        this.angryGooseTexture = texture;
    }

    public Player getPlayer() {
        return player;
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

    public void separate(Goose other) {
    if (!overlaps(other)) {
        return;
    }

    float pushX;
    float pushY;

    if (rect.x < other.rect.x) {
        pushX = -0.01f;
    }   
    else {
    pushX = 0.01f;
    }

    if (rect.y < other.rect.y) {
        pushY = -0.01f;
    }
    else {
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
        Vector2 direction = player.rect.getCenter(TMP_VEC2).sub(gooseCentre).nor().scl(SPEED * dt);


        if (player.megaphoneActive()) {
            // Move away from player centre.
            direction.scl(-1);
        }
        rect.setPosition(rect.getX() + direction.x, rect.getY() + direction.y);
    }

    /**
     * Updates feeding and returns if a goose should move this frame.
     *
     * @param dt deltatime
     * @return false while the goose is feeding
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
     * Spawns the goose outside of the map's boundaries, which is determined by the viewport.
     * 
     * @param viewport the current screen's viewport
     * @param texture  the image representing the goose
     * @param player   the target the goose moves towards
     * @return the spawned goose
     */
    public static Goose spawn(Viewport viewport, Texture texture, Player player) {
        float[] spawnPos = calcSpawn(viewport, texture);

        return new Goose(spawnPos[0], spawnPos[1], texture, player);
    }

    // Helper method for spawn that calculates the position of the goose spawning.
    protected static float[] calcSpawn(Viewport viewport, Texture texture) {
        int edge = MathUtils.random(0, 3);
        float x, y;

        switch (edge) {
            // Spawn at top of map
            case 0:
                x = MathUtils.random(0f, 1f) * viewport.getWorldWidth();
                y = viewport.getWorldHeight();
                break;

            // Spawn at right of map
            case 1:
                x = viewport.getWorldWidth();
                y = MathUtils.random(0f, 1f) * viewport.getWorldHeight();
                break;

            // Spawn at bottom of map
            case 2:
                x = MathUtils.random(0f, 1f) * viewport.getWorldWidth();
                y = -texture.getHeight() * SCALE;
                break;

            // Spawn at left of map
            default:
                x = -texture.getWidth() * SCALE;
                y = MathUtils.random(0f, 1f) * viewport.getWorldHeight();
        }
        
        return new float[]{x, y};
    }

    
}
