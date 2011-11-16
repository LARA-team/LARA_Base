package de.cesr.lara.components.eventbus.events;

/**
 * The LaraEventBus will notify subscribers of Events implementing this
 * interface synchronous. This means: all subscribers are notified -->
 * subscribers do their job in parallel --> EventBus waits until all subscribers
 * finish --> LaraEventBus.publish(LaraEvent event) returns
 */
public interface LaraSynchronousEvent extends LaraEvent {

}
