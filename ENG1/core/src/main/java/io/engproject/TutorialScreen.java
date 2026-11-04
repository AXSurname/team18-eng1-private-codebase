package io.engproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The first screen of the game that explains game objectives and controls. It is disposed
 * on any keyboard input, and then calls GameManager to set screen to the playable game.
 */
public class TutorialScreen extends ScreenAdapter {
    private static final String TUTORIAL_TEXT = "Avoid the geese and feed them bread for them to leave you!\n"
                                                + "Move: WASD\nThrow bread: left-click\n"
                                                + "Press any key to begin!";

    private final GameManager gm;
    
    private final Batch batch;
    private final BitmapFont font;
    private final Viewport viewport = new ScreenViewport();
    private final GlyphLayout textLayout = new GlyphLayout();

    /**
     * Creates a TutorialScreen.
     * 
     * @param gm the 'setup' GameManager class used to instantiate batch and font
     */
    public TutorialScreen(GameManager gm) {
        this.gm = gm;
        this.batch = gm.getBatch();
        this.font = gm.getFont();
    }

    // Updates the viewport's window size if configuration.setResizable() is changed to true.
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**    (non-Javadoc)
     * Detects user input for starting the game, and displays the game's instructions and controls.
     * @see com.badlogic.gdx.ScreenAdapter#render(float)
     */
    @Override
	public void render(float dt) {
        // Start game if user presses key
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            gm.setScreen(new GameScreen(gm));
            dispose();
            return;
        }

        // Display text
        float textX;
        float textY = viewport.getWorldHeight()/2 * 1.3f;

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        for (String line : TUTORIAL_TEXT.split("\n")) {
            textLayout.setText(font, line);
            textX = viewport.getWorldWidth()/2 - textLayout.width/2;
            font.draw(batch, textLayout, textX, textY);
            textY -= textLayout.height * 1.3f;
        }

        batch.end();
	}
}
