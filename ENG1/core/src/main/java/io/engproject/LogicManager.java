package io.engproject;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handles most non-graphical logic, and manages interactions between multiple
 * GmaeObject classes.
 */
public class LogicManager {
    private static final float GOOSE_SPAWN_INTERVAL = 2f;
    private static final int MAX_GEESE = 15;   // No new geese spawn while this many are active
    private static final float DAMAGE_PER_SEC = 1f;

    // Interval time between spawn of items (in seconds).
    private static final float PICKUP_SPAWN_INTERVAL = 15f;

    // The number of energy drinks and megaphones the player starts with when the game
    // starts or resets.
    private static final int STARTING_ENERGY_DRINKS = 2;
    private static final int STARTING_MEGAPHONES = 1;

    // Puddles: new hidden puddles appear over time, up to a cap.
    private static final float PUDDLE_SPAWN_INTERVAL = 20f; // Seconds between new puddles (lower = more)
    private static final int MAX_PUDDLES = 8;               // No new puddles while this many exist
    private static final int STARTING_PUDDLES = 2;          // Fixed puddles created by GameScreen

    private static final float BOSS_SPAWN_TIME = 60f;
    private static final float EVENT_MESSAGE_TIME = 3f;   // Seconds an event message stays on screen

    private boolean bossSpawned = false;
    private float bossSpawnTimer;
    private float gooseSpawnTimer;
    private String eventMessage;
    private float eventMessageTimer;
    private float pickupSpawnTimer;
    private float puddleSpawnTimer;

    private final GameTimer gameTimer = new GameTimer();   // 5:00 countdown and pause state
    private final Player player;
    private final Array<Goose> geese;
    private final Array<Puddle> puddles;
    private final Array<ItemPickup> itemPickups;
    private final Backpack backpack;

    private GameScreen game;

    /**
     * Creates a LogicManager.
     * 
     * @param gameScreen the current game's GUI
     */
    public LogicManager(GameScreen gameScreen) {
        this.game = gameScreen;
        this.player = game.getPlayer();
        this.geese = game.getGeese();
        this.puddles = game.getPuddles();
        this.itemPickups = game.getItemPickups();
        this.backpack = game.getBackpack();
    }
    
    /**
     * Returns the game timer, so GameScreen can pause it and display the time left.
     *
     * @return the 5 minute game timer
     */
    public GameTimer getGameTimer() {
        return gameTimer;
    }
    /**
     * Returns the message for an event that has just triggered, so GameScreen can show it.
     *
     * @return the message, or null if no event triggered in the last few seconds
     */
    public String getEventMessage() {
        return eventMessageTimer > 0f ? eventMessage : null;
    }

    /**
     * Shows a message on screen for a few seconds, so the player knows an event happened.
     *
     * @param message the text to show
     */
    private void showEventMessage(String message) {
        eventMessage = message;
        eventMessageTimer = EVENT_MESSAGE_TIME;
    }

    /**
     * Resets the game state so that geese are deleted, timers are reset, and both the player's
     * position and health are reset.
     */
    public void resetGame() {
        player.reset(player.getResetPos());
        geese.clear();
        itemPickups.clear();
        backpack.clear();

        // Adds initial items to inventory.
        backpack.addItem(game.getBread());
        for (int i = 0; i < STARTING_ENERGY_DRINKS; i++) {
            backpack.addItem(game.getEnergyDrink());
        }
        for (int i = 0; i < STARTING_MEGAPHONES; i++) {
            backpack.addItem(game.getMegaphone());
        }

        gameTimer.reset();   // back to 5:00, paused        
        bossSpawned = false;
        bossSpawnTimer = 0f;
        gooseSpawnTimer = 0f;
        eventMessageTimer = 0f;
        pickupSpawnTimer = 0f;
        puddleSpawnTimer = 0f;

        // Remove puddles spawned during the last run, and hide the starting ones again
        // so they can be rediscovered (and trigger the splash event) after a restart.
        puddles.truncate(STARTING_PUDDLES);
        for (Puddle puddle : puddles) {
            puddle.setIsVisible(false);
            puddle.setPlayerInside(false);
        }
    }

    /** Checks if an event has been triggered, and starts it if so. */
    private void startEvent() 
    {
        if (!bossSpawned && bossSpawnTimer >= BOSS_SPAWN_TIME) {
            bossSpawned = true;
            showEventMessage("A BOSS GOOSE HAS ARRIVED WITH ITS FLOCK!");
            player.eventTriggered();


            geese.add(game.getBossGoose());
            for (int i = 0; i < 8; i++) {
                geese.add(game.getGoose());
            }
        }

        player.applySpeedModifier(1f);
        for (Puddle puddle : puddles) {
            if (player.overlaps(puddle)) {
                player.applySpeedModifier(puddle.getSpeedModifier());
            }
        }

        // Geese are slowed by puddles the same amount as the player, including hidden ones.
        // They don't reveal puddles or trigger the splash message (only the player does,
        // in checkCollision).
        for (Goose goose : geese) {
            goose.applySpeedModifier(1f);
            for (Puddle puddle : puddles) {
                if (goose.overlaps(puddle)) {
                    goose.applySpeedModifier(puddle.getSpeedModifier());
                }
            }
        }
    }
    
