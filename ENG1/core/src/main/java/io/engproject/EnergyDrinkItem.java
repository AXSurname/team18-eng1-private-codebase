package io.engproject;

import com.badlogic.gdx.graphics.Texture;

/**
 * An energy drink the player carries in the {@link Backpack} which can only be used
 * once. It doubles the player's speed for a number of seconds defined in {@link Player}.
 * 
 * When it is used is determined by other the behaviour of other classes. Its icon
 * {@link Texture} is owned (and disposed) by the screen that created it, not by
 * this class.
 * 
 * @see {@link Player#activateBoost()}
 * */
public class EnergyDrinkItem extends Item {
    private static final String ITEM_NAME = "Energy Drink";

    /**
     * Creates the energy drink item that is shown in the player's inventory.
     * 
     * @param player
     * @param icon the image representing the energy drink in a backpack slot
     */
    public EnergyDrinkItem(Player player, Texture icon) {
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
        player.activateBoost();
    }
}