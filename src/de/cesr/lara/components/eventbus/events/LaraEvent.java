package de.cesr.lara.components.eventbus.events;

/**
 * Components can publish events (even custom events) to the LaraEventBus. The
 * LaraEventBus will notify all subscribers of Events of the given event type.
 * All events to be published via the LaraEventBus need to implement this
 * interface - but not directly. Implementing one of its subtypes
 * LaraAsynchronousEvent, LaraSequentialEvent and LaraSynchronousEvent defines
 * how the EventBus notifies the corresponding subscribers (asynchronous,
 * synchronous or sequential).
 */
public abstract interface LaraEvent {

}