    /** Spawns a random item pickup if the spawn timer has elapsed. */
    private void spawnItem() {
        if (pickupSpawnTimer >= PICKUP_SPAWN_INTERVAL)
        {
            pickupSpawnTimer = 0f;
        

            float itemSpawnChance = MathUtils.random(0f, 100f); // Decided which item to spawn.

            float rndX = MathUtils.random(0f, game.getWorldWidth() - ItemPickup.SIZE);
            float rndY = MathUtils.random(0f, game.getWorldHeight() - ItemPickup.SIZE);

            // 75% Chance to spawn an energy drink.
            if (itemSpawnChance < 75f) {
                Item item = game.getEnergyDrink();
                itemPickups.add(game.getPickup(rndX, rndY, item));
            }
            else 
            {   // 25% Chance to spawn a megaphone.
                Item item = game.getMegaphone();
                itemPickups.add(game.getPickup(rndX, rndY, item));
            } 
        }
    }

    /**
     * Spawns a new hidden puddle at a random spot if the spawn timer has elapsed
     * and the cap hasn't been reached. Skips spots under the player so they
     * aren't slowed by a puddle appearing beneath them.
     */
    private void spawnPuddle() {
        if (puddleSpawnTimer < PUDDLE_SPAWN_INTERVAL || puddles.size >= MAX_PUDDLES) {
            return;
        }

        float rndX = MathUtils.random(0f, game.getWorldWidth() - Puddle.WIDTH);
        float rndY = MathUtils.random(0f, game.getWorldHeight() - Puddle.HEIGHT);
        Puddle puddle = game.getPuddle(rndX, rndY);

        if (player.overlaps(puddle)) {
            return;   // Try again next frame with a new random spot
        }

        puddleSpawnTimer = 0f;
        puddles.add(puddle);
    }

    /** Calculates whether another goose should be spawned, and spawns it if so. */
    private void spawnGoose() {
        if (gooseSpawnTimer > GOOSE_SPAWN_INTERVAL && geese.size < MAX_GEESE) {
            gooseSpawnTimer = 0f;

            Goose goose = game.getGoose();
            geese.add(goose);
        }
    }

    /**
     * Makes a Goose friendly if its Rectangle collides with an Interaction's Rectangle,
     * and decreases the Players's health if a Goose's Rectangle collides with the
     * Player's.
     * 
     * @param dt deltatime
     */
    private void checkCollision(float dt) {
        // Make geese friendly if collide with interaction.
        for (Interaction interaction : player.getInteractions()) {
            var iterator = geese.iterator();

            while (iterator.hasNext()) {
                Goose next = iterator.next();

                if (interaction.overlaps(next)) {
                    next.makeFriendly(game.getFriendlyGooseTexture());
                }
            }
        }

        // Decrease player health and speed if player collides with any geese.
        int hitNum = 0;

        for (Goose goose : geese) {
            if (!goose.isFriendly() && player.overlaps(goose)) {
                ++hitNum;
            }
        }

        player.setGeeseTouching(hitNum);   // Modifies player speed
                                           // (Each goose touching slows player)

        if (hitNum > 0) {
            player.subLife(DAMAGE_PER_SEC * hitNum * dt);
        }

        // Decrease player speed if collision with any puddles.
        for (Puddle puddle : puddles) {
            boolean playerOverlapsPuddle = player.overlaps(puddle);

            if (playerOverlapsPuddle && !puddle.isPlayerInside()) {
                if (!puddle.getIsVisible()) {
                    puddle.setIsVisible(true);

                    player.eventTriggered();
                    showEventMessage("SPLASH! A PUDDLE SLOWS YOU DOWN");
                }
            }

            puddle.setPlayerInside(playerOverlapsPuddle);
        }

        // Item pickup logic.
        for (int i = itemPickups.size - 1; i >= 0; i--) {
            ItemPickup pickup = itemPickups.get(i);

            if (player.overlaps(pickup) && backpack.addItem(pickup.getItem())) {
                itemPickups.removeIndex(i);
            }
        }
    }

    /**
     * Updates the game state by ticking timers, as well as updating players, geese
     * and collision detection.
     * @param dt deltatime
     */
    public void update(float dt) {
        gameTimer.update(dt);
        bossSpawnTimer += dt;
        gooseSpawnTimer += dt;
        pickupSpawnTimer += dt;
        puddleSpawnTimer += dt;
        eventMessageTimer = Math.max(0f, eventMessageTimer - dt);

        player.update(dt);

        spawnGoose();
        
        for (Goose goose : geese) {
            goose.update(dt);
        }

        separateGeese();
        checkCollision(dt);

        startEvent();
        spawnItem();
        spawnPuddle();
    }

    /** Prevents geese from overlapping each other on the map as they chase the player. */
    private void separateGeese() {
        for (int pass = 0; pass < 3; pass++) {
            for (int i = 0; i < geese.size; i++) {
                Goose goose1 = geese.get(i);
                for (int j = i + 1; j < geese.size; j++) {
                    Goose goose2 = geese.get(j);
                    if (goose1.overlaps(goose2)) {
                        goose1.separate(goose2);
                    }
                }
            }
        }
    }
}
