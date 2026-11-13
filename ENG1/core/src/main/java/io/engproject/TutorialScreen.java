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
    // Package-private so GameScreen can show the controls on the pause screen
    static final String CONTROLS_TEXT = "Move using the arrow keys\n"
                                                + "Select item in inventory: 1, 2, 3, 4, 5\n"
                                                + "Use selected item in inventory: E\n"
                                                + "The first item is bread, it distracts the geese when fed to them and causes them to leave you!\n"
                                                + "The other item can be either be a red bull which when used temporarily increases your speed.\n"
                                                + "Or it can be a megaphone which temporarily scares the geese away when used.\n"
                                                + "The items besides bread spawn randomly and are one use items.\n"
                                                + "Toggle high-contrast visuals: H\n"
                                                + "Pause/unpause: Esc";

    private static final String INTRO_TEXT = "Avoid the geese and feed them bread for them to leave you!\n"
                                             + "The objective is to survive for 5 minutes to win.";

    private static final String CONTINUE_TEXT = "Press any key to continue";

    // Text size of the controls lines, relative to the normal font size (1 = same size)
    private static final float CONTROLS_SCALE = 0.6f;
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

        String[] introLines = INTRO_TEXT.split("\n");
        String[] controlsLines = CONTROLS_TEXT.split("\n");
        String[] continueLines = CONTINUE_TEXT.split("\n");

        // Centre the whole block of text vertically (2 blank lines separate the 3 blocks)
        font.getData().setScale(1f);
        float lineHeight = font.getLineHeight();
        float totalHeight = (introLines.length + 2 + continueLines.length) * lineHeight
                            + controlsLines.length * lineHeight * CONTROLS_SCALE;
        float textY = viewport.getWorldHeight()/2 + totalHeight/2;

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        textY = drawLines(introLines, textY, 1f);
        textY -= lineHeight;
        textY = drawLines(controlsLines, textY, CONTROLS_SCALE);
        textY -= lineHeight;
        drawLines(continueLines, textY, 1f);

        batch.end();

        // Reset scale since the font is shared with other screens
        font.getData().setScale(1f);
	}

    /**
     * Draws each line horizontally centred at the given scale, starting at textY.
     *
     * @return the y position below the last line drawn
     */
    private float drawLines(String[] lines, float textY, float scale) {
        font.getData().setScale(scale);
        for (String line : lines) {
            textLayout.setText(font, line);
            float textX = viewport.getWorldWidth()/2 - textLayout.width/2;
            font.draw(batch, textLayout, textX, textY);
            textY -= font.getLineHeight();
        }
        return textY;
    }
}
