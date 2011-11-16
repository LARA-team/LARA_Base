package de.cesr.lara.components.eventbus.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.lara.components.eventbus.AbstractLaraEventSubscriber;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;
import de.cesr.lara.components.eventbus.LaraInternalEventSubscriber;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.events.LaraAsynchronousEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.events.LaraHasConsecutiveEvent;
import de.cesr.lara.components.eventbus.events.LaraRequiresPrecedingEvent;
import de.cesr.lara.components.eventbus.events.LaraSynchronousEvent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * The LEventbus serves as a communication system between different components,
 * where the components do not necessarily know each other. Components can
 * publish events to the LaraEventbus. All subscribers of this event will be
 * notified on occurrence. The event bus is implemented as a singleton to ensure
 * all components communicate over the same channel and to make use of the event
 * bus easier. GOAL: Loose coupling of components, Event driven initialization.
 */
public class LEventbus {

	private static LEventbus instance = null;
	private static Map<String, LEventbus> instances = new HashMap<String, LEventbus>();
	private static Logger logger = Log4jLogger.getLogger(LEventbus.class
			.getSimpleName());

	/**
	 * returns a reference to the global eventbus
	 */
	public static LEventbus getInstance() {
		if (instance == null) {
			instance = new LEventbus();
			Log4jLogger.init();
			logger.debug("LaraEventbus instantiated");
		}
		return instance;
	}

	/**
	 * returns a reference to a special eventbus
	 * 
	 * @param id
	 * @return
	 */
	public static LEventbus getInstance(String id) {
		LEventbus theInstance = instances.get(id);
		if (theInstance == null) {
			theInstance = new LEventbus();
			instances.put(id, theInstance);
			logger.debug("LaraEventbus '" + id + "' instantiated");
		}
		return theInstance;
	}

	private Set<String> eventsThisTimestep = new HashSet<String>();

	Map<Class<? extends LaraEvent>, Set<AbstractLaraEventSubscriber>> eventSubscriberMap = new HashMap<Class<? extends LaraEvent>, Set<AbstractLaraEventSubscriber>>();

	Map<LaraEvent, Integer> eventWaitingCounters = new HashMap<LaraEvent, Integer>();

	Map<Class<? extends LaraEvent>, Long> statistics = new HashMap<Class<? extends LaraEvent>, Long>();

	/**
	 * This is a singleton. Use of constructor is permitted. Use getInstance()
	 * to obtain a reference to the event bus.
	 */
	protected LEventbus() {
	}

	public void notifySubscribers(LaraEvent event) {
		// notify all subscribers
		if (eventSubscriberMap.containsKey(event.getClass())) {
			// get subscribers set
			Set<AbstractLaraEventSubscriber> subscribers = eventSubscriberMap
					.get(event.getClass());
			logger.debug("notifying " + subscribers.size()
					+ " subscribers of event of type "
					+ event.getClass().getSimpleName());
			// notify subscriber according to event type
			if (event instanceof LaraSynchronousEvent) {
				notifySubscribersSynchronous(subscribers, event);
			} else if (event instanceof LaraAsynchronousEvent) {
				notifySubscribersAsynchronous(subscribers, event);
			} else {
				notifySubscribersSequential(subscribers, event);
			}
		} else {
			// no subscribers - log this
			logger.warn("Event of type "
					+ event.getClass().getSimpleName()
					+ " published, but has no subscribers. Maybe you should check if this was intended.");
		}
		// if event has consecutive event fire this
		if (event instanceof LaraHasConsecutiveEvent) {
			publish(((LaraHasConsecutiveEvent) event).getConsecutiveEvent());
		}
	}

	/**
	 * Publish / fire event - this will notify subscribers of this event about
	 * occurrence of this event
	 * 
	 * @param event
	 */
	public void publish(LaraEvent event) {
		// reset set of events which occured this timestep if new timestep
		// beginns. otherwise store event type.
		if (event instanceof LModelStepEvent) {
			eventsThisTimestep.clear();
		}
		eventsThisTimestep.add(event.getClass().getSimpleName());
		// check precondition
		if (event instanceof LaraRequiresPrecedingEvent) {
			if (eventsThisTimestep
					.contains(((LaraRequiresPrecedingEvent) event)
							.getRequiredPrecedingEventName())) {
				notifySubscribers(event);
			} else {
				// no subscribers - log this
				logger.warn("Event of type "
						+ event.getClass().getSimpleName()
						+ " required event of type "
						+ ((LaraRequiresPrecedingEvent) event)
								.getRequiredPrecedingEventName()
						+ " to have occured before - which did not happen.");
			}
		} else {
			notifySubscribers(event);
		}
	}

	/**
	 * Unsubscribe a subscriber from an event
	 * 
	 * @param subscriber
	 * @param event
	 */
	public void unsubscribe(AbstractLaraEventSubscriber subscriber,
			Class<? extends LaraEvent> eventClass) {
		logger.debug("instance of " + subscriber.getClass().getSimpleName()
				+ " unsubscribes from event of type "
				+ eventClass.getSimpleName());
		if (eventSubscriberMap.containsKey(eventClass)) {
			// get existing set of subscribers
			Set<AbstractLaraEventSubscriber> subscribers = eventSubscriberMap
					.get(eventClass);
			subscribers.remove(subscriber);
		} else {
			logger.debug("instance of " + subscriber.getClass().getSimpleName()
					+ " wants to unsubscribe from event of type "
					+ eventClass.getSimpleName()
					+ " but is not a subscriber at the moment");
		}
	}

