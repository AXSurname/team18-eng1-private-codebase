package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/** An enemy for the player to evade. */
public class Goose extends GameObject {
    private static final float SCALE = 1/32f;
    private static final float SPEED = 0.7f; // makes it so geese are esasier to outrun
    
    private final Player player;
    private boolean friendly;

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
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Makes this goose friendly and changes its colour. A friendly goose
     * will move away from the player instead of attacking them.
     *
     * @param Texture the Texture representing a friendly goose
     */
    public void makeFriendly(Texture Texture) {
        texture = Texture;
        friendly = true;
    }

    public boolean isFriendly() {
        return friendly;
    }

    /**
     * Updates the goose's movement.
     * 
     * @param dt deltatime
     */
    @Override
    public void update(float dt) {
        Vector2 gooseCentre = new Vector2(rect.x + rect.width/2, rect.y + rect.height/2);
        Vector2 direction = player.rect.getCenter(TMP_VEC2).sub(gooseCentre).nor().scl(SPEED * dt);


        if (friendly || player.megaphoneActive()) {
            // Move away from player centre
            direction.scl(-1);
        }
        // Move towards player centre
        rect.setPosition(rect.getX() + direction.x, rect.getY() + direction.y);
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
        edge = 2;
        float edgeX = viewport.getCamera().position.x;
        float edgeY = viewport.getCamera().position.y;
        float x, y;

        switch (edge) {
            // Spawn above the camera-view
            case 0:
                x = MathUtils.random(0f, 1f) * (viewport.getWorldWidth() + edgeX);
                y = viewport.getWorldHeight() + edgeY;
                break;

            // Spawn right of the camera-view
            case 1:
                x = viewport.getWorldWidth() + edgeX;
                y = MathUtils.random(0f, 1f) * (viewport.getWorldHeight() + edgeY);
                break;

            // Spawn below the camera-view
            case 2:
                x = MathUtils.random(0f, 1f) * (viewport.getWorldWidth() + edgeX);
                y = -texture.getHeight() * SCALE + (edge*viewport.getCamera().combined.getScaleY());
                break;

            // Spawn left of the camera-view
            default:
                x = (-texture.getWidth() * SCALE) + edgeX;
                y = MathUtils.random(0f, 1f) * (viewport.getWorldHeight() + edgeY);
        }
        
        return new float[]{x, y};
    }
}
