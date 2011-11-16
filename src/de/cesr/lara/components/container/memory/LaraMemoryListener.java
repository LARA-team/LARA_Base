/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 26.03.2010
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
	public void memoryEventOccured(MemoryEvent event, LaraProperty<?> property);
}
