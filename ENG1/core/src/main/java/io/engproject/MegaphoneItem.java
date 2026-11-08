package io.engproject;

import com.badlogic.gdx.graphics.Texture;

public class MegaphoneItem extends Item {
    private static final String ITEM_NAME = "Megaphone";

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
