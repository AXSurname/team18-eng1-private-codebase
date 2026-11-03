package io.engproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GUI extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private static final float GOOSE_SPAWN_INTERVAL = 1.5f;
    private static final boolean DRAW_DEBUG = false;

    // General
    private final Batch batch;
    private final ShapeRenderer shapeRenderer;
    private final Viewport gameViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT);

    // Assets
    private final Texture bgdTexture = new Texture(Gdx.files.internal("bkgnd.png"));
    private final Texture playerTexture = new Texture(Gdx.files.internal("player.png"));
    private final Texture gooseTexture = new Texture(Gdx.files.internal("goose.png"));

    // Player
    private final Player player = new Player(
        WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, // spawn location
        gameViewport, // boundary
        playerTexture // graphic
    );
    private final Vector2 inputMovement = new Vector2();

    // Geese
    private final Array<Goose> geese = new Array<>();
    private float gooseSpawnTimer;

    public GUI(GameManager game) {
        this.batch = game.getBatch();
        this.shapeRenderer = game.getShapeRenderer();

        bgdTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
    }

    @Override
    public void show() {
        resetGame();
    }

    private void resetGame() {
        player.reset(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);

        geese.clear();
        gooseSpawnTimer = 0f;
    }

    @Override
    public void render(float deltaTime) {
        processInput();
        updateLogic(deltaTime);
        checkCollisions();

        draw();
    }

    private void processInput() {
        inputMovement.setZero();
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            inputMovement.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            inputMovement.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            inputMovement.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            inputMovement.x += 1;
        }

        inputMovement.nor();
        player.changeDirection(inputMovement);
    }

    private void updateLogic(float deltaTime) {
        // Player (movement)
        player.update(deltaTime);

        // Goose spawning
        gooseSpawnTimer += deltaTime;
        if (gooseSpawnTimer >= GOOSE_SPAWN_INTERVAL) {
            gooseSpawnTimer = 0f;
            Goose newGoose = Goose.spawn(gameViewport, gooseTexture, player);
            geese.add(newGoose);
        }

        // Update geese
        for (Goose goose : geese) {
            goose.update(deltaTime);
        }
    }

    private void checkCollisions() {
        // Check geese vs player: each touching goose slows the player down
        int numHits = 0;
        for (Goose goose : geese) {
            if (player.overlaps(goose)) {
                ++numHits;
            }
        }

        player.setGeeseTouching(numHits);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        gameViewport.apply();

        // Draw textures
        batch.setProjectionMatrix(gameViewport.getCamera().combined);
        batch.begin();
        drawBackground();
        for (Goose goose : geese) {
            goose.draw(batch);
        }
        player.draw(batch);
        batch.end();

        drawDebug();
    }

    private void drawBackground() {
        // Calculate how many times the texture fits into the world dimensions
        float u2 = gameViewport.getWorldWidth() / WORLD_WIDTH;
        float v2 = gameViewport.getWorldHeight() / WORLD_HEIGHT;

        // Draw with custom UV coordinates to correctly repeat the texture
        batch.draw(bgdTexture,
            0, 0,
            gameViewport.getWorldWidth(),
            gameViewport.getWorldHeight(),
            0, 0,
            u2, v2
        );
    }

    private void drawDebug() {
        if (!DRAW_DEBUG) {
            return;
        }

        shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw player (green)
        player.drawDebug(shapeRenderer, Color.GREEN);

        // Draw geese (red)
        for (Goose goose : geese) {
            goose.drawDebug(shapeRenderer, Color.RED);
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        bgdTexture.dispose();
        playerTexture.dispose();
        gooseTexture.dispose();
    }
}
