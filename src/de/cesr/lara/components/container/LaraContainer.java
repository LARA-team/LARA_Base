package de.cesr.lara.components.container;

import de.cesr.lara.components.LaraProperty;

/**
 * TODO could this class be typeless? (ME) this way, one looses the type save
 * characteristic (SH)
 * 
 * @param <PropertyType>
 *            the type of properties this container shall store
 */
public interface LaraContainer<PropertyType extends LaraProperty<?>> extends
		Iterable<PropertyType> {

	/**
	 * A constant to indicate that a storage has (virtually) unlimited capacity.
	 */
	public static final int UNLIMITED_CAPACITY = -1;

	/**
	 * Returns the capacity of this container, i.e. the number of items that can
	 * be stored or {@link #UNLIMITED_CAPACITY} (the default) if it is
	 * (virtually) unlimited.
	 * 
	 * 
	 * @return the capacity of this storage.
	 * 
	 *         Created by Michael Elbers on 22.02.2010
	 */
	public int getCapacity();

	/**
	 * Returns the current size, i.e. the number of items currently stored.
	 * 
	 * @return the current size, i.e. the number of items currently stored.
	 */
	public int getSize();

	/**
	 * Returns true, if and only if this container is empty.
	 * 
	 * @return true, if and only if this container is empty.
	 * 
	 *         Created by Michael Elbers on 22.02.2010
	 */
	public boolean isEmpty();

	/**
	 * Returns true, if and only if this container is full (see also
	 * {@link LaraCapacityManager}).
	 * 
	 * @return true, if and only if this container is full.
	 * 
	 *         Created by Michael Elbers on 22.02.2010
	 */
	public boolean isFull();

}
