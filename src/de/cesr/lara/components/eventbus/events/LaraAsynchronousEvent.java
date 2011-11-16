package de.cesr.lara.components.eventbus.events;

/**
 * The LaraEventBus will notify subscribers of Events implementing this
 * interface asynchronous. This means: all subscribers are notified -->
 * subscribers do their job in parallel --> EventBus does not wait until all
 * subscribers finish --> LaraEventBus.publish(LaraEvent event) returns with
 * jobs still running in parallel
 */
public interface LaraAsynchronousEvent extends LaraEvent {

}
