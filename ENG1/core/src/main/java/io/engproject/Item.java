package io.engproject;

public abstract class Item {

    public final void Use()
    {
        if (CanUse() == true)
        {
            applyEffect();
            consume();
        }
    }

    protected abstract String getName();

    protected abstract boolean CanUse();

    protected abstract void applyEffect();

    protected abstract void consume();

}
