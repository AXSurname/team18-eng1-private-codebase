package io.engproject;

public class PlayerStats 
{
    private int itemsUsed;
    private int eventsTriggered;

    public PlayerStats() 
    {
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
}
