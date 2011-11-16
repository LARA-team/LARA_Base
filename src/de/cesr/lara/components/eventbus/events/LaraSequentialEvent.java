package de.cesr.lara.components.eventbus.events;

/**
 * The LaraEventBus will notify subscribers of Events implementing this
 * interface sequential. This means: one subscribers is notified --> subscriber
 * finishes --> next subscriber is notified ... -->
 * LaraEventBus.publish(LaraEvent event) returns
 */
public interface LaraSequentialEvent extends LaraEvent {

}
