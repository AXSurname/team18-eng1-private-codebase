package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

public class BreadInteraction extends Interaction {
    private static final float DURATION = 0.4f;
    private static final float SIZE = 1f;

    public BreadInteraction(Vector2 pos, Vector2 direction, Animation<Texture> animation) {
        super(pos, direction, DURATION, SIZE, animation);
    }
}
