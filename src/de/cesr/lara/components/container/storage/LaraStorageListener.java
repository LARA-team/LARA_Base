/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 26.03.2010
 */
package de.cesr.lara.components.container.storage;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.memory.LaraMemoryListener.MemoryEvent;

/**
 * 
 */
public interface LaraStorageListener {
	/**
	 * Categories of storage property processes a listener may be registered
	 * for.
	 */
	public enum StorageEvent {
		/**
		 * Triggered if a storage property was removed because the capacity
		 * manager need to erase properties.
		 */
		PROPERTY_AUTO_REMOVED,

		/**
		 * This event is triggered in case a property was fetched from the
		 * storage.
		 */
		PROPERTY_FETCHED,

		/**
		 * Triggered if a storage property was stored and the same key was
		 * present before (i.e. the old property was removed). NOTE: Also an
		 * PROPERTY_RESTORED event is triggered.
		 */
		PROPERTY_OVERWRITTEN,

		/**
		 * Triggered if a storage property was removed on purpose.
		 */
		PROPERTY_REMOVED,

		/**
		 * Triggered if a storage property was stored and the same key was
		 * present before (i.e. the old property was removed). NOTE: Also an
		 * PROPERTY_OVERWRITTEN event is triggered.
		 */
		PROPERTY_RESTORED,

		/**
		 * This event occurs every time a property is stored.
		 */
		PROPERTY_STORED;
	}

	/**
	 * @param event
	 *            the type of {@link MemoryEvent} that occurred
	 * @param property
	 *            the property that is affected
	 */
	public void storageEventOccured(StorageEvent event,
			LaraProperty<?, ?> property);
}
