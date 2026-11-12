package io.engproject;

import java.util.Optional;

import com.badlogic.gdx.graphics.Texture;

public class BreadItem extends Item {
    private static final String ITEM_NAME = "Bread";
    private final Backpack inventory;
    private boolean wasUsed = false;

    public BreadItem(Player player, Backpack backpack, Texture icon) {
        super(player, icon);
        this.inventory = backpack;
    }

    @Override
    protected String getName() {
        return ITEM_NAME;
    }

    /**
     * Applies the item's effect if it can be used right now, and counts the use
     * in the player's stats.
     *
     * @return true if the item was used (so the caller can remove it)
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

    @Override
    protected void applyEffect()
    {
        player.setIsThrowingBread(true);
        player.startThrowBread();
    }
}
