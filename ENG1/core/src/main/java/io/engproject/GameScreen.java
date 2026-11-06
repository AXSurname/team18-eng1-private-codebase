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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The game's GUI, and also instantiates the Player, an array of Goose, and all renderables,
 * which it displays. Its game state is updated via its calls to a LogicManager.
 */
public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private static final boolean DRAW_DEBUG = false; // Manually set for debugging

    // Asset filenames
    private static final String BKGND_FILENAME = "bkgnd.png";
    private static final String PLAYER_FILENAME = "player.png";
    private static final String GOOSE_FILENAME = "goose.png";
    private static final String INTERACT_PART_FILENAME = "bread_";

    // General
    private final ShapeRenderer shapeRenderer;
    private final Batch batch;
    private final BitmapFont font;
    private final GlyphLayout gameoverLayout = new GlyphLayout();

    private final Viewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
    private final Viewport uiViewport = new ScreenViewport();

    // Assets
    private final Texture bkgndTexture = new Texture(Gdx.files.internal(BKGND_FILENAME));
    private final Texture playerTexture = new Texture(Gdx.files.internal(PLAYER_FILENAME));
    private final Texture gooseTexture = new Texture(Gdx.files.internal(GOOSE_FILENAME));

    private final Array<Texture> interactTextures = loadInteractTextures();
    private final Animation<Texture> interactAnimation = new Animation<>(1/12f, interactTextures);

    // Player and geese
    private final Player player = new Player(
        WORLD_WIDTH/2f, WORLD_HEIGHT/2f,    // Spawn position
        viewport,                           // Movement boundries
        playerTexture,                      // Graphics
        interactAnimation);
    private final Vector2 inputMovement = new Vector2();

    private final Array<Goose> geese = new Array<>();

    // Manager classes
    private final GameManager gm;
    private final LogicManager lm;

    /**
     * Creates a GameScreen and instantiates its GameManager, LogicManager, batch and font.
     * 
     * @param gm the 'setup' GameManager class used to instantiate batch and font
     */
    public GameScreen(GameManager gm) {
        this.gm = gm;
        this.shapeRenderer = gm.getShapeRenderer();
        this.batch = gm.getBatch();
        this.font = gm.getFont();
        this.lm = new LogicManager(this);

        bkgndTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    }

    /**
     * Creates the textures required for GameScreen's interactTextures field (to be used
     * in the field interactAnimation that is passed into a Player).
     * 
     * @return the textures used for the player's interactions
     */
    private Array<Texture> loadInteractTextures() {
        var textures = new Array<Texture>();

        for (int i=0; i<4; i++) {
            textures.add(new Texture(Gdx.files.internal(String.format(INTERACT_PART_FILENAME
                                                                      + "%01d.png", i))));
        }

        return textures;
    }

    protected Player getPlayer() {
        return player;
    }

    protected Array<Goose> getGeese() {
        return geese;
    }

    /**
     * Spawns a Goose outside the map and returns it.
     * @return a goose that now exists in the game world.
     */
    protected Goose getGoose() {
        return Goose.spawn(viewport, gooseTexture, player);
    }

    // Updates both viewports' window size if configuration.setResizable() is changed to true.
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    // Resets the game when the screen is set as the current screen.
    @Override
    public void show() {
        lm.resetGame();
    }

    /**    (non-Javadoc)
     * Allows renderables to be drawn and calls LogicManager to update logic and game state.
     * @see com.badlogic.gdx.ScreenAdapter#render(float)
     */
    @Override
    public void render(float dt) {
        // Calls to input and logic
        if (player.isAlive()) {
            getInput();
            lm.update(dt);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            lm.resetGame();
        }
        
        // Then render GUI
        draw();
    }

    /**
     * Detects and processes the player's input on keyboard and mouse. Note it is the
     * only exempt method in GameScreen to interact directly with Player's logic
     * directly to pass in input data.
     */
    private void getInput() {
        // Pass in movement input to Player
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

        // Pass in interaction input to Player
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            player.setRecievedInput(true);
        } else {
            player.setRecievedInput(false);
        }
    }

    /**
     * Draws all renderables such as background, geese, player, items, interactions,
     * and inventory.
     */
    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        // Render textures
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawBackground();

        for (Goose goose : geese) {
            goose.draw(batch);
        }

        player.draw(batch);

        drawItems();

        for (Interaction interaction : player.getInteractions()) {
            interaction.draw(batch);
        }

        drawInventory();

        batch.end();

        // Render UI
        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();

        // Draw text that displays player life left
        String lifeText = "Life left: " + String.format("%.1f", player.getLife());
        font.draw(batch, lifeText, 40f, uiViewport.getWorldHeight() - 40);

        // Display game over text if player is dead
        if (!player.isAlive()) {
            String gameoverText = "GAME OVER!\nPress R to Restart";
            float gameoverX;
            float gameoverY = uiViewport.getWorldHeight()/2 + gameoverLayout.height;

            for (String line : gameoverText.split("\n")) {
                gameoverLayout.setText(font, line);
                gameoverX = uiViewport.getWorldWidth()/2 - gameoverLayout.width/2;
                font.draw(batch, gameoverLayout, gameoverX, gameoverY);
                gameoverY -= gameoverLayout.height * 1.3f;
            }
        }

        batch.end();

        // Debugging
        drawDebug();
    }

    private void drawBackground() {
        // Calculate how many times the texture fits into the world dimensions
        float u2 = viewport.getWorldWidth() / WORLD_WIDTH;
        float v2 = viewport.getWorldHeight() / WORLD_HEIGHT;

        // Draw with custom (u, v) coordinates to correctly repeat the texture
        batch.draw(bkgndTexture,
            0, 0,
            viewport.getWorldWidth(), viewport.getWorldHeight(),
            0, 0,
            u2, v2
        );
    }

    // To be implemented as needed
    private void drawInventory() {

    }
    
    // To be implemented as needed
    private void drawItems() {

    }

    // A debugging method to draw objects without their textures.
    private void drawDebug() {
        if (!DRAW_DEBUG) {
            return;
        }

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw player (blue)
        player.drawDebug(shapeRenderer, Color.BLUE);

        // Draw geese (red)
        for (Goose goose : geese) {
            goose.drawDebug(shapeRenderer, Color.RED);
        }
        shapeRenderer.end();
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