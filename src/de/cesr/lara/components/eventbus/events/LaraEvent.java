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
package de.cesr.lara.components.eventbus.events;


/**
 * Components can publish events (even custom events) to the LaraEventBus. The LaraEventBus will notify all subscribers
 * of Events of the given event type. All events to be published via the LaraEventBus need to implement this interface -
 * but not directly. Implementing one of its sub-types LaraAsynchronousEvent, LaraSequentialEvent and
 * LaraSynchronousEvent defines how the EventBus notifies the corresponding subscribers (asynchronous, synchronous or
 * sequential).
 */
public abstract interface LaraEvent {

}