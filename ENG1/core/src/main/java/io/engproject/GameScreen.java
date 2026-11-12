package io.engproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
    private static final String GOOSE_FILENAME = "goose.png";
    private static final String PUDDLE_FILENAME = "puddle.png";
    private static final String INTERACT_PART_FILENAME = "bread_";
    private static final String BREAD_ITEM_FILENAME = "backpack_bread.png";
    private static final String ENERGY_DRINK_FILENAME = "energydrink.png";
    private static final String MEGAPHONE_FILENAME = "megaphone.png";

    // Backpack position in world units (bottom-left corner of the map)
    private static final float BACKPACK_X = 0f;
    private static final float BACKPACK_Y = 0.2f;
    // General
    private final ShapeRenderer shapeRenderer;
    private final Batch batch;
    private final BitmapFont font;
    private final GlyphLayout gameoverLayout = new GlyphLayout();

    private final Viewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
    private final Viewport uiViewport = new ScreenViewport();

    // Assets
    private final Texture bkgndTexture = loadTextureFromPath(BKGND_FILENAME);
    private final Texture playerTexture = createPlayerTexture();
    private final Texture gooseTexture = loadTextureFromPath(GOOSE_FILENAME);
    private final Texture puddleTexture = loadTextureFromPath(PUDDLE_FILENAME);
    private final Texture breadItemTexture = loadTextureFromPath(BREAD_ITEM_FILENAME);
    private final Texture energyDrinkTexture = loadTextureFromPath(ENERGY_DRINK_FILENAME);
    private final Texture megaphoneTexture = loadTextureFromPath(MEGAPHONE_FILENAME);
    private final Texture slotTexture = createSlotTexture();
    
    private final Array<Texture> interactTextures = loadInteractTextures();
    private final Animation<Texture> interactAnimation = new Animation<>(1/12f, interactTextures);

    // Inventory (no bag icon, so the slots start at BACKPACK_X)
    private final Backpack backpack = new Backpack(BACKPACK_X, BACKPACK_Y, 0f, 1f, null, slotTexture);

    // Player and geese
    private final Player player = new Player(
        WORLD_WIDTH/2f, WORLD_HEIGHT/2f,    // Spawn position
        viewport,                           // Movement boundries
        playerTexture,                      // Graphics
        interactAnimation);
    private final Vector2 inputMovement = new Vector2();

    private final Array<Goose> geese = new Array<>();
    private final Array<Puddle> puddles = new Array<>();
    private final Array<ItemPickup> itemPickups = new Array<>();

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

        createPuddles();
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
    
    private Texture loadTextureFromPath(String filepath) {
        return new Texture(Gdx.files.internal(filepath));
    }

    /**
     * Creates the texture drawn behind each backpack slot: a translucent dark square
     * with a light border. Made in code so no extra asset file is needed.
     *
     * @return the slot texture
     */
    private Texture createSlotTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.5f);
        pixmap.fill();
        pixmap.setColor(Color.WHITE);
        pixmap.drawRectangle(0, 0, 32, 32);
        pixmap.drawRectangle(1, 1, 30, 30);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    /**Creates the player sprite, This is going to be a circle with 
     * a triangle pointer on its right side.
     * The pointer faces right (0 degrees), and Player rotates to face the
     * direction the player is moving in. Made in the code, so no extra asset
     * files are needed,
     * 
     * @return the player texture
     */
    private Texture createPlayerTexture() {
        Pixmap pixmap = new Pixmap(32,32, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);

        // Transparent bg
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        //Body - a white circle in the centre (white so high contrast works)
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fillCircle(16, 16, 10);

        //Pointer - a dark grey triangle on the right, pointing right
        pixmap.setColor(0.2f, 0.2f, 0.2f, 1f);
        pixmap.fillTriangle(22, 9, 22, 23, 31, 16);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    protected Backpack getBackpack() {
        return backpack;
    }

    private void createPuddles() {
        puddles.add(new Puddle(3f, 2f, 2f, 1f, puddleTexture));
        puddles.add(new Puddle(10f, 6f, 2f, 1f, puddleTexture));
    }

    protected BreadItem getBread() {
        return new BreadItem(player, backpack, breadItemTexture);
    }

    /**
     * Creates a new energy drink for the player.
     * @return an energy drink that uses this screen's icon texture
     */
    protected EnergyDrinkItem getEnergyDrink() {
        return new EnergyDrinkItem(player, energyDrinkTexture);
    }

    /**
     * Creates a new megaphone for the player.
     * @return a megaphone that uses this screen's icon texture
     */
    protected MegaphoneItem getMegaphone() {
        return new MegaphoneItem(player, megaphoneTexture);
    }

    /**
     * Creates a new item pickup for the game.
     * @param x the x-coordinate of the item pickup
     * @param y the y-coordinate of the item pickup
     * @param item the item to be picked up
     * @return an item pickup that now exists in the game world
     */
    protected ItemPickup getPickup(float x, float y, Item item) {
        return new ItemPickup(x, y, item);
    }

    protected Player getPlayer() {
        return player;
    }

    protected Array<Goose> getGeese() {
        return geese;
    }

    protected Array<Puddle> getPuddles() {
        return puddles;
    }

    protected Array<ItemPickup> getItemPickups() {
        return itemPickups;
    }

    /**
     * Spawns a Goose outside the map and returns it.
     * @return a goose that now exists in the game world.
     */
    protected Goose getGoose() {
        return Goose.spawn(viewport, gooseTexture, player);
    }
    
    /**
     * Spawns a BossGoose outside the map and returns it.
     * @return a boss goose that now exists in the game world.
     */
    protected BossGoose getBossGoose() {
        return BossGoose.spawn(viewport, gooseTexture, player);
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
        // Enables high contrast mode with H
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            AccessibilitySettings.toggleHighContrast();
        }

        // Calls to input and logic, unless paused (the game starts paused) or over
        GameTimer gameTimer = lm.getGameTimer();
        if (player.isAlive() && !gameTimer.isFinished()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                gameTimer.togglePause();
            }
            if (!gameTimer.isPaused()) {
                getInput();
                lm.update(dt);
            }
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
        
        // Backpack: keys 1-5 select a slot, left-click or E uses the selected item
        backpack.update(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
            || Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            backpack.useSelectedItem();
        }
    }

    /**
     * Draws all renderables such as background, geese, player, items, interactions,
     * and inventory.
     */
    private void draw() {
        boolean highContrast = AccessibilitySettings.isHighContrast();

        ScreenUtils.clear(Color.BLACK);

        // Render textures
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        if (highContrast) {
            batch.setColor(new Color(0.25f, 0.25f, 0.25f, 1f));
        } else {
            batch.setColor(Color.WHITE);
        }
           
        drawBackground();
        batch.setColor(Color.WHITE);

        for (Puddle puddle : puddles) {
            if (puddle.getIsVisible()) {
                puddle.draw(batch);
            }
        }

        for (Goose goose : geese) {
            batch.setColor(highContrast ? Color.PINK : Color.WHITE);
            goose.draw(batch);
        }

        batch.setColor(highContrast ? Color.YELLOW : Color.WHITE);
        player.draw(batch);

        batch.setColor(Color.WHITE);
        drawItems();

        for (Interaction interaction : player.getInteractions()) {
            batch.setColor(highContrast ? Color.CYAN : Color.WHITE);
            interaction.draw(batch);
        }

        batch.setColor(Color.WHITE);
        drawInventory();

        batch.end();

        // Render UI
        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();

        // Draw text that displays player life left
        String lifeText = "Life left: " + String.format("%.1f", player.getLife());
        font.draw(batch, lifeText, 40f, uiViewport.getWorldHeight() - 40);

        // Draw text that displays number of items used.
        String itemsText = "Items used: " + player.getItemsUsed() + (player.isBoosted() ? "   BOOST!" : "") + (player.megaphoneActive() ? "   MEGAPHONE!" : "");
        font.draw(batch, itemsText, 40f, uiViewport.getWorldHeight() - 80);

        // Draw text that displays number of events triggered.
        String eventsText = "Events triggered: " + player.getEventsTriggered();
        font.draw(batch, eventsText, 40f, uiViewport.getWorldHeight() - 120);
        
        // Event message, centred below the HUD, for a few seconds after an event triggers
        String eventMessage = lm.getEventMessage();
        if (eventMessage != null) {
            gameoverLayout.setText(font, eventMessage);
            font.draw(batch, gameoverLayout,
                      uiViewport.getWorldWidth()/2 - gameoverLayout.width/2,
                      uiViewport.getWorldHeight() - 240);
        }


        // Draw the time left (GameTimer)
        GameTimer gameTimer = lm.getGameTimer();
        String timeText = "Time left: " + gameTimer.getFormattedTime();
        font.draw(batch, timeText, 40f, uiViewport.getWorldHeight() - 160);

        // Centre message: game over, survived the 5 minutes, or paused
        String centreText = null;
        if (!player.isAlive()) {
            centreText = "GAME OVER!\nPress R to Restart";
        } else if (gameTimer.isFinished()) {
            centreText = "YOU SURVIVED!\nPress R to Restart";
        } else if (gameTimer.isPaused()) {
            centreText = "PAUSED\nPress ESC to play";
        }

        if (centreText != null) {
            String gameoverText = centreText;
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
        float v = viewport.getWorldHeight() / WORLD_HEIGHT;
        float u2 = viewport.getWorldWidth() / WORLD_WIDTH;

        // Draw with custom (u, v) coordinates to correctly repeat the texture
        batch.draw(bkgndTexture,
            0, 0,
            viewport.getWorldWidth(), viewport.getWorldHeight(),
            0, v,
            u2, 0
        );
    }

    private void drawInventory() {
        backpack.draw(batch);
    }
    
    
    //Draws all item pickups in the game.
    private void drawItems() {
        for (ItemPickup itemPickup : itemPickups) {
            itemPickup.draw(batch);
        }
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

    //Returns the world width in world units.
    public float getWorldWidth() {
        return viewport.getWorldWidth();
    }

    //Returns the world height in world units.
    public float getWorldHeight() {
        return viewport.getWorldHeight();
    }

    @Override
    public void dispose() {
        // Dispose textures
        bkgndTexture.dispose();
        playerTexture.dispose();
        gooseTexture.dispose();
        puddleTexture.dispose();
        breadItemTexture.dispose();
        energyDrinkTexture.dispose();
        megaphoneTexture.dispose();
        interactTextures.forEach(Texture::dispose);
        slotTexture.dispose();
        
        // Dispose other
        batch.dispose();
        font.dispose();
    }

}
