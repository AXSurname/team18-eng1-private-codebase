package io.engproject;

public class GameStatistics 
{
    private int itemsUsed;
    private int eventsTriggered;

    public GameStatistics() 
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
