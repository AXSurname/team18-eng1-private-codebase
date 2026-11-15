package io.engproject;

import com.badlogic.gdx.graphics.Texture;

/**
 * A megaphone the player carries in the {@link Backpack} which can only be used
 * once. It scares the geese away for a duration of time, causing them to move away
 * from the player.
 * 
 * When it is used is determined by other the behaviour of other classes. Its icon
 * {@link Texture} is owned (and disposed) by the screen that created it, not by
 * this class.
 * 
 * @see {@link Player#activateMegaphone()}
 */
public class MegaphoneItem extends Item {
    private static final String ITEM_NAME = "Megaphone";

    /**
     * Creates the megaphone item that is shown in the player's inventory.
     * 
     * @param player
     * @param icon
     */
    public MegaphoneItem(Player player, Texture icon) {
        super(player, icon);
    }

    @Override
    protected String getName() {
        return ITEM_NAME;
    }

    @Override
    protected boolean canUse() {
        return player.isAlive();
    }

    @Override
    protected void applyEffect()
    {
        player.activateMegaphone();
    }
}
