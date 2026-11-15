package io.engproject;

import com.badlogic.gdx.graphics.Texture;

import java.util.Optional;

/**
 * An infinite loaf of bread the player carries in the {@link Backpack}. It can
 * therefore be used an infinite number of times, unlike other items. Allows the
 * player to throw bread to make the geese friendly and stop chasing them.
 * 
 * Its use and when it is used are determined by other the behaviour of other
 * classes. Its icon {@link Texture} is owned (and disposed) by the screen that
 * created it, not by this class.
 */
public class BreadItem extends Item {
    private static final String ITEM_NAME = "Bread";
    private final Backpack inventory;
    private boolean wasUsed = false;

    /**
     * Creates the bread item that is shown in the player's inventory.
     * 
     * @param player
     * @param backpack the player's inventory storing the bread and other items
     * @param icon     the image representing the bread in a backpack slot
     */
    public BreadItem(Player player, Backpack backpack, Texture icon) {
        super(player, icon);
        this.inventory = backpack;
    }

    @Override
    protected String getName() {
        return ITEM_NAME;
    }

    @Override
    protected boolean canUse() {
        if (inventory.getSelectedItem().equals(Optional.of(this))) {
            return player.isAlive() && player.getCanInteract();
        }

        return false;
    }

    @Override
    public boolean isOnCooldown() {
        return player.isAlive() && !player.getCanInteract();
    }

    /**
     * Applies the item's effect if it can be used right now, and counts the use
     * in the player's stats.
     *
     * @return {@code true} if the item was used (so the caller can remove it)
     */
    @Override
    public final boolean use()
    {
        if (!canUse())
        {
            return false;
        }

        applyEffect();

        if (!wasUsed) {
            player.itemUsed();
            wasUsed = true;
        }

        return true;
    }

    @Override
    protected void applyEffect()
    {
        player.setIsThrowingBread(true);
        player.startThrowBread();
    }
}
