package de.cesr.lara.components.container;

import de.cesr.lara.components.LaraProperty;

/**
 * @param <ItemType>
 */
public interface LaraCapacityManagementView<PropertyType extends LaraProperty>
		extends LaraContainer<PropertyType> {

	/**
	 * @param item
	 */
	public void remove(PropertyType property);

}
