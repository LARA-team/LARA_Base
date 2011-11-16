package de.cesr.lara.components.container;

import de.cesr.lara.components.LaraProperty;

/**
 * {@link LaraContainer}s implementing this interface accept a
 * {@link LaraCapacityManager} and commit themselves to calling its
 * {@link LaraCapacityManager#manage(LaraCapacityManagementView))} whenever they
 * are full and a new item is to be stored. Usually, the manager will then
 * remove an item from the container at a position according to its strategy and
 * thus clear space for the new item.
 * 
 * @author Michael Elbers
 * @param <ItemType>
 * @date 22.02.2010
 * 
 */
public interface LaraCapacityManageable<PropertyType extends LaraProperty> {

	/**
	 * @param capacityManageable
	 * @return
	 */
	public LaraCapacityManagementView<PropertyType> getCapacityManagementView();

	/**
	 * @return Created by Michael Elbers on 22.02.2010
	 */
	public LaraCapacityManager<PropertyType> getCapacityManager();

	/**
	 * 
	 * @param capacity
	 * @return
	 */
	public boolean setCapacity(int capacity);

	/**
	 * @param manager
	 *            Created by Michael Elbers on 22.02.2010
	 */
	public void setCapacityManager(LaraCapacityManager<PropertyType> manager);

}
