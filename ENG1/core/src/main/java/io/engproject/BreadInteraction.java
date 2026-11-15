package io.engproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

/**
 * A type of {@link Interaction} the player can trigger to affect the goose, and is
 * animated so the player appears to be feeding the goose bread.
 * 
 * The {@link #DURATION} is set as {@value #DURATION} in accordance with the length
 * of the bread throwing animation (i.e. the number of bread images converted into frames).
 */
public class BreadInteraction extends Interaction {
    private static final float DURATION = 0.4f;
    private static final float SIZE = 1.5f;

    public BreadInteraction(Vector2 pos, Vector2 direction, Animation<Texture> animation) {
        super(pos, direction, DURATION, SIZE, animation);
    }
}
