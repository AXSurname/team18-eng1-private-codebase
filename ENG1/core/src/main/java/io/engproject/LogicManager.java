package io.engproject;

import com.badlogic.gdx.utils.Array;

/**
 * Handles most non-graphical logic, and manages interactions between multiple
 * GmaeObject classes.
 */
public class LogicManager {
    private static final float GOOSE_SPAWN_INTERVAL = 2f;
    private static final float DAMAGE_PER_SEC = 1f;

    private static final float BOSS_SPAWN_TIME = 5f;
    private boolean bossSpawned = false;

    private float timer;
    private float gooseSpawnTimer;

    private final Player player;
    private final Array<Goose> geese;

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
    }

    /**
     * Resets the game state so that geese are deleted, timers are reset, and both the player's
     * position and health are reset.
     */
    public void resetGame() {
        player.reset(player.getResetPos());
        geese.clear();

        bossSpawned = false;
        timer = 0f;
        gooseSpawnTimer = 0f;
    }

    /** Calculates whether another goose should be spawned, and spawns it if so. */
    private void spawnGoose() {
        if (gooseSpawnTimer > GOOSE_SPAWN_INTERVAL) {
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

        if (hitNum > 0) {
            player.subLife(DAMAGE_PER_SEC * hitNum * dt);
        }
    }

    // To be implemented as needed
    private void startEvent() 
    {
        if (!bossSpawned && timer >= BOSS_SPAWN_TIME) {
            bossSpawned = true;
            player.eventTriggered();
            geese.add(game.getBossGoose());
            for (int i = 0; i < 6; i++) {
                geese.add(game.getGoose());
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
        timer += dt;
        gooseSpawnTimer += dt;

        player.update(dt);

        spawnGoose();
        startEvent();

        for (Goose goose : geese) {
            goose.update(dt);
        }

        checkCollision(dt);

        // Put startEvent() and spawnItem() implementation here
    }

}
