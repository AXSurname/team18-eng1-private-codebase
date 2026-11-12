package io.engproject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A Boss Goose that cases the player.
 *
 * A Boss Goose is a {@link Goose} that follows the player's center at a 
 * constant speed.
 * When megaphone is used, the Boss Goose moves away from the player.
 *
 * The size of the Boss Goose is scaled with {@value #SCALE}, movement
 * speed is defined by {@value #SPEED}.
 *
 *Textures are owned by the screen that created them, not by this class.
 */

public class BossGoose extends Goose {
    private static final float SCALE = 1/16f;
    private static final float SPEED = 0.5f;

    /**
     * Created a Boss Goos at the given position.
     * 
     * @param x           the initial x-coordinates
     * @param y           the initial y-coordinates
     * @param texture     texture of the Boss Goose
     * @param player      the player
     */
    public BossGoose(float x, float y, Texture texture, Player player) {
        super(x, y, texture, player);
        this.rect.setSize(texture.getWidth() * SCALE, texture.getHeight() * SCALE);
    }

    /**
     * Updates Boss Goose's position based on player's position.
     * 
     * Boss Goose moves towards the player unless the megaphone is active.
     * If megaphone is active, Boss Goose moves away fromt the player.
     *
     * @param dt         time since the last update
     */
    @Override
    public void update(float dt) {
        Vector2 gooseCentre = new Vector2(rect.x + rect.width/2, rect.y + rect.height/2);
        Vector2 direction = this.getPlayer().rect.getCenter(TMP_VEC2).sub(gooseCentre).nor().scl(SPEED * dt);
        if (this.getPlayer().megaphoneActive()) {
            // Move away from player centre
            direction.scl(-1);
        }

        rect.setPosition(rect.getX() + direction.x, rect.getY() + direction.y);
    }

 /**
  * Spawns a boss goose at a position calculated relative to the viewport. 
  *
  * @param viewport     the viewport used to calculate the spawn position 
  * @param texture      the texture used by the Boss Goose 
  * @param player       the player that the Boss Goose follows 
  * @return             a new BossGoose instance 
  */
    public static BossGoose spawn(Viewport viewport, Texture texture, Player player) {
        float[] spawnPos = calcSpawn(viewport, texture);

        return new BossGoose(spawnPos[0], spawnPos[1], texture, player);
    }
}
