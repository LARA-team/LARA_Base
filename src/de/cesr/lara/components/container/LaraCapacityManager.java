package de.cesr.lara.components.container;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.storage.LaraStorage;

/**
 * Interface for classes that implement a capacity management strategy for
 * {@link LaraStorage}s, i.e. to remove an item from the storage at some
 * position or increase the storages capacity whenever the storage is full and a
 * new item is to be stored.
 * 
 * @author Michael Elbers
 * @param <PropertyType>
 *            The type of properties the according container stores
 * @date 22.02.2010
 * 
 */
public interface LaraCapacityManager<PropertyType extends LaraProperty<?>> {

	/**
	 * Tries to apply this manager's capacity management strategy to the storage
	 * provided. Storages using a capacity manager should <b>always</b> call
	 * this method <b>before</b> each item they try to add (i.e. they should
	 * make the following call <code>manager.manage(this)</code>), and only try
	 * to add the new item, if the capacity manager has returned
	 * <code>true</code>.
	 * 
	 * 
	 * @param capacityManageable
	 *            the container that shall be managed the storage to manage.
	 * @return true if management was successful, i.e. if at least one new item
	 *         can be added to the storage, false otherwise.
	 * 
	 *         Created by Michael Elbers on 22.02.2010
	 */
	public boolean manage(
			LaraCapacityManageable<PropertyType> capacityManageable);

}
