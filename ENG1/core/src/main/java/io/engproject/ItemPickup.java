package io.engproject;


//Represents a pickup item in the game.
public class ItemPickup extends GameObject {
    static final float SIZE = 0.5f;   
    private final Item item;

    public ItemPickup(float x, float y, Item item) {
        super(x, y, SIZE, SIZE, item.getIcon());
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override 
    public void update(float dt) {}
}
