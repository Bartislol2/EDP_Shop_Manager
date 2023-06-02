package pl.gawryszewski.edp_projekt.controller;

import javafx.event.Event;
import javafx.event.EventType;

public class NoItemsFoundEvent extends Event {
    public static final EventType<NoItemsFoundEvent> NO_ITEMS_FOUND =
            new EventType<>(Event.ANY, "NO_ITEMS_FOUND");
    public NoItemsFoundEvent() {
        super(NO_ITEMS_FOUND);
    }
}
