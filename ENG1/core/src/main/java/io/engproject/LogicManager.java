package io.engproject;

import com.badlogic.gdx.utils.Array;

/**
 * Handles most non-graphical logic, and manages interactions between multiple
 * GmaeObject classes.
 */
public class LogicManager {
    private static final float GOOSE_SPAWN_INTERVAL = 2f;
    private static final int MAX_GEESE = 15;   // no new geese spawn while this many are on screen
    private static final float DAMAGE_PER_SEC = 1f;

    
    // Energy drinks the backpack starts each run with, until the team decides how
    // items should appear (pickups via spawnItem() or a starting stock)
    private static final int STARTING_ENERGY_DRINKS = 3;
    private static final int STARTING_MEGAPHONES = 1;

    private static final float BOSS_SPAWN_TIME = 60f;
    private static final float EVENT_MESSAGE_TIME = 3f;   // seconds an event message stays on screen
    private boolean bossSpawned = false;

    private float timer;
    private float gooseSpawnTimer;
    private String eventMessage;
    private float eventMessageTimer;

    private final GameTimer gameTimer = new GameTimer();   // 5:00 countdown and pause state
    private final Player player;
    private final Array<Goose> geese;
    private final Array<Puddle> puddles;
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

        
        backpack.clear();
        for (int i = 0; i < STARTING_ENERGY_DRINKS; i++) {
            backpack.addItem(game.getEnergyDrink());
        }
        for (int i = 0; i < STARTING_MEGAPHONES; i++) {
            backpack.addItem(game.getMegaphone());
        }

      

        bossSpawned = false;
        timer = 0f;
        gameTimer.reset();   // back to 5:00, paused        
        gooseSpawnTimer = 0f;
        eventMessageTimer = 0f;
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
     * Removes a Goose if its Rectangle collides with an Interaction's Rectangle,
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

        player.setGeeseTouching(hitNum);   // Modifies player speed
                                            // (Each goose touching slows player)

        if (hitNum > 0) {
            player.subLife(DAMAGE_PER_SEC * hitNum * dt);
        }

        for (Puddle puddle : puddles) {
            boolean playerOverlapsPuddle = player.overlaps(puddle);

            if (playerOverlapsPuddle && !puddle.isPlayerInside()) {
                player.eventTriggered();
                showEventMessage("SPLASH! A PUDDLE SLOWS YOU DOWN");
            }

            puddle.setPlayerInside(playerOverlapsPuddle);
        }
    }

    // To be implemented as needed
    private void startEvent() 
    {
        if (!bossSpawned && timer >= BOSS_SPAWN_TIME) {
            bossSpawned = true;
            showEventMessage("A BOSS GOOSE HAS ARRIVED WITH ITS FLOCK!");
            player.eventTriggered();


            geese.add(game.getBossGoose());
            for (int i = 0; i < 6; i++) {
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
    

    // To be implemented as needed
    private void spawnItem() {
        
    }

    /**
     * Updates the game state by ticking timers, as well as updating players, geese
     * and collision detection.
     * @param dt deltatime
     */
    public void update(float dt) {
        gameTimer.update(dt);
        timer += dt;
        gooseSpawnTimer += dt;
        eventMessageTimer = Math.max(0f, eventMessageTimer - dt);




        player.update(dt);

        spawnGoose();
        startEvent();

        for (Goose goose : geese) {
            goose.update(dt);
        }

        checkCollision(dt);

        // Put startEvent() and spawnItem() methods here
    }

}
