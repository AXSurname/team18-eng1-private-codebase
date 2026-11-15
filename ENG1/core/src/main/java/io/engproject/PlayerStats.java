package io.engproject;

/**
 * Stores the player's statistics: the number of items used and the number of
 * events that have been triggered.
 */
public class PlayerStats {
    private int itemsUsed;
    private int eventsTriggered;

    /** Creates a class storing the player's statistics. */
    public PlayerStats() {
        this.itemsUsed = 0;
        this.eventsTriggered = 0;
    }

    public void incrementItemsUsed() 
    {
        itemsUsed++;
    }

    public void incrementEventsTriggered() 
    {
        eventsTriggered++;
    }

    public int getItemsUsed() 
    {
        return itemsUsed;
    }

    public int getEventsTriggered() 
    {
        return eventsTriggered;
    }

    public void reset() 
    {
        itemsUsed = 0;
        eventsTriggered = 0;
    }
}
