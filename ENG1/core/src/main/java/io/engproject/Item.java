package io.engproject;

import com.badlogic.gdx.graphics.Texture;

/**
 * Something the player carries in the Backpack and uses once.
 * Subclasses decide when they can be used and what their effect is.
 * The icon texture is owned (and disposed) by the screen that loaded it.
 */
public abstract class Item {

    protected final Player player;
    private final Texture icon;

    public Item(Player player, Texture icon) {
        this.player = player;
        this.icon = icon;
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

    public Texture getIcon() {
        return icon;
    }

    protected abstract String getName();

    protected abstract boolean canUse();

    protected abstract void applyEffect();
}
