/**
 * This file is part of
 * 
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 * 
 * Copyright (C) 2012 Center for Environmental Systems Research, Kassel, Germany
 * 
 * LARA is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * LARA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cesr.lara.components.eventbus;

import de.cesr.lara.components.eventbus.events.LaraEvent;

/**
 * Common interface for subscribers of events.
 */
public interface LaraEventSubscriber extends LaraAbstractEventSubscriber {

	/**
	 * Will be called with an event as a parameter when an event the subscriber
	 * subscribed to occurs. Implement this to react on events. Typically you
	 * would start doing something like <pseudocode> if (event instanceof
	 * VeryInterestingEvent) { //do something smart } </pseudocode>
	 * 
	 * @param event
	 */
	public abstract <T extends LaraEvent> void onEvent(T event);
}
