package de.cesr.lara.components.eventbus;

/**
 * There are two types of subscribers: 1. the LaraEventSubscriber - components
 * in general 2. LaraInternalEventSubscriber - used by LARA core base classes.
 * Both are extended interfaces of this interface. The internal subscriber will
 * be notified first. This is necessary to give super classes the possibility to
 * react on the same Events as their subclasses. Otherwise they would loose
 * functionality when the onEvent-method would be overridden.
 * The abstract in the interface declaration should indicate that you should not directly implement this.
 */
public abstract interface AbstractLaraEventSubscriber {

}
