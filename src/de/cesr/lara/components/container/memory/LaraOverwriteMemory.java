/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 18.05.2010
 */
package de.cesr.lara.components.container.memory;

import java.util.Collection;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManageable;
import de.cesr.lara.components.container.exceptions.LRemoveException;

/**
 * Memorises only one property per key. I.e., there is no history.
 * 
 * @param <PropertyType>
 * 
 */
public interface LaraOverwriteMemory<PropertyType extends LaraProperty<?>>
		extends LaraMemory<PropertyType>, LaraCapacityManageable<PropertyType> {

	/**
	 * @param key
	 * @return the property to forget
	 * @throws LRemoveException
	 */
	public PropertyType forget(String key) throws LRemoveException;

	@Override
	public Collection<PropertyType> forgetAll(
			Collection<PropertyType> propertiesToBeRemoved)
			throws LRemoveException;

}
