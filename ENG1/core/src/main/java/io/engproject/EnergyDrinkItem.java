package io.engproject;

import com.badlogic.gdx.graphics.Texture;

/** Doubles the player's speed for a few seconds (see Player.activateBoost). */
public class EnergyDrinkItem extends Item {

    private static final String ITEM_NAME = "Energy Drink";

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