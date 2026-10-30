package io.engproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private static final float GOOSE_SPAWN_INTERVAL = 2f;
    private static final float DAMAGE_PER_SEC = 1f;

    private final GameManager gm;

    private final Batch batch;
    private final BitmapFont font;
    private final GlyphLayout gameoverLayout = new GlyphLayout();
    private final Texture bkgndTexture = new Texture(Gdx.files.internal("bkgnd.png"));
    private final Texture playerTexture = new Texture(Gdx.files.internal("player.png"));
    private final Texture gooseTexture = new Texture(Gdx.files.internal("goose.png"));
    private final Array<Texture> interactTextures = loadInteractTextures();
    private final Animation<Texture> interactAnimation = new Animation<>(1/12f, interactTextures);

    private final Viewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
    private final Viewport uiViewport = new ScreenViewport();
    private final Vector2 inputMovement = new Vector2();

    private final Player player = new Player(WORLD_WIDTH/2f, WORLD_HEIGHT/2f,
                                             viewport,
                                             playerTexture,
                                             interactAnimation);
    private final Array<Goose> geese = new Array<>();

    private float timer;
    private float gooseSpawnTimer;

    public MainScreen(GameManager gm) {
        this.gm = gm;
        this.batch = gm.getBatch();
        this.font = gm.getFont();

        bkgndTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    }

    private Array<Texture> loadInteractTextures() {
        var textures = new Array<Texture>();

        for (int i=0; i<4; i++) {
            textures.add(new Texture(Gdx.files.internal(String.format("bread_%01d.png", i))));
        }

        return textures;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void show() {
        resetGame();
    }

    private void resetGame() {
        player.reset(WORLD_WIDTH/2, WORLD_HEIGHT/2);
        geese.clear();

        timer = 0f;
        gooseSpawnTimer = 0f;
    }

    private void processInput() {
        inputMovement.setZero();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            inputMovement.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            inputMovement.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            inputMovement.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            inputMovement.x += 1;
        }

        inputMovement.nor();
        player.changeDirection(inputMovement);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            player.setRecievedInput(true);
        } else {
            player.setRecievedInput(false);
        }
    }

    @Override
	public void render(float dt) {
        if (player.isAlive()) {
            processInput();
            updateLogic(dt);
            checkCollision(dt);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
        }
        
        draw();
	}

    private void checkCollision(float dt) {
        for (Interaction interaction : player.getInteractions()) {
            var iterator = geese.iterator();

            while (iterator.hasNext()) {
                Goose next = iterator.next();

                if (interaction.overlaps(next)) {
                    iterator.remove();
                }
            }
        }

        int hitNum = 0;

        for (Goose goose : geese) {
            if (player.overlaps(goose)) {
                ++hitNum;
            }
        }

        if (hitNum > 0) {
            player.subLife(DAMAGE_PER_SEC * hitNum * dt);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.CHARTREUSE);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawBackground();

        for (Goose goose : geese) {
            goose.draw(batch);
        }

        player.draw(batch);

        for (Interaction interaction : player.getInteractions()) {
            interaction.draw(batch);
        }

        batch.end();

        // Render UI
        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();
        font.draw(batch, "Life: " + String.format("%.1f", player.getLife()), 40f, uiViewport.getWorldHeight() - 40);

        if (!player.isAlive()) {
            gameoverLayout.setText(font, "GAME OVER\nPress R to Restart");
            float gameoverX = uiViewport.getWorldWidth()/2 - gameoverLayout.width/2;
            float gameoverY = uiViewport.getWorldHeight()/2 - gameoverLayout.height/2;
            font.draw(batch, gameoverLayout, gameoverX, gameoverY);
        }

        batch.end();
    }

    private void updateLogic(float dt) {
        player.update(dt);

        timer += dt;
        gooseSpawnTimer += dt;

        if (gooseSpawnTimer > GOOSE_SPAWN_INTERVAL) {
            gooseSpawnTimer = 0f;
            Goose goose = Goose.spawn(viewport, gooseTexture, player);
            geese.add(goose);
        }

        for (Goose goose : geese) {
            goose.update(dt);
        }
    }

    private void drawBackground() {
        float u2 = viewport.getWorldWidth() / WORLD_WIDTH;
        float v2 = viewport.getWorldHeight() / WORLD_HEIGHT;
        //batch.draw(bkgndTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.draw(bkgndTexture,
            0, 0,
            viewport.getWorldWidth(), viewport.getWorldHeight(),
            0, 0,
            u2, v2
        );
    }

    @Override
    public void dispose() {
        bkgndTexture.dispose();
        playerTexture.dispose();
        gooseTexture.dispose();
        batch.dispose();
        font.dispose();
        interactTextures.forEach(Texture::dispose);
    }
}
