package io.engproject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class GameManager extends Game {
    private Batch batch;
    private BitmapFont font;
    
    @Override
    public void create() {
        batch = new SpriteBatch();

        var fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/Matcha Style.ttf"));
        var fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 24;
        fontParams.color = Color.CORAL;
        font = fontGenerator.generateFont(fontParams);
        fontGenerator.dispose();

        setScreen(new TutorialScreen(this));
    }

    public Batch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
    }
}
