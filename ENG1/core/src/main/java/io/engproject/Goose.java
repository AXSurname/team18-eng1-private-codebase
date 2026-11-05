package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Goose extends GameObject {
    private static final float SCALE = 1/32f;
    private static final float SPEED = 1.5f;
    
    private final Player player;

    public Goose(float x, float y, Texture texture, Player player) {
        super(x, y, texture.getWidth() * SCALE, texture.getHeight() * SCALE, texture);
        this.player = player;
    }

    @Override
    public void update(float dt) {
        Vector2 gooseCentre = new Vector2(rect.x + rect.width/2, rect.y + rect.height/2);
        Vector2 direction = player.rect.getCenter(TMP_VEC2).sub(gooseCentre).nor().scl(SPEED * dt);

        // Move towards player centre
        rect.setPosition(rect.getX() + direction.x, rect.getY() + direction.y);
    }

    public static Goose spawn(Viewport viewport, Texture texture, Player player) {
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

        return new Goose(x, y, texture, player);
    }
}