	/**
	 * Subscribe for an event - subscriber will be informed about occurrence of
	 * event (triggers onEvent() method of implemented interface
	 * LaraEventSubscriber)
	 * 
	 * @param subscriber
	 * @param event
	 */
	public void subscribe(AbstractLaraEventSubscriber subscriber,
			Class<? extends LaraEvent> eventClass) {
		logger.debug("instance of " + subscriber.getClass().getSimpleName()
				+ " subscribes to event of type " + eventClass.getSimpleName());
		if (eventSubscriberMap.containsKey(eventClass)) {
			// add to existing set
			Set<AbstractLaraEventSubscriber> subscribers = eventSubscriberMap
					.get(eventClass);
			subscribers.add(subscriber);
		} else {
			// add to new set
			Set<AbstractLaraEventSubscriber> subscribers = new HashSet<AbstractLaraEventSubscriber>();
			subscribers.add(subscriber);
			// add to event-subscriber mapping
			eventSubscriberMap.put(eventClass, subscribers);
		}
	}

	/**
	 * Decrements the counter of how many event subscribers are notified but
	 * have not yet finished their work. If last subscriber finishes the monitor
	 * stops waiting.
	 * 
	 * @param event
	 * @param monitor
	 */
	private synchronized void decrementWaitingCounter(LaraEvent event,
			Object monitor) {
		Integer oldValue = eventWaitingCounters.get(event);
		if (oldValue == null) {
			logger.error("something went wrong during synchronized event notification of event "
					+ event.getClass().getSimpleName());
		} else {
			Integer newValue = new Integer(oldValue.intValue() - 1);
			logger.debug("number of worker threads: " + newValue.intValue());
			if (newValue.intValue() > 0) {
				eventWaitingCounters.put(event, newValue);
			} else {
				// is last worker thread
				synchronized (monitor) {
					// do not waste memory
					eventWaitingCounters.remove(event);
					// stop waiting
					monitor.notify();
				}
			}
		}
	}

	/**
	 * Increments the counter of how many event subscribers are notified but
	 * have not yet finished their work.
	 * 
	 * @param event
	 */
	private synchronized void incrementWaitingCounter(LaraEvent event) {
		Integer oldValue = eventWaitingCounters.get(event);
		if (oldValue == null) {
			oldValue = new Integer(0);
		}
		Integer newValue = new Integer(oldValue.intValue() + 1);
		logger.debug("number of worker threads: " + newValue.intValue());
		eventWaitingCounters.put(event, newValue);
	}

	/**
	 * Notifies all subscribers at once. Subscribers will execute their related
	 * code in parallel. Method will not wait for subscribers to finish. Method
	 * will return immediately
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void notifySubscribersAsynchronous(
			Set<AbstractLaraEventSubscriber> subscribers, final LaraEvent event) {
		// internal first
		for (final AbstractLaraEventSubscriber s : subscribers) {
			Thread workerThread = new Thread() {
				@Override
				public void run() {
					if (s instanceof LaraInternalEventSubscriber) {
						((LaraInternalEventSubscriber) s)
								.onInternalEvent(event);
					}
				}
			};
			workerThread.start();
		}
		for (final AbstractLaraEventSubscriber s : subscribers) {
			Thread workerThread = new Thread() {
				@Override
				public void run() {
					if (s instanceof LaraEventSubscriber) {
						((LaraEventSubscriber) s).onEvent(event);
					}
				}
			};
			workerThread.start();
		}
	}

	/**
	 * Notifies subscriber sequential. Subscribers will execute their related
	 * code sequential. Method finishes after last subscriber finishes.
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void notifySubscribersSequential(
			Set<AbstractLaraEventSubscriber> subscribers, LaraEvent event) {
		// internal first
		for (AbstractLaraEventSubscriber s : subscribers) {
			if (s instanceof LaraInternalEventSubscriber) {
				((LaraInternalEventSubscriber) s).onInternalEvent(event);
			}
		}
		for (AbstractLaraEventSubscriber s : subscribers) {
			if (s instanceof LaraEventSubscriber) {
				((LaraEventSubscriber) s).onEvent(event);
			}
		}
	}

	/**
	 * Notifies all subscribers at once. Subscribers will execute their related
	 * code in parallel. Method will wait until last subscriber finishes.
	 * 
	 * @param subscribers
	 * @param event
	 */
	private void notifySubscribersSynchronous(
			Set<AbstractLaraEventSubscriber> subscribers, final LaraEvent event) {
		final Object monitorInternal = new Object();
		// internal first
		for (final AbstractLaraEventSubscriber s : subscribers) {
			Thread workerThread = new Thread() {
				@Override
				public void run() {
					if (s instanceof LaraInternalEventSubscriber) {
						((LaraInternalEventSubscriber) s)
								.onInternalEvent(event);
					}
					decrementWaitingCounter(event, monitorInternal);
				}
			};
			incrementWaitingCounter(event);
			workerThread.start();
		}
		// wait for all threads to finish
		waitUntilWorkDone(monitorInternal);
		final Object monitor = new Object();
		for (final AbstractLaraEventSubscriber s : subscribers) {
			Thread workerThread = new Thread() {
				@Override
				public void run() {
					if (s instanceof LaraEventSubscriber) {
						((LaraEventSubscriber) s).onEvent(event);
					}
					decrementWaitingCounter(event, monitor);
				}
			};
			incrementWaitingCounter(event);
			workerThread.start();
		}
		// wait for all threads to finish
		waitUntilWorkDone(monitor);
	}

	/**
	 * Wait for worker threads to finish. The last thread that finishes will
	 * notify the monitor.
	 * 
	 * @param monitor
	 */
	private void waitUntilWorkDone(Object monitor) {
		synchronized (monitor) {
			try {
				logger.debug("waiting for worker threads to finish");
				monitor.wait();
				logger.debug("all worker threads finished");
			} catch (InterruptedException e) {
				logger.error("waiting for worker threads to finished failed", e);
			}
		}
	}
}