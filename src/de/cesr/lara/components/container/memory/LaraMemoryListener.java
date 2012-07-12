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
package de.cesr.lara.components.container.memory;

import de.cesr.lara.components.LaraProperty;

/**
 * Objects that want to observe one or more categories of memory property
 * processes need to implement this interface. This listener only applies to
 * events associated with certain lara properties in the memory since the called
 * method receives the property object.
 */
public interface LaraMemoryListener {

	/**
	 * Categories of memory property processes a listener may be registered for.
	 */
	public enum MemoryEvent {
		/**
		 * Triggered if a memory property was forgotten on purpose or because
		 * the capacity manager need to erase properties.
		 */
		PROPERTY_FORGOTTEN,

		/**
		 * This event occurs every time a property is memorised.
		 */
		PROPERTY_MEMORIZED,

		/**
		 * This event is triggered in case a property was recalled from the
		 * memory.
		 */
		PROPERTY_RECALLED,

		/**
		 * Triggered if a storage property was removed because the according key
		 * was refreshed. NOTE: Simultaneously, a REFRESHED_PROPERTY_MEMORIZED
		 * event is triggered.
		 */
		REFRESHED_PROPERTY_FORGOTTEN,

		/**
		 * Triggered if a storage property was stored because the according key
		 * was refreshed. NOTE: Simultaneously, a REFRESHED_PROPERTY_FORGOTTEN
		 * event is triggered.
		 */
		REFRESHED_PROPERTY_MEMORIZED,
	}

	/**
	 * @param event
	 *            the type of {@link MemoryEvent} that occurred
	 * @param property
	 *            the property that is affected
	 */
	public void memoryEventOccured(MemoryEvent event,
			LaraProperty<?, ?> property);
}