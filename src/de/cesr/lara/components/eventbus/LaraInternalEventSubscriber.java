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
 * Common interface for subscribers of events. Events declare an inherited
 * interface. Only for core components in de.cesr.lara.components.
 */
public interface LaraInternalEventSubscriber extends
		LaraAbstractEventSubscriber {

	/**
	 * Only for internal super classes beeing part of the LARA core. Use this in
	 * your base class to react on events, when the reaction should not be
	 * overridden by subclasses.
	 * 
	 * @param event
	 */
	public abstract void onInternalEvent(LaraEvent event);

}