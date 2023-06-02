package pl.gawryszewski.edp_projekt.controller;

import javafx.event.Event;
import javafx.event.EventType;

public class ItemsReceivedEvent extends Event {
    public static final EventType<ItemsReceivedEvent> ITEMS_RECEIVED =
            new EventType<>(Event.ANY, "ITEMS_RECEIVED");
    public ItemsReceivedEvent(){
        super(ITEMS_RECEIVED);
    }
}
