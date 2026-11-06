package io.engproject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract class for game objects that require physics (such as collision detection and
 * movement) and rendering.
 */
public abstract class GameObject {
    protected static final Vector2 TMP_VEC2 = new Vector2();
    
    protected final Rectangle rect;
    protected Texture texture;

    /**
     * Creates a GameObject.
     * 
     * @param x       the x-position the object spawns at
     * @param y       the y-position the object spawns at
     * @param w       the width of the object
     * @param h       the height of the object
     * @param texture the image representing the object
     */
    public GameObject(float x, float y, float w, float h, Texture texture) {
        this.rect = new Rectangle(x, y, w, h);
        this.texture = texture;
    }

    /**
     * Creates a GameObject with no image to render it.
     * 
     * @param x       the x-position the object spawns at
     * @param y       the y-position the object spawns at
     * @param w       the width of the object
     * @param h       the height of the object
     */
    public GameObject(float x, float y, float w, float h) {
        this(x, y, w, h, null);
    }
    
    public Vector2 getCentre(Vector2 objVector2) {
        return this.rect.getCenter(objVector2);
    }
    
    // Returns whether another GameObject's Rectangle overlaps this obejct's one.
    public boolean overlaps(GameObject other) {
        return rect.overlaps(other.rect);
    }

    /**
     * Draws the object onto the screen if it has been assigned a Texture.
     * @param batch the current screen's batch
     */
    public void draw(Batch batch) {
        if (texture == null) {
            return;
        }

        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Debugging method to display where the object is on the screen.
     * @param shapeRenderer the current screen's ShapeRenderer
     * @param color the color to display the object as
     */
    public void drawDebug(ShapeRenderer shapeRenderer, Color color) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    abstract void update(float dt);
}
