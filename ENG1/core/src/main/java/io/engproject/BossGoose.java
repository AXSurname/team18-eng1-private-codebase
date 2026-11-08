package io.engproject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;


public class BossGoose extends Goose {
    private static final float SCALE = 1/16f;
    private static final float SPEED = 0.5f;

    public BossGoose(float x, float y, Texture texture, Player player) {
        super(x, y, texture, player);
        this.rect.setSize(texture.getWidth() * SCALE, texture.getHeight() * SCALE);
    }

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

    public static BossGoose spawn(Viewport viewport, Texture texture, Player player) {
        float[] spawnPos = calcSpawn(viewport, texture);

        return new BossGoose(spawnPos[0], spawnPos[1], texture, player);
    }
}
