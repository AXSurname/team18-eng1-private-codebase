package io.engproject;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handles most non-graphical logic, and manages interactions between multiple
 * GmaeObject classes.
 */
public class LogicManager {
    private static final float GOOSE_SPAWN_INTERVAL = 2f;
    private static final int MAX_ANGRY_GEESE = 15;   // no new geese spawn while this many are active
    private static final float DAMAGE_PER_SEC = 1f;

    //Time between spawn of items.
    private static final float PICKUP_SPAWN_INTERVAL = 30f; // Inverval between item spawns in seconds
    
    // Energy drinks the backpack starts each run with, until the team decides how
    // items should appear (pickups via spawnItem() or a starting stock)
    private static final int STARTING_ENERGY_DRINKS = 2;
    private static final int STARTING_MEGAPHONES = 1;

    private static final float BOSS_SPAWN_TIME = 60f;
    private static final float EVENT_MESSAGE_TIME = 3f;   // seconds an event message stays on screen
    private boolean bossSpawned = false;

    private float bossSpawnTimer;
    private float gooseSpawnTimer;
    private String eventMessage;
    private float eventMessageTimer;
    private float pickupSpawnTimer;

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
    }

    /** Calculates whether another goose should be spawned, and spawns it if so. */
    private void spawnGoose() {
        int angryGooseCount = 0;
        for (Goose goose : geese) {
            if (!goose.isFriendly()) {
                angryGooseCount++;
            }
        }

        if (gooseSpawnTimer > GOOSE_SPAWN_INTERVAL && angryGooseCount < MAX_ANGRY_GEESE) {
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
        for (Interaction interaction : player.getInteractions()) {
            var iterator = geese.iterator();

            while (iterator.hasNext()) {
                Goose next = iterator.next();

                if (interaction.overlaps(next)) {
                    next.makeFriendly(game.getFriendlyGooseTexture());
                }
            }
        }

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

        for (int i = itemPickups.size - 1; i >= 0; i--) {
            ItemPickup pickup = itemPickups.get(i);

            if (player.overlaps(pickup) && backpack.addItem(pickup.getItem())) {
                itemPickups.removeIndex(i);
            }
        }
    }

    // To be implemented as needed
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
    }
    

    // Spawns a random item pickup if the spawn timer has elapsed.
    private void spawnItem() {
        if (pickupSpawnTimer >= PICKUP_SPAWN_INTERVAL)
        {
            pickupSpawnTimer = 0f;
        

            float itemSpawnChance = MathUtils.random(0f, 101f); // Decided which item to spawn.

            float rndX = MathUtils.random(0f, game.getWorldWidth() - ItemPickup.SIZE);
            float rndY = MathUtils.random(0f, game.getWorldHeight() - ItemPickup.SIZE);

            if (itemSpawnChance < 75f) { // 75% Chance to spawn an energy drink.
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
     * Updates the game state by ticking timers, as well as updating players, geese
     * and collision detection.
     * @param dt deltatime
     */
    public void update(float dt) {
        gameTimer.update(dt);
        bossSpawnTimer += dt;
        gooseSpawnTimer += dt;
        pickupSpawnTimer += dt;
        eventMessageTimer = Math.max(0f, eventMessageTimer - dt);

        player.update(dt);

        spawnGoose();
        
        for (Goose goose : geese) {
            goose.update(dt);
        }

        checkCollision(dt);

        startEvent();
        spawnItem();
    }

}
