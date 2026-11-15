package io.engproject;

import com.badlogic.gdx.graphics.Texture;

/**
 * Something the player carries in the {@link Backpack} and typically uses once.
 * 
 * Their effects and when they are used are determined by other the behaviour of
 * other classes. Its icon {@link Texture} is owned (and disposed) by the screen that
 * created them, not by this class.
 */
public abstract class Item {
    protected final Player player;
    private final Texture icon;

    /**
     * Creates an item that is shown in the player's inventory.
     * 
     * @param player
     * @param icon the image representing the item in a backpack slot
     */
    public Item(Player player, Texture icon) {
        this.player = player;
        this.icon = icon;
    }

    public Texture getIcon() {
        return icon;
    }

    /**
     * Returns whether the item is on a cooldown.
    
     * @return true when the item is on a cooldown
     */
    public boolean isOnCooldown() {
        return false;
    }

    /**
     * Applies the item's effect if it can be used right now, and counts the use
     * in the player's stats.
     *
     * @return true if the item was used (so the caller can remove it)
     */
    public boolean use()
    {
        if (!canUse())
        {
            return false;
        }

        applyEffect();
        player.itemUsed();
        return true;
    }

    protected abstract String getName();

    protected abstract boolean canUse();

    protected abstract void applyEffect();
}
