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

public class TutorialScreen extends ScreenAdapter {
    private final GameManager gm;
    private final Batch batch;
    private final BitmapFont font;
    private final Viewport viewport = new ScreenViewport();
    private final GlyphLayout textLayout = new GlyphLayout();

    public TutorialScreen(GameManager gm) {
        this.gm = gm;
        this.batch = gm.getBatch();
        this.font = gm.getFont();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
	public void render (float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gm.setScreen(new MainScreen(gm));
            dispose();
            return;
        }

        float textX = viewport.getWorldWidth()/2 - textLayout.width/2;
        float textY = (viewport.getWorldHeight()/2) * 1.3f;

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Renderables
        textLayout.setText(font, "Move: WASD");
        font.draw(batch, textLayout, textX, textY);

        batch.end();
	}
}
