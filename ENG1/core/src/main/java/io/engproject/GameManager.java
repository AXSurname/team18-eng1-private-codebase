package io.engproject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * The 'startup' class. It is the first class instantiated (by the LwjglLauncher) which
 * acts as the entry point to all other class instantiation. Has no constructor as
 * Game.create() is used instead.
 */
public class GameManager extends Game {
    private static final String FONT_PATH = "font/Matcha Style.ttf";
    private static final Color FONT_COLOR = Color.SKY;
    private static final int FONT_SIZE = 40;

    private ShapeRenderer shapeRenderer;
    private Batch batch;
    private BitmapFont font;
    
    /**    (non-Javadoc)
     * Creates the batch, font, and sets the TutorialScreen.
     * @see com.badlogic.gdx.ApplicationListener#create()
     */
    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        createFont();

        setScreen(new TutorialScreen(this));
    }

    /** Instantiates {@link GameManager#font} */
    private void createFont() {
        var fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        var fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParams.size = FONT_SIZE;
        fontParams.color = FONT_COLOR;

        this.font = fontGenerator.generateFont(fontParams);
        fontGenerator.dispose();
    }
    
    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public Batch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    // Does not dispose TutorialScreen (as it disposes itself).
    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
