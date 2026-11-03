package io.engproject;

public abstract class Item {

    protected Player player;

    public Item(Player player) {
        this.player = player;
    }

    public final void use()
    {
        if (canUse() == true)
        {
            applyEffect();
            consume();
        }
    }

    protected abstract String getName();

    protected abstract boolean canUse();

    protected abstract void applyEffect();

    protected abstract void consume();

    protected abstract void incrementItemsUsed();


}
