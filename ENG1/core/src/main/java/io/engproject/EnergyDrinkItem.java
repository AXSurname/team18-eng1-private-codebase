package io.engproject;

public class EnergyDrinkItem extends Item {

    private static final String ITEM_NAME = "Energy Drink";

    public EnergyDrinkItem(Player player) {
        super(player);
    }

    @Override
    protected String getName() {
        return ITEM_NAME;
    } 

    @Override
    protected boolean canUse() {
        return true;
    }

    @Override
    protected void applyEffect() 
    {
        player.activateBoost();
    }

    @Override
    protected void consume() 
    {
    }

    @Override
    protected void incrementItemsUsed() {
        
    }
}
