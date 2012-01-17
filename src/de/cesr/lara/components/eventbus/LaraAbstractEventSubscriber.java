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

/**
 * There are two types of subscribers:
 * 
 * 1. the LaraEventSubscriber - components in general 2.
 * LaraInternalEventSubscriber - used by LARA core base classes.
 * 
 * Both are extending this interface. The internal subscriber will be notified
 * first. This is necessary to give super classes the possibility to react on
 * the same Events as their subclasses (otherwise they would loose functionality
 * when the onEvent-method would be overridden).
 * 
 * The abstract in the interface declaration should indicate that you should not
 * directly implement this.
 */
public abstract interface LaraAbstractEventSubscriber {
}
